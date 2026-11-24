package com.mha.addonshub

import android.app.DownloadManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.security.MessageDigest
import java.io.FileInputStream

class AddonDownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != DownloadManager.ACTION_DOWNLOAD_COMPLETE) return
        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
        if (id < 0L) return
        val prefs = context.getSharedPreferences("addon_downloads", Context.MODE_PRIVATE)
        val expected = prefs.getString("sha_$id", null) ?: return
        val name = prefs.getString("name_$id", "Minecraft add-on") ?: "Minecraft add-on"
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query().setFilterById(id)
        val cursor = manager.query(query)
        var successful = false
        try {
            if (cursor != null && cursor.moveToFirst()) {
                val statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                successful = statusIndex >= 0 && cursor.getInt(statusIndex) == DownloadManager.STATUS_SUCCESSFUL
            }
        } finally { cursor?.close() }

        if (!successful) {
            postNotice(context, "Download failed", "$name could not be downloaded. Please retry.")
            prefs.edit().remove("sha_$id").remove("name_$id").apply()
            return
        }

        try {
            val digest = MessageDigest.getInstance("SHA-256")
            manager.openDownloadedFile(id).use { descriptor ->
                FileInputStream(descriptor.fileDescriptor).use { input ->
                    val buffer = ByteArray(16 * 1024)
                    while (true) {
                        val count = input.read(buffer)
                        if (count <= 0) break
                        digest.update(buffer, 0, count)
                    }
                }
            }
            val actual = digest.digest().joinToString("") { "%02x".format(it) }
            if (!actual.equals(expected, ignoreCase = true)) {
                manager.remove(id)
                postNotice(context, "Package check failed", "$name failed its SHA-256 integrity check and was removed.")
                prefs.edit().remove("sha_$id").remove("name_$id").apply()
                return
            }

            val uri = manager.getUriForDownloadedFile(id) ?: error("Downloaded file URI unavailable")
            val mime = when {
                prefs.getString("file_$id", "")?.endsWith(".mcpack", true) == true -> "application/octet-stream"
                else -> "application/octet-stream"
            }
            val openIntent = Intent(context, MainActivity::class.java)
                .putExtra("import_uri", uri.toString())
                .putExtra("import_mime", mime)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            val pending = PendingIntent.getActivity(context, id.toInt(), openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            postNotice(context, "Package verified", "$name is ready. Tap to import into Minecraft.", pending)
            prefs.edit().remove("sha_$id").remove("name_$id").remove("file_$id").apply()
        } catch (_: Exception) {
            postNotice(context, "Import preparation failed", "$name downloaded, but could not be verified or opened.")
        }
    }

    private fun postNotice(context: Context, title: String, message: String, pending: PendingIntent? = null) {
        val channelId = "addon_imports"
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            manager.createNotificationChannel(android.app.NotificationChannel(channelId, "Add-on imports", android.app.NotificationManager.IMPORTANCE_DEFAULT))
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setAutoCancel(true)
            .setContentIntent(pending)
            .build()
        try { NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification) } catch (_: SecurityException) { }
    }
}

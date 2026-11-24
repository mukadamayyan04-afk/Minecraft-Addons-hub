package com.mha.addonshub

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL

private const val CATALOG_URL = "https://raw.githubusercontent.com/mukadamayyan04-afk/Minecraft-Addons-hub/main/catalog/addons.json"

data class Addon(
    val id: String,
    val name: String,
    val description: String,
    val category: String,
    val version: String,
    val sizeBytes: Long,
    val fileUrl: String,
    val sha256: String,
    val minBedrock: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AddonsHubApp() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddonsHubApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var addons by remember { mutableStateOf<List<Addon>>(emptyList()) }
    var selected by remember { mutableStateOf("Home") }
    var query by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Connect to load the latest catalog.") }
    var loading by remember { mutableStateOf(false) }
    var selectedAddon by remember { mutableStateOf<Addon?>(null) }

    fun refreshCatalog() {
        scope.launch {
            loading = true
            status = "Loading catalog…"
            try {
                val result = withContext(Dispatchers.IO) { loadCatalog() }
                addons = result
                status = if (result.isEmpty()) "Catalog is online but has no verified add-ons yet." else "${result.size} add-ons loaded."
            } catch (e: Exception) {
                status = "Catalog unavailable. Check your internet and try again."
            } finally { loading = false }
        }
    }

    LaunchedEffect(Unit) { refreshCatalog() }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = androidx.compose.ui.graphics.Color(0xFF63D6A5),
        secondary = androidx.compose.ui.graphics.Color(0xFF8AB4F8),
        background = androidx.compose.ui.graphics.Color(0xFF0B1014),
        surface = androidx.compose.ui.graphics.Color(0xFF151D23)
    )) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Column {
                        Text("MINECRAFT ADDONS HUB", fontWeight = FontWeight.Bold)
                        Text("Bedrock Edition • Community catalog", style = MaterialTheme.typography.labelSmall)
                    } },
                    actions = {
                        IconButton(onClick = { refreshCatalog() }, enabled = !loading) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh catalog")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            bottomBar = {
                NavigationBar {
                    listOf("Home", "Explore", "Downloads", "Updates", "Settings").forEach { item ->
                        val icon = when(item) {
                            "Home" -> Icons.Default.Home
                            "Explore" -> Icons.Default.Explore
                            "Downloads" -> Icons.Default.Download
                            "Updates" -> Icons.Default.Update
                            else -> Icons.Default.Settings
                        }
                        NavigationBarItem(
                            selected = selected == item,
                            onClick = { selected = item },
                            icon = { Icon(icon, contentDescription = item) },
                            label = { Text(item) }
                        )
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                if (selected == "Home" || selected == "Explore") {
                    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.padding(18.dp)) {
                            Text(if (selected == "Home") "Discover your next world" else "Explore add-ons", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            Text("Find community add-ons and download compatible packs for Minecraft Bedrock.")
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(
                                value = query, onValueChange = { query = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("Search add-ons") },
                                leadingIcon = { Icon(Icons.Default.Search, null) },
                                singleLine = true
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(status, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                        if (loading) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                    }
                    Spacer(Modifier.height(8.dp))
                    val visible = addons.filter { it.name.contains(query, true) || it.category.contains(query, true) }
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(visible) { addon ->
                            Card(
                                modifier = Modifier.fillMaxWidth().clickable { selectedAddon = addon },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text(addon.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(addon.category + " • " + addon.version, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.secondary)
                                    Spacer(Modifier.height(6.dp))
                                    Text(addon.description, maxLines = 3, style = MaterialTheme.typography.bodyMedium)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Bedrock " + addon.minBedrock + " • " + formatSize(addon.sizeBytes), style = MaterialTheme.typography.labelSmall)
                                    TextButton(onClick = { downloadAndImport(context, addon) }) {
                                        Icon(Icons.Default.Download, null)
                                        Spacer(Modifier.width(6.dp))
                                        Text("Download")
                                    }
                                }
                            }
                        }
                    }
                } else {
                    val message = when(selected) {
                        "Downloads" -> "Downloads started by this app are managed by Android Download Manager. Open a completed .mcpack or .mcaddon file and choose Minecraft."
                        "Updates" -> "Catalog updates arrive from the online repository. App code updates require a new APK installation."
                        else -> "Free • No ads • No subscriptions\n\nCatalog source: GitHub\nOnly install add-ons from sources you trust."
                    }
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.padding(18.dp)) {
                            Text(selected, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text(message)
                            if (selected == "Settings") {
                                Spacer(Modifier.height(12.dp))
                                Text("Catalog refresh is available from the refresh icon in the top bar.")
                            }
                        }
                    }
                }
            }
        }
    }

    selectedAddon?.let { addon ->
        AlertDialog(
            onDismissRequest = { selectedAddon = null },
            title = { Text(addon.name) },
            text = { Text("${addon.description}\n\nPackage size: ${formatSize(addon.sizeBytes)}\nBedrock: ${addon.minBedrock}\nVersion: ${addon.version}") },
            confirmButton = {
                TextButton(onClick = { downloadAndImport(context, addon); selectedAddon = null }) { Text("Download") }
            },
            dismissButton = { TextButton(onClick = { selectedAddon = null }) { Text("Close") } }
        )
    }
}

private fun loadCatalog(): List<Addon> {
    val connection = URL(CATALOG_URL).openConnection() as HttpURLConnection
    connection.connectTimeout = 12000
    connection.readTimeout = 12000
    connection.requestMethod = "GET"
    try {
        if (connection.responseCode !in 200..299) error("HTTP ${connection.responseCode}")
        val root = JSONArray(connection.inputStream.bufferedReader().use { it.readText() })
        return buildList {
            for (i in 0 until root.length()) {
                val item = root.getJSONObject(i)
                val url = item.optString("fileUrl")
                val hash = item.optString("sha256")
                if (url.startsWith("https://") && hash.matches(Regex("[a-fA-F0-9]{64}"))) {
                    add(Addon(
                        item.getString("id"), item.getString("name"), item.optString("description"),
                        item.optString("category", "Community"), item.optString("version", "1.0"),
                        item.optLong("sizeBytes"), url, hash, item.optString("minBedrock", "unknown")
                    ))
                }
            }
        }
    } finally { connection.disconnect() }
}

private fun downloadAndImport(context: Context, addon: Addon) {
    val request = DownloadManager.Request(Uri.parse(addon.fileUrl))
        .setTitle(addon.name)
        .setDescription("Downloading add-on package")
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MinecraftAddons/${addon.id}.mcaddon")
        .setMimeType("application/octet-stream")
        .setAllowedOverMetered(false)
        .setAllowedOverRoaming(false)
    val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    manager.enqueue(request)
}

private fun formatSize(bytes: Long): String =
    if (bytes <= 0) "Size not supplied" else String.format("%.1f MB", bytes / 1048576.0)

package com.mha.addonshub

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext

private val Ink = Color(0xFF09131A)
private val Panel = Color(0xFF14232C)
private val Mint = Color(0xFF61E6B0)
private val Muted = Color(0xFF9DB2BC)

data class AddonInfo(val name: String, val category: String, val description: String, val tag: String = "In curation")

private val plannedAddons = listOf(
    AddonInfo("Modern Furniture World", "Build & Decor", "Modern homes, furniture concepts and stylish interiors."),
    AddonInfo("Ultimate Pets & Animals", "Pets & Nature", "A peaceful collection focused on friendly animals and pets."),
    AddonInfo("Vehicles & Transport", "Vehicles", "Explore creative transport and vehicle builds."),
    AddonInfo("Realistic Nature", "Nature", "Richer plants, trees and natural scenery."),
    AddonInfo("Fantasy Structures", "Building", "Fantasy castles, towers and decorative structures."),
    AddonInfo("Advanced Farming", "Farming", "More crops, farm decorations and peaceful farming ideas."),
    AddonInfo("Ocean Discovery", "Ocean", "Discover underwater scenery and marine life."),
    AddonInfo("Sky Islands", "Exploration", "Floating islands, scenic routes and sky builds."),
    AddonInfo("Seasons & Weather", "Environment", "Seasonal atmosphere and changing environmental visuals."),
    AddonInfo("HD Ocean Textures", "Textures", "A visual refresh for water and ocean environments."),
    AddonInfo("Realistic Sky Pack", "Textures", "Atmospheric sky and cloud visuals."),
    AddonInfo("HD Lighting & Interiors", "Textures", "Cozy lighting and interior-focused visual details."),
    AddonInfo("Adventure Explorer Pack", "Adventure", "Peaceful exploration, puzzles and collectible discoveries."),
    AddonInfo("Solo Leveling – Hunter World", "Fan-made world", "Original fan-made, peaceful exploration and story-inspired world. Not official franchise content."),
    AddonInfo("Naruto – Shinobi World", "Fan-made world", "Original fan-made, peaceful exploration and story-inspired world. Not official franchise content.")
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AddonsHubApp() }
        handleImportIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleImportIntent(intent)
    }

    private fun handleImportIntent(intent: Intent?) {
        val raw = intent?.getStringExtra("import_uri") ?: return
        val mime = intent.getStringExtra("import_mime") ?: "application/octet-stream"
        val view = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(Uri.parse(raw), mime)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        try {
            startActivity(view)
        } catch (_: Exception) {
            Toast.makeText(this, "Minecraft ya compatible importer install nahi mila. File Downloads mein available hai.", Toast.LENGTH_LONG).show()
        }
        intent?.removeExtra("import_uri")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddonsHubApp() {
    val context = LocalContext.current
    var tab by remember { mutableStateOf("Home") }
    var search by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<AddonInfo?>(null) }
    val filtered = plannedAddons.filter { it.name.contains(search, true) || it.category.contains(search, true) }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Mint, secondary = Color(0xFF91B7FF),
            background = Ink, surface = Panel, onBackground = Color.White,
            onSurface = Color.White
        )
    ) {
        Scaffold(
            containerColor = Ink,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(Modifier.size(38.dp).background(Brush.linearGradient(listOf(Mint, Color(0xFF279D9C))), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Extension, null, tint = Ink, modifier = Modifier.size(23.dp))
                            }
                            Column {
                                Text("ADDONS HUB", fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                                Text("MINECRAFT BEDROCK", color = Muted, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Ink)
                )
            },
            bottomBar = {
                NavigationBar(containerColor = Color(0xFF101D25), contentColor = Mint) {
                    listOf("Home", "Explore", "Downloads", "Settings").forEach { item ->
                        val icon = when (item) {
                            "Home" -> Icons.Default.Home
                            "Explore" -> Icons.Default.Explore
                            "Downloads" -> Icons.Default.Download
                            else -> Icons.Default.Settings
                        }
                        NavigationBarItem(
                            selected = tab == item, onClick = { tab = item },
                            icon = { Icon(icon, contentDescription = item) },
                            label = { Text(item) },
                            colors = NavigationBarItemDefaults.colors(selectedIconColor = Mint, selectedTextColor = Mint, indicatorColor = Mint.copy(alpha = .12f))
                        )
                    }
                }
            }
        ) { padding ->
            when (tab) {
                "Home", "Explore" -> LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Panel),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(Modifier.background(Brush.linearGradient(listOf(Color(0xFF173B3A), Color(0xFF172633), Panel))).padding(20.dp)) {
                                Column {
                                    Surface(color = Mint.copy(alpha = .14f), shape = CircleShape) {
                                        Text("  BEDROCK ADD-ONS  ", color = Mint, modifier = Modifier.padding(vertical = 6.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(Modifier.height(14.dp))
                                    Text(if (tab == "Home") "Build a world\nthat feels yours." else "Explore add-ons", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = Color.White)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Discover new builds, nature, pets and peaceful adventures.", color = Muted)
                                    Spacer(Modifier.height(16.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Verified, tint = Mint, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(Modifier.width(6.dp))
                                        Text("Free • No ads • No subscriptions", color = Mint, style = MaterialTheme.typography.labelMedium)
                                    }
                                }
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = search, onValueChange = { search = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("Search 15 add-ons…") },
                            leadingIcon = { Icon(Icons.Default.Search, null) },
                            singleLine = true, shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Mint, unfocusedBorderColor = Color(0xFF344750),
                                focusedContainerColor = Panel, unfocusedContainerColor = Panel
                            )
                        )
                    }
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("FEATURED COLLECTION", color = Muted, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                            Text("15 items", color = Mint, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    items(filtered) { addon ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { selected = addon },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = Panel)
                        ) {
                            Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(54.dp).background(Color(0xFF203A40), RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) {
                                    Icon(categoryIcon(addon.category), null, tint = Mint, modifier = Modifier.size(28.dp))
                                }
                                Spacer(Modifier.width(13.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(addon.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Spacer(Modifier.height(4.dp))
                                    Text(addon.category, color = Muted, style = MaterialTheme.typography.labelSmall)
                                    Spacer(Modifier.height(7.dp))
                                    Text(addon.tag, color = Color(0xFFFFD18A), style = MaterialTheme.typography.labelSmall)
                                }
                                Icon(Icons.Default.ChevronRight, null, tint = Muted)
                            }
                        }
                    }
                    item {
                        Text("Packages will appear as downloadable only after the real file, SHA-256 and Bedrock compatibility are verified.", color = Muted, style = MaterialTheme.typography.bodySmall)
                    }
                }
                "Downloads" -> Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Downloads", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Icon(Icons.Default.CloudDownload, tint = Mint, modifier = Modifier.size(34.dp), contentDescription = null)
                            Spacer(Modifier.height(10.dp))
                            Text("Your downloads will show here", fontWeight = FontWeight.Bold)
                            Text("Verified package downloads will appear here when published.", color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Text("Import will use Android's supported open-file permission flow. The app will not access Minecraft's private folders.", color = Muted)
                }
                else -> Column(Modifier.fillMaxSize().padding(padding).padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Card(colors = CardDefaults.cardColors(containerColor = Panel), shape = RoundedCornerShape(20.dp)) {
                        Column(Modifier.padding(18.dp)) {
                            Text("Minecraft Addons Hub", fontWeight = FontWeight.Bold)
                            Text("Version 1.1 • Free • Ad-free", color = Muted)
                            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFF344750))
                            Text("Import permission", fontWeight = FontWeight.SemiBold)
                            Text("Android will ask which app can open the add-on. Choose Minecraft. No access to Minecraft's private folders is needed.", color = Muted, style = MaterialTheme.typography.bodySmall)
                            HorizontalDivider(Modifier.padding(vertical = 12.dp), color = Color(0xFF344750))
                            Text("Wi-Fi preference", fontWeight = FontWeight.SemiBold)
                            Text("Downloads will be Wi-Fi-only by default when verified packages are enabled.", color = Muted, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
        selected?.let { addon ->
            AlertDialog(
                onDismissRequest = { selected = null },
                containerColor = Panel,
                title = { Text(addon.name, fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(addon.description)
                        Text("Category: " + addon.category, color = Muted)
                        Text("Download is not enabled yet: the package file has not passed source, checksum and Bedrock-version checks.", color = Color(0xFFFFD18A), style = MaterialTheme.typography.bodySmall)
                    }
                },
                confirmButton = { TextButton(onClick = { selected = null }) { Text("Got it", color = Mint) } },
                dismissButton = { TextButton(onClick = { selected = null }) { Text("Close") } }
            )
        }
    }
}

private fun categoryIcon(category: String) = when {
    category.contains("Pet") || category.contains("Nature") -> Icons.Default.Pets
    category.contains("Build") || category.contains("Building") -> Icons.Default.Home
    category.contains("Vehicle") -> Icons.Default.DirectionsCar
    category.contains("Ocean") -> Icons.Default.Waves
    category.contains("Farm") -> Icons.Default.Eco
    category.contains("Texture") -> Icons.Default.Palette
    category.contains("Adventure") || category.contains("Exploration") -> Icons.Default.Explore
    else -> Icons.Default.AutoAwesome
}

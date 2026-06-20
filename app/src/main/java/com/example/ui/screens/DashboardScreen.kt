package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.example.ui.NexusViewModel

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay

import com.example.ui.UnityAdsHelper
import com.example.ui.AdBannerComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: NexusViewModel, onNavigateToAdmin: () -> Unit) {
    val showUpgradeDialog by viewModel.showUpgradeDialog.collectAsStateWithLifecycle()
    val credits by viewModel.credits.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItemIndex by remember { mutableStateOf(0) }
    val context = LocalContext.current
    
    var showAdminDialog by remember { mutableStateOf(false) }
    var adminPin by remember { mutableStateOf("") }
    val ADMIN_PIN = "8000"

    val items = listOf(
        "Home" to Icons.Filled.Home,
        "AI Image" to Icons.Filled.Image,
        "My Creations" to Icons.Filled.Folder,
        "Inspiration" to Icons.Filled.Lightbulb,
        "Templates" to Icons.Filled.AutoAwesomeMosaic,
        "AI Tools" to Icons.Filled.Build,
        "Enhance" to Icons.Filled.AutoFixHigh,
        "Remix" to Icons.Filled.Shuffle,
        "Upscale" to Icons.Filled.HighQuality,
        "Remove BG" to Icons.Filled.FormatPaint
    )

    val premiumTools = listOf("Upscale", "Remove BG")

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(280.dp)
            ) {
                Spacer(Modifier.height(24.dp))
                Text(
                    "NEXUS AI",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = 2.sp,
                    modifier = Modifier
                        .padding(horizontal = 28.dp, vertical = 16.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    val job = scope.launch {
                                        delay(3000)
                                        showAdminDialog = true
                                    }
                                    tryAwaitRelease()
                                    job.cancel()
                                }
                            )
                        }
                )
                HorizontalDivider(color = Color.DarkGray, modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(16.dp))
                
                items.forEachIndexed { index, pair ->
                    NavigationDrawerItem(
                        icon = { 
                            Icon(
                                pair.second,
                                contentDescription = pair.first,
                                tint = if (selectedItemIndex == index) MaterialTheme.colorScheme.primary else Color.Gray
                            ) 
                        },
                        label = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    pair.first,
                                    color = if (selectedItemIndex == index) Color.White else Color.LightGray,
                                    fontWeight = if (selectedItemIndex == index) FontWeight.Bold else FontWeight.Normal
                                )
                                if (premiumTools.contains(pair.first)) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        Icons.Filled.Star,
                                        contentDescription = "Premium",
                                        tint = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        },
                        selected = selectedItemIndex == index,
                        onClick = {
                            if (premiumTools.contains(pair.first)) {
                                viewModel.usePremiumTool()
                            } else {
                                selectedItemIndex = index
                            }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            unselectedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                
                Spacer(Modifier.weight(1f))
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.Gray) },
                    label = { Text("Logout", color = Color.LightGray) },
                    selected = false,
                    onClick = { viewModel.logout() },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(items[selectedItemIndex].first, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        ContainerCredits(credits)
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Select a tool to begin.",
                        color = Color.DarkGray,
                        fontSize = 18.sp
                    )
                }
                AdBannerComponent()
            }
        }
    }

    if (showAdminDialog) {
        AlertDialog(
            onDismissRequest = { 
                showAdminDialog = false 
                adminPin = ""
            },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text("Secret Access", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Enter Secret PIN to access the Admin Panel.", color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = adminPin,
                        onValueChange = { adminPin = it },
                        visualTransformation = PasswordVisualTransformation(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = Color.DarkGray
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        if (adminPin == ADMIN_PIN) {
                            showAdminDialog = false
                            adminPin = ""
                            scope.launch { drawerState.close() }
                            onNavigateToAdmin()
                        } else {
                            Toast.makeText(context, "Access Denied", Toast.LENGTH_SHORT).show()
                            showAdminDialog = false
                            adminPin = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Verify", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showAdminDialog = false
                    adminPin = ""
                }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }

    if (showUpgradeDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissUpgradeDialog() },
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text("Upgrade to Premium", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column {
                    Text("Unlock high-end features like Upscale and Remove BG.", color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("7-Day Trial", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text("₹49", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Monthly Subscription", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text("₹99 / month", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        viewModel.dismissUpgradeDialog() 
                        val phoneNumber = "9128692966"
                        val message = "Hi Aryan, I want to subscribe to Nexus AI."
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://api.whatsapp.com/send?phone=$phoneNumber&text=${Uri.encode(message)}")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "WhatsApp not installed.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Subscribe via WhatsApp", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissUpgradeDialog() }) {
                    Text("Cancel", color = Color.Gray)
                }
            }
        )
    }
}

@Composable
fun ContainerCredits(credits: Int) {
    Row(
        modifier = Modifier
            .padding(end = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Credits",
            tint = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "$credits Credits",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

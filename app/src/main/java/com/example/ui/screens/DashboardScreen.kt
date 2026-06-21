
package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.AdBannerComponent
import com.example.ui.NexusViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: NexusViewModel, onNavigateToAdmin: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    // एडमिन पैनल का लॉजिक
    var showAdminDialog by remember { mutableStateOf(false) }
    var adminPin by remember { mutableStateOf("") }
    val ADMIN_PIN = "12qwasyx"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // ऐप का नाम जिस पर 3 सेकंड तक दबाने से एडमिन पैनल खुलेगा
                    Text(
                        "NEXUS AI",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    val job = scope.launch {
                                        delay(3000) // 3 सेकंड का इंतज़ार
                                        showAdminDialog = true
                                    }
                                    tryAwaitRelease()
                                    job.cancel()
                                }
                            )
                        }
                    )
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.BottomCenter) {
            // यहाँ तुम्हारा मेन कंटेंट रहेगा
            Column(modifier = Modifier.fillMaxSize().padding(bottom = 60.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("Dashboard", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
            
            // यहाँ बैनर ऐड हमेशा दिखेगा[span_1](start_span)[span_1](end_span)
            AdBannerComponent()
        }
    }

    // एडमिन पिन डायलॉग
    if (showAdminDialog) {
        AlertDialog(
            onDismissRequest = { showAdminDialog = false },
            title = { Text("Admin Access") },
            text = {
                OutlinedTextField(
                    value = adminPin,
                    onValueChange = { adminPin = it },
                    label = { Text("Enter PIN") },
                    visualTransformation = PasswordVisualTransformation()
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (adminPin == ADMIN_PIN) {
                        onNavigateToAdmin()
                        showAdminDialog = false
                    }
                    adminPin = ""
                }) { Text("Verify") }
            }
        )
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.NexusViewModel
import com.example.ui.UnityAdsHelper
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        UnityAdsHelper.initialize(this)
        
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: NexusViewModel = viewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
                var showAdmin by remember { mutableStateOf(false) }
                
                if (showAdmin && isLoggedIn) {
                    AdminDashboardScreen(viewModel = viewModel, onBack = { showAdmin = false })
                } else if (!isLoggedIn) {
                    LoginScreen(viewModel = viewModel)
                } else {
                    DashboardScreen(viewModel = viewModel, onNavigateToAdmin = { showAdmin = true })
                }
            }
        }
    }
}

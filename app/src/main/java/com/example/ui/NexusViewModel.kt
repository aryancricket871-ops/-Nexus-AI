package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.UserEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NexusViewModel(application: Application) : AndroidViewModel(application) {

    private val userDao = AppDatabase.getDatabase(application).userDao()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _loginStatus = MutableStateFlow<String?>(null)
    val loginStatus: StateFlow<String?> = _loginStatus.asStateFlow()

    private val _credits = MutableStateFlow(120)
    val credits: StateFlow<Int> = _credits.asStateFlow()

    private val _showUpgradeDialog = MutableStateFlow(false)
    val showUpgradeDialog: StateFlow<Boolean> = _showUpgradeDialog.asStateFlow()

    val totalUsers: StateFlow<Int> = userDao.getTotalUsers()
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(), 0)

    val activeUsers: StateFlow<Int> = userDao.getActiveUsers(System.currentTimeMillis() - 86400000) // Last 24 hours
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(), 0)

    val totalGenerations: StateFlow<Int> = userDao.getTotalGenerations()
        .stateIn(viewModelScope, kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(), 0)

    fun login(fullName: String, username: String, pass: String, avatarIndex: Int) {
        viewModelScope.launch {
            val existingUser = userDao.getUserByUsername(username)
            if (existingUser != null) {
                if (existingUser.passwordHash == pass) {
                    userDao.saveUser(
                        existingUser.copy(
                            isLoggedIn = true, 
                            avatarIndex = avatarIndex, 
                            fullName = fullName,
                            lastLoginTimestamp = System.currentTimeMillis()
                        )
                    )
                    _loginStatus.value = "match"
                    _isLoggedIn.value = true
                } else {
                    _loginStatus.value = "null" // Invalid password
                }
            } else {
                userDao.saveUser(
                    UserEntity(
                        fullName = fullName,
                        username = username,
                        avatarIndex = avatarIndex,
                        passwordHash = pass,
                        isLoggedIn = true,
                        lastLoginTimestamp = System.currentTimeMillis()
                    )
                )
                _loginStatus.value = "registered"
                _isLoggedIn.value = true
            }
        }
    }

    fun grantPro(username: String, days: Int) {
        viewModelScope.launch {
            val expiry = System.currentTimeMillis() + (days * 86400000L) // ms in a day
            userDao.grantPro(username, expiry)
        }
    }

    fun clearLoginStatus() {
        _loginStatus.value = null
    }

    fun logout() {
        viewModelScope.launch {
            userDao.clearSession()
            _isLoggedIn.value = false
        }
    }

    fun usePremiumTool() {
        _showUpgradeDialog.value = true
    }

    fun dismissUpgradeDialog() {
        _showUpgradeDialog.value = false
    }
}

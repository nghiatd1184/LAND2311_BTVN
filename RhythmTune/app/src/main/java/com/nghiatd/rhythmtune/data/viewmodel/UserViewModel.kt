package com.nghiatd.rhythmtune.data.viewmodel

import androidx.lifecycle.ViewModel
import com.nghiatd.rhythmtune.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedViewModel : ViewModel() {
    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser = _selectedUser.asStateFlow()

    fun setUser(user: User?) {
        _selectedUser.value = user
    }
}
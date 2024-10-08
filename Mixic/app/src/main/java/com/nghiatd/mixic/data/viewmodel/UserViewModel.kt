package com.nghiatd.mixic.data.viewmodel

import androidx.lifecycle.ViewModel
import com.nghiatd.mixic.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserViewModel : ViewModel() {
    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser = _selectedUser.asStateFlow()

    fun setUser(user: User?) {
        _selectedUser.value = user
    }
}
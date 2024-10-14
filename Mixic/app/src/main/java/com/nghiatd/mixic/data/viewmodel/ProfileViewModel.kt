package com.nghiatd.mixic.data.viewmodel

import androidx.lifecycle.ViewModel
import com.nghiatd.mixic.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser = _currentUser.asStateFlow()


}
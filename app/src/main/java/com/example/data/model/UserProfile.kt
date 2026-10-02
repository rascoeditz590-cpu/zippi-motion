package com.example.data.model

data class UserProfile(
    val id: String,
    val displayName: String,
    val email: String,
    val isGuest: Boolean = true,
    val projectCount: Int = 0,
    val cloudSyncEnabled: Boolean = false
)

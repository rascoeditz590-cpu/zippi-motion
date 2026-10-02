package com.example.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "zippi_settings")

class UserPreferencesRepository(private val context: Context) {

    private val KEY_USER_ID = stringPreferencesKey("user_id")
    private val KEY_DISPLAY_NAME = stringPreferencesKey("display_name")
    private val KEY_EMAIL = stringPreferencesKey("email")
    private val KEY_IS_GUEST = booleanPreferencesKey("is_guest")
    private val KEY_PROXY_ENABLED = booleanPreferencesKey("proxy_enabled")
    private val KEY_SNAPPING_ENABLED = booleanPreferencesKey("snapping_enabled")

    val userProfile: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        val userId = preferences[KEY_USER_ID] ?: "guest_user"
        val name = preferences[KEY_DISPLAY_NAME] ?: "Guest Creator"
        val email = preferences[KEY_EMAIL] ?: "guest@zippimotion.app"
        val isGuest = preferences[KEY_IS_GUEST] ?: true
        UserProfile(
            id = userId,
            displayName = name,
            email = email,
            isGuest = isGuest
        )
    }

    val isProxyEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_PROXY_ENABLED] ?: true
    }

    val isSnappingEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_SNAPPING_ENABLED] ?: true
    }

    suspend fun setUser(id: String, displayName: String, email: String, isGuest: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = id
            preferences[KEY_DISPLAY_NAME] = displayName
            preferences[KEY_EMAIL] = email
            preferences[KEY_IS_GUEST] = isGuest
        }
    }

    suspend fun setProxyEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PROXY_ENABLED] = enabled
        }
    }

    suspend fun setSnappingEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_SNAPPING_ENABLED] = enabled
        }
    }
}

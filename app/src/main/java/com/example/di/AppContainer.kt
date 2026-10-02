package com.example.di

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.repository.DeviceCapabilityRepository
import com.example.data.repository.ProjectRepository
import com.example.data.repository.ProjectRepositoryImpl
import com.example.data.repository.UserPreferencesRepository

interface AppContainer {
    val projectRepository: ProjectRepository
    val deviceCapabilityRepository: DeviceCapabilityRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    override val projectRepository: ProjectRepository by lazy {
        ProjectRepositoryImpl(
            projectDao = database.projectDao(),
            layerDao = database.layerDao(),
            keyframeDao = database.keyframeDao()
        )
    }

    override val deviceCapabilityRepository: DeviceCapabilityRepository by lazy {
        DeviceCapabilityRepository()
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }
}

package com.example

import android.app.Application
import com.example.data.repository.ProjectRepositoryImpl
import com.example.di.AppContainer
import com.example.di.DefaultAppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class ZippiMotionApp : Application() {

    lateinit var container: AppContainer
        private set

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)

        applicationScope.launch {
            (container.projectRepository as? ProjectRepositoryImpl)?.seedInitialDataIfEmpty()
        }
    }
}

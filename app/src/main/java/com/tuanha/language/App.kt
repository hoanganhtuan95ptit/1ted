package com.tuanha.language

import com.tuanha.coreapp.App
import com.tuanha.coreapp.di.coreCacheModule
import com.tuanha.language.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : App() {

    companion object {
        lateinit var shared: App
    }

    override fun onCreate() {
        shared = this

        super.onCreate()

        startKoin {

            androidContext(this@App)

            androidLogger(Level.NONE)

            modules(
                appModule,

                apiModule,

                daoModule,

                cacheModule,

                memoryModule,

                coreCacheModule,

                realtimeModule,

                repositoryModule,

                interactModule,

                exceptionModule,
                viewModelModule
            )
        }
    }
}
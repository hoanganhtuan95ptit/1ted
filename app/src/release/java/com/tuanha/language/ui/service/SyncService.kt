package com.tuanha.language.ui.service

import android.util.Log
import com.tuanha.coreapp.ui.servicer.BaseForegroundService
import com.tuanha.coreapp.utils.extentions.serviceScope
import com.tuanha.language.data.api.AppApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.io.*


class SyncService : BaseForegroundService() {

    override fun onCreate() {
        super.onCreate()

    }
}
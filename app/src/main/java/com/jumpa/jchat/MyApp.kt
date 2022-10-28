package com.jumpa.jchat

import android.app.Application
import android.content.Context

class MyApp : Application() {
    companion object {
        private lateinit var appContext : Context
        val context: Context
            get() = appContext
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext

    }
}
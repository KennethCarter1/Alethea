package com.example.alethea

import android.app.Application

class AletheaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}

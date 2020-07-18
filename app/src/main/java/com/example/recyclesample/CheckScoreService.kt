package com.example.recyclesample

import android.app.Service
import android.content.Intent
import android.os.IBinder

class CheckScoreService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}

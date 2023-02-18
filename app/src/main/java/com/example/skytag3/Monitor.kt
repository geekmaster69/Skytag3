package com.example.skytag3

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class Monitor: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val serviceIntent = Intent()
        serviceIntent.action = "ServicioCodigoFacilito"
        context?.startForegroundService(serviceIntent)

    }
}
package com.example.skytag3.gpsService

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.skytag3.service.DefaultLocationClient
import com.example.skytag3.service.LocationClient
import com.google.android.gms.location.LocationServices
import io.paperdb.Paper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class GpsService : Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    private lateinit var lat: String
    private lateinit var long: String
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        getLocation()


        return super.onStartCommand(intent, flags, startId)
    }

    private fun getLocation() {
        locationClient.getLocationClient(7*24*60*60*1000)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                lat = location.latitude.toString()
                long = location.longitude.toString()



            }
            .launchIn(serviceScope)

        stopSelf()

    }

    override fun onCreate() {
        super.onCreate()

        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext))

        Paper.init(applicationContext)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}
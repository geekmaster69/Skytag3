package com.example.skytag3.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skytag3.base.db.UserInfoApplication
import com.example.skytag3.data.entity.UserInfoEntity
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


private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
private lateinit var locationClient: LocationClient
class LocationWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        Paper.init(applicationContext)
        Log.i("Get location", "get location")


        makeStatusNotification("Get Location", applicationContext)
        starLocation()
        return Result.success()
    }

    private fun starLocation() {

        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext))

        locationClient.getLocationClient(10000L)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->

                Paper.book().write("latitude", location.latitude)
                Paper.book().write("longitude", location.longitude)

              /*  val userInfo = UserInfoApplication.database.userInfoDao().getAllData()

                val usuario = userInfo.usuario
                val contrasena = userInfo.contrasena
                val mensaje = userInfo.mensaje
                val identificador = userInfo.identificador
                val tagkey = userInfo.tagkey


                UserInfoApplication.database.userInfoDao()
                    .addUserInfo(
                        UserInfoEntity(
                            tagkey = tagkey,
                            usuario = usuario,
                            contrasena = contrasena,
                            mensaje = mensaje,
                            identificador = identificador,
                        latitud = location.latitude,
                        longitud = location.longitude))

*/

                Log.w("LocationWorkManager", "${location.latitude} ${location.longitude}")

            }
            .launchIn(serviceScope)

    }
}
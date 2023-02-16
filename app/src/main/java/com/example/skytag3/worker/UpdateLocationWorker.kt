package com.example.skytag3.worker

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skytag3.base.db.UserInfoApplication
import com.example.skytag3.model.UserInfo
import com.example.skytag3.network.UserService
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

private lateinit var dateFormat: SimpleDateFormat

class UpdateLocationWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    private val userService = UserService()
    override suspend fun doWork(): Result {
        makeStatusNotification("update location", applicationContext)

        delay(20000)
        uploadLocation()

        return Result.success()
    }

    private suspend fun uploadLocation() {
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val userInfo = UserInfoApplication.database.userInfoDao().getAllData()

        val mensaje = "RegistraPosicion"
        val usuario = userInfo.usuario
        val tagKey = userInfo.tagkey
        val codigo = "3"
        val date = Date()
        val contrasena = userInfo.contrasena
        val fecha = dateFormat.format(date)
        val ideantificador = userInfo.identificador
        val lat = userInfo.latitud
        val long = userInfo.longitud

        val response =  userService.updateUserInfo(
            UserInfo(
            mensaje = mensaje,
            usuario = usuario,
            longitud = long,
            latitud = lat,
            tagkey = tagKey,
            contrasena = contrasena,
            codigo = codigo,
            fechahora = fecha,
            identificador = ideantificador)
        )

        Log.w(TAG, response.toString())
    }
}
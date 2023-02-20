package com.example.skytag3.worker

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skytag3.model.UserInfo
import com.example.skytag3.network.UserService
import io.paperdb.Paper
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

private lateinit var dateFormat: SimpleDateFormat

class UpdateLocationWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    private val userService = UserService()
    override suspend fun doWork(): Result {
        Paper.init(applicationContext)
        Log.i("update location", "Update location")


        delay(10000)
        makeStatusNotification("update location", applicationContext)
        uploadLocation()

        return Result.success()
    }

    private suspend fun uploadLocation() {
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val mensaje = Paper.book().read<String>("mensaje")
        val usuario = Paper.book().read<String>("user")
        val tagkey =  Paper.book().read<String>("tagkey") ?: "No disponible"
        val contrasena = Paper.book().read<String>("contrasena")
        val latitude =  Paper.book().read<Double>("latitude")
        val longitude = Paper.book().read<Double>("longitude")
        val identificador = Paper.book().read<String>("identificador")
        val fecha = dateFormat.format(Date())
        val codigo =  "3"


        val response =  userService.updateUserInfo(
            UserInfo(
            mensaje = mensaje!!,
            usuario = usuario!!,
            longitud = longitude!!,
            latitud = latitude!!,
            tagkey = tagkey,
            contrasena = contrasena!!,
            codigo = codigo,
            fechahora = fecha,
            identificador = identificador!!))

        Paper.book().delete("latitude")
        Paper.book().delete("longitude")

        Log.w(TAG, response.toString())
    }
}
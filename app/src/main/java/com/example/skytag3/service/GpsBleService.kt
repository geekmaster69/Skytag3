package com.example.skytag3.service

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.os.*
import android.util.Log
import com.example.skytag3.base.db.UserInfoApplication
import com.example.skytag3.data.entity.UserInfoEntity
import com.example.skytag3.model.UserInfo
import com.example.skytag3.network.UserService
import com.google.android.gms.location.LocationServices
import com.polidea.rxandroidble3.NotificationSetupMode
import com.polidea.rxandroidble3.RxBleClient
import com.polidea.rxandroidble3.RxBleDevice
import com.polidea.rxandroidble3.scan.ScanFilter
import com.polidea.rxandroidble3.scan.ScanSettings
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.text.SimpleDateFormat
import java.util.*

class GpsBleService : Service() {
    private val TAG: String = GpsBleService::class.java.simpleName

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private lateinit var locationClient: LocationClient

    private lateinit var rxBleClient: RxBleClient
    private val serviceUUID: ParcelUuid = ParcelUuid.fromString("0000ffe0-0000-1000-8000-00805f9b34fb")
    private val characteristicUUID: UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb")
    private var view: BLEView? = null
    private val serviceBinder = ServiceBinder()


    private lateinit var tagKey: String
    private lateinit var codigo: String
    private lateinit var contrasena: String
    private lateinit var ideantificador: String
    private lateinit var usuario: String
    private lateinit var lat: String
    private lateinit var long: String

    private val userService = UserService()

    private var i: Int = 0


    private lateinit var dateFormat: SimpleDateFormat


    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "Service onCreate")
        //Creacion de bluetooth
        rxBleClient = RxBleClient.create(applicationContext)
        //Creacion de GPS
        locationClient = DefaultLocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "Service onStartCommand")

        when(intent?.action){
            ACTION_STAR -> start()
            ACTION_STOP -> stop()
        }
        return START_STICKY
    }

    private fun start(){

        getLocationClient()


    }

    private fun getLocationClient() {

        locationClient.getLocationClient(60*1000)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                lat = location.latitude.toString()
                long = location.longitude.toString()

                Thread{
                    UserInfoApplication.database.userInfoDao()
                        .addUserInfo(
                            UserInfoEntity(
                                latitud = lat.toDouble(), longitud = long.toDouble()))
                }.start()
                sendLocation()


            }
            .launchIn(serviceScope)
    }


    override fun onBind(intent: Intent): IBinder {

        return serviceBinder
    }

    fun bindView(view: BLEView) {
        this.view = view
    }

    fun scan(){
        view?.onScanning()

        rxBleClient.scanBleDevices(scanSettings(), scanFilter())
            .firstElement()
            .subscribe(
                { scanResult ->
                    connect(scanResult.bleDevice)







                }, onError())
    }

    private fun connect(bleDevice: RxBleDevice){
        bleDevice.establishConnection(true)
            .subscribe({ rxBleConnection ->
                view?.onConnected(bleDevice)



                rxBleConnection.setupIndication(characteristicUUID, NotificationSetupMode.COMPAT)
                    .subscribe({ observable ->
                        observable.subscribe({ _ ->
                           // view?.onKeyPressed()
                            pressKey()
                        }, onError())
                    }, onError())
            }, onError())
    }

    private fun pressKey() {
        i++
        Handler(Looper.getMainLooper()).postDelayed({
            if (i==1){
                Log.i(TAG, "Button Bluetooth Pressed!!!")
                view?.onKeyPressed()

            }else if (i==2){
                Log.i(TAG, "Button Bluetooth twice")
                start()


            }
            i = 0

        }, 500)
    }
    suspend fun sendLocation(){
        dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())


            val userInfo = UserInfoApplication.database.userInfoDao().getAllData()


         tagKey = userInfo.tagkey
         codigo = "3"
         contrasena = userInfo.contrasena

         ideantificador = userInfo.identificador
         usuario = userInfo.usuario
         lat = userInfo.latitud.toString()
         long = userInfo.longitud.toString()



        val mensaje = "RegistraPosicion"
        val fecha = dateFormat.format(Date())





        val response =  userService.updateUserInfo(
            UserInfo(
                mensaje = mensaje,
                usuario = usuario,
                longitud = long.toDouble(),
                latitud = lat.toDouble(),
                tagkey = tagKey,
                contrasena = contrasena,
                codigo = codigo,
                fechahora = fecha,
                identificador = ideantificador)
        )

        Log.w(ContentValues.TAG, response.toString())

    }

    private fun scanSettings(): ScanSettings =
        ScanSettings.Builder()
            .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

    private fun scanFilter(): ScanFilter =
        ScanFilter.Builder()
            .setServiceUuid(serviceUUID)
            .build()

    inner class ServiceBinder : Binder() {
        internal val service: GpsBleService
            get() = this@GpsBleService
    }
    private fun onError(): (Throwable) -> Unit {
        return { throwable ->
            throwable.message?.let { Log.e(TAG, it) }
            view?.onError(throwable)
        }
    }
    private fun stop(){
        stopForeground(true)
        stopSelf()
    }
    override fun onDestroy() {

        serviceScope.cancel()
    }

    companion object{
        const val ACTION_STAR = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}
package com.example.skytag3

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.skytag3.base.db.UserInfoApplication
import com.example.skytag3.data.entity.UserInfoEntity
import com.example.skytag3.databinding.ActivityMainBinding
import com.example.skytag3.login.LoginActivity
import com.example.skytag3.service.BLEView
import com.example.skytag3.service.GpsBleService
import com.example.skytag3.worker.LocationWorker
import com.example.skytag3.worker.UpdateLocationWorker
import com.example.skytag3.worker.makeStatusNotification
import com.polidea.rxandroidble3.RxBleDevice
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity(), BLEView {
    private lateinit var dateFormat: SimpleDateFormat
    private var bleService: GpsBleService? = null
    private var bleServiceBound = false


    //connect to the service
    private val bleServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? GpsBleService.ServiceBinder
            bleService = binder?.service

            bleService?.bindView(this@MainActivity)

            bleServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bleServiceBound = false
        }
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        binding.btnLogout.setOnClickListener { logout() }

        binding.btnAddBluetooth.setOnClickListener {
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }else{
                bleService?.scan()
            }
        }

        binding.btnStop.setOnClickListener {
            Intent(applicationContext, GpsBleService::class.java).apply {
                action = GpsBleService.ACTION_STOP
                startService(this)
            }

        }

        binding.btnStar.setOnClickListener {
            getLocation()
            updateLocation()

        }

    }
    private fun updateLocation() {
        val constraints = Constraints.Builder()
            .build()

        val updateLocationWork = PeriodicWorkRequest.Builder(
            UpdateLocationWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).setConstraints(constraints)
            .addTag("my_id")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("my_id", ExistingPeriodicWorkPolicy.REPLACE, updateLocationWork)



    }
    private fun getLocation(){

        val constraints = Constraints.Builder()
            .build()

        val getLocationWorker = PeriodicWorkRequest.Builder(
            LocationWorker::class.java,
            15,
            TimeUnit.MINUTES
        ).setConstraints(constraints)
            .addTag("my_id")
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork("my_idd", ExistingPeriodicWorkPolicy.KEEP, getLocationWorker)
    }

    private fun logout() {
        val sp = getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        with(sp.edit()){
            putBoolean("active", false)
            apply()
        }

        startActivity(Intent(this, LoginActivity::class.java))
        finish()


    }

    override fun onResume() {
        super.onResume()
        if (checkAllRequiredPermissions()) {
            if (!bleServiceBound) {
                val bleServiceIntent = Intent(applicationContext, GpsBleService::class.java)
                applicationContext.bindService(bleServiceIntent, bleServiceConnection, Context.BIND_AUTO_CREATE)
                applicationContext.startService(bleServiceIntent)
            }
        }
    }

    private fun checkAllRequiredPermissions(): Boolean {

        val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.SEND_SMS
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS
            )
        }

        for (permission in requiredPermissions) {
            if (ContextCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, requiredPermissions, REQUEST_ALL_PERMISSIONS)
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_ALL_PERMISSIONS -> finishIfRequiredPermissionsNotGranted(grantResults)
            else -> {
            }
        }
    }


    private fun finishIfRequiredPermissionsNotGranted(grantResults: IntArray) {
        if (grantResults.isNotEmpty()) {
            for (grantResult in grantResults) {

                if (grantResult == PackageManager.PERMISSION_GRANTED) {


                } else {

                    Toast.makeText(this, "Se requieren todos los permisos", Toast.LENGTH_LONG).show()
                    finish()
                    break
                }
            }
        } else {
            Toast.makeText(this, "Se requieren todos los permisos", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onScanning() {
        runOnUiThread {
            Toast.makeText(this, "Buscando Tag", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDoubleClick() {
        runOnUiThread {
            makeStatusNotification("SOS EMERGENCY", applicationContext)
        }
    }

    override fun onConnected(bleDevice: RxBleDevice) {

        UserInfoApplication.database.userInfoDao()
            .addUserInfo(
                UserInfoEntity(
                    tagkey = bleDevice.macAddress.toString())
            )
        runOnUiThread {
            Toast.makeText(this, "Conectado con ${bleDevice.name?.trim()}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onKeyPressed() {
        runOnUiThread {
            makeStatusNotification("Simple Click", applicationContext)
        }

    }

    override fun onError(throwable: Throwable) {
        runOnUiThread {
            Toast.makeText(this, "${throwable.message}", Toast.LENGTH_SHORT).show()
        }

    }
    companion object {
        private const val REQUEST_ALL_PERMISSIONS = 1001
        private const val REQUEST_ENABLE_BT = 1
    }
}
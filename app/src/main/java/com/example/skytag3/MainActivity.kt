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
import android.os.IBinder
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.skytag3.databinding.ActivityMainBinding
import com.example.skytag3.login.LoginActivity
import com.example.skytag3.service.BLEView
import com.example.skytag3.service.GpsBleService
import com.example.skytag3.worker.LocationWorker
import com.example.skytag3.worker.UpdateLocationWorker
import com.example.skytag3.worker.makeStatusNotification
import com.polidea.rxandroidble3.RxBleDevice
import io.paperdb.Paper
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

@SuppressLint("MissingPermission")
class MainActivity : AppCompatActivity(), BLEView {
    private lateinit var mGpsBleService: GpsBleService
    private var mBound = false
    private val backgroundLocation = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if (it){ }
    }

    private val workManager = WorkManager.getInstance(application)

    //connect to the service
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? GpsBleService.ServiceBinder
            mGpsBleService = binder?.service!!

            mGpsBleService.bindView(this@MainActivity)

            mBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mBound = false
        }
    }

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Paper.init(this)

        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter

        binding.btnLogout.setOnClickListener { logout() }

        binding.btnAddBluetooth.setOnClickListener {
            if (bluetoothAdapter?.isEnabled == false) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }else{
               mGpsBleService?.scan()
            }
        }

        binding.btnStop.setOnClickListener {
           stopService(Intent(this, GpsBleService::class.java))

            workManager.cancelAllWork()
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

        stopService(Intent(this, GpsBleService::class.java))

        startActivity(Intent(this, LoginActivity::class.java))
        finish()

    }


    override fun onResume() {
        super.onResume()
        if (checkAllRequiredPermissions()) {
            if (!mBound) {
                val bleServiceIntent = Intent(applicationContext, GpsBleService::class.java)
                applicationContext.bindService(bleServiceIntent, connection, Context.BIND_AUTO_CREATE)
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
                Manifest.permission.SEND_SMS,)
        } else {
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS,)
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

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        backgroundLocation.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                    }

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
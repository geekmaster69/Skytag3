package com.example.skytag3.service

import com.polidea.rxandroidble3.RxBleDevice

interface BLEView {
    fun onScanning()
    fun onDoubleClick()

    fun onConnected(bleDevice: RxBleDevice)

    fun onKeyPressed()
    fun onError(throwable: Throwable)
}
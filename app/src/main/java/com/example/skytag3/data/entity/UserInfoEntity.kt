package com.example.skytag3.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user_info_entity")
data class UserInfoEntity(
    @PrimaryKey
    var id: Int = 0,

    var mensaje: String = "",
    var usuario: String = "",
    var contrasena: String = "",
    var identificador: String = "",
    var fechahora: String = "",
    var codigo: String = "",
    var latitud: Double = 0.0,
    var longitud: Double = 0.0,
    var tagkey: String = ""
)

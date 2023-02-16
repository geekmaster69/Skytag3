package com.example.skytag3.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "user_info_entity")
data class UserInfoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val mensaje: String = "",
    val usuario: String = "",
    val contrasena: String = "",
    val  identificador: String = "",
    val  fechahora: String = "",
    val   codigo: String = "",
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val  tagkey: String = ""
)

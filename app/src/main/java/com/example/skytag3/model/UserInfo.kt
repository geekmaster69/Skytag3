package com.example.skytag3.model

import com.google.gson.annotations.SerializedName

data class UserInfo(
    @SerializedName("mensaje") val mensaje: String = "",
    @SerializedName("usuario")val usuario: String = "",
    @SerializedName("contrasena")val contrasena: String = "",
    @SerializedName("identificador")val  identificador: String = "",
    @SerializedName("fechahora")val  fechahora: String = "",
    @SerializedName("codigo")val   codigo: String = "",
    @SerializedName("latitud")val latitud: Double = 0.0,
    @SerializedName("longitud")val longitud: Double = 0.0,
    @SerializedName("tagkey")val  tagkey: String
)

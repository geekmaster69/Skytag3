package com.example.skytag3.login.model

import com.google.gson.annotations.SerializedName

class LoginUserInfo(
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("usuario") val usuario: String,
    @SerializedName("contrasena") val contrasena: String)

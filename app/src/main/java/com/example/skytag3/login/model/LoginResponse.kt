package com.example.skytag3.login.model

import com.google.gson.annotations.SerializedName

data class LoginResponse (
    @SerializedName("estado") val estado: Int,
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("usuario") val usuario: Usuario)


data class Usuario(
    @SerializedName("usuario") val usuario: String,
    @SerializedName("tagkey") val tagkey: String,
    @SerializedName("creacion") val creacion: String,
    @SerializedName("empresa") val empresa: String
)
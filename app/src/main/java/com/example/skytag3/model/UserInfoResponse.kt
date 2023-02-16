package com.example.skytag3.model

import com.google.gson.annotations.SerializedName

data class UserInfoResponse (
    @SerializedName("estado")val estado: Int = 0,
    @SerializedName("mensaje")val mensaje: String = ""
)
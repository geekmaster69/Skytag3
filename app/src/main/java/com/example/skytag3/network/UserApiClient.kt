package com.example.skytag3.network

import com.example.skytag3.base.Constans
import com.example.skytag3.model.UserInfo
import com.example.skytag3.model.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiClient {
    @POST(Constans.TAG_KEY_LOCATIO_PATH)
    suspend fun sendInfoSos(@Body data: UserInfo) : UserInfoResponse
}
package com.example.skytag3.login.network


import com.example.skytag3.base.Constans
import com.example.skytag3.login.model.LoginResponse
import com.example.skytag3.login.model.LoginUserInfo
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiClient {
    @POST(Constans.TAG_KEY_LOGIN_PATH)
    suspend fun onLogin(@Body data: LoginUserInfo) : LoginResponse
}

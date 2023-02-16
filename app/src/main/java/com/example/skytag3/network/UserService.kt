package com.example.skytag3.network


import com.example.skytag3.base.BaseRetrofit
import com.example.skytag3.model.UserInfo
import com.example.skytag3.model.UserInfoResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserService {
    private val retrofit = BaseRetrofit.getRetrofit()

    suspend fun updateUserInfo(userInfo: UserInfo) : UserInfoResponse {
        return withContext(Dispatchers.IO){
            val response = retrofit.create(UserApiClient::class.java).sendInfoSos(userInfo)
            response
        }
    }
}
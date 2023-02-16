package com.example.skytag3.login.domain


import com.example.skytag3.login.model.LoginResponse
import com.example.skytag3.login.model.LoginUserInfo
import com.example.skytag3.login.network.LoginUserService
import kotlin.math.log

class LoginUseCase {

    private val loginUserService = LoginUserService()

    suspend operator fun invoke(loginUserInfo: LoginUserInfo): LoginResponse {

        return loginUserService.login(loginUserInfo)
    }


}
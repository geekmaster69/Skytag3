package com.example.skytag3.login.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skytag3.login.domain.LoginUseCase
import com.example.skytag3.login.model.LoginResponse
import com.example.skytag3.login.model.LoginUserInfo
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    var loginModel = MutableLiveData<LoginResponse>()
    val loginUseCase = LoginUseCase()

    fun onLogin(loginUserInfo: LoginUserInfo){
        viewModelScope.launch {
            val result = loginUseCase(loginUserInfo)
            loginModel.postValue(result)
            Log.e("viewMOdel", result.toString())
        }
    }
}
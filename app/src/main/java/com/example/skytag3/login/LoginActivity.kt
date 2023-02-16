package com.example.skytag3.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.skytag3.MainActivity
import com.example.skytag3.base.db.UserInfoApplication
import com.example.skytag3.data.entity.UserInfoEntity
import com.example.skytag3.databinding.ActivityLoginBinding
import com.example.skytag3.login.model.LoginUserInfo
import com.example.skytag3.login.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val appUtils: AppUtils = AppUtils(this)
    private val mLoginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sp =  getSharedPreferences("login_prefs", Context.MODE_PRIVATE)



        checkLogin(sp)

        binding.btnLogin.setOnClickListener {
            login(sp)
        }
    }

    private fun checkLogin(sp: SharedPreferences) {
        if (sp.getBoolean("active", false)){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun login(sp: SharedPreferences) {

        val user = binding.etUser.text.toString()
        val password = binding.etPassword.text.toString()

        val identificador = appUtils.getDeviceId().take(10)

        Thread{
            UserInfoApplication.database.userInfoDao()
                .addUserInfo(
                    UserInfoEntity(
                        usuario = user, contrasena = password, identificador = identificador)
                )

        }.start()


        with(sp.edit()){
            putString("user", user)
            putString("password", password)
            putString("identificador", identificador)

            putBoolean("active", true)
            apply()
        }

        mLoginViewModel.onLogin(LoginUserInfo(mensaje = "Usuario", usuario = user, contrasena = password))

        mLoginViewModel.loginModel.observe(this){
            when(it.estado){

                200 ->{
                    Toast.makeText(this, "Bienvenido ${it.usuario.usuario}", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }

                400 ->{
                    Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
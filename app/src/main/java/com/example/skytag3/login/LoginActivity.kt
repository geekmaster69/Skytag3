package com.example.skytag3.login

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.skytag3.MainActivity
import com.example.skytag3.databinding.ActivityLoginBinding
import com.example.skytag3.login.model.LoginUserInfo
import com.example.skytag3.login.viewmodel.LoginViewModel
import io.paperdb.Paper

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var identificador: String
    private val appUtils: AppUtils = AppUtils(this)
    private val mLoginViewModel: LoginViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Paper.init(this)

        val sp =  getSharedPreferences("login_prefs", Context.MODE_PRIVATE)



        identificador = appUtils.getDeviceId().take(10)

        checkLogin(sp)

        binding.btnLogin.setOnClickListener {
            login(sp)
        }

        binding.tvIdentificador.setOnClickListener {
            copyToClipboard(identificador)
        }
    }

    private fun checkLogin(sp: SharedPreferences) {
        if (sp.getBoolean("active", false)){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    fun copyToClipboard(content: String) {
        val clipboardManager = ContextCompat.getSystemService(this, ClipboardManager::class.java)!!
        val clip = ClipData.newPlainText("Id Compuesto", content)
        clipboardManager.setPrimaryClip(clip)
        Toast.makeText(this, "Identificador Copiado", Toast.LENGTH_SHORT).show()
    }

    fun login(sp: SharedPreferences) {

        val user = binding.etUser.text.toString()
        val password = binding.etPassword.text.toString()

        val mensaje = "RegistraPosicion"


        with(sp.edit()){
            putBoolean("active", true)
            apply()
        }

        mLoginViewModel.onLogin(LoginUserInfo(mensaje = "Usuario", usuario = user, contrasena = password))

        mLoginViewModel.loginModel.observe(this){
            when(it.estado){

                200 ->{
                    Toast.makeText(this, "Bienvenido ${it.usuario.usuario}", Toast.LENGTH_SHORT).show()
                    Paper.book().write("user", user)
                    Paper.book().write("contrasena", password)
                    Paper.book().write("identificador", identificador)
                    //Paper.book().write("identificador", "2BC70ECB91")
                    //8D8270A372
                    //75451CC200
                    Paper.book().write("mensaje", mensaje)

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
package com.example.skytag3.login

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.skytag3.MainActivity
import com.example.skytag3.databinding.ActivityLoginBinding
import com.example.skytag3.login.model.LoginUserInfo
import com.example.skytag3.login.viewmodel.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import io.paperdb.Paper
import retrofit2.HttpException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var identificador: String
    private lateinit var identificador2: String
    private val mLoginViewModel: LoginViewModel by viewModels()
    @SuppressLint("MissingPermission", "HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Paper.init(this)

        val sp =  getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

        val idApk = android.provider.Settings.Secure.getString(applicationContext.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID).uppercase()

        identificador = idApk
        identificador2 = idApk.take(10)

        checkLogin(sp)

        binding.btnLogin.setOnClickListener {
            login(sp)
           /* startActivity(Intent(this, MainActivity::class.java))
            finish()*/
        }

        binding.tvIdentificador.setOnClickListener { copyToClipboard(identificador) }
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
        Log.i("Identificador", content)
    }

    @SuppressLint("SuspiciousIndentation")
    fun login(sp: SharedPreferences) {

        val user = binding.etUser.text.toString()
        val password = binding.etPassword.text.toString()

        val mensaje = "RegistraPosicion"

        val response = mLoginViewModel.onLogin(LoginUserInfo(mensaje = "usuario", usuario = user, contrasena = password))




            mLoginViewModel.loginModel.observe(this){
                when(it.estado){

                    200 ->{
                        Toast.makeText(this, "Bienvenido ${it.usuario.usuario}", Toast.LENGTH_SHORT).show()
                        Paper.book().write("user", user)
                        Paper.book().write("contrasena", password)
                        Paper.book().write("identificador", identificador2)
                        Paper.book().write("mensaje", mensaje)


                        //7A68971972

                        with(sp.edit()){
                            putBoolean("active", true)
                            apply()
                        }

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }

                    400 ->{
                        Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()

                        with(sp.edit()){
                            putBoolean("active", false)
                            apply()
                        }
                    }

                    else -> Toast.makeText(this, "Error desconocido", Toast.LENGTH_SHORT).show()
                }


            }






    }

    private fun showMessage(message: String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }




}
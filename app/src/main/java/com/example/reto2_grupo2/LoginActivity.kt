package com.example.reto2_grupo2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.reto2_grupo2.Singleton.SocketClientSingleton.socketClient
import java.util.Locale
import com.example.reto2_grupo2.socketIO.SocketClient

class LoginActivity : AppCompatActivity() {

    private lateinit var userTextField: EditText
    private lateinit var passwordTextField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextButton: TextView
    private lateinit var rememberMe: CheckBox
    private lateinit var forgotPassword: TextView



    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val sharedPreferences = getSharedPreferences("app_preferences", Context.MODE_PRIVATE)

        if (sharedPreferences.contains("selected_theme")) {
            val selectedTheme = sharedPreferences.getString("selected_theme", "light")
            if (selectedTheme == "light") {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            if (selectedTheme == "dark") {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }

        if (sharedPreferences.contains("selected_language")) {
            val selectedLanguage = sharedPreferences.getString("selected_language", "es")
            if (selectedLanguage != null) {
                setLocale(selectedLanguage, this)
            }
        }

        forgotPassword = findViewById(R.id.forgotPassword)
        socketClient =SocketClient(this)
        socketClient!!.connect()
        userTextField = findViewById(R.id.userNameTxt)
        passwordTextField = findViewById(R.id.passwordTxt)
        rememberMe = findViewById(R.id.rememberMeCheck)
        registerTextButton = findViewById(R.id.registerTextButton)




        registerTextButton.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
            }

        loginButton = findViewById(R.id.registerButton)
        loginButton.setOnClickListener {

            if (userTextField.text.isNotEmpty() || passwordTextField.text.isNotEmpty()) {
                socketClient!!.doLogin(
                    userTextField.text.toString(),
                    passwordTextField.text.toString()
                )

            } else {
                Toast.makeText(
                    this@LoginActivity,
                    "Por favor, ingrese un nombre de usuario y contraseña",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        socketClient!!.disconnect()
    }

    private fun setLocale(languageCode: String, context: Context) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
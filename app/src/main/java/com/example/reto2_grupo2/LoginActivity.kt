package com.example.reto2_grupo2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reto2_grupo2.socketIO.SocketClient

class LoginActivity : AppCompatActivity() {

    private lateinit var userTextField: EditText
    private lateinit var passwordTextField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextButton: TextView
    private lateinit var rememberMe: CheckBox
    private var socketClient : SocketClient? = null


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        socketClient = SocketClient(this)
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

        loginButton = findViewById(R.id.loginButton)
        loginButton.setOnClickListener {

            if (userTextField.text.isNotEmpty() || passwordTextField.text.isNotEmpty()) {
                socketClient!!.doLogin(
                    userTextField.text.toString(),
                    passwordTextField.text.toString()
                )
                /*val intent = Intent(this@LoginActivity, MainFrame::class.java)
                //intent.putExtra("user", el objeto de user que traigamos de la base de datos)
                intent.putExtra("user", userTextField.text.toString())
                startActivity(intent)
                finish()*/
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
}
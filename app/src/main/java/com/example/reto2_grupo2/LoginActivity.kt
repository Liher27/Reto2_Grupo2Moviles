package com.example.reto2_grupo2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var userTextField: EditText
    private lateinit var passwordTextField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextButton: TextView
    private lateinit var rememberMe: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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
            if (userTextField.text.toString() == "profesor" && passwordTextField.text.toString() == "profesor") {
                val intent = Intent(this@LoginActivity, MainProfessorActivity::class.java)
                startActivity(intent)
                finish()
            } else if (userTextField.text.toString() == "alumno" && passwordTextField.text.toString() == "alumno") {
                val intent = Intent(this@LoginActivity, MainStudentActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

    }
}
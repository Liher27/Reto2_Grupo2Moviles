package com.example.reto2_grupo2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview

class LoginActivity : AppCompatActivity() {

    private lateinit var userTextField: EditText
    private lateinit var passwordTextField: EditText
    private lateinit var loginButton: Button
    private lateinit var registerTextButton: TextView
    private lateinit var rememberMe: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainprofessor_activity)


        val composeView = findViewById<ComposeView>(R.id.my_composable)
        composeView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MainScreen()
            }
        }

        //registerTextButton = findViewById(R.id.registerTextButton)
        //registerTextButton.setOnClickListener {
        //    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
          //  startActivity(intent)
            //finish()
        //}

    }

    @Preview(showBackground = true)
    @Composable
    fun MainScreen() {
        StaticCalendar()
    }
}
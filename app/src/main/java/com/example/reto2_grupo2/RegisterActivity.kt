package com.example.reto2_grupo2

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var userTextField: EditText
    private lateinit var nameTextField: EditText
    private lateinit var surnameTextField: EditText
    private lateinit var secondSurnameTextField: EditText
    private lateinit var dniTextField: EditText
    private lateinit var directionTextField: EditText
    private lateinit var telephone1TextField: EditText
    private lateinit var telephone2TextField: EditText

    private lateinit var courseNameTextField: EditText
    private lateinit var cycleNameTextField: EditText
    private lateinit var gradoDobleCheck: CheckBox

    private lateinit var passwordTextField: EditText
    private lateinit var repeatPasswordTextField: EditText

    private lateinit var addPhotoButton: Button
    private lateinit var backButton: Button
    private lateinit var registerButton: Button

    private val REQUEST_CODE_RECORD_IMAGE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)


        userTextField = findViewById(R.id.loginTxt)
        nameTextField = findViewById(R.id.nameTxt)
        surnameTextField = findViewById(R.id.surname1Txt)
        secondSurnameTextField = findViewById(R.id.surname2Txt)
        dniTextField = findViewById(R.id.dniTxt)
        directionTextField = findViewById(R.id.directionTxt)
        telephone1TextField = findViewById(R.id.telephone1Txt)
        telephone2TextField = findViewById(R.id.telephone2Txt)
        courseNameTextField = findViewById(R.id.courseTxt)
        cycleNameTextField = findViewById(R.id.cycleTxt)
        gradoDobleCheck = findViewById(R.id.intensiveCheck)


        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        registerButton = findViewById(R.id.loginButton)
        // if (credentialsOk()) {
        registerButton.setOnClickListener {
            //  val intent = Intent(this@RegisterActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        // }

        addPhotoButton = findViewById(R.id.addPhotoButton)
        addPhotoButton.setOnClickListener {
            val imageIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (imageIntent.resolveActivity(packageManager) != null) {
                startActivityForResult(imageIntent, REQUEST_CODE_RECORD_IMAGE)
            } else {
                Toast.makeText(this, "No camera app found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Deprecated("")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_RECORD_IMAGE && resultCode == RESULT_OK) {
            val takenImage: Uri? = data?.data
            if (takenImage != null) {
                val imageView = findViewById<ImageView>(R.id.imageView)
                imageView.setImageURI(takenImage)
            } else {
                Toast.makeText(this, "Photo recording canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun credentialsOk(): Boolean {
        var ret = false

        return ret

    }
}
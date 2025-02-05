package com.example.reto2_grupo2

import android.content.Intent
import android.graphics.Bitmap
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.reto2_grupo2.Singleton.ConnectionChangeReceiver
import com.example.reto2_grupo2.Singleton.SocketClientSingleton.socketClient
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.entity.Course
import com.example.reto2_grupo2.entity.Student
import kotlin.properties.Delegates

private const val REQUEST_CODE_RECORD_IMAGE = 1

class RegisterActivity : AppCompatActivity() {

    private lateinit var nameTextField: EditText
    private lateinit var surnameTextField: EditText
    private lateinit var secondSurnameTextField: EditText
    private lateinit var dniTextField: EditText
    private lateinit var directionTextField: EditText
    private lateinit var telephone1TextField: EditText
    private lateinit var telephone2TextField: EditText

    private lateinit var courseNameTextField: EditText
    private lateinit var cycleNameTextField: EditText
    private lateinit var dualCheck: CheckBox

    private lateinit var passwordTextField: EditText
    private lateinit var repeatPasswordTextField: EditText
    private lateinit var scholarInfoTitleView : TextView
    private lateinit var cycleTitleView : TextView
    private lateinit var courseTitleView : TextView
    private lateinit var scrollView : ScrollView
    private lateinit var addPhotoButton: Button
    private lateinit var backButton: Button
    private lateinit var registerButton: Button
    private lateinit var registerCheckButton: Button
    private var dual by Delegates.notNull<Boolean>()
    private lateinit var myReceiver : ConnectionChangeReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver()

        setContentView(R.layout.activity_register)

        scrollView = findViewById(R.id.scrollView2)
        nameTextField = findViewById(R.id.nameTxt)
        surnameTextField = findViewById(R.id.surname1Txt)
        secondSurnameTextField = findViewById(R.id.surname2Txt)
        dniTextField = findViewById(R.id.dniTxt)
        directionTextField = findViewById(R.id.directionTxt)
        telephone1TextField = findViewById(R.id.telephone1Txt)
        telephone2TextField = findViewById(R.id.telephone2Txt)
        courseNameTextField = findViewById(R.id.courseTxt)
        cycleNameTextField = findViewById(R.id.cycleTxt)
        cycleTitleView = findViewById(R.id.cicleTitleTxt)
        courseTitleView = findViewById(R.id.courseTitleTxt)
        scholarInfoTitleView = findViewById(R.id.scholarInfoTitle)
        cycleTitleView= findViewById(R.id.cicleTitleTxt)
        courseTitleView = findViewById(R.id.courseTitleTxt)
        scholarInfoTitleView = findViewById(R.id.scholarInfoTitle)
        dualCheck = findViewById(R.id.intensiveCheck)
        passwordTextField = findViewById(R.id.password1Txt2)
        repeatPasswordTextField = findViewById(R.id.password2Txt2)

        init()
        preloadInfo()

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        registerCheckButton = findViewById(R.id.registerCheckBtn)

        registerCheckButton.setOnClickListener {
            if (passwordTextField.text.toString() != repeatPasswordTextField.text.toString()) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Las contraseñas no son mismas",
                    Toast.LENGTH_SHORT
                ).show()

            } else {
                scrollCenter()

            }
        }

        registerButton = findViewById(R.id.registerUserButton)
        registerButton.setOnClickListener {
            if (nameTextField.text.isEmpty() || surnameTextField.text.isEmpty() || secondSurnameTextField.text.isEmpty()
                || dniTextField.text.isEmpty() || directionTextField.text.isEmpty() || telephone1TextField.text.isEmpty() ||
                repeatPasswordTextField.text.toString()
                    .isEmpty()
            ) {

                Toast.makeText(
                    this@RegisterActivity,
                    "Hay campos que están vacíos",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                if (passwordTextField.text.toString() != repeatPasswordTextField.text.toString()) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Las contraseñas no son las mismas",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {

                    val telephoneInt = try {
                        telephone1TextField.text.toString().toInt()
                    } catch (e: NumberFormatException) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "El teléfono no es válido",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnClickListener
                    }

                    dual = dualCheck.isChecked
                    if (socketClient != null) {
                        socketClient?.doRegister(
                            nameTextField.text.toString(),
                            surnameTextField.text.toString(),
                            secondSurnameTextField.text.toString(),
                            passwordTextField.text.toString(),
                            dniTextField.text.toString(),
                            directionTextField.text.toString(),
                            telephoneInt
                        )
                    }

                }
            }
        }
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

    @Deprecated("Deprecated in API 23")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_RECORD_IMAGE && resultCode == RESULT_OK) {
            val takenImage: Bitmap? = data?.extras?.get("data") as? Bitmap
            if (takenImage != null) {
                val imageView = findViewById<ImageView>(R.id.imageView)
                imageView.setImageBitmap(takenImage)
            } else {
                Toast.makeText(this, "Photo recording canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun init() {
        val client: Client? = intent.getParcelableExtra("user")
        if (client != null) {
            if (client.userType) {
                cycleTitleView.visibility = View.GONE
                courseTitleView.visibility = View.GONE
                scholarInfoTitleView.visibility = View.GONE
                courseNameTextField.visibility = View.GONE
                cycleNameTextField.visibility = View.GONE
                dualCheck.visibility = View.GONE
            }
        }
    }

    private fun preloadInfo() {
        val client: Client? = intent.getParcelableExtra("user")
        if (client != null) {
            nameTextField.setText(client.userName)
            surnameTextField.setText(client.surname)
            secondSurnameTextField.setText(client.secondSurname)
            dniTextField.setText(client.dni)
            directionTextField.setText(client.direction)
            telephone1TextField.setText(client.telephone.toString())

        }
        val course: Course? = intent.getParcelableExtra("userCourse")
        if (course != null) {
            courseNameTextField.setText(course.title)
        }
        val student: Student? = intent.getParcelableExtra("studentInfo")
        if (student != null) {
            cycleNameTextField.setText(student.userYear.toString())
            dualCheck.isChecked = student.intensiveDual
        }
    }
    private fun registerReceiver() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        myReceiver = ConnectionChangeReceiver()
        registerReceiver(myReceiver, filter)
    }
    private fun scrollCenter() {
             scrollView.post {
            val targetView = dniTextField
            val targetPosition = IntArray(2).apply {
                targetView.getLocationInWindow(this)
            }
            val scrollViewHeight =scrollView.height
            val scrollToY = targetPosition[1] - (scrollViewHeight/2)
                 scrollView.smoothScrollTo(0,scrollToY)
        }
    }
    }
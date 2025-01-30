package com.example.reto2_grupo2.socketIO

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.LoginActivity
import com.example.reto2_grupo2.MainFrame
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.RegisterActivity
import com.example.reto2_grupo2.entity.Client

import com.example.reto2_grupo2.entity.Course
import com.example.reto2_grupo2.entity.RootData
import com.example.reto2_grupo2.entity.Student

import com.example.reto2_grupo2.entity.ExternalCourse
import com.example.reto2_grupo2.entity.Professor
import com.example.reto2_grupo2.socketIO.config.Events
import com.example.reto2_grupo2.socketIO.model.MessageInput
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketClient(private val activity: Activity) {
    private val ipPort = "http://10.5.104.21:2888"
    private val socket: Socket = IO.socket(ipPort)
    private var context: Context
    private var fragment: Fragment? = null
    private lateinit var rootData: RootData
    private lateinit var userClient: Client
    private lateinit var userStudent: Student
    private lateinit var userCourse: Course
    private lateinit var userProfessor: Professor

    constructor(fragment: Fragment) : this(fragment.requireActivity()) {
        this.fragment = fragment
    }

    private var tag = "socket.io"

    init {
        context = fragment?.requireContext() ?: activity
        // Event called when the socket connects
        socket.on(Socket.EVENT_CONNECT) {
            Log.d(tag, "Connected...")
        }

        // Event called when the socket disconnects
        socket.on(Socket.EVENT_DISCONNECT) {
            Log.d(tag, "Disconnected...")
        }

        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->
            Log.e(tag, "Error al conectar: ${args[0]}")
        }
        socket.on(Events.ON_LOGIN_ANSWER.value) { args ->
            val response = args[0] as JSONObject
            val message = response.getString("message")

            val gson = Gson()
            val jsonObject = gson.fromJson(message, JsonObject::class.java)

            val id = jsonObject["userId"].asInt
            val name = jsonObject["userName"].asString
            val surname = jsonObject["surname"].asString
            val secondSurname = jsonObject["secondSurname"].asString
            val pass = jsonObject["pass"].asString
            val dni = jsonObject["dni"].asString
            val direction = jsonObject["direction"].asString
            val telephone = jsonObject["telephone"].asInt
            val type = jsonObject["userType"].asBoolean
            val registered = jsonObject["registered"].asBoolean

            // Log the values for debugging
            Log.d(
                tag,
                "id: $id, name: $name, surname: $surname, 2ndSurname:$secondSurname,pass: $pass,dni:$dni,direction:$direction,telephone:$telephone  tipo:$type, registered:$registered"
            )

        }



        socket.on(Events.ON_GET_ALL_ANSWER.value) { args ->


            val response = args[0] as JSONObject
            val message = response.getString("message") as String
            val gson = Gson()
            val itemType = object : TypeToken<List<Client>>() {}.type
            val list = gson.fromJson<List<Client>>(message, itemType)

            // The logging
            activity.findViewById<TextView>(R.id.textView).append("\nAnswer to getAll:$list")
            Log.d(tag, "Answer to getAll: $list")
        }
    }
    fun connect() {
        socket.connect()

        // Log traces
        Log.d(tag, "Connecting to server...")
    }

    fun disconnect() {
        socket.disconnect()

        // Log traces
        Log.d(tag, "Disconnecting from server...")
    }

    fun doLogin(userName: String, password: String) {
        val loginData = mapOf(
            "message" to userName,
            "userPass" to password
        )
        socket.emit(Events.ON_LOGIN.value, Gson().toJson(loginData))
        socket.on(Events.ON_LOGIN_SUCCESS.value) { args ->
            val response = args[0] as JSONObject
            // The response contains a JSON string under the "message" key
            val message = response.getString("message")
            // Now we can use Gson to parse the message into a JsonObject
            val gson = Gson()
            rootData = gson.fromJson(message, RootData::class.java)
            Log.d(tag, "JSON: $rootData")


            val client = rootData.loginClient
            userClient = Client(
                client.userId,
                client.userName,
                client.surname,
                client.secondSurname,
                client.pass,
                client.dni,
                client.direction,
                client.telephone,
                client.userType,
                client.registered
            )
            if(userClient.userType){
                val professor = rootData.professor
                userProfessor = Professor(
                    professor.userId
                )

            }else {
                val student = rootData.student
                userStudent = Student(
                    student.userId,
                    student.userYear,
                    student.intensiveDual
                )

                val course = rootData.course
                userCourse = Course(
                    course.title,
                    course.email,
                    course.courseDescription
                )
            }


            val intent = Intent(context, MainFrame::class.java).apply {
                putExtra("user", userClient)
                if(userClient.userType){
                    putExtra("professorInfo", userProfessor)
                }else{
                    putExtra("studentInfo", userStudent)
                        .putExtra("userCourse", userCourse)
                }
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        socket.on(Events.ON_LOGIN_FAIL.value) { args ->
            val response = args[0] as String
            Log.d(tag, "Login fallado: $response")
            activity.runOnUiThread {
                Toast.makeText(context, "No se ha logueado correctamente", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        socket.on(Events.ON_REGISTER.value) { args ->
            val response = args[0] as JSONObject
            // The response contains a JSON string under the "message" key
            val message = response.getString("message")
            // Now we can use Gson to parse the message into a JsonObject
            val gson = Gson()
            rootData = gson.fromJson(message, RootData::class.java)
            Log.d(tag, "JSON: $rootData")


            val client = rootData.loginClient
            userClient = Client(
                client.userId,
                client.userName,
                client.surname,
                client.secondSurname,
                client.pass,
                client.dni,
                client.direction,
                client.telephone,
                client.userType,
                client.registered
            )
            if(userClient.userType){
                val professor = rootData.professor
                userProfessor = Professor(
                    professor.userId
                )

            }else {
                val student = rootData.student
                userStudent = Student(
                    student.userId,
                    student.userYear,
                    student.intensiveDual
                )

                val course = rootData.course
                userCourse = Course(
                    course.title,
                    course.email,
                    course.courseDescription
                )
            }
            val intent = Intent(context, RegisterActivity::class.java).apply {
                putExtra("user", userClient)
                if(userClient.userType){
                    putExtra("professorInfo", userProfessor)
                }else{
                    putExtra("studentInfo", userStudent)
                        .putExtra("userCourse", userCourse)
                }

            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
    fun doRegister(
        userName: String,
        surname: String,
        secondSurname: String,
        password: String,
        dni: String,
        direction: String,
        telephone: Int,
    ) {
        val registerData = mapOf(
            "username" to userName,
            "surname" to surname,
            "secondsurname" to secondSurname,
            "userpass" to password,
            "dni" to dni,
            "direction" to direction,
            "telephone" to telephone
        )
        Log.d(tag,"name:$userName,surname:$surname,secondsurname:$secondSurname,userpass:$password")
        socket.emit(Events.ON_REGISTER_ANSWER.value, Gson().toJson(registerData))

        socket.on(Events.ON_REGISTER_SUCCESS.value){ args ->
            val response = args[0] as String
            Log.d(tag, "Received ON_REQUEST_SUCCESS event: $response")
            activity.runOnUiThread {
                val intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }


        }
        socket.on(Events.ON_REGISTER_FAIL.value){ args ->
            val response = args[0] as String
            Log.d(tag, "Received ON_REQUEST_FALL event: $response")



        }
        socket.on(Events.ON_REGISTER_SAME_PASSWORD.value){ args ->
            val response = args[0] as String
            Log.d(tag, "Received ON_REGISTER_SAME_PASSWORD event: $response")

        }
    }

    fun filterByCourse(client: Client?, callback: (List<String>) -> Unit) {
        val loginData = mapOf("message" to client)
        val jsonData = Gson().toJson(loginData)

        socket.emit(Events.ON_FILTER_BY_COURSE.value, jsonData)
        socket.on(Events.ON_FILTER_BY_COURSE_RESPONSE.value) { args ->
            val jsonDocuments = args[0] as String
            Log.d(tag, "JSON: $jsonDocuments")
            try {
                val gson = Gson()
                val documentListType = object : TypeToken<List<String>>() {}.type
                val documentsLinks: List<String> = gson.fromJson(jsonDocuments, documentListType)
                callback(documentsLinks)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
    }


    fun filterByCycle(client: Client?, callback: (List<String>) -> Unit) {
        val loginData = mapOf("message" to client)
        val jsonData = Gson().toJson(loginData)

        socket.emit(Events.ON_FILTER_BY_CYCLE.value, jsonData)
        socket.on(Events.ON_FILTER_BY_CYCLE_RESPONSE.value) { args ->
            val jsonDocuments = args[0] as String
            Log.d(tag, "JSON: $jsonDocuments")
            try {
                val gson = Gson()
                val documentListType = object : TypeToken<List<String>>() {}.type
                val documentsLinks: List<String> = gson.fromJson(jsonDocuments, documentListType)
                callback(documentsLinks)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
    }

    fun filterBySubject(client: Client?, callback: (List<String>) -> Unit) {
        val loginData = mapOf("message" to client)
        val jsonData = Gson().toJson(loginData)

        socket.emit(Events.ON_FILTER_BY_SUBJECT.value, jsonData)
        socket.on(Events.ON_FILTER_BY_SUBJECT_RESPONSE.value) { args ->
            val jsonDocuments = args[0] as String
            Log.d(tag, "JSON: $jsonDocuments")
            try {
                val gson = Gson()
                val documentListType = object : TypeToken<List<String>>() {}.type
                val documentsLinks: List<String> = gson.fromJson(jsonDocuments, documentListType)
                callback(documentsLinks)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
    }

    fun getExternalCourses(client: Client?, callback: (List<ExternalCourse>) -> Unit) {
        val loginData = mapOf("message" to client)
        val jsonData = Gson().toJson(loginData)

        socket.emit(Events.ON_GET_EXTERNAL_COURSES.value, jsonData)
        socket.on(Events.ON_GET_EXTERNAL_COURSES_ANSWER.value) { args ->
            val jsonDocuments = args[0] as String
            Log.d(tag, "JSONDocuments: $jsonDocuments")
            try {
                val gson = Gson()
                val externalCoursesType = object : TypeToken<List<ExternalCourse>>() {}.type
                val externalCoursesList: List<ExternalCourse> = gson.fromJson(jsonDocuments, externalCoursesType)
                Log.d(tag, "JSONList: $externalCoursesList")
                callback(externalCoursesList)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
        socket.on(Events.ON_GET_EXTERNAL_COURSES_ERROR.value) { args ->
            val response = args[0] as String
            Log.d(tag, "Login fallado: $response")
        }
    }



    fun doLogout(userName: String) {
        val message = MessageInput(userName) // The server is expecting a MessageInput
        socket.emit(Events.ON_LOGOUT.value, Gson().toJson(message))

        // Log traces

        Log.d(tag, "Attempt of logout - $message")
    }
}

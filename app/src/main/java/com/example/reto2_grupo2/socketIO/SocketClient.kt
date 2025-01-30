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
import com.example.reto2_grupo2.entity.ExternalCourse
import com.example.reto2_grupo2.entity.Reunion
import com.example.reto2_grupo2.entity.RootData
import com.example.reto2_grupo2.entity.Student
import com.example.reto2_grupo2.entity.room.ClientDatabase
import com.example.reto2_grupo2.entity.room.LoginForROOM
import com.example.reto2_grupo2.socketIO.config.Events
import com.example.reto2_grupo2.socketIO.model.MessageInput
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class SocketClient(private val activity: Activity) {
    private val ipPort = "http://192.168.1.147:2888"
    private val socket: Socket = IO.socket(ipPort)
    private var context: Context
    private var fragment: Fragment? = null
    private lateinit var rootData: RootData
    private lateinit var userClient: Client
    private lateinit var userStudent: Student
    private lateinit var userCourse: Course

    // For log purposes
    private var tag = "socket.io"

    // We add here ALL the events we are eager to LISTEN from the server
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


        // Event called when the socket gets an answer from a login attempt.
        // We get the message and print it. Note: this event is called after
        // We try to login
        socket.on(Events.ON_LOGIN_ANSWER.value) { args ->
            // Ensure args[0] is a MessageOutput (which contains a JSON string)
            val response = args[0] as JSONObject

            // The response contains a JSON string under the "message" key
            val message = response.getString("message")

            // Now we can use Gson to parse the message into a JsonObject
            val gson = Gson()
            val jsonObject = gson.fromJson(message, JsonObject::class.java)


            // Extract values from the JsonObject
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

            // Create a Client object (or any other appropriate model class)
            val client = Client(
                id,
                name,
                surname,
                secondSurname,
                pass,
                dni,
                direction,
                telephone,
                type,
                registered
            )

            // Display the result in the UI
            /* activity.findViewById<TextView>(R.id.textView).append("\nAnswer to Login: $client")
             Log.d(tag, "Answer to Login: $client")*/


        }


        // Event called when the socket gets an answer from a getAll.
        // We get the message and print it.
        socket.on(Events.ON_GET_ALL_ANSWER.value) { args ->

            // The response from the server is a JSON
            val response = args[0] as JSONObject

            // The answer should be like this:
            // [
            // {"id":0,"name":"patata","surname":"potato","pass":"pass","edad":20},
            // {"id":1,"name":"patata2","surname":"potato2","pass":"pass2","edad":22},
            // {"id":2,"name":"patata3","surname":"potato3","pass":"pass3","edad":23}
            // ]
            // We extract the field 'message'
            val message = response.getString("message") as String

            // We parse the JSON. Note we use Alumno to parse the server response
            val gson = Gson()
            val itemType = object : TypeToken<List<Client>>() {}.type
            val list = gson.fromJson<List<Client>>(message, itemType)

            // The logging
            activity.findViewById<TextView>(R.id.textView).append("\nAnswer to getAll:$list")
            Log.d(tag, "Answer to getAll: $list")
        }
    }

    // Default events

    // This method is called when we want to establish a connection with the server
    fun connect() {
        socket.connect()

        // Log traces
        Log.d(tag, "Connecting to server...")
    }

    // This method is called when we want to disconnect from the server
    fun disconnect() {
        socket.disconnect()

        // Log traces
        Log.d(tag, "Disconnecting from server...")
    }

    // Custom events

    // This method is called when we want to login. We get the userName,
    // put in into an MessageOutput, and convert it into JSON to be sent
    fun doLogin(userName: String, password: String, rememberMe: Boolean) {
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

            //Añadimos a la base de datos el cliente
            if (rememberMe)
                saveClientInROOM(userClient)

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
            val intent = Intent(context, MainFrame::class.java).apply {
                putExtra("user", userClient).putExtra("studentInfo", userStudent)
                    .putExtra("userCourse", userCourse)
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
            val intent = Intent(context, RegisterActivity::class.java).apply {
                putExtra("user", userClient).putExtra("studentInfo", userStudent)
                    .putExtra("userCourse", userCourse)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun saveClientInROOM(userClient: Client) {
        //Creamos la instancia de la base de datos
        val db = ClientDatabase(context)
        GlobalScope.launch(Dispatchers.IO) {
            //Si hay un usuario en la base de datos, lo eliminamos primero
            if (db.clientDao().getAll().isNotEmpty()) {
                db.clientDao().deleteClients()
            }

            val loginForRoom = LoginForROOM(userClient.userName, userClient.pass)

            //Añadimos el cliente a la base de datos
            loginForRoom.let { db.clientDao().insert(it) }
        }
    }

    fun doRegister(
        userName: String,
        password: String,
        surname: String,
        secondSurname: String,
        dni: String,
        direction: String,
        telephone: Int,
        year: Char,
        courseName: String,
        dual: Boolean
    ) {
        val registerData = mapOf(
            "username" to userName,
            "userpass" to password,
            "surname" to surname,
            "secondsurname" to secondSurname,
            "dni" to dni,
            "direction" to direction,
            "telephone" to telephone,
            "year" to year,
            "courseName" to courseName,
            "dual" to dual
        )
        socket.emit(Events.ON_REGISTER_ANSWER.value, Gson().toJson(registerData))

        socket.on(Events.ON_REGISTER_SUCCESS.value) { args ->
            val response = args[0] as String
            Log.d(tag, "Received ON_REQUEST_SUCCESS event: $response")
            activity.runOnUiThread {
                val intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }


        }
        socket.on(Events.ON_REGISTER_FAIL.value) { args ->
            val response = args[0] as String
            Log.d(tag, "Received ON_REQUEST_FALL event: $response")


        }
        socket.on(Events.ON_REGISTER_SAME_PASSWORD.value) { args ->
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
                val externalCoursesList: List<ExternalCourse> =
                    gson.fromJson(jsonDocuments, externalCoursesType)
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


    // This method is called when we want to logout. We get the userName,
    // put in into an MessageOutput, and convert it into JSON to be sent
    fun doLogout(userName: String) {
        val message = MessageInput(userName) // The server is expecting a MessageInput
        socket.emit(Events.ON_LOGOUT.value, Gson().toJson(message))

        // Log traces

        Log.d(tag, "Attempt of logout - $message")
    }

    fun changePassword(client: Client?, newPassword: String) {
        val userData = mapOf(
            "userId" to client?.userId,
            "newPassword" to newPassword
        )
        socket.emit(Events.ON_CHANGE_PASSWORD.value, Gson().toJson(userData))

        socket.on(Events.ON_CHANGE_PASSWORD_ANSWER.value) {
            activity.runOnUiThread {
                Toast.makeText(context, "Contraseña cambiada correctamente", Toast.LENGTH_SHORT)
                    .show()
            }
            client?.pass = newPassword
            saveClientInROOM(client!!)
        }
        socket.on(Events.ON_CHANGE_PASSWORD_FAIL.value) {
            activity.runOnUiThread {
                Toast.makeText(context, "No se ha podido cambiar la contraseña", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun getReunions(client: Client?, callback: (List<Reunion>) -> Unit) {
        val loginData = mapOf("message" to client)
        val jsonData = Gson().toJson(loginData)

        socket.emit(Events.ON_GET_REUNIONS.value, jsonData)

        socket.off(Events.ON_GET_REUNIONS_ANSWER.value)

        socket.on(Events.ON_GET_REUNIONS_ANSWER.value) { args ->
            val jsonDocuments = args[0] as String
            Log.d(tag, "JSONDocuments: $jsonDocuments")
             try {
                 val gson = Gson()
                 val reunionType = object : TypeToken<List<Reunion>>() {}.type
                 val reunionList: List<Reunion> =
                     gson.fromJson(jsonDocuments, reunionType)
                 Log.d(tag, "JSONList: $reunionList")
                 callback(reunionList)
             } catch (e: Exception) {
                 callback(emptyList())
             }
        }
        socket.on(Events.ON_GET_REUNIONS_ERROR.value) { args ->
            val response = args[0] as String
            Log.d(tag, "Login fallado: $response")
        }
    }
}

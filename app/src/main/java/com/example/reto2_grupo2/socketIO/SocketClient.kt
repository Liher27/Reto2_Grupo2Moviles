package com.example.reto2_grupo2.socketIO

import com.example.reto2_grupo2.entity.Schedule
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.LoginActivity
import com.example.reto2_grupo2.MainFrame
import com.example.reto2_grupo2.RegisterActivity
import com.example.reto2_grupo2.entity.Client
import com.example.reto2_grupo2.entity.Course
import com.example.reto2_grupo2.entity.ExternalCourse
import com.example.reto2_grupo2.entity.Professor
import com.example.reto2_grupo2.entity.Reunion
import com.example.reto2_grupo2.entity.RootData
import com.example.reto2_grupo2.entity.Student
import com.example.reto2_grupo2.entity.room.ClientDatabase
import com.example.reto2_grupo2.entity.room.LoginForROOM
import com.example.reto2_grupo2.socketIO.config.Events
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SocketClient(private val activity: Activity) {
    private val ipPort = "http://10.5.104.65:2888"
    private val socket: Socket = IO.socket(ipPort)
    private var context: Context
    private var fragment: Fragment? = null
    private lateinit var rootData: RootData
    private lateinit var userClient: Client
    private lateinit var userStudent: Student
    private lateinit var userCourse: Course
    private lateinit var userProfessor: Professor

    init {
        context = fragment?.requireContext() ?: activity
    }

    fun connect() {
        socket.connect()
    }

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
            if (userClient.userType) {
                val professor = rootData.professor
                userProfessor = Professor(
                    professor.userId,
                    professor.name
                )

            } else {
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
                if (userClient.userType) {
                    putExtra("professorInfo", userProfessor)
                } else {
                    putExtra("studentInfo", userStudent)
                        .putExtra("userCourse", userCourse)
                }
            }
            if (rememberMe) {
                saveClientInROOM(userClient)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        socket.on(Events.ON_LOGIN_FAIL.value) { args ->
            val response = args[0] as String
            activity.runOnUiThread {
                Toast.makeText(context, response, Toast.LENGTH_SHORT)
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
            if (userClient.userType) {
                val professor = rootData.professor
                userProfessor = Professor(
                    professor.userId,
                    professor.name
                )

            } else {
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
                if (userClient.userType) {
                    putExtra("professorInfo", userProfessor)
                } else {
                    putExtra("studentInfo", userStudent)
                        .putExtra("userCourse", userCourse)
                }

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
        socket.emit(Events.ON_REGISTER_ANSWER.value, Gson().toJson(registerData))

        socket.on(Events.ON_REGISTER_SUCCESS.value) { args ->
            val response = args[0] as String
            activity.runOnUiThread {
                Toast.makeText(context, response, Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(context, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
        socket.on(Events.ON_REGISTER_FAIL.value) { args ->
            val response = args[0] as String
            activity.runOnUiThread {
                Toast.makeText(context, response, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        socket.on(Events.ON_REGISTER_SAME_PASSWORD.value) { args ->
            val response = args[0] as String
            activity.runOnUiThread {
                Toast.makeText(context, response, Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun filterByCourse(client: Client?, callback: (List<String>) -> Unit) {
        val loginData = mapOf("message" to client)
        val jsonData = Gson().toJson(loginData)

        socket.emit(Events.ON_FILTER_BY_COURSE.value, jsonData)
        socket.on(Events.ON_FILTER_BY_COURSE_RESPONSE.value) { args ->
            val jsonDocuments = args[0] as String
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
            try {
                val gson = Gson()
                val documentListType = object : TypeToken<List<String>>() {}.type
                val documentsLinks: List<String> = gson.fromJson(jsonDocuments, documentListType)
                callback(documentsLinks)
            } catch (e: Exception) {
                socket.on(Events.ON_FILTER_ERROR.value) {
                }
                callback(emptyList())
            }
        }
    }
    fun getScheduleSubjects(client: Client?, callback: (List<Schedule>) -> Unit) {
        val loginData = mapOf("message" to client)
        val jsonData = Gson().toJson(loginData)

        socket.emit(Events.ON_FILTER_BY_SCHEDULE.value, jsonData)
        socket.on(Events.ON_FILTER_BY_SCHEDULE_RESPONSE.value) { args ->
            val jsonDocuments = args[0] as String
            try {
                val gson = Gson()
                val listType = object : TypeToken<List<Schedule>>() {}.type
                val schedules: List<Schedule> = gson.fromJson(jsonDocuments, listType)
                callback(schedules)
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
            try {
                val gson = Gson()
                val externalCoursesType = object : TypeToken<List<ExternalCourse>>() {}.type
                val externalCoursesList: List<ExternalCourse> =
                    gson.fromJson(jsonDocuments, externalCoursesType)
                callback(externalCoursesList)
            } catch (e: Exception) {
                callback(emptyList())
            }
        }
        socket.on(Events.ON_GET_EXTERNAL_COURSES_ERROR.value) {
            callback(emptyList())
        }
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
                Toast.makeText(
                    context,
                    "No se ha podido cambiar la contraseña",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        }
    }

    fun getReunions(client: Client?, callback: (List<Reunion>, List<Professor>) -> Unit) {
        val loginData = mapOf("message" to client)
        val jsonData = Gson().toJson(loginData)

        socket.emit(Events.ON_GET_REUNIONS.value, jsonData)

        socket.off(Events.ON_GET_REUNIONS_ANSWER.value)

        socket.on(Events.ON_GET_REUNIONS_ANSWER.value) { args ->
            val jsonDocuments = args[0] as String
            val jsonDocuments2 = args[1] as String
            try {
                val gson = Gson()
                val reunionType = object : TypeToken<List<Reunion>>() {}.type
                val reunionList: List<Reunion> =
                    gson.fromJson(jsonDocuments, reunionType)
                val professorsTpe = object : TypeToken<List<Professor>>() {}.type
                val professorsList: List<Professor> =
                    gson.fromJson(jsonDocuments2, professorsTpe)
                callback(reunionList, professorsList)
            } catch (e: Exception) {
                callback(emptyList(), emptyList())
            }
        }
        socket.on(Events.ON_GET_REUNIONS_ERROR.value) {
            callback(emptyList(), emptyList())
        }
    }

    fun acceptReunion(reunionId: Int) {
        val userData = mapOf(
            "reunionId" to reunionId
        )
        socket.emit(Events.ON_ACCEPT_REUNION.value, Gson().toJson(userData))

        socket.on(Events.ON_ACCEPT_REUNION_ANSWER.value) {
            activity.runOnUiThread {
                Toast.makeText(context, "Reunión aceptada correctamente", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        socket.on(Events.ON_ACCEPT_REUNION_ERROR.value) {
            activity.runOnUiThread {
                Toast.makeText(context, "No se ha podido aceptar la reunión", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun cancelReunion(reunionId: Int) {
        val userData = mapOf(
            "reunionId" to reunionId
        )
        socket.emit(Events.ON_REJECT_REUNION.value, Gson().toJson(userData))

        socket.on(Events.ON_REJECT_REUNION_ANSWER.value) {
            activity.runOnUiThread {
                Toast.makeText(context, "Reunión rechazada correctamente", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        socket.on(Events.ON_REJECT_REUNION_ERROR.value) {
            activity.runOnUiThread {
                Toast.makeText(context, "No se ha podido rechazar la reunión", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun forceAcceptReunion(reunionId: Int) {
        val userData = mapOf(
            "reunionId" to reunionId
        )
        socket.emit(Events.ON_FORCE_REUNION.value, Gson().toJson(userData))

        socket.on(Events.ON_FORCE_REUNION_ANSWER.value) {
            activity.runOnUiThread {
                Toast.makeText(context, "Reunión forzada correctamente", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        socket.on(Events.ON_FORCE_REUNION_ERROR.value) {
            activity.runOnUiThread {
                Toast.makeText(context, "No se ha podido forzar la reunión", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun createReunion(
        reunionThemeText: String,
        reunionReasonText: String,
        reunionDateText: String,
        reunionHourText: Int,
        reunionClassText: String,
        reunionProfessorText: ArrayList<Professor>,
        reunionProfessorId: Int?
    ) {
        val reunionFields = mapOf(
            "reunionTheme" to reunionThemeText,
            "reunionReason" to reunionReasonText,
            "reunionDate" to reunionDateText,
            "reunionHour" to reunionHourText,
            "reunionClass" to reunionClassText,
            "reunionProfessors" to reunionProfessorText,
            "reunionProfessorId" to reunionProfessorId
        )
        Log.d("reunionFields", Gson().toJson(reunionFields))
        socket.emit(Events.ON_CREATE_REUNION.value, Gson().toJson(reunionFields))
        socket.on(Events.ON_CREATE_REUNION_ANSWER.value) {
            activity.runOnUiThread {
                Toast.makeText(context, "Reunion creada correctamente", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        socket.on(Events.ON_CREATE_REUNION_ERROR.value) {
            activity.runOnUiThread {
                Toast.makeText(context, "No se ha podido crear la reunión", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    fun forgotPassword(userName: String) {
        val userData = mapOf(
            "message" to userName
        )
        socket.emit(Events.ON_FORGOT_PASSWORD.value, Gson().toJson(userData))

        socket.on(Events.ON_FORGOT_PASSWORD_ANSWER.value) {
            activity.runOnUiThread {
                Toast.makeText(context, "Contraseña enviada correctamente", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        socket.on(Events.ON_FORGOT_PASSWORD_ERROR.value) {
            activity.runOnUiThread {
                Toast.makeText(context, "No se ha podido enviar la contraseña", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}



package com.example.reto2_grupo2.socketIO

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.example.reto2_grupo2.LoginActivity
import com.example.reto2_grupo2.MainFrame
import com.example.reto2_grupo2.R
import com.example.reto2_grupo2.RegisterActivity
import com.example.reto2_grupo2.entity.Client
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject
import com.example.reto2_grupo2.socketIO.config.Events
import com.example.reto2_grupo2.socketIO.model.MessageInput
import kotlin.properties.Delegates

class SocketClient(private val activity: Activity) {

    private val ipPort = "http://172.29.48.1:2888"
    private val socket: Socket = IO.socket(ipPort)
    private var name: String = ""
    private lateinit var context: Context
    private lateinit var perf: SharedPreferences
    private var saveUser by Delegates.notNull<Boolean>()
    private var fragment: Fragment? = null

    constructor(fragment: Fragment) : this(fragment.requireActivity()) {
        this.fragment = fragment
    }

    // For log purposes
    private var tag = "socket.io"

    // We add here ALL the events we are eager to LISTEN from the server
    init {

        context = fragment?.requireContext() ?: activity.applicationContext
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
    fun doLogin(userName: String, password: String) {
        val loginData = mapOf(
            "message" to userName,
            "userPass" to password
        )
        socket.emit(Events.ON_LOGIN.value, Gson().toJson(loginData))
        Log.d(tag, "Attempt of login - $userName, $password")

        socket.on(Events.ON_LOGIN_SUCCESS.value) { args ->
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
            val secondSurname = jsonObject["secondsurname"].asString
            val pass = jsonObject["pass"].asString
            val dni = jsonObject["dni"].asString
            val direction = jsonObject["direction"].asString
            val telephone = jsonObject["telephone"].asInt
            val type = jsonObject["userType"].asBoolean
            val registered = jsonObject["registered"].asBoolean

            // Log the values for debugging
            Log.d(
                tag,
                "id: $id, name: $name, surname: $surname, pass: $pass, tipo:$type, registered:$registered"
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
            val intent = Intent(context, MainFrame::class.java).apply {
                putExtra("user", client)
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

        socket.on(Events.ON_LOGIN_FALL.value) { args ->
            val response = args[0] as String
            Log.d(tag, "Login fallado: $response")
            activity.runOnUiThread {
                Toast.makeText(context, "No se ha logueado correctamente", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        socket.on(Events.ON_REGISTER.value) { args ->
            val response = args[0] as String
            Log.d(tag, "Registra su usuario por: $response")
            val intent = Intent(context, RegisterActivity::class.java)

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }


    fun doRegister() {

    }

    fun filterByCourse() {
    }

    // This method is called when we want to getAll the Alumno.
    fun doGetAll() {
        socket.emit(Events.ON_GET_ALL.value)

        // Log traces

        Log.d(tag, "Attempt of getAll...")
    }

    // This method is called when we want to logout. We get the userName,
    // put in into an MessageOutput, and convert it into JSON to be sent
    fun doLogout(userName: String) {
        val message = MessageInput(userName) // The server is expecting a MessageInput
        socket.emit(Events.ON_LOGOUT.value, Gson().toJson(message))

        // Log traces

        Log.d(tag, "Attempt of logout - $message")
    }

}

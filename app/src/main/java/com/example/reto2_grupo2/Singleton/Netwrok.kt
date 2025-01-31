package com.example.reto2_grupo2.Singleton

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.widget.Toast
import com.example.reto2_grupo2.LoginActivity


class ConnectionChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiNetInfo: NetworkInfo? = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

        if (wifiNetInfo?.isConnected == false) {
            Toast.makeText(context, "No hay conexion de internet", Toast.LENGTH_SHORT)
                .show()
            Log.e("tagError","Error de la conexion")
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {

        }
    }
}
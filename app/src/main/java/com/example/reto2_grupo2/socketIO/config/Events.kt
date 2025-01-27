package com.example.reto2_grupo2.socketIO.config

enum class Events(val value: String) {
    ON_LOGIN ("onLogin"),
    ON_GET_ALL ("onGetAll"),
    ON_LOGOUT ("onLogout"),
    ON_LOGIN_ANSWER ("onLoginAnswer"),
    ON_GET_ALL_ANSWER ("onGetAllAnswer"),
    ON_LOGIN_SUCCESS("onLoginSuccess"),
    ON_LOGIN_FALL("onLoginFall"),
    ON_REGISTER_AWNSER("onRegisterAwnser"),
    ON_REGISTER("onRegister");
}
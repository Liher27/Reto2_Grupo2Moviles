package com.example.reto2_grupo2.socketIO.config

enum class Events(val value: String) {
    ON_LOGIN ("onLogin"),
    ON_GET_ALL ("onGetAll"),
    ON_LOGOUT ("onLogout"),
    ON_LOGIN_ANSWER ("onLoginAnswer"),
    ON_GET_ALL_ANSWER ("onGetAllAnswer"),
    ON_LOGIN_SUCCESS("onLoginSuccess"),
    ON_LOGIN_FALL("onLoginFall"),
    ON_LOGIN_STUDENT("onLoginStudent"),
    ON_LOGIN_PROFESSOR("onLoginProfessor"),
    ON_REGISTER("onRegister");
}
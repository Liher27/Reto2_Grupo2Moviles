package com.example.reto2_grupo2.socketIO.config

enum class Events(val value: String) {
    ON_LOGIN("onLogin"),
    ON_GET_ALL("onGetAll"),
    ON_LOGOUT("onLogout"),
    ON_LOGIN_ANSWER("onLoginAnswer"),
    ON_GET_ALL_ANSWER("onGetAllAnswer"),
    ON_LOGIN_SUCCESS("onLoginSuccess"),
    ON_LOGIN_FALL("onLoginFall"),
    ON_FILTER_BY_COURSE("onFilterByCourse"),
    ON_FILTER_BY_CYCLE("onFilterByCycle"),
    ON_FILTER_BY_SUBJECT("onFilterBySubject"),
    ON_FILTER_BY_COURSE_RESPONSE("onFilterByCourseResponse"),
    ON_FILTER_BY_CYCLE_RESPONSE("onFilterByCycleResponse"),
    ON_FILTER_BY_SUBJECT_RESPONSE("onFilterBySubjectResponse"),
    ON_FILTER_ERROR("onFilterError"),
    ON_LOGIN_STUDENT("onLoginStudent"),
    ON_LOGIN_PROFESSOR("onLoginProfessor"),
    ON_REGISTER_ANSWER("onRegisterAwnser"),
    ON_REGISTER("onRegister");

}
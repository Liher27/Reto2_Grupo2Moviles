package com.example.reto2_grupo2.entity.room

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LoginForROOM
    (
    @PrimaryKey val userName: String,
    val pass: String,
)
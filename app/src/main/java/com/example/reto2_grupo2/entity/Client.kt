package com.example.reto2_grupo2.entity

import android.os.Parcel
import android.os.Parcelable

data class Client
    (
    val userId:Int,
    val userName:String,
    val surname:String,
    val secondSurname :String,
    val pass:String,
    val dni:String,
    val direction:String,
    val telephone :Int,
    val userType:Boolean,
    val registered:Boolean):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readBoolean(),
        parcel.readBoolean()
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.let {
            dest.writeInt(userId)
            dest.writeString(userName)
            dest.writeString(surname)
            dest.writeString(secondSurname)
            dest.writeString(pass)
            dest.writeString(dni)
            dest.writeString(direction)
            dest.writeInt(telephone)
            dest.writeBoolean(userType)
            dest.writeBoolean(registered)
        }
        }


    companion object CREATOR : Parcelable.Creator<Client> {
        override fun createFromParcel(parcel: Parcel): Client {
            return Client(parcel)
        }

        override fun newArray(size: Int): Array<Client?> {
            return arrayOfNulls(size)
        }
    }
}
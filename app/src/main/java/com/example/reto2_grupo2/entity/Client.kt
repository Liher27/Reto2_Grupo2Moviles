package com.example.reto2_grupo2.entity

import android.os.Parcel
import android.os.Parcelable

data class Client
    (public val userId:Int,
     public val name:String,
     public val surname:String,
     public val password:String,
     public val userType:Boolean,
     public val registered:Boolean):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
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
            dest.writeString(name)
            dest.writeString(surname)
            dest.writeString(password)
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
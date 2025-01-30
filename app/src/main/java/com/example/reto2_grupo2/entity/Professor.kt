package com.example.reto2_grupo2.entity

import android.os.Parcel
import android.os.Parcelable

data class Professor (
    val userId : Int
): Parcelable {
    constructor(parcel: Parcel) : this(parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Professor> {
        override fun createFromParcel(parcel: Parcel): Professor {
            return Professor(parcel)
        }

        override fun newArray(size: Int): Array<Professor?> {
            return arrayOfNulls(size)
        }
    }
}

package com.example.reto2_grupo2.entity

import Assistant
import android.os.Parcel
import android.os.Parcelable

data class Reunion(
    val reunionId: Int,
    val professor: Professor,
    val title: String,
    val affair: String,
    val day: String,
    val class_: String,
    val reunionState: Int,
    val hour: Int,
    val assistants: List<Assistant>,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readParcelable(Professor::class.java.classLoader) ?: Professor(0),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.createTypedArrayList(Assistant.CREATOR) ?: emptyList(),
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(reunionId)
        dest.writeParcelable(professor, flags)
        dest.writeString(title)
        dest.writeString(affair)
        dest.writeString(day)
        dest.writeString(class_)
        dest.writeInt(reunionState)
        dest.writeInt(hour)
        dest.writeTypedList(assistants)
    }

    companion object CREATOR : Parcelable.Creator<Reunion> {
        override fun createFromParcel(parcel: Parcel): Reunion {
            return Reunion(parcel)
        }

        override fun newArray(size: Int): Array<Reunion?> {
            return arrayOfNulls(size)
        }
    }
}
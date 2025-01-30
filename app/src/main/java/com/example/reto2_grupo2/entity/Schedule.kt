package com.example.reto2_grupo2.entity

import android.os.Parcel
import android.os.Parcelable
import java.net.IDN

data class Schedule(
    val scheduleId : Int,
    val userID : Int,
    val scheduleDay : Int,
    val scheduleHour : Int,
    val subjectID: Int
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(scheduleId)
        parcel.writeInt(userID)
        parcel.writeInt(scheduleDay)
        parcel.writeInt(scheduleHour)
        parcel.writeInt(subjectID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Schedule> {
        override fun createFromParcel(parcel: Parcel): Schedule {
            return Schedule(parcel)
        }

        override fun newArray(size: Int): Array<Schedule?> {
            return arrayOfNulls(size)
        }
    }
}
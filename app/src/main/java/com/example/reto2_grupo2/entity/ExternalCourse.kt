package com.example.reto2_grupo2.entity

import android.os.Parcel
import android.os.Parcelable
import java.sql.Date

data class ExternalCourse(
    val externalCourseId: Int,
    val title: String,
    val description: String,
    val telephone: Int,
    val startDate: Date,
    val endDate: Date,
    val schedule: Int,
    val latitude: Float,
    val longitude: Float
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readInt(),
        Date(parcel.readLong()),
        Date(parcel.readLong()),
        parcel.readInt(),
        parcel.readFloat(),
        parcel.readFloat()
    )

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(externalCourseId)
        dest.writeString(title)
        dest.writeString(description)
        dest.writeInt(telephone)
        dest.writeLong(startDate.time)
        dest.writeLong(endDate.time)
        dest.writeInt(schedule)
        dest.writeFloat(latitude)
        dest.writeFloat(longitude)
    }

    companion object CREATOR : Parcelable.Creator<ExternalCourse> {
        override fun createFromParcel(parcel: Parcel): ExternalCourse {
            return ExternalCourse(parcel)
        }

        override fun newArray(size: Int): Array<ExternalCourse?> {
            return arrayOfNulls(size)
        }
    }
}

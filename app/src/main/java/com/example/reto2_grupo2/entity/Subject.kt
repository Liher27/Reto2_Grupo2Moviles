package com.example.reto2_grupo2.entity

import android.os.Parcel
import android.os.Parcelable

data class Subject(
    val subjectID : Int,
    val CourseId : Int,
    val subjectName : String
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(subjectID)
        parcel.writeInt(CourseId)
        parcel.writeString(subjectName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Subject> {
        override fun createFromParcel(parcel: Parcel): Subject {
            return Subject(parcel)
        }

        override fun newArray(size: Int): Array<Subject?> {
            return arrayOfNulls(size)
        }
    }
}
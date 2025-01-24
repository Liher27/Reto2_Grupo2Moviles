package com.example.reto2_grupo2.entity

import android.os.Parcel
import android.os.Parcelable

data class Document
    (
    val documentId: Int,
    val allowedCourse: Int,
    val link: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString().toString(),
    ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.let {
            dest.writeInt(documentId)
            dest.writeInt(allowedCourse)
            dest.writeString(link)
        }
    }


    companion object CREATOR : Parcelable.Creator<Document> {
        override fun createFromParcel(parcel: Parcel): Document {
            return Document(parcel)
        }

        override fun newArray(size: Int): Array<Document?> {
            return arrayOfNulls(size)
        }
    }
}
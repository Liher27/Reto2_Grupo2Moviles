import android.os.Parcel
import android.os.Parcelable
import com.example.reto2_grupo2.entity.Subject
import com.google.gson.annotations.SerializedName

data class Schedule(
    val scheduleId: Int,
    val userID: Int,
    val scheduleDay: Int,
    val scheduleHour: Int,
    val subject: Subject
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readParcelable(Subject::class.java.classLoader)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(scheduleId)
        parcel.writeInt(userID)
        parcel.writeInt(scheduleDay)
        parcel.writeInt(scheduleHour)
        parcel.writeParcelable(subject, flags) // Guardamos el objeto Subject
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

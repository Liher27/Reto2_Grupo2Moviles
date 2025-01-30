import android.os.Parcel
import android.os.Parcelable
import com.example.reto2_grupo2.entity.Professor

data class Assistant(
    val professor: Professor
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Professor::class.java.classLoader) ?: Professor(0)
    )

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeParcelable(professor, flags)
    }

    companion object CREATOR : Parcelable.Creator<Assistant> {
        override fun createFromParcel(parcel: Parcel): Assistant {
            return Assistant(parcel)
        }

        override fun newArray(size: Int): Array<Assistant?> {
            return arrayOfNulls(size)
        }
    }
}
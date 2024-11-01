package com.example.iti.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey val time: Long,
    val kind: String
) : Parcelable {

    // Write the object's data to the provided Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(time)
        parcel.writeString(kind)
    }

    // Describe the contents of the Parcelable
    override fun describeContents(): Int {
        return 0
    }

    // Companion object to facilitate the Parcelable creation
    companion object CREATOR : Parcelable.Creator<AlarmEntity> {
        override fun createFromParcel(parcel: Parcel): AlarmEntity {
            val time = parcel.readLong()
            val kind = parcel.readString() ?: ""
            return AlarmEntity(time, kind)
        }

        override fun newArray(size: Int): Array<AlarmEntity?> {
            return arrayOfNulls(size)
        }
    }
}

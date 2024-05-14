package com.nghiatd.networking.model

import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

data class User(
    var login : String = "",
    var id : Int = 0,
    var nodeId : String = "",
    var avatarUrl : String = "",
    var bitmap : Bitmap? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readInt(),
        parcel.readString().toString(),
        parcel.readString().toString()
    )

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        TODO("Not yet implemented")
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
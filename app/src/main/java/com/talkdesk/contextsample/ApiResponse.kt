package com.talkdesk.contextsample

import android.os.Parcel
import android.os.Parcelable

data class ApiResponse(val success: Boolean, val body: String = ""): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readByte() != 0.toByte(),
        parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (success) 1 else 0)
        parcel.writeString(body)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ApiResponse> {
        override fun createFromParcel(parcel: Parcel): ApiResponse {
            return ApiResponse(parcel)
        }

        override fun newArray(size: Int): Array<ApiResponse?> {
            return arrayOfNulls(size)
        }
    }
}

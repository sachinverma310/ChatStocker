package stws.chatstocker.model

import android.os.Parcel
import android.os.Parcelable

data class LoginResponse(var name: String?, var uid: String?, var email: String?, var profile: String?,var phone: String?,var deviceToken: String?) : Parcelable {


    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),parcel.readString(),parcel.readString(),parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(uid)
        parcel.writeString(email)
        parcel.writeString(profile)
        parcel.writeString(phone)
        parcel.writeString(deviceToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LoginResponse> {
        override fun createFromParcel(parcel: Parcel): LoginResponse {
            return LoginResponse(parcel)
        }

        override fun newArray(size: Int): Array<LoginResponse?> {
            return arrayOfNulls(size)
        }
    }
}
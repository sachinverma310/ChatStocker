package stws.chatstocker.model

import android.os.Parcel
import android.os.Parcelable

class GroupUser(val name:String?,val image:String?,val online:Boolean? ,val email:String?,val lastSeen:String?,val uid:String?,val isSelected:Boolean?): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(), parcel.readValue(Boolean::class.java.classLoader) as? Boolean) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeValue(online)
        parcel.writeString(email)
        parcel.writeString(lastSeen)
        parcel.writeString(uid)
        parcel.writeValue(isSelected)
    }

    override fun describeContents(): Int {
        return 0
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
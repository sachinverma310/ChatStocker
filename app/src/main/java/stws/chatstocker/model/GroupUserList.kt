package stws.chatstocker.model

import android.os.Parcel
import android.os.Parcelable

data class GroupUserList(val pos:Int,val selectedPos:Int,val user: User?):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readParcelable(User::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(pos)
        parcel.writeInt(selectedPos)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GroupUserList> {
        override fun createFromParcel(parcel: Parcel): GroupUserList {
            return GroupUserList(parcel)
        }

        override fun newArray(size: Int): Array<GroupUserList?> {
            return arrayOfNulls(size)
        }
    }
}
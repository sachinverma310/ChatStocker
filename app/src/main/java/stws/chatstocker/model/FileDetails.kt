package stws.chatstocker.model

import android.os.Parcel
import android.os.Parcelable

data class FileDetails(val fileId:String?, val thumbnail:String?, val createdTime: Long):Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(fileId)
        parcel.writeString(thumbnail)
        parcel.writeLong(createdTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FileDetails> {
        override fun createFromParcel(parcel: Parcel): FileDetails {
            return FileDetails(parcel)
        }

        override fun newArray(size: Int): Array<FileDetails?> {
            return arrayOfNulls(size)
        }
    }


}
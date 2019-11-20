package stws.chatstocker.model

import android.os.Parcel
import android.os.Parcelable

class  ChatMessage :Parcelable {
    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest!!.writeString(msg)
        dest!!.writeString(seen)
        dest!!.writeString(type)
        dest!!.writeString(from)
        dest!!.writeString(to)
        dest!!.writeString(deletedFrom)
        dest!!.writeString(senderName)
        dest.writeByte(if (isSelected) 1 else 0)
        dest!!.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    var msg:String=""
     var seen:String=""
     var type:String=""
     var from:String=""
     var to:String=""
     var deletedFrom:String=""
    var senderName:String=""
    var isSelected:Boolean=false
    var isSent:Boolean=true
    set(value) {
        field=value
    }
    get() =field

    var isSentToserver:Boolean=false
        set(value) {
            field=value
        }
        get() =field
    var progressValue=100
        set(value) {
            field=value
        }
        get() =field

//    var istoDeleted:Boolean=false
    var date:String=""
    var isNow:String=""
        set(value) {
            field=value
        }
        get() =field

    constructor(parcel: Parcel) : this() {
        msg = parcel.readString()!!
        seen = parcel.readString()!!
        type = parcel.readString()!!
        from = parcel.readString()!!
        to = parcel.readString()!!
        deletedFrom = parcel.readString()!!
        senderName = parcel.readString()!!
        isSelected = parcel.readByte() != 0.toByte()
        date = parcel.readString()!!
    }

    constructor( ) {
        //code
    }
    constructor( msg:String, seen:String, type:String, from:String, date:String,to:String,deletedFrom:String
              ,isSelected:Boolean , senderName:String) : this() {
        //code
        this.from=from
        this.msg=msg
        this.seen=seen
        this.type=type
        this.date=date
        this.to=to
        this.deletedFrom=deletedFrom
        this.isSelected=isSelected
        this.senderName=senderName
//        this.istoDeleted=istoDeleted
    }

    companion object CREATOR : Parcelable.Creator<ChatMessage> {
        override fun createFromParcel(parcel: Parcel): ChatMessage {
            return ChatMessage(parcel)
        }

        override fun newArray(size: Int): Array<ChatMessage?> {
            return arrayOfNulls(size)
        }
    }

}
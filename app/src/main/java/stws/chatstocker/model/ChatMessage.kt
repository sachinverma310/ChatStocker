package stws.chatstocker.model

class  ChatMessage {
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

}
package stws.chatstocker.model

class  ChatMessage {
     var msg:String=""
     var seen:String=""
     var type:String=""
     var from:String=""
     var to:String=""
     var deletedFrom:String=""
//    var istoDeleted:Boolean=false
    var date:String=""
    constructor( ) {
        //code
    }
    constructor( msg:String, seen:String, type:String, from:String, date:String,to:String,deletedFrom:String
               ) : this() {
        //code
        this.from=from
        this.msg=msg
        this.seen=seen
        this.type=type
        this.date=date
        this.to=to
        this.deletedFrom=deletedFrom
//        this.istoDeleted=istoDeleted
    }

}
package stws.chatstocker.model

class  ChatMessage {
    public var msg:String=""
    public var seen:String=""
    public var type:String=""
    public var from:String=""
    var date:String=""
    constructor( ) {
        //code
    }
    constructor( msg:String, seen:String, type:String, from:String, date:String) : this() {
        //code
        this.from=from
        this.msg=msg
        this.seen=seen
        this.type=type
        this.date=date
    }

}
package stws.chatstocker.model

data class Friends(val time:String,val uid:String): Comparable<Friends> {
    override fun compareTo(other: Friends): Int {
        return other.time.compareTo(this.time);
    }

}
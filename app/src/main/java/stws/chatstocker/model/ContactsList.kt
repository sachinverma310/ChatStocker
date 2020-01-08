package stws.chatstocker.model

data class ContactsList(val name:String,val number:String,val firstCharName:String):Comparable<ContactsList> {


    override fun compareTo(other: ContactsList): Int {
        return name.compareTo(other.name)
    }
}
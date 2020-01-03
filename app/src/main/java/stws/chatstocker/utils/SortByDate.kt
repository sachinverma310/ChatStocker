package stws.chatstocker.utils

import stws.chatstocker.model.FileDetails

class SortByDate:Comparator<String>{
    override fun compare(o1: String?, o2: String?): Int {
        return o2!!.compareTo(o1!!)
    }

}
package stws.chatstocker.utils

import stws.chatstocker.model.FileDetails

class SortByTime:Comparator<FileDetails>{
    override fun compare(o1: FileDetails?, o2: FileDetails?): Int {
        return o2!!.createdTime.compareTo(o1!!.createdTime)
    }
}
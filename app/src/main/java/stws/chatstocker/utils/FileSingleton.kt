package stws.chatstocker.utils

import stws.chatstocker.model.FileDetails

public class FileSingleton {


    private constructor() {

    }

    companion object {
        var fileSingleton: FileSingleton? = null;

        fun getInstance(): FileSingleton {
            if (fileSingleton == null)
                fileSingleton = FileSingleton()
            return fileSingleton!!;
        }

    }
    var list:List<FileDetails>?=null
        set(value) {
            field=value
        }
        get() = field
}
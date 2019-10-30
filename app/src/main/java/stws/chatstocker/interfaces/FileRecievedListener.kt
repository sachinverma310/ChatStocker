package stws.chatstocker.interfaces

import android.graphics.Bitmap
import stws.chatstocker.model.FileDetails

interface FileRecievedListener {
    fun Downloaded(list: List<FileDetails>)
}
package stws.chatstocker.viewmodel

import androidx.lifecycle.ViewModel
import stws.chatstocker.model.Photos

class PhotoViewModel(val photos: Photos):ViewModel() {
    var date:String=photos.date
    var url=photos.url
    fun setPhotos(photos:Photos){
        date=photos.date
        url=photos.url
    }


}
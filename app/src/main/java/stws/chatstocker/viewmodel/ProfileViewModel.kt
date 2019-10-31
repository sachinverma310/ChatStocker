package stws.chatstocker.viewmodel

import androidx.lifecycle.ViewModel

class ProfileViewModel():ViewModel() {
    var name:String=""
    var phone:String=""
    var email:String=""
    constructor( name:String, phone:String, email:String):this(){
        this.name=name
        this.phone=phone
        this.email=email
    }
}
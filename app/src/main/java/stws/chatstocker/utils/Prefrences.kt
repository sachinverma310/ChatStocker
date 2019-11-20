package stws.chatstocker.utils

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseUser
import com.google.gson.Gson
import stws.chatstocker.ConstantsValues
import stws.chatstocker.ConstantsValues.KEY_LOGIN_DATA
import stws.chatstocker.model.LoginResponse
import stws.chatstocker.utils.Prefrences.Companion.editor
import java.io.IOException

class Prefrences : ConstantsValues {
    companion object {
        lateinit var editor: SharedPreferences.Editor

        @Throws(IOException::class)
        fun saveUser(context: Context, key: String, user: LoginResponse) {
            val gson = Gson()
            val json = gson.toJson(user)
            editor = context.getSharedPreferences(context.packageName, MODE_PRIVATE).edit()
            editor.putString(key, json).commit()
        }


        //
        @Throws(IOException::class, ClassNotFoundException::class)
        fun getUserDetails(context: Context, key: String): LoginResponse {
            var preferences = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
            val gson = Gson()
            val json = preferences.getString(key, "")
            val firebaseUser = gson.fromJson(json, LoginResponse::class.java);
            return firebaseUser
        }

        fun saveBoolean(context: Context, key: String, value: Boolean) {
            editor = context.getSharedPreferences(context.packageName, MODE_PRIVATE).edit()
            editor.putBoolean(key, value).commit()
        }
        fun saveString(context: Context, key: String, value: String) {
            editor = context.getSharedPreferences(context.packageName, MODE_PRIVATE).edit()
            editor.putString(key, value).commit()
        }
        fun getStringValue(context: Context, key: String): String? {
            var preferences = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
            return preferences.getString(key,null)
        }
        fun getBoolean(context: Context, key: String): Boolean {
            var preferences = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
            return preferences.getBoolean(key, false)
        }

        fun getUserUid(context: Context):String?{
            var preferences = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
            val gson = Gson()
            val json = preferences.getString(KEY_LOGIN_DATA, "")
            val firebaseUser = gson.fromJson(json, LoginResponse::class.java);
            return firebaseUser.uid
        }

        //    public static int getInt(Context context, String key){
        //        preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
        //        return preferences.getInt(key,0);
        //    }
        fun clerPref(context: Context) {
            var preferences = context.getSharedPreferences(context.packageName, MODE_PRIVATE)
            preferences.edit().clear().commit()

        }
    }

}
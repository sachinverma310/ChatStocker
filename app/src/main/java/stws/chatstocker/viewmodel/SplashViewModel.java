package stws.chatstocker.viewmodel;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModel;

import stws.chatstocker.ConstantsValues;
import stws.chatstocker.utils.Prefrences;
import stws.chatstocker.view.ChatActivity;
import stws.chatstocker.view.HomeActivity;
import stws.chatstocker.view.LoginActivity;


public class SplashViewModel extends ViewModel implements ConstantsValues {
    private int SPLASH_TIME_OUT = 3000;
    int REQUEST_PERMISION_CODE=101;
    String REQUEST_PERMISSION[]=new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
            ,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA  ,Manifest.permission.READ_CONTACTS ,Manifest.permission.WRITE_CONTACTS,Manifest.permission.RECORD_AUDIO};

    public void callSplash(final Context context) {
        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                if (Prefrences.Companion.getBoolean(context, KEY_IS_LOGIN)) {
                    Intent i = new Intent(context, HomeActivity.class);
                    context.startActivity(i);
                } else {
                    Intent i = new Intent( context, LoginActivity.class);
                    context.startActivity(i);
                }

                // close this activity
                ((AppCompatActivity)context).finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public boolean checkPermission(Context context){

        if (REQUEST_PERMISSION != null) {
            for (String permission : REQUEST_PERMISSION) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;

    }
    public void requestPermission(Context context) {

        ActivityCompat.requestPermissions((AppCompatActivity) context, REQUEST_PERMISSION, REQUEST_PERMISION_CODE);

    }
}

package com.example.chatstocker.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.example.chatstocker.R;
import com.example.chatstocker.databinding.ActivitySplashBinding;
import com.example.chatstocker.viewmodel.SplashViewModel;

public class SplashActivity extends AppCompatActivity {
private int REQUEST_PERMISION_CODE=101;
private SplashViewModel splashViewModel;
private ActivitySplashBinding activitySplashBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashBinding= DataBindingUtil.setContentView(this,R.layout.activity_splash);
//        setContentView(R.layout.activity_splash);
        splashViewModel= ViewModelProviders.of(this).get(SplashViewModel.class);
        activitySplashBinding.setViewModel(splashViewModel);
        if (!splashViewModel.checkPermission(SplashActivity.this))
            splashViewModel.requestPermission(SplashActivity.this);
        else
            splashViewModel.callSplash(SplashActivity.this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==REQUEST_PERMISION_CODE &&grantResults.length == 2&& grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

           splashViewModel.callSplash(SplashActivity.this);
        }
        else {
            Toast.makeText(SplashActivity.this,"Please Grant permissions",Toast.LENGTH_SHORT).show();
            finish();

        }


    }
}

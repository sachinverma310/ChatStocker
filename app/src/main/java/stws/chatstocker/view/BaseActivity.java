package stws.chatstocker.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import stws.chatstocker.BaseApplication;
import stws.chatstocker.ConstantsValues;
import stws.chatstocker.R;
import stws.chatstocker.di.component.ApplicationComponent;


import dagger.android.support.DaggerAppCompatActivity;
import stws.chatstocker.model.LoginResponse;
import stws.chatstocker.utils.Prefrences;
import stws.chatstocker.view.fragments.ContactsFragment;
import stws.chatstocker.view.fragments.UserFragment;

public abstract class BaseActivity extends AppCompatActivity implements ConstantsValues,BottomNavigationView.OnNavigationItemSelectedListener {
    public FrameLayout frameLayout;
    public BottomNavigationView bottomNavigationView;
    public TextView tvName;
    private ConstraintLayout mainActionbar,userActionBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        frameLayout=findViewById(R.id.frameLayout);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        mainActionbar=findViewById(R.id.mainActionBar);
        userActionBar=findViewById(R.id.userActionBar);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        tvName=findViewById(R.id.tvName);
        getUserDetails();

    }

    private void getUserDetails(){
        try {
            LoginResponse firebaseUser= Prefrences.Companion.getUserDetails(BaseActivity.this,KEY_LOGIN_DATA);
            tvName.setText(firebaseUser.getName());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.navigation_chat:
                if (!(this instanceof UserFragment))
                startActivity(new Intent(this,UserFragment.class));
                break;
            case R.id.navigation_contacts:
                if (!(this instanceof ContactsFragment))
                    startActivity(new Intent(this,ContactsFragment.class));
                break;
            case R.id.navigation_profile:
                break;
        }
        return true;
    }

    private void callFrag(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,fragment).commit();
    }
}
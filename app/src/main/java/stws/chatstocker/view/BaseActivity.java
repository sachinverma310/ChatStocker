package stws.chatstocker.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.Collections;

import stws.chatstocker.BaseApplication;
import stws.chatstocker.ConstantsValues;
import stws.chatstocker.R;
import stws.chatstocker.di.component.ApplicationComponent;


import dagger.android.support.DaggerAppCompatActivity;
import stws.chatstocker.model.LoginResponse;
import stws.chatstocker.utils.DriveServiceHelper;
import stws.chatstocker.utils.Prefrences;
import stws.chatstocker.view.fragments.ContactsFragment;
import stws.chatstocker.view.fragments.UserFragment;

public abstract class BaseActivity extends AppCompatActivity implements ConstantsValues,BottomNavigationView.OnNavigationItemSelectedListener {
    public FrameLayout frameLayout;
    public BottomNavigationView bottomNavigationView;
    public TextView tvName;
    private ConstraintLayout mainActionbar,userActionBar;
    public static Drive mDriveService;
     public static DriveServiceHelper mDriveServiceHelper;
     int REQUEST_CODE_SIGN_IN=101;
     public ImageView imgCall,imgSearchBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        frameLayout=findViewById(R.id.frameLayout);
        imgSearchBar=findViewById(R.id.imgSearch);
        bottomNavigationView=findViewById(R.id.bottomNavigationView);
        mainActionbar=findViewById(R.id.mainActionBar);
        userActionBar=findViewById(R.id.userActionBar);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        tvName=findViewById(R.id.tvName);
        imgCall=findViewById(R.id.imgCall);
        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:0123456789"));
                startActivity(intent);
            }
        });
        getUserDetails();
        signIn();

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
    private void signIn() {
        Log.d("TAG", "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }


    private void handleSignInResult( Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result).addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                        BaseActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
                credential.setSelectedAccount(googleSignInAccount.getAccount());
                Drive googleDriveService = new Drive.Builder(
                        AndroidHttp.newCompatibleTransport(),
                        new GsonFactory(),
                        credential)
                        .setApplicationName("Drive API Migration")
                        .build();

                // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                // Its instantiation is required before handling any onClick actions.
                mDriveService = googleDriveService;
                mDriveServiceHelper = DriveServiceHelper.getInstance(googleDriveService);
//                mDriveServiceHelper.listAllFiles()
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE_SIGN_IN&&resultCode == RESULT_OK)
        handleSignInResult(data);
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
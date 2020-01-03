package stws.chatstocker.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import stws.chatstocker.BaseApplication;
import stws.chatstocker.ConstantsValues;
import stws.chatstocker.R;
import stws.chatstocker.di.component.ApplicationComponent;


import dagger.android.support.DaggerAppCompatActivity;
import stws.chatstocker.model.LoginResponse;
import stws.chatstocker.model.User;
import stws.chatstocker.utils.DriveServiceHelper;
import stws.chatstocker.utils.Prefrences;
import stws.chatstocker.utils.ProgressBarHandler;
import stws.chatstocker.view.fragments.ContactsFragment;
import stws.chatstocker.view.fragments.UserFragment;

public abstract class BaseActivity extends AppCompatActivity implements ConstantsValues, BottomNavigationView.OnNavigationItemSelectedListener {
    public FrameLayout frameLayout;
    public BottomNavigationView bottomNavigationView;
    public TextView tvName;
    private ConstraintLayout mainActionbar, userActionBar;
    public static Drive mDriveService;
    public static DriveServiceHelper mDriveServiceHelper;
    int REQUEST_CODE_SIGN_IN = 103;
    public ImageView imgCall, imgSearchBar, imgLogout, imgSetting,imgBack;
    CircleImageView imgProfile;
    public static GoogleSignInClient client;
    private FirebaseAuth mAuth;
    private String token;
    private DatabaseReference mUserDatabase;
    private TextView tvSetting,tvCall,tvLogout;

    private Boolean isLogin=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        final ImagePopup imagePopup = new ImagePopup(this);
        imagePopup.setWindowHeight(500); // Optional
        imagePopup.setWindowWidth(500); // Optional
        imagePopup.setBackgroundColor(Color.BLACK);  // Optional
        imagePopup.setFullScreen(true); // Optional
        imagePopup.setHideCloseIcon(true);  // Optional
        imagePopup.setImageOnClickClose(true);  // Optional
        frameLayout = findViewById(R.id.frameLayout);
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("User");
        imgSearchBar = findViewById(R.id.imgSearch);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        mainActionbar = findViewById(R.id.mainActionBar);
        userActionBar = findViewById(R.id.userActionBar);
        imgProfile = findViewById(R.id.imgProfile);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        tvName = findViewById(R.id.tvName);
        imgCall = findViewById(R.id.imgCall);
        tvSetting = findViewById(R.id.tvSetting);
        tvCall = findViewById(R.id.tvCall);
        tvLogout = findViewById(R.id.tvLogout);
        imgBack=findViewById(R.id.imgBack);

        imgCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:0123456789"));
                startActivity(intent);
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               BaseActivity.super.onBackPressed();
            }
        });
        tvCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
//                intent.setData(Uri.parse("tel:0123456789"));
                startActivity(intent);
            }
        });
        imgLogout = findViewById(R.id.imgLogout);
        imgSetting = findViewById(R.id.imgSettings);
        imgLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        imgSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaseActivity.this, SettingActivity.class));
            }
        });
        tvSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaseActivity.this, SettingActivity.class));
            }
        });
        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(BaseActivity.this, FullProfilePicViewrActivity.class);
                intent.putExtra(KEY_USER_ID, "");
                try {
                    intent.putExtra(KEY_FILE_URL,Prefrences.Companion.getUserDetails(BaseActivity.this,KEY_LOGIN_DATA).getProfile());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
               startActivity(intent);
//                try {
//                    imagePopup.initiatePopupWithGlide(Prefrences.Companion.getUserDetails(BaseActivity.this,KEY_LOGIN_DATA).getProfile());
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//                imagePopup.viewPopup();
            }
        });
        if (this instanceof HomeActivity) {
            getUserDetails();
            signIn();
        }

    }

    private void getUserDetails() {
        try {
            LoginResponse firebaseUser = Prefrences.Companion.getUserDetails(BaseActivity.this, KEY_LOGIN_DATA);
            getFirebaseToken(firebaseUser);
            tvName.setText(firebaseUser.getName());

            Glide.with(this).load(firebaseUser.getProfile()).into(imgProfile);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    private void signIn() {
        Log.d("TAG", "Requesting sign-in");
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    private void signOut() {
        client.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                Prefrences.Companion.saveBoolean(BaseActivity.this, KEY_IS_LOGIN, false);
                Prefrences.Companion.saveInt(BaseActivity.this,ConstantsValues.KEY_Noti_RADIO_ID,-1);
                finishAffinity();
            }
        });
    }

    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result).addOnSuccessListener(new OnSuccessListener<GoogleSignInAccount>() {
            @Override
            public void onSuccess(GoogleSignInAccount googleSignInAccount) {
                GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(
                        BaseActivity.this, Collections.singleton(DriveScopes.DRIVE_FILE));
                credential.setSelectedAccount(googleSignInAccount.getAccount());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String accessToken=credential.getToken();
                            Log.e("acc",accessToken);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (GoogleAuthException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

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
                isLogin=true;
                Intent intent = new Intent("stws.chatstocker");
                intent.putExtra("isLogin",true);
                sendBroadcast(intent);
//                mDriveServiceHelper.listAllFiles()
            }
        });


    }

    public void firebaseAuthWithGoogle(GoogleSignInAccount acct, Context context) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId() + " " + acct.getIdToken());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((AppCompatActivity) context, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
//                            ProgressBarHandler.Companion.hide();
                            updateUi(user);

//                            (user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                    }
                });
    }

    private void updateUi(FirebaseUser user) {
        String userId = null;
        LoginResponse loginResponse = null;
        try {
            loginResponse = Prefrences.Companion.getUserDetails(this, KEY_LOGIN_DATA);
             userId =loginResponse.getUid();
            if (userId == null)
                userId = user.getUid();
            loginResponse.setEmail(user.getEmail());
            if (loginResponse.getProfile() == null)
                loginResponse.setProfile(user.getPhotoUrl().toString());
            Glide.with(this).load(loginResponse.getProfile()).into(imgProfile);
//            else
//                loginResponse.setProfile(user.getPhotoUrl().toString());
            loginResponse.setName(user.getDisplayName());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Prefrences.Companion.saveUser(BaseActivity.this, KEY_LOGIN_DATA, loginResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Map addValue = new HashMap();
        addValue.put("device_token", loginResponse.getDeviceToken());
        addValue.put("online", true);
        addValue.put("name", user.getDisplayName());
        addValue.put("email", user.getEmail());
        addValue.put("numbers", loginResponse.getPhone());
        if (loginResponse.getProfile() == null)
            addValue.put("profileImage", user.getPhotoUrl().toString());
        else
            addValue.put("profileImage", loginResponse.getProfile());
        addValue.put("lastSeen", Calendar.getInstance().getTime().toString());
        addValue.put("uid", userId);
        //---IF UPDATE IS SUCCESSFULL , THEN OPEN MAIN ACTIVITY---
        mUserDatabase.child(userId).updateChildren(addValue, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

//                if (databaseError == null) {
//
//                    //---OPENING MAIN ACTIVITY---
//                    Log.e("Login : ", "Logged in Successfully");
//                    Prefrences.Companion.saveBoolean(LoginActivity.this, KEY_IS_LOGIN, true);
//                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                } else {
//                    Toast.makeText(LoginActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
//                    Log.e("Error is : ", databaseError.toString());
//
//                }
            }
        });
//        mUserDatabase.child("letters").push().setValue("a");
//        mUserDatabase.child("letters").push().setValue("z");
//        mUserDatabase.child("letters").push().setValue("c");

//        mUserDatabase.child(user.getUid()).child("device_token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Prefrences.Companion.saveBoolean(LoginActivity.this,KEY_IS_LOGIN,true);
//                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
//
//
//            }
//        });
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//
//                // A new comment has been added, add it to the displayed list
//                Comment comment = dataSnapshot.getValue(Comment.class);
//                Prefrences.Companion.saveBoolean(LoginActivity.this,KEY_IS_LOGIN,true);
//                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
//                // ...
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
//
//                // A comment has changed, use the key to determine if we are displaying this
//                // comment and if so displayed the changed comment.
//                Comment newComment = dataSnapshot.getValue(Comment.class);
//                String commentKey = dataSnapshot.getKey();
//
//                // ...
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
//
//                // A comment has changed, use the key to determine if we are displaying this
//                // comment and if so remove it.
//                String commentKey = dataSnapshot.getKey();
//
//                // ...
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
//
//                // A comment has changed position, use the key to determine if we are
//                // displaying this comment and if so move it.
//                Comment movedComment = dataSnapshot.getValue(Comment.class);
//                String commentKey = dataSnapshot.getKey();
//
//                // ...
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
//                Toast.makeText(LoginActivity.this, "Failed to load comments.",
//                        Toast.LENGTH_SHORT).show();
//            }
//        };
//        mUserDatabase.addChildEventListener(childEventListener);


    }
    private void updateEamil(User user) {
        String userId = null;
        LoginResponse loginResponse = null;
        try {
            loginResponse = Prefrences.Companion.getUserDetails(this, KEY_LOGIN_DATA);
            userId =loginResponse.getUid();
            if (userId == null)
                userId = user.getUid();
            loginResponse.setEmail(user.getEmail());
            if (loginResponse.getProfile() == null)
                loginResponse.setProfile(user.getImage().toString());
            Glide.with(this).load(loginResponse.getProfile()).into(imgProfile);
//            else
//                loginResponse.setProfile(user.getPhotoUrl().toString());
            loginResponse.setName(user.getName());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Prefrences.Companion.saveUser(BaseActivity.this, KEY_LOGIN_DATA, loginResponse);

        } catch (IOException e) {
            e.printStackTrace();
        }
        Map addValue = new HashMap();
        addValue.put("device_token", loginResponse.getDeviceToken());
        addValue.put("online", true);
        addValue.put("name", user.getName());
        addValue.put("email", user.getEmail());
        addValue.put("numbers", loginResponse.getPhone());
        if (loginResponse.getProfile() == null)
            addValue.put("profileImage", user.getImage().toString());
        else
            addValue.put("profileImage", loginResponse.getProfile());
        addValue.put("lastSeen", Calendar.getInstance().getTime().toString());
        addValue.put("uid", userId);
        //---IF UPDATE IS SUCCESSFULL , THEN OPEN MAIN ACTIVITY---
        mUserDatabase.child(userId).updateChildren(addValue, new DatabaseReference.CompletionListener() {

            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

//                if (databaseError == null) {
//
//                    //---OPENING MAIN ACTIVITY---
//                    Log.e("Login : ", "Logged in Successfully");
//                    Prefrences.Companion.saveBoolean(LoginActivity.this, KEY_IS_LOGIN, true);
//                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                } else {
//                    Toast.makeText(LoginActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
//                    Log.e("Error is : ", databaseError.toString());
//
//                }
            }
        });
//        mUserDatabase.child("letters").push().setValue("a");
//        mUserDatabase.child("letters").push().setValue("z");
//        mUserDatabase.child("letters").push().setValue("c");

//        mUserDatabase.child(user.getUid()).child("device_token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Prefrences.Companion.saveBoolean(LoginActivity.this,KEY_IS_LOGIN,true);
//                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
//
//
//            }
//        });
//        ChildEventListener childEventListener = new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());
//
//                // A new comment has been added, add it to the displayed list
//                Comment comment = dataSnapshot.getValue(Comment.class);
//                Prefrences.Companion.saveBoolean(LoginActivity.this,KEY_IS_LOGIN,true);
//                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
//                // ...
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());
//
//                // A comment has changed, use the key to determine if we are displaying this
//                // comment and if so displayed the changed comment.
//                Comment newComment = dataSnapshot.getValue(Comment.class);
//                String commentKey = dataSnapshot.getKey();
//
//                // ...
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
//
//                // A comment has changed, use the key to determine if we are displaying this
//                // comment and if so remove it.
//                String commentKey = dataSnapshot.getKey();
//
//                // ...
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());
//
//                // A comment has changed position, use the key to determine if we are
//                // displaying this comment and if so move it.
//                Comment movedComment = dataSnapshot.getValue(Comment.class);
//                String commentKey = dataSnapshot.getKey();
//
//                // ...
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, "postComments:onCancelled", databaseError.toException());
//                Toast.makeText(LoginActivity.this, "Failed to load comments.",
//                        Toast.LENGTH_SHORT).show();
//            }
//        };
//        mUserDatabase.addChildEventListener(childEventListener);


    }
    private void getFirebaseToken(LoginResponse firebaseUser) {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
//                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        token = task.getResult().getToken();
                        firebaseUser.setDeviceToken(token);
                        try {
                            Prefrences.Companion.saveUser(BaseActivity.this, KEY_LOGIN_DATA, firebaseUser);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        // Log and toast

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN && resultCode == RESULT_OK) {
            handleSignInResult(data);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                User user=new User(account.getDisplayName(),account.getPhotoUrl().toString(),true,account.getEmail(),"","",false,false);
                updateEamil(user);
//                firebaseAuthWithGoogle(account, BaseActivity.this);
//                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }

        }
        else if (requestCode==REQUEST_CODE_SIGN_IN &&resultCode==RESULT_CANCELED){
            Toast.makeText(this,"Please Select google account",Toast.LENGTH_SHORT).show();
            signIn();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.navigation_chat:
                if (!(this instanceof UserFragment))
                    startActivity(new Intent(this, UserFragment.class));
                break;
            case R.id.navigation_contacts:
                if (!(this instanceof ContactsFragment))
                    startActivity(new Intent(this, ContactsFragment.class));
                break;
            case R.id.navigation_profile:
//                if (!(this instanceof ProfileActivity))
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }
        return true;
    }

    private void callFrag(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (this instanceof HomeActivity) {
//            if (isLogin)
//            {
//                Intent intent = getIntent();
//                intent.putExtra("isLogin",true);
//                sendBroadcast(intent);
//            }
//        }
        getUserDetails();
    }

    public   boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }


}
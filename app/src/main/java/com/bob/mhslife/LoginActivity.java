package com.bob.mhslife;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

public class LoginActivity extends Activity{

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "1YrTfiAJxr0AID9pQXQS5K1L4";
    private static final String TWITTER_SECRET = "7OVNPAJwXIbMGDKzegFPjfzgp9Jvs5mzDkXuOBMNeO9gKDZ6RW";


    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private TwitterLoginButton twitterLoginButton;

    private static final String TAG = "LoginActivity";

    private TextView MHSLifeTV;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button loginButton;
    private Button goToRegisterButton;
    private PopupWindow creditsPopupWindow;
    private LayoutInflater layoutInflater;

    private Typeface KGDefyingGravity;
    private Typeface Biko;
    private Typeface RoundedElegance;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_login);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    User.UID = user.getUid();
//                    Log.d(TAG, "User Signed in: " + User.UID);
                }else{
//                    Log.d(TAG, "User not signed in...");
                }
            }
        };

        if(firebaseAuth.getCurrentUser() != null){
            goToHome();
        }

        initFonts();

        MHSLifeTV = (TextView) findViewById(R.id.MHSLifeTV);
        MHSLifeTV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                showCredits();
                return true;
            }
        });

        loginButton = (Button) findViewById(R.id.loginButton);
        goToRegisterButton = (Button) findViewById(R.id.goToRegisterButton);

        editTextEmail = (EditText) findViewById(R.id.emailTV);
        editTextPassword = (EditText) findViewById(R.id.passwordTV);

        MHSLifeTV.setTypeface(KGDefyingGravity);
        loginButton.setTypeface(Biko);
        goToRegisterButton.setTypeface(Biko);

        progressDialog = new ProgressDialog(this);

        // TWITTER
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                handleTwitterSession(session);
            }
            @Override
            public void failure(TwitterException exception) {
//                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });

    }

    //Email Login
    public void loginUser(View view){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Failed to Log In", Toast.LENGTH_SHORT).show();
                }else{
                    goToHome();
                }
            }
        });
        editTextPassword.setText("");
    }

    //TWITTER
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    private void handleTwitterSession(TwitterSession session){
        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if(!task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Error with Twitter Connection", Toast.LENGTH_SHORT).show();
                }else{
                    goToHome();
                }
            }
        });
    }

    //Navigation
    public void goToRegister(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void goToHome(){
        Intent intent = new Intent(this, FragmentTabs.class);
        startActivity(intent);
    }

    private void showCredits(){
        layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup container = (ViewGroup) layoutInflater.inflate(R.layout.popoverwindow_credits, null);

        creditsPopupWindow = new PopupWindow(container, ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT, true);

        ((TextView) creditsPopupWindow.getContentView().findViewById(R.id.creditsTV)).setTypeface(RoundedElegance);
        ((TextView) creditsPopupWindow.getContentView().findViewById(R.id.createdbyTV)).setTypeface(RoundedElegance);
        ((TextView) creditsPopupWindow.getContentView().findViewById(R.id.creatortwitterTV)).setTypeface(RoundedElegance);
        ((TextView) creditsPopupWindow.getContentView().findViewById(R.id.creatoremailTV)).setTypeface(RoundedElegance);

        creditsPopupWindow.showAtLocation(this.findViewById(R.id.loginFrameLayout), Gravity.NO_GRAVITY, 0, 0);
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                creditsPopupWindow.dismiss();
                return true;
            }
        });
    }

    //Init/Deinit AuthListeners
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null){
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //Init Fonts
    private void initFonts(){
        KGDefyingGravity = Typeface.createFromAsset(getAssets(), "kgdefyinggravity.ttf");
        Biko = Typeface.createFromAsset(getAssets(), "biko.otf");
        RoundedElegance = Typeface.createFromAsset(getAssets(), "roundedelegance.ttf");
    }
}
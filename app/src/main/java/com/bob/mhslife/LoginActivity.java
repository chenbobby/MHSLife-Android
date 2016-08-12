package com.bob.mhslife;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends Activity{

    private FirebaseAuth firebaseAuth;

    private static final String TAG = "LoginActivity";

    private EditText editTextEmail;
    private EditText editTextPassword;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            goToHome();
        }

        editTextEmail = (EditText) findViewById(R.id.emailTV);
        editTextPassword = (EditText) findViewById(R.id.passwordTV);

        progressDialog = new ProgressDialog(this);
    }

    public void loginUser(View view){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Log.e(TAG, "Email cannot be empty.");
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(password)){
            Log.e(TAG, "Password cannot be empty.");
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging In...");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if(task.isSuccessful()){
                    goToHome();
                }else{
                    Toast.makeText(LoginActivity.this, "Failed to Log In", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void goToRegister(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void goToHome(){
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

}
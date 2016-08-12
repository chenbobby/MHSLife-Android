package com.bob.mhslife;

import android.app.Activity;
import android.app.ProgressDialog;
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

public class RegisterActivity extends Activity {

    private FirebaseAuth firebaseAuth;

    private static final String TAG = "RegisterActivity";

    private EditText editTextEmail;
    private EditText editTextNewPassword;
    private EditText editTextConfirmPassword;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.emailTV);
        editTextNewPassword = (EditText) findViewById(R.id.newPasswordTV);
        editTextConfirmPassword = (EditText) findViewById(R.id.confirmPasswordTV);

        progressDialog = new ProgressDialog(this);
    }

    public void registerUser(View view){
        String email = editTextEmail.getText().toString().trim();
        String newPassword = editTextNewPassword.getText().toString().trim();
        String confirmPassword = editTextConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)){
            Log.e(TAG, "Please Enter Email");
            Toast.makeText(this, "Please Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(newPassword)){
            Log.e(TAG, "Please Enter New Password");
            Toast.makeText(this, "Please Enter New Password", Toast.LENGTH_SHORT).show();
            return;
        }else if(TextUtils.isEmpty(confirmPassword)){
            Log.e(TAG, "Please Confirm Password");
            Toast.makeText(this, "Please Confirm Password", Toast.LENGTH_SHORT).show();
            return;
        }else if(!newPassword.equals(confirmPassword)){
            editTextNewPassword.setText("");
            editTextConfirmPassword.setText("");
            Log.e(TAG, "Passwords do not match");
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email, newPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if(task.isSuccessful()){
                    editTextEmail.setText("");
                    editTextNewPassword.setText("");
                    editTextConfirmPassword.setText("");
                    Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(RegisterActivity.this, "Failed to Create Account", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

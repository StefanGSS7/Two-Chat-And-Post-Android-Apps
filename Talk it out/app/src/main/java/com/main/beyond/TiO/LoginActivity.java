package com.main.beyond.TiO;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private Button loginButton , signupButton;
    private EditText loginEmail , loginPassword;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog loadingBar;
    private TextView forgetPasswordLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        loadingBar = new ProgressDialog(this);

        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signupButton);
        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPass);
        forgetPasswordLink = findViewById(R.id.forgotPassword);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendUserToRegister();

            }


            private void SendUserToRegister() {

                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent);

            }
        });


        forgetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(LoginActivity.this,ResetPasswordActivity.class));

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AllowUserToLogin();

            }

            private void AllowUserToLogin() {

                String email = loginEmail.getText().toString();
                String password = loginPassword.getText().toString();

                if(TextUtils.isEmpty(email)){

                    Toast.makeText(LoginActivity.this, "Please Write Your Email", Toast.LENGTH_SHORT).show();

                }else if(TextUtils.isEmpty(password)){

                    Toast.makeText(LoginActivity.this, "Please Write Your Password", Toast.LENGTH_SHORT).show();

                }else {

                    loadingBar.setTitle("Loging in");
                    loadingBar.setMessage("Please wait while we log you in");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);

                    firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                SendUserToMainActivity();

                                Toast.makeText(LoginActivity.this, "You're logged", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }else {

                                String message = task.getException().getMessage();
                                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }

                        }
                    });

                }

            }
        });

    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(LoginActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if(currentUser != null){

            SendUserToMainActivity();

        }

    }
}

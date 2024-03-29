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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private EditText registerEmail, registerPass, registerConPass;
    private Button createAccountButton;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerEmail = findViewById(R.id.registerEmail);
        registerPass = findViewById(R.id.registerPass);
        registerConPass = findViewById(R.id.registerConfPass);
        createAccountButton = findViewById(R.id.registerButton);
        loadingBar = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateNewAccount();

            }

            private void CreateNewAccount() {

                String email = registerEmail.getText().toString();
                String password = registerPass.getText().toString();
                String conPass = registerConPass.getText().toString();

                if(TextUtils.isEmpty(email)){

                    Toast.makeText(RegisterActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();

                }else if(TextUtils.isEmpty(password)){

                    Toast.makeText(RegisterActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();

                }else if(TextUtils.isEmpty(conPass)){

                    Toast.makeText(RegisterActivity.this, "Please confirm your password", Toast.LENGTH_SHORT).show();

                }else if (!password.equals(conPass)) {

                    Toast.makeText(RegisterActivity.this, "Passwords does not match", Toast.LENGTH_SHORT).show();

                } else {

                    loadingBar.setTitle("Creating New Account");
                    loadingBar.setMessage("Please wait while we setup your account");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);

                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                          if(task.isSuccessful()){

                              SendUserToSetupActivity();
                              loadingBar.dismiss();

                          }

                          else{

                              String message = task.getException().getMessage();

                              Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();

                              loadingBar.dismiss();

                          }

                        }
                    });

                }



            }

            private void SendUserToSetupActivity() {

                Intent setupIntent = new Intent(RegisterActivity.this,SetupActivity.class);
                setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(setupIntent);
                finish();

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){

            SendUserToMainActivity();

        }

    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }


}

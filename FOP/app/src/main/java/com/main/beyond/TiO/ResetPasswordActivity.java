package com.main.beyond.TiO;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ResetPasswordActivity extends AppCompatActivity {

    private EditText forgotenPasswordEmail;
    private Button prati;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        firebaseAuth = FirebaseAuth.getInstance();

forgotenPasswordEmail = findViewById(R.id.fp_userMail);
prati = findViewById(R.id.fp_pratiButton);


        prati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userEmail = forgotenPasswordEmail.getText().toString();

                if(TextUtils.isEmpty(userEmail)){

                    Toast.makeText(ResetPasswordActivity.this, "Ве молиме пишете ја вашата валидна е-пошта", Toast.LENGTH_SHORT).show();

                }else {

                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                Toast.makeText(ResetPasswordActivity.this, "Проверете си ја е-поштата", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));

                            }else{

                                String errorMessage = task.getException().toString();
                                Toast.makeText(ResetPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();

                            }

                        }
                    });

                }

            }
        });


    }

}

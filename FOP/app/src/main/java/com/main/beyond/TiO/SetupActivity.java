package com.main.beyond.TiO;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SetupActivity extends AppCompatActivity {

    private EditText firstName, lastName, birthday;
    private Button saveInfoButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private ProgressDialog loadingBar;

    String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId);

        loadingBar = new ProgressDialog(this);

        firstName = findViewById(R.id.saveInfoFirstName);
        lastName = findViewById(R.id.saveInfoLastName);
        birthday = findViewById(R.id.saveInfoBirthday);
        saveInfoButton = findViewById(R.id.saveInfoCreateAccount);

        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveAccountInformation();

            }
        });



    }

    private void saveAccountInformation() {

        String fName = firstName.getText().toString();
        String lName = lastName.getText().toString();
        String bDay = birthday.getText().toString();

        if(TextUtils.isEmpty(fName)){

            Toast.makeText(this, "Please Enter FirstName", Toast.LENGTH_SHORT).show();

        }

        if(TextUtils.isEmpty(lName)){

            Toast.makeText(this, "Please Enter LastName", Toast.LENGTH_SHORT).show();

        }

        if(TextUtils.isEmpty(bDay)){

            Toast.makeText(this, "Please Enter Birthday", Toast.LENGTH_SHORT).show();

        } else {

            loadingBar.setTitle("Saving Information");
            loadingBar.setMessage("Please wait while we save your information");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);

            HashMap userMap = new HashMap();

            userMap.put("FirstName", fName);
            userMap.put("LastName", lName);
            userMap.put("DateofBirth", bDay);
            userMap.put("EKSTRA","EKSTRA");
            userMap.put("Gender","none");

            databaseReference.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(task.isSuccessful()){

                        SendUserToMainActivity();
                        Toast.makeText(SetupActivity.this, "Your account is created", Toast.LENGTH_LONG).show();
                        loadingBar.dismiss();

                    }else {

                        String message = task.getException().getMessage();

                        Toast.makeText(SetupActivity.this, message, Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();

                    }

                }
            });

        }

    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(SetupActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();

    }
}

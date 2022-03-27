package com.main.beyond.TiO;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SetupActivity extends AppCompatActivity  {

    private EditText firstName, lastName, birthday;
    private Button saveInfoButton,confirmImageButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private ImageView addProfilePicture,avatar1,avatar2,avatar3,avatar4;
    private ConstraintLayout setupLayout,imagePickerLayout;
    private String imageUrl;

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
        addProfilePicture = findViewById(R.id.addProfilePicture);
        setupLayout = findViewById(R.id.setupLayout);
        imagePickerLayout = findViewById(R.id.imagePickerLayout);
        confirmImageButton = findViewById(R.id.confirmImageButton);
        avatar1 = findViewById(R.id.avatar1);
        avatar2 = findViewById(R.id.avatar2);
        avatar3 = findViewById(R.id.avatar3);
        avatar4 = findViewById(R.id.avatar4);






        saveInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                saveAccountInformation();

            }
        });

        addProfilePicture.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                setupLayout.setVisibility(View.GONE);
                imagePickerLayout.setVisibility(View.VISIBLE);

            }
        });



        confirmImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setupLayout.setVisibility(View.VISIBLE);
                imagePickerLayout.setVisibility(View.GONE);

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
            userMap.put("typingTo","noOne");
            userMap.put("isOnlineStatus","offline");
            userMap.put("profilePicture",imageUrl);


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

    public void imageClicked(View view) {

        if (view.getId() == R.id.avatar1) {

            addProfilePicture.setBackground(avatar1.getDrawable());
            avatar1.setBackgroundResource(R.drawable.selectavatar);
            avatar2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            avatar3.setBackgroundColor(Color.parseColor("#FFFFFF"));
            avatar4.setBackgroundColor(Color.parseColor("#FFFFFF"));
            imageUrl =  "https://firebasestorage.googleapis.com/v0/b/talk-it-out-55b8f.appspot.com/o/avatar1%20-%20Copy.png?alt=media&token=914056fc-cc0a-4039-bc7e-3c0ac1a02e7e";



        } else if (view.getId() == R.id.avatar2) {
            addProfilePicture.setBackground(avatar2.getDrawable());

            avatar1.setBackgroundColor(Color.parseColor("#FFFFFF"));
            avatar2.setBackgroundResource(R.drawable.selectavatar);
            avatar3.setBackgroundColor(Color.parseColor("#FFFFFF"));
            avatar4.setBackgroundColor(Color.parseColor("#FFFFFF"));
            imageUrl =  "https://firebasestorage.googleapis.com/v0/b/talk-it-out-55b8f.appspot.com/o/avatar2%20-%20Copy.png?alt=media&token=fdc61949-ca76-4b80-b281-bced85d001f8";



        }else if (view.getId() == R.id.avatar3) {
            addProfilePicture.setBackground(avatar3.getDrawable());

            avatar1.setBackgroundColor(Color.parseColor("#FFFFFF"));
            avatar2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            avatar3.setBackgroundResource(R.drawable.selectavatar);
            avatar4.setBackgroundColor(Color.parseColor("#FFFFFF"));


            imageUrl =  "https://firebasestorage.googleapis.com/v0/b/talk-it-out-55b8f.appspot.com/o/avatar3%20-%20Copy.png?alt=media&token=e92c0185-6df9-42f6-83b5-eade32041443";



        }else if (view.getId() == R.id.avatar4) {
            addProfilePicture.setBackground(avatar4.getDrawable());

            avatar1.setBackgroundColor(Color.parseColor("#FFFFFF"));
            avatar2.setBackgroundColor(Color.parseColor("#FFFFFF"));
            avatar3.setBackgroundColor(Color.parseColor("#FFFFFF"));
            avatar4.setBackgroundResource(R.drawable.selectavatar);



            imageUrl =  "https://firebasestorage.googleapis.com/v0/b/talk-it-out-55b8f.appspot.com/o/avatar4%20-%20Copy.png?alt=media&token=2803675e-3550-42a8-b6c2-228b7c5d4e1a";



        }

    }

}

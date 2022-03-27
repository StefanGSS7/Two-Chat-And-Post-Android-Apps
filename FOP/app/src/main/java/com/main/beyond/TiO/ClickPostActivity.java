package com.main.beyond.TiO;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ClickPostActivity extends AppCompatActivity {

    private EditText PostEditText;
    private Button PostButton,DeleteButton;
    private Spinner spinner;
    private Toolbar toolbar;
    private DatabaseReference databaseReference,userRef;

    private FirebaseAuth firebaseAuth;

    private String postKey,currentUserId,databaseUserId,content,category,postCategory,isAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_post);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        toolbar = findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Промени/Избриши објава");

        postKey = getIntent().getExtras().get("PostKey").toString();
        postCategory = getIntent().getExtras().get("PostCategoryEdit").toString();



        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts").child(postCategory).child(postKey);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                isAdmin = dataSnapshot.child(currentUserId).child("EKSTRA").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String[] arraySpinner = new String[] {
                "Сексуална Експлатација", "Трудова Експлатација", "Принудни Бракови", "Трансплатација на Органи", "Посвојување Деца"
        };

        spinner = (Spinner) findViewById(R.id.EditspinnerID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);


        PostEditText = findViewById(R.id.EditPostEditText);
        PostButton = findViewById(R.id.EditPostButton);
        DeleteButton = findViewById(R.id.EditPostButtonDelete);

        PostEditText.setEnabled(false);
        DeleteButton.setVisibility(View.GONE);
        PostButton.setVisibility(View.GONE);
        spinner.setEnabled(false);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    content = dataSnapshot.child("Content").getValue().toString();
                    category = dataSnapshot.child("Category").getValue().toString();
                    databaseUserId = dataSnapshot.child("uid").getValue().toString();

                    PostEditText.setText(content);

                    if(category.equals("Сексуална Експлатација")){

                        spinner.setSelection(1);

                    }

                    if(category.equals("Трудова Експлатација")){

                        spinner.setSelection(2);

                    }

                    if(category.equals("Принудни Бракови")){

                        spinner.setSelection(3);

                    }

                    if(category.equals("Трансплатација на Органи")){

                        spinner.setSelection(4);

                    }

                    if(category.equals("Посвојување Деца")){

                        spinner.setSelection(5);

                    }

                    if(currentUserId.equals(databaseUserId) || isAdmin.equals("Admin")){

                        DeleteButton.setVisibility(View.VISIBLE);
                        PostButton.setVisibility(View.VISIBLE);
                        spinner.setEnabled(true);
                        PostEditText.setEnabled(true);

                    }

                    PostButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditCurrentPost(content);
                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DeleteCurrentPost();

            }
        });

    }

    private void EditCurrentPost(String content) {



        AlertDialog.Builder builder = new AlertDialog.Builder(ClickPostActivity.this);
        builder.setTitle("Дали сте сигурни за промената?");


        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                databaseReference.child("Content").setValue(PostEditText.getText().toString());
                Toast.makeText(ClickPostActivity.this, "Постот е променет успешно", Toast.LENGTH_SHORT).show();
                SendUserToMainActivity();
            }
        });

        builder.setNegativeButton("Не", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });

        Dialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.white);

        ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);
        ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);

    }


    private void DeleteCurrentPost() {

        databaseReference.removeValue();

        SendUserToMainActivity();

        Toast.makeText(this, "Постот е избришан", Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==android.R.id.home){

            SendUserToMainActivity();

        }
        return super.onOptionsItemSelected(item);

    }

    private void SendUserToMainActivity() {

        Intent mainActivityIntent = new Intent(ClickPostActivity.this,MainActivity.class);
        startActivity(mainActivityIntent);


    }
}

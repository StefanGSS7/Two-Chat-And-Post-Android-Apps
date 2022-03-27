package com.main.beyond.TiO;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class PostActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private EditText PostEditText,PostNameText;
    private Button PostButton;
    private Spinner spinner;
    String kategorija, post, currentUserId;
    private DatabaseReference userRef,databaseReference;
    private FirebaseAuth firebaseAuth;
    private long i=0;
    private CheckBox checkBox;
    String userFirstName;
    String userLastName;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Posts");

        toolbar = findViewById(R.id.update_post_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Нова објава");

        checkBox = findViewById(R.id.checkBox);
        PostNameText = findViewById(R.id.postNameText);

        String[] arraySpinner = new String[] {
                "Сексуална Експлатација", "Трудова Експлатација", "Принудни Бракови", "Трансплатација на Органи", "Посвојување Деца"
        };
        spinner = (Spinner) findViewById(R.id.spinnerID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);




       databaseReference.addValueEventListener(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                i = dataSnapshot.getChildrenCount();
            }
            public void onCancelled(DatabaseError databaseError) { }
        });


        PostEditText = findViewById(R.id.postEditText);
        PostButton = findViewById(R.id.postButton);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                kategorija = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        PostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidatePostInfo();

            }
        });

    }

    private void ValidatePostInfo() {

        post = PostEditText.getText().toString();

        if(TextUtils.isEmpty(post)){

            Toast.makeText(this, "Пишете нешто", Toast.LENGTH_SHORT).show();

        }else if(TextUtils.isEmpty(PostNameText.getText().toString())) {

            Toast.makeText(this, "Пишете Име На Објава", Toast.LENGTH_SHORT).show();

        }else StoringDataToFireBase();

    }

    private void StoringDataToFireBase() {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());

        final String postRandomName = saveCurrentDate + saveCurrentTime;


        userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                HashMap postsMap = new HashMap();

                if(dataSnapshot.exists()){

                    if(checkBox.isChecked()){

                        postsMap.put("FirstName","Анонимна");
                        postsMap.put("LastName","Објава");

                    }else {
                         userFirstName = dataSnapshot.child("FirstName").getValue().toString();
                         userLastName = dataSnapshot.child("LastName").getValue().toString();

                        postsMap.put("FirstName",userFirstName);
                        postsMap.put("LastName",userLastName);
                    }

                    postsMap.put("uid",currentUserId);
                    postsMap.put("Date",saveCurrentDate);
                    postsMap.put("Time",saveCurrentTime);
                    postsMap.put("Category",kategorija);
                    postsMap.put("Content",PostEditText.getText().toString());
                    postsMap.put("PostName",PostNameText.getText().toString());
                    postsMap.put("EKSTRA",dataSnapshot.child("EKSTRA").getValue().toString());

                    postsMap.put("Order",i+1);


                    databaseReference.child(kategorija).child(kategorija +  postRandomName + i).updateChildren(postsMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if(task.isSuccessful()){

                                Toast.makeText(PostActivity.this, "Постирано", Toast.LENGTH_SHORT).show();
                                SendUserToMainActivity();

                            }else {

                                Toast.makeText(PostActivity.this, "Имаше некоја грешка при постирање", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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

        Intent mainActivityIntent = new Intent(PostActivity.this,MainActivity.class);
        startActivity(mainActivityIntent);


    }
}

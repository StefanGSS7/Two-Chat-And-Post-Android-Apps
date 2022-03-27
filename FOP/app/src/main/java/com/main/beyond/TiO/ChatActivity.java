package com.main.beyond.TiO;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import android.widget.EditText;
import android.widget.Toast;



import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    private Toolbar chatToolBar;
    private Button sendTextButton;
    private EditText userMessage;
    private String messageReceverID,messageReciverFirstName,messageReceverLastName,messageSenderId,senderLastName;
    private DatabaseReference rootRef, messageRef;
    private FirebaseAuth firebaseAuth;
    private RecyclerView userMessagesList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MessagesAdapter messagesAdapter;
    private  String senderFirstName,receverFullName;
    private static final int TOTAL_ITEMS_TO_LOAD = 20;
    private int i = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatToolBar = findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(chatToolBar);
        sendTextButton = findViewById(R.id.chat_sendButton);
        userMessage = findViewById(R.id.chat_editText);




        messagesAdapter = new MessagesAdapter(messagesList);
        userMessagesList = findViewById(R.id.chat_messages_list_users);
        swipeRefreshLayout = findViewById(R.id.chat_swipeToRefreshLayout);

        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messagesAdapter);


        rootRef = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        messageSenderId = firebaseAuth.getCurrentUser().getUid();


        messageReceverID = getIntent().getExtras().get("user_id").toString();
        messageReciverFirstName = getIntent().getExtras().get("ReceverFirstName").toString();
        messageReceverLastName = getIntent().getExtras().get("ReceverLastName").toString();
        receverFullName = messageReciverFirstName + " " + messageReceverLastName;
        senderFirstName = getIntent().getExtras().get("SenderFirstName").toString();
        senderLastName = getIntent().getExtras().get("SenderLastName").toString();


        sendTextButton.setEnabled(false);

        setSupportActionBar(chatToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(messageReciverFirstName + " " + messageReceverLastName);






        FetchMessages();


        userMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if(TextUtils.isEmpty(s)){

                    sendTextButton.setEnabled(false);

                } else sendTextButton.setEnabled(true);

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(TextUtils.isEmpty(s)){

                    sendTextButton.setEnabled(false);

                } else sendTextButton.setEnabled(true);

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(TextUtils.isEmpty(s)){

                    sendTextButton.setEnabled(false);

                } else sendTextButton.setEnabled(true);

            }
        });

        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendMessage();

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                i++;

                messagesList.clear();

                FetchMessages();

            }
        });


    }

    private void FetchMessages() {

        messageRef =  rootRef.child("Messages").child(messageSenderId).child(messageReceverID);

        Query messageQuery = messageRef.limitToLast(i * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){

                    if(!dataSnapshot.getKey().equals("FirstName") && !dataSnapshot.getKey().equals("LastName") && !dataSnapshot.getKey().equals("uid") ){
                    Messages messages = dataSnapshot.getValue(Messages.class);
                    messagesList.add(messages);
                    messagesAdapter.notifyDataSetChanged();

                    userMessagesList.scrollToPosition(messagesList.size()-1);

                    swipeRefreshLayout.setRefreshing(false);

                    }

                }



            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendMessage() {



       String messageText = userMessage.getText().toString();
       String messageSenderRef =  messageReceverID  + "/" + messageSenderId ;
       String messageReceverRef =  messageSenderId + "/" + messageReceverID ;
       String addReceverName = "Messages/" + messageReceverID + "/" + messageSenderId + "/";
       String addSenderName = "Messages/" + messageSenderId + "/"  + messageReceverID + "/";

       DatabaseReference userMessageKey = rootRef.child("Messages").child(messageSenderId).child(messageReceverID).push();

       String messagePushID = userMessageKey.getKey();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());

        Map addName = new HashMap();

        addName.put(addReceverName + "/" + "FirstName" , senderFirstName);
        addName.put(addReceverName + "/" + "LastName" , senderLastName);
        addName.put(addReceverName + "/" + "uid", messageSenderId);

        addName.put(addSenderName + "/" + "FirstName" , messageReciverFirstName);
        addName.put(addSenderName + "/" + "LastName" , messageReceverLastName);
        addName.put(addSenderName + "/" + "uid", messageReceverID);



        Map messageTextBody = new HashMap();

        messageTextBody.put("Message", messageText);
        messageTextBody.put("Time", saveCurrentTime);
        messageTextBody.put("Date", saveCurrentDate);
        messageTextBody.put("Type", "text");
        messageTextBody.put("From", messageSenderId);

        Map messageBodyDetails = new HashMap();

        messageBodyDetails.put(messageReceverRef + "/" + messagePushID, messageTextBody);
        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageTextBody);


        rootRef.updateChildren(addName);

        rootRef.child("Messages").updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if(task.isSuccessful()){
                    userMessage.setText("");

                }else {

                    String message = task.getException().getMessage();

                    Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();

                    userMessage.setText("");
                }
            }
        });










    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==android.R.id.home){

            startActivity(new Intent(ChatActivity.this,MainActivity.class));

        }
        return super.onOptionsItemSelected(item);

    }

}

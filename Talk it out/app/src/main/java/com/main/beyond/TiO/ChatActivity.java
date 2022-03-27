package com.main.beyond.TiO;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.iceteck.silicompressorr.FileUtils;
import com.iceteck.silicompressorr.SiliCompressor;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ChatActivity extends AppCompatActivity {

    private Toolbar chatToolBar;
    private Toolbar imageViewToolbar;
    private TextView imageViewUserName;
    private Button sendTextButton,sendImage,captureImage,sendVoice,closeViewImage,downloadImage,captureVideo;
    private EditText userMessage;
    private TextView istyping;
    private String messageReceverID,messageReciverFirstName,messageReceverLastName,messageSenderId,senderLastName,vStatus,mCurrentPhotoPath = "";
    private DatabaseReference rootRef, messageRef;
    private FirebaseAuth firebaseAuth;
    private RecyclerView userMessagesList;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MessagesAdapter messagesAdapter;
    private ImageView receverPicture;
    private ConstraintLayout viewImageLayout;
    private  String senderFirstName,receverFullName,imageUrl,checker="",mPrevKey,mLastKey;
    private static final int TOTAL_ITEMS_TO_LOAD = 15;
    private int i = 1,messagePos = 0;
    private Uri fileUri,imageURI,videoUri;
    private File imageFile,videoFile;
    private StorageTask uploadTask;
    private ProgressDialog loadingBar;
    private ImageView viewImage;
    private VideoView viewVideo;
    static final int REQUEST_IMAGE_CAPTURE = 34,VIDEO_REQUEST = 35;

    private static final String IMAGE_DIRECTORY_NAME = "TiO";
    ValueEventListener seenListener;
    private GridView galleryGrid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatToolBar = findViewById(R.id.chat_page_toolbar);
        setSupportActionBar(chatToolBar);
        sendTextButton = findViewById(R.id.chat_sendButton);
        userMessage = findViewById(R.id.chat_editText);
        receverPicture = findViewById(R.id.receverPicture);
        viewImage = findViewById(R.id.viewImage);
        viewImageLayout = findViewById(R.id.viewImageLayout);
        closeViewImage = findViewById(R.id.closeViewImage);
        downloadImage = findViewById(R.id.downloadImage);
        imageViewUserName = findViewById(R.id.imageViewUserName);
        captureImage = findViewById(R.id.chat_captureImage);
        sendImage = findViewById(R.id.chat_sendImage);
        captureVideo = findViewById(R.id.chat_captureVideo);
        viewVideo = findViewById(R.id.viewVideo);

        galleryGrid = findViewById(R.id.galleryGrid);

        rootRef = FirebaseDatabase.getInstance().getReference();

        firebaseAuth = FirebaseAuth.getInstance();
        messageSenderId = firebaseAuth.getCurrentUser().getUid();


        messageReceverID = getIntent().getExtras().get("user_id").toString();
        messageReciverFirstName = getIntent().getExtras().get("ReceverFirstName").toString();
        messageReceverLastName = getIntent().getExtras().get("ReceverLastName").toString();
        receverFullName = messageReciverFirstName + " " + messageReceverLastName;
        senderFirstName = getIntent().getExtras().get("SenderFirstName").toString();
        senderLastName = getIntent().getExtras().get("SenderLastName").toString();

        loadingBar = new ProgressDialog(this);



        messagesAdapter = new MessagesAdapter(ChatActivity.this,ChatActivity.this,viewImage,messagesList,viewImageLayout,viewVideo);
        userMessagesList = findViewById(R.id.chat_messages_list_users);
        swipeRefreshLayout = findViewById(R.id.chat_swipeToRefreshLayout);
        istyping = findViewById(R.id.istyping);


        linearLayoutManager = new LinearLayoutManager(this);
        userMessagesList.setHasFixedSize(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        userMessagesList.setAdapter(messagesAdapter);




        imageViewUserName.setText(messageReciverFirstName + " " + messageReceverLastName);

        sendTextButton.setEnabled(false);

        setSupportActionBar(chatToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(messageReciverFirstName + " " + messageReceverLastName);





        rootRef.child("Users").child(messageReceverID).addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {

       String typingStatus = "" + dataSnapshot.child("typingTo").getValue();
       String isOnlineStatus = "" + dataSnapshot.child("isOnlineStatus").getValue();
       final String receverPictureUrl = "" + dataSnapshot.child("profilePicture").getValue();

               Picasso.get().load(receverPictureUrl).transform(new CropCircleTransformation()).into(receverPicture);

               Calendar calForDate = Calendar.getInstance();
               SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
               final String saveCurrentDate = currentDate.format(calForDate.getTime());
               String sameDate = "";

               if(!isOnlineStatus.equals("online")){

                   sameDate = isOnlineStatus.substring(0,11);

                   if(saveCurrentDate.equals(sameDate)) {

                       getSupportActionBar().setSubtitle("Last Seen " + "Today at" + isOnlineStatus.substring(11));

                   }else{

                       getSupportActionBar().setSubtitle("Last Seen on" + isOnlineStatus.substring(0,11));

                   }
               }else getSupportActionBar().setSubtitle(isOnlineStatus);



               if(typingStatus.equals(messageSenderId)){

           getSupportActionBar().setSubtitle("typing...");

       }

  }

            @Override
           public void onCancelled(@NonNull DatabaseError error) {

      }

  });




        userMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(TextUtils.isEmpty(s)){

                    sendImage.setVisibility(View.VISIBLE);
                    captureImage.setVisibility(View.VISIBLE);

                    sendTextButton.setEnabled(false);

                } else {
                    sendImage.setVisibility(View.GONE);
                    captureImage.setVisibility(View.GONE);
                    sendTextButton.setEnabled(true);
                }

                if(s.toString().trim().length() == 0){

                        typingStatus("noOne");

                }else {

                    typingStatus(messageReceverID);

                }

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

        captureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TakePicture();
            }
        });


        captureVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CaptureVideo();
            }
        });
        closeViewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewImageLayout.setVisibility(View.GONE);
            }
        });

        sendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 438);
                }else{


                    Intent intent = new Intent();
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select Image"), 438);
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {


                messagePos = 0;


                FetchMoreMessage();

            }
        });

        FetchMessages();


    }



    @Override
    protected void onPause() {
        super.onPause();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
        final String saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm aa");
        final String saveCurrentTime = currentTime.format(calForTime.getTime());



        isOnlineStatus(saveCurrentDate + " " + saveCurrentTime);


    }

    @Override
    protected void onResume() {
        super.onResume();

        isOnlineStatus("online");

    }

    private void FetchMoreMessage(){

        messageRef =  rootRef.child("Messages").child(messageSenderId).child(messageReceverID);

        Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if(dataSnapshot.exists()){

                    if(!dataSnapshot.getKey().equals("FirstName") && !dataSnapshot.getKey().equals("LastName") && !dataSnapshot.getKey().equals("uid") ){
                        Messages messages = dataSnapshot.getValue(Messages.class);

                        String messageKey = dataSnapshot.getKey();

                        if(!mPrevKey.equals(messageKey)){

                            messagesList.add(messagePos++, messages);

                        }else {

                            mPrevKey = mLastKey;

                        }

                        if(messagePos == 1){

                            mLastKey = messageKey;

                        }

                        messagesAdapter.notifyDataSetChanged();

                        swipeRefreshLayout.setRefreshing(false);

                        linearLayoutManager.scrollToPositionWithOffset(10,0);

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

    private void FetchMessages() {

        messageRef =  rootRef.child("Messages").child(messageSenderId).child(messageReceverID);

        Query messageQuery = messageRef.limitToLast(TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {



                if(dataSnapshot.exists()){

                    if(!dataSnapshot.getKey().equals("FirstName") && !dataSnapshot.getKey().equals("LastName") && !dataSnapshot.getKey().equals("uid") ){


                    Messages messages = dataSnapshot.getValue(Messages.class);

                    messagePos++;

                    if(messagePos == 1){

                        mLastKey = dataSnapshot.getKey();
                        mPrevKey = dataSnapshot.getKey();

                    }

                    messagesList.add(messages);
                    messagesAdapter.notifyDataSetChanged();

                    userMessagesList.scrollToPosition(messagesList.size()-1);

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
        messageTextBody.put("isSeen", "Delivered");

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

    private void typingStatus(String typing){

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(messageSenderId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("typingTo", typing);
        dbRef.updateChildren(hashMap);

    }
    private void isOnlineStatus(String status){

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(messageSenderId);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("isOnlineStatus", status);
        dbRef.updateChildren(hashMap);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        class ImportTask extends AsyncTask<Void, Void, Uri> {

            private Uri uri;
            private File file;

            ImportTask( File file, Uri uri ){

                this.uri = uri;
                this.file = file;
            }

            @Override
            protected void onPreExecute() {

            }

            @Override
            protected Uri doInBackground(Void... voids ) {

                String video = "";
                try {
                  video =  SiliCompressor.with(getApplicationContext()).compressVideo(uri,file.getParent());
                  uri = Uri.fromFile(new File(video));
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
                return uri;
            }

            @Override
            protected void onPostExecute(Uri res ) {

                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(messageReceverID  + "/" + messageSenderId + "Media");

                final String messageText = userMessage.getText().toString();
                final String messageSenderRef =  messageReceverID  + "/" + messageSenderId ;
                final String messageReceverRef =  messageSenderId + "/" + messageReceverID ;
                final String addReceverName = "Messages/" + messageReceverID + "/" + messageSenderId + "/";
                final String addSenderName = "Messages/" + messageSenderId + "/"  + messageReceverID + "/";

                DatabaseReference userMessageKey = rootRef.child("Messages").child(messageSenderId).child(messageReceverID).push();

                final String messagePushID = userMessageKey.getKey();

                final StorageReference filePath = storageReference.child(messagePushID + "." + "mp4");

                uploadTask = filePath.putFile(res);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception {
                        if(!task.isSuccessful()){

                            throw task.getException();


                        }


                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if(task.isSuccessful()){

                            Uri downloadUrl = task.getResult();
                            imageUrl = downloadUrl.toString();

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



                            Map messageImageBody = new HashMap();

                            messageImageBody.put("Message", imageUrl);
                            messageImageBody.put("name", fileUri.getLastPathSegment());

                            messageImageBody.put("Time", saveCurrentTime);
                            messageImageBody.put("Date", saveCurrentDate);
                            messageImageBody.put("Type", "video");
                            messageImageBody.put("From", messageSenderId);

                            Map messageBodyDetails = new HashMap();

                            messageBodyDetails.put(messageReceverRef + "/" + messagePushID, messageImageBody);
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageImageBody);


                            rootRef.updateChildren(addName);

                            rootRef.child("Messages").updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {

                                    if(task.isSuccessful()){
                                        loadingBar.dismiss();


                                    }else {
                                        loadingBar.dismiss();

                                        String message = task.getException().getMessage();

                                        Toast.makeText(ChatActivity.this, message, Toast.LENGTH_LONG).show();

                                        userMessage.setText("");
                                    }
                                }
                            });


                        }

                    }
                });





            }
        }




        if(requestCode == VIDEO_REQUEST && resultCode == RESULT_OK){

            loadingBar.setTitle("Sending Video");
            loadingBar.show();

            fileUri= data.getData();

            File file = new File(getCacheDir()+"temp");

            new ImportTask(file ,fileUri ).execute();

        }


        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK) {

            loadingBar.setTitle("Sending Image");
            loadingBar.show();

            File file = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this, Uri.fromFile(new File(imageFile.getAbsolutePath()))), new File(this.getCacheDir(), "temp")));

            fileUri = Uri.fromFile(file);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(messageReceverID  + "/" + messageSenderId + "Media");

            final String messageText = userMessage.getText().toString();
            final String messageSenderRef =  messageReceverID  + "/" + messageSenderId ;
            final String messageReceverRef =  messageSenderId + "/" + messageReceverID ;
            final String addReceverName = "Messages/" + messageReceverID + "/" + messageSenderId + "/";
            final String addSenderName = "Messages/" + messageSenderId + "/"  + messageReceverID + "/";

            DatabaseReference userMessageKey = rootRef.child("Messages").child(messageSenderId).child(messageReceverID).push();

            final String messagePushID = userMessageKey.getKey();

            final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

            uploadTask = filePath.putFile(fileUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){

                        throw task.getException();


                    }


                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){

                        Uri downloadUrl = task.getResult();
                        imageUrl = downloadUrl.toString();

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



                        Map messageImageBody = new HashMap();

                        messageImageBody.put("Message", imageUrl);
                        messageImageBody.put("name", fileUri.getLastPathSegment());

                        messageImageBody.put("Time", saveCurrentTime);
                        messageImageBody.put("Date", saveCurrentDate);
                        messageImageBody.put("Type", "image");
                        messageImageBody.put("From", messageSenderId);

                        Map messageBodyDetails = new HashMap();

                        messageBodyDetails.put(messageReceverRef + "/" + messagePushID, messageImageBody);
                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageImageBody);


                        rootRef.updateChildren(addName);

                        rootRef.child("Messages").updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if(task.isSuccessful()){
                                    loadingBar.dismiss();


                                }else {
                                    loadingBar.dismiss();

                                    String message = task.getException().getMessage();

                                    Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();

                                    userMessage.setText("");
                                }
                            }
                        });


                    }

                }
            });


        }


        /** Send IMAGE from GALLERY**/
        if(requestCode==438 && resultCode==RESULT_OK && data!=null && data.getData() != null){


            loadingBar.setTitle("Sending Image");
            loadingBar.show();


            File file = new File(SiliCompressor.with(this).compress(FileUtils.getPath(this, data.getData()), new File(this.getCacheDir(), "temp")));

            fileUri = Uri.fromFile(file);

            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(messageReceverID  + "/" + messageSenderId + "Media");

            final String messageText = userMessage.getText().toString();
            final String messageSenderRef =  messageReceverID  + "/" + messageSenderId ;
            final String messageReceverRef =  messageSenderId + "/" + messageReceverID ;
            final String addReceverName = "Messages/" + messageReceverID + "/" + messageSenderId + "/";
            final String addSenderName = "Messages/" + messageSenderId + "/"  + messageReceverID + "/";

            DatabaseReference userMessageKey = rootRef.child("Messages").child(messageSenderId).child(messageReceverID).push();

            final String messagePushID = userMessageKey.getKey();

            final StorageReference filePath = storageReference.child(messagePushID + "." + "jpg");

            uploadTask = filePath.putFile(fileUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if(!task.isSuccessful()){

                        throw task.getException();


                    }


                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if(task.isSuccessful()){

                        Uri downloadUrl = task.getResult();
                        imageUrl = downloadUrl.toString();

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



                        Map messageImageBody = new HashMap();

                        messageImageBody.put("Message", imageUrl);
                        messageImageBody.put("name", fileUri.getLastPathSegment());

                        messageImageBody.put("Time", saveCurrentTime);
                        messageImageBody.put("Date", saveCurrentDate);
                        messageImageBody.put("Type", "image");
                        messageImageBody.put("From", messageSenderId);

                        Map messageBodyDetails = new HashMap();

                        messageBodyDetails.put(messageReceverRef + "/" + messagePushID, messageImageBody);
                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID, messageImageBody);


                        rootRef.updateChildren(addName);

                        rootRef.child("Messages").updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {

                                if(task.isSuccessful()){
                                    loadingBar.dismiss();


                                }else {
                                    loadingBar.dismiss();

                                    String message = task.getException().getMessage();

                                    Toast.makeText(ChatActivity.this, message, Toast.LENGTH_SHORT).show();

                                    userMessage.setText("");
                                }
                            }
                        });


                    }

                }
            });

        }

    }

    public void onBackPressed() {

        if(View.VISIBLE == viewImageLayout.getVisibility()){

            viewImageLayout.setVisibility(View.GONE);



        } else {

         Intent intent = new Intent(ChatActivity.this, MainActivity.class);
         startActivity(intent);

        }

        return;
    }

    private void CaptureVideo() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, VIDEO_REQUEST);
        } {
            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10);
            takeVideoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);

            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {

                try {

                    videoFile = createVideoFile();
                    displayMessage(getBaseContext(),videoFile.getAbsolutePath());
                    Log.i("imageFileAbsolutePath",videoFile.getAbsolutePath());

                    // Continue only if the File was successfully created
                    if (videoFile != null) {
                        videoUri = FileProvider.getUriForFile(this,
                                "com.main.beyond.TiO.provider",
                                videoFile);
                        takeVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
                        startActivityForResult(takeVideoIntent, VIDEO_REQUEST);
                    }
                } catch (Exception ex) {
                    // Error occurred while creating the File
                    displayMessage(getBaseContext(),ex.getMessage().toString());
                }


            }else
            {
                displayMessage(getBaseContext(),"Nullll");
            }
        }

    }
    public void TakePicture(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, REQUEST_IMAGE_CAPTURE);
        }

        else
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                try {

                    imageFile = createImageFile();
                    displayMessage(getBaseContext(),imageFile.getAbsolutePath());
                    Log.i("imageFileAbsolutePath",imageFile.getAbsolutePath());

                    // Continue only if the File was successfully created
                    if (imageFile != null) {
                        imageURI = FileProvider.getUriForFile(this,
                                "com.main.beyond.TiO.provider",
                                imageFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                } catch (Exception ex) {
                    // Error occurred while creating the File
                    displayMessage(getBaseContext(),ex.getMessage().toString());
                }


            }else
            {
                displayMessage(getBaseContext(),"Nullll");
            }
        }




    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private File createVideoFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String videoFileName = "TiO_Video"+timeStamp+"_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File video = File.createTempFile(
                videoFileName,  /* prefix */
                "mp4",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = video.getAbsolutePath();
        return video;
    }

    private void displayMessage(Context context, String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == 438) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                Intent intent = new Intent();
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent.createChooser(intent, "Select Image"), 438);

            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                TakePicture();
            }
        }else if (requestCode == VIDEO_REQUEST) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                CaptureVideo();
            }

        }

    }

    public String compressVideo(String videoFilePath, String destinationDir) {



    	return null;
    }






}

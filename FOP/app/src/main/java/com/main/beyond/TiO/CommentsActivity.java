package com.main.beyond.TiO;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;

import android.os.Bundle;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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

public class CommentsActivity extends AppCompatActivity {

    private ImageButton postsCommentButton;
    private EditText CommentsInput;
    private RecyclerView CommentsList;
    private TextView postContents;

private DatabaseReference userRef,postSeksualnaEksplatacijaRef,oPosterID;
    private FirebaseAuth firebaseAuth;

    private String postKey,postStringContent,currentUserId,postStringCategory,databaseUserId,opID,isAdmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        postKey = getIntent().getExtras().get("PostKey").toString();
        postStringContent = getIntent().getExtras().get("PostContent").toString();
        postStringCategory = getIntent().getExtras().get("PostCategory").toString();


        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        postSeksualnaEksplatacijaRef = FirebaseDatabase.getInstance().getReference().child("Posts").child(postStringCategory).child(postKey).child("Comments");


        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                isAdmin = dataSnapshot.child(currentUserId).child("EKSTRA").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        oPosterID = FirebaseDatabase.getInstance().getReference().child("Posts").child(postStringCategory).child(postKey);


        oPosterID.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                opID = dataSnapshot.child("uid").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        postContents = findViewById(R.id.postViewComments);
        CommentsList = findViewById(R.id.commentsList);
        CommentsInput = findViewById(R.id.commentInput);
        postsCommentButton = findViewById(R.id.commentsButton);

        postContents.setText(postStringContent);


        CommentsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        CommentsList.setLayoutManager(linearLayoutManager);


        PopulateComments();


        postsCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()){

                            String FirstName = dataSnapshot.child("FirstName").getValue().toString();
                            String LastName = dataSnapshot.child("LastName").getValue().toString();


                            ValidateComment(FirstName,LastName);

                           CommentsInput.setText("");

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void PopulateComments(){



        FirebaseRecyclerOptions<Comments> firebaseRecyclerOptions = new FirebaseRecyclerOptions.Builder<Comments>().setQuery(postSeksualnaEksplatacijaRef,Comments.class).build();

        FirebaseRecyclerAdapter<Comments, CommentsActivity.CommentsViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull CommentsViewHolder holder, int position, @NonNull Comments model) {


                final String commentKey = getRef(position).getKey();

                databaseUserId = model.getUid();


                holder.Username.setText(model.getFirstName() +" "+ model.getLastName() + " ");
                holder.Date.setText(model.getDate() + " ");
                holder.Time.setText(model.getTime());
                holder.Content.setText(model.getComment());


                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        DeleteSelectedComment(commentKey);


                    }
                });

            }

            @Override
            public CommentsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_comments_layout,parent,false);
                CommentsActivity.CommentsViewHolder commentsViewHolder = new CommentsActivity.CommentsViewHolder(view);
                return commentsViewHolder;
            }
        };


        CommentsList.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();



    }

    private void DeleteSelectedComment(final String commentKey) {




        AlertDialog.Builder builder = new AlertDialog.Builder(CommentsActivity.this);
        builder.setTitle("Избриши го коментарот");




        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(currentUserId.equals(databaseUserId) || currentUserId.equals(opID) || isAdmin.equals("Admin")){
                    postSeksualnaEksplatacijaRef.child(commentKey).removeValue();
                }
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

    public static class CommentsViewHolder extends RecyclerView.ViewHolder{

        TextView Username;
        TextView Date;
        TextView Time;
        TextView Content;

        public CommentsViewHolder(View itemView) {
            super(itemView);

            Username = itemView.findViewById(R.id.commentUserName);
            Date = itemView.findViewById(R.id.commentDate);
            Time = itemView.findViewById(R.id.commentTime);
            Content = itemView.findViewById(R.id.commentContent);


        }
    }

    private void ValidateComment(String firstName, String lastName) {

        String commentText = CommentsInput.getText().toString();

        if(TextUtils.isEmpty(commentText)){

            Toast.makeText(this, "Пишете Коментар", Toast.LENGTH_SHORT).show();

        }else {

            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMM-yyyy");
            final String saveCurrentDate = currentDate.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
            final String saveCurrentTime = currentTime.format(calForTime.getTime());

            final String commentRandomName = saveCurrentDate + saveCurrentTime;

            HashMap commentsMap = new HashMap();
            commentsMap.put("uid",currentUserId);
            commentsMap.put("Comment",commentText);
            commentsMap.put("Date",saveCurrentDate);
            commentsMap.put("Time",saveCurrentTime);
            commentsMap.put("FirstName",firstName);
            commentsMap.put("LastName",lastName);


            postSeksualnaEksplatacijaRef.child(firstName + lastName + currentUserId + commentRandomName).updateChildren(commentsMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {

                    if(!task.isSuccessful()){

                        Toast.makeText(CommentsActivity.this, "Има грешка", Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }

    }



}

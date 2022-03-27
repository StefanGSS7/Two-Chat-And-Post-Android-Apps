package com.main.beyond.TiO;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.input.InputManager;
import android.media.Image;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Trace;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.VideoView;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.perf.FirebasePerformance;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class MessagesAdapter extends RecyclerView.Adapter <MessagesAdapter.MessageViewHolder>{

    private List<Messages> userMessagesList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference rootRef;
    private ImageView viewImage;
    private Context context;
    private Activity chatActivity;
    private ConstraintLayout viewImageLayout;
    private VideoView viewVideo;




    public MessagesAdapter(Activity chatActivity, Context context, ImageView viewImage , List<Messages> userMessagesList, ConstraintLayout viewImageLayout, VideoView viewVideo){

        this.viewImageLayout = viewImageLayout;
        this.chatActivity = chatActivity;
        this.context = context;
        this.viewImage = viewImage;
        this.userMessagesList = userMessagesList;
        this.viewVideo = viewVideo;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText,receverMessageText,isSeenView;
        public ImageView senderMessageImage,receverMessageImage,receverProfilePicture,senderProfilePicture;
        public ImageView receverMessageVideo,senderMessageVideo,senderVideoPlay,receverVideoPlay;




        public MessageViewHolder(View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.senderMesageText);
            receverMessageText = itemView.findViewById(R.id.receverMesageText);
            senderMessageImage = itemView.findViewById(R.id.senderMesageImage);
            receverMessageImage = itemView.findViewById(R.id.receverMesageImage);
            senderProfilePicture = itemView.findViewById(R.id.senderProfilePicture);
            receverProfilePicture = itemView.findViewById(R.id.receverProfilePicture);
            receverMessageVideo = itemView.findViewById(R.id.receverMessageVideo);
            senderMessageVideo = itemView.findViewById(R.id.senderMessageVideo);
            senderVideoPlay = itemView.findViewById(R.id.senderVideoPlay);
            receverVideoPlay = itemView.findViewById(R.id.receverVideoPlay);





        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_of_user, parent, false);

        firebaseAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MessageViewHolder holder, final int position) {
        String messageSenderID = firebaseAuth.getCurrentUser().getUid();

        final Messages messages = userMessagesList.get(position);
        MediaMetadataRetriever mediaMetadataRetriever = null;

        String fromUserID = messages.getFrom();
        final String fromMessageType = messages.getType();
        String fromTime = messages.getTime();
        String fromDate = messages.getDate();
        final String isSeen = messages.getIsSeen();



        rootRef = FirebaseDatabase.getInstance().getReference();

        rootRef.child("Users").child(fromUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final String receverPictureUrl = "" + snapshot.child("profilePicture").getValue();

                Picasso.get().load(receverPictureUrl).transform(new CropCircleTransformation()).into(holder.receverProfilePicture);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rootRef.child("Users").child(messageSenderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final String receverPictureUrl = "" + snapshot.child("profilePicture").getValue();

                Picasso.get().load(receverPictureUrl).transform(new CropCircleTransformation()).into(holder.senderProfilePicture);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(fromMessageType.equals("text")){

            holder.receverMessageText.setVisibility(View.INVISIBLE);
            if(fromUserID.equals(messageSenderID)){
                holder.senderMessageImage.setVisibility(View.GONE);
                holder.receverMessageImage.setVisibility(View.GONE);
                holder.senderMessageVideo.setVisibility(View.GONE);
                holder.receverMessageVideo.setVisibility(View.GONE);
                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setGravity(Gravity.LEFT);
                holder.senderMessageText.setText(messages.getMessage());
                holder.senderProfilePicture.setVisibility(View.GONE);
                holder.receverProfilePicture.setVisibility(View.GONE);

                 if(position == userMessagesList.size()-1){


                     holder.senderProfilePicture.setVisibility(View.VISIBLE);

                 }

            }else {

                holder.senderMessageText.setVisibility(View.INVISIBLE);
                holder.senderMessageImage.setVisibility(View.GONE);
                holder.receverMessageImage.setVisibility(View.GONE);
                holder.senderMessageVideo.setVisibility(View.GONE);
                holder.receverMessageVideo.setVisibility(View.GONE);
                holder.receverMessageText.setVisibility(View.VISIBLE);
                holder.receverMessageText.setBackgroundResource(R.drawable.recever_message_text_background);
                holder.receverMessageText.setTextColor(Color.WHITE);
                holder.receverMessageText.setGravity(Gravity.RIGHT);
                holder.receverMessageText.setText(messages.getMessage());
                holder.senderProfilePicture.setVisibility(View.GONE);
                holder.receverProfilePicture.setVisibility(View.GONE);

                if(position == userMessagesList.size()-1){

                    holder.receverProfilePicture.setVisibility(View.VISIBLE);

                }

            }

        }else if(fromMessageType.equals("image")){

            holder.receverMessageImage.setVisibility(View.INVISIBLE);
            if(fromUserID.equals(messageSenderID)){
                holder.receverMessageText.setVisibility(View.GONE);
                holder.senderMessageText.setVisibility(View.GONE);
                holder.senderMessageVideo.setVisibility(View.GONE);
                holder.receverMessageVideo.setVisibility(View.GONE);
                holder.senderProfilePicture.setVisibility(View.VISIBLE);
                holder.receverProfilePicture.setVisibility(View.GONE);
                holder.senderMessageImage.setVisibility(View.VISIBLE);
                holder.senderMessageImage.setBackgroundResource(R.drawable.sender_message_text_background);

                holder.senderMessageImage.setBackgroundColor(Color.TRANSPARENT);
                Picasso.get().load(messages.getMessage()).placeholder(R.drawable.gifproba).into(holder.senderMessageImage);


            }else {

                holder.receverMessageText.setVisibility(View.GONE);
                holder.senderMessageText.setVisibility(View.GONE);
                holder.senderMessageVideo.setVisibility(View.GONE);
                holder.receverMessageVideo.setVisibility(View.GONE);
                holder.senderProfilePicture.setVisibility(View.GONE);
                holder.receverProfilePicture.setVisibility(View.VISIBLE);
                holder.senderMessageImage.setVisibility(View.INVISIBLE);

                holder.receverMessageImage.setVisibility(View.VISIBLE);
                holder.receverMessageImage.setBackgroundResource(R.drawable.recever_message_text_background);



                holder.receverMessageImage.setBackgroundColor(Color.TRANSPARENT);

                Picasso.get().load(messages.getMessage()).placeholder(R.drawable.gifproba).into(holder.receverMessageImage);

            }

        } else if(fromMessageType.equals("video")){

            holder.receverMessageVideo.setVisibility(View.INVISIBLE);
            if(fromUserID.equals(messageSenderID)){
                holder.receverMessageText.setVisibility(View.GONE);
                holder.senderMessageText.setVisibility(View.GONE);
                holder.senderMessageImage.setVisibility(View.GONE);
                holder.receverMessageImage.setVisibility(View.GONE);
                holder.senderProfilePicture.setVisibility(View.VISIBLE);
                holder.receverProfilePicture.setVisibility(View.GONE);
                holder.senderMessageVideo.setVisibility(View.VISIBLE);
                holder.receverVideoPlay.setVisibility(View.INVISIBLE);


                holder.senderMessageVideo.setImageResource(R.drawable.playbutton);
                GlideApp.with(context).load(messages.getMessage()).error(R.drawable.playbutton).into(holder.senderMessageVideo);



            }else {

                holder.receverMessageText.setVisibility(View.GONE);
                holder.senderMessageText.setVisibility(View.GONE);
                holder.senderMessageImage.setVisibility(View.GONE);
                holder.receverMessageImage.setVisibility(View.GONE);
                holder.senderProfilePicture.setVisibility(View.GONE);
                holder.receverProfilePicture.setVisibility(View.VISIBLE);
                holder.senderMessageVideo.setVisibility(View.INVISIBLE);
                holder.receverMessageVideo.setVisibility(View.VISIBLE);
                holder.receverMessageVideo.setBackgroundResource(R.drawable.recever_message_text_background);
                holder.senderVideoPlay.setVisibility(View.INVISIBLE);
                holder.receverMessageVideo.setBackgroundColor(Color.TRANSPARENT);


                GlideApp.with(context).load(messages.getMessage()).error(R.drawable.playbutton).into(holder.receverMessageVideo);
                holder.receverMessageVideo.setImageResource(R.drawable.playbutton);

              


            }

        }


        holder.senderMessageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)chatActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

                View focusedView = chatActivity.getCurrentFocus();
                if(focusedView != null){

                    inputManager.hideSoftInputFromWindow(chatActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }



                viewImageLayout.setVisibility(View.VISIBLE);
                viewImage.setImageDrawable(holder.senderMessageImage.getDrawable());
                viewImage.setOnTouchListener(new ImageMatrixTouchHandler(view.getContext()));



            }
        });

        holder.receverMessageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)chatActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

                View focusedView = chatActivity.getCurrentFocus();
                if(focusedView != null){

                    inputManager.hideSoftInputFromWindow(chatActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }






                viewImageLayout.setVisibility(View.VISIBLE);
                viewImage.setImageDrawable(holder.receverMessageImage.getDrawable());
                viewImage.setOnTouchListener(new ImageMatrixTouchHandler(view.getContext()));






            }
        });


        holder.senderMessageVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)chatActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

                View focusedView = chatActivity.getCurrentFocus();
                if(focusedView != null){

                    inputManager.hideSoftInputFromWindow(chatActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }



                viewImageLayout.setVisibility(View.VISIBLE);
                viewImage.setVisibility(View.INVISIBLE);
                viewVideo.setVideoPath(messages.getMessage());

                viewVideo.start();



            }
        });

        holder.receverMessageVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InputMethodManager inputManager = (InputMethodManager)chatActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

                View focusedView = chatActivity.getCurrentFocus();
                if(focusedView != null){

                    inputManager.hideSoftInputFromWindow(chatActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }






                viewImageLayout.setVisibility(View.VISIBLE);
                viewImage.setVisibility(View.INVISIBLE);
                viewVideo.setVideoPath(messages.getMessage());
                viewVideo.start();







            }
        });


    }



    @Override
    public int getItemCount() {

        return userMessagesList.size();
    }


}

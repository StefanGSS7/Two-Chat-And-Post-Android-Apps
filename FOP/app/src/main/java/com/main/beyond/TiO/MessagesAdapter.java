package com.main.beyond.TiO;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter <MessagesAdapter.MessageViewHolder>{

    private List<Messages> userMessagesList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference usersDatabaseRef;

    public MessagesAdapter(List<Messages> userMessagesList){

        this.userMessagesList = userMessagesList;

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText,receverMessageText;


        public MessageViewHolder(View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.senderMesageText);
            receverMessageText = itemView.findViewById(R.id.receverMesageText);
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_layout_of_user, parent, false);

        firebaseAuth = FirebaseAuth.getInstance();

        return new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        String messageSenderID = firebaseAuth.getCurrentUser().getUid();

        Messages messages = userMessagesList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();
        String fromTime = messages.getTime();
        String fromDate = messages.getDate();


        if(fromMessageType.equals("text")){


            holder.receverMessageText.setVisibility(View.INVISIBLE);
            if(fromUserID.equals(messageSenderID)){

                holder.senderMessageText.setVisibility(View.VISIBLE);
                holder.senderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                holder.senderMessageText.setTextColor(Color.BLACK);
                holder.senderMessageText.setGravity(Gravity.LEFT);
                holder.senderMessageText.setText(messages.getMessage());

            }else {

                holder.senderMessageText.setVisibility(View.INVISIBLE);

                holder.receverMessageText.setVisibility(View.VISIBLE);
                holder.receverMessageText.setBackgroundResource(R.drawable.recever_message_text_background);
                holder.receverMessageText.setTextColor(Color.WHITE);
                holder.receverMessageText.setGravity(Gravity.RIGHT);
                holder.receverMessageText.setText(messages.getMessage());

            }

        }


    }

    @Override
    public int getItemCount() {

        return userMessagesList.size();
    }
}

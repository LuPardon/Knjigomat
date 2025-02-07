package com.example.knjigomat.chat;

import android.content.Context;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knjigomat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class screen5RVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Message> messageList;
    FirebaseAuth mAuth;
    String receiverDP;
    DatabaseReference reference;

    public screen5RVAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Messages");
        reference.keepSynced(true);
    }

    public String getReceiverDP() {
        return receiverDP;
    }

    public void setReceiverDP(String receiverDP) {
        this.receiverDP = receiverDP;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        if (viewType == R.layout.send_message_row) {
            itemView = LayoutInflater.from(context).inflate(R.layout.send_message_row, null, false);
            return new screen5SendMessageViewHolder(itemView);
        } else if (viewType == R.layout.receive_message_row) {
            itemView = LayoutInflater.from(context).inflate(R.layout.receive_message_row, null, false);
            return new screen5ReceiveMessageViewHolder(itemView);
        } else if (viewType == R.layout.send_image_row) {
            itemView = LayoutInflater.from(context).inflate(R.layout.send_image_row, null, false);
            return new screen5SendImageViewHolder(itemView);
        } else {
            itemView = LayoutInflater.from(context).inflate(R.layout.receive_image_row, null, false);
            return new screen5ReceiveImageViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof screen5SendMessageViewHolder) {
            ((screen5SendMessageViewHolder) holder).message.setText(messageList.get(position).getTekst());
            ((screen5SendMessageViewHolder) holder).time.setText(messageList.get(position).getTimestamp() + "");
            ((screen5SendMessageViewHolder) holder).relativeLayout.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Edit Message");

                // Set up the input
                final EditText input = new EditText(context);
                input.setText(((screen5SendMessageViewHolder) holder).message.getText().toString());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("Delete", (dialog, which) -> reference.child(messageList.get(holder.getAdapterPosition()).getKey()).removeValue());
                builder.setNegativeButton("Save", (dialog, which) -> {
                    String text = input.getText().toString();
                    reference.child(messageList.get(holder.getAdapterPosition()).getKey()).child("message").setValue(text);
                });
                builder.setNeutralButton("Cancel", (dialog, which) -> dialog.cancel());

                builder.show();
                return true;
            });
        }
//        else if (holder instanceof screen5ReceiveMessageViewHolder) {
//            ((screen5ReceiveMessageViewHolder)holder).message.setText(messageList.get(position).getMessage());
//            ((screen5ReceiveMessageViewHolder)holder).time.setText(messageList.get(position).getTime());
//            Picasso.get().load(receiverDP).into(((screen5ReceiveMessageViewHolder)holder).dp);
//        }
//        else if (holder instanceof screen5SendImageViewHolder) {
//            ((screen5SendImageViewHolder)holder).time.setText(messageList.get(position).getTime());
//            ((screen5SendImageViewHolder)holder).location.setText(messageList.get(position).getLocation());
////            Picasso.get().load(messageList.get(position).getImage()).into(((screen5SendImageViewHolder)holder).image);
//        }
//        else if (holder instanceof screen5ReceiveImageViewHolder) {
//            ((screen5ReceiveImageViewHolder)holder).time.setText(messageList.get(position).getTime());
//            ((screen5ReceiveImageViewHolder)holder).location.setText(messageList.get(position).getLocation());
////            Picasso.get().load(messageList.get(position).getImage()).into(((screen5ReceiveImageViewHolder)holder).image);
//            Picasso.get().load(receiverDP).into(((screen5ReceiveImageViewHolder)holder).dp);
//        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderId().equals(mAuth.getUid())) { // Ako je korisnik poslao poruku
//            if (message.getImage() == null) {
            return R.layout.send_message_row; // Tekstualna poruka poslana
//            } else {
//                return R.layout.send_image_row; // Poslana slika
//            }
        } else { // Ako je poruku poslao drugi korisnik
//            if (message.getImage() == null) {
            return R.layout.receive_message_row; // Tekstualna poruka primljena
//            } else {
//                return R.layout.receive_image_row; // Primljena slika
//            }
        }

    }

//    public void addMessage(String chatID, Message newMessage) {
//        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Chats");
//
//        db.child(chatID).child("Messages").push().setValue(newMessage);
//        notifyDataSetChanged();
//    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class screen5SendMessageViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        TextView message, time;

        public screen5SendMessageViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.rl_send_message);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
        }
    }

    class screen5SendImageViewHolder extends RecyclerView.ViewHolder {
        TextView time, location;
        RelativeLayout relativeLayout;
        ImageView image;

        public screen5SendImageViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.location);
            relativeLayout = itemView.findViewById(R.id.rl_send_image);
            image = itemView.findViewById(R.id.image);
        }
    }

    class screen5ReceiveMessageViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        TextView message, time;
        ImageView dp;

        public screen5ReceiveMessageViewHolder(View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.rl_receive_message);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            dp = itemView.findViewById(R.id.dp);
        }
    }

    class screen5ReceiveImageViewHolder extends RecyclerView.ViewHolder {
        TextView time, location;
        RelativeLayout relativeLayout;
        ImageView image;
        ImageView dp;

        public screen5ReceiveImageViewHolder(View itemView) {
            super(itemView);
            time = itemView.findViewById(R.id.time);
            location = itemView.findViewById(R.id.location);
            relativeLayout = itemView.findViewById(R.id.rl_receive_image);
            image = itemView.findViewById(R.id.image);
            dp = itemView.findViewById(R.id.dp);
        }
    }
}
package com.example.knjigomat.chat;

import android.content.Context;
import android.text.InputType;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.knjigomat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    List<Message> messageList;
    FirebaseAuth mAuth;
    String receiverDP;
    DatabaseReference reference;


    public ConversationAdapter(Context context, List<Message> messageList, String chatID) {
        this.context = context;
        this.messageList = messageList;
        mAuth = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference("Chats").child(chatID).child("Messages");
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
            ((screen5SendMessageViewHolder) holder).time.setText(Instant.ofEpochSecond(messageList.get(position).getTimestamp()).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            ((screen5SendMessageViewHolder) holder).relativeLayout.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getResources().getString(R.string.obrisi_poruku));

                // Set up the input
                final EditText input = new EditText(context);
                input.setText(((screen5SendMessageViewHolder) holder).message.getText().toString());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
//                String nesto = messageList.get(holder.getAdapterPosition()).getKey();
                builder.setPositiveButton(context.getResources().getString(R.string.obrisi), (dialog, which) -> {
                    reference.child(messageList.get(holder.getAdapterPosition()).getKey()).removeValue();
                    messageList.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();
                });
//                builder.setNegativeButton(context.getResources().getString(R.string.spremi), (dialog, which) -> {
//                    String text = input.getText().toString();
//                    reference.child(messageList.get(holder.getAdapterPosition()).getKey()).child("message").setValue(text);
//                });
                builder.setNeutralButton(context.getResources().getString(R.string.odustani_update), (dialog, which) -> dialog.cancel());

                builder.show();
                return true;
            });
        } else if (holder instanceof screen5ReceiveMessageViewHolder) {
            ((screen5ReceiveMessageViewHolder) holder).message.setText(messageList.get(position).getTekst());
            ((screen5ReceiveMessageViewHolder) holder).time.setText(Instant.ofEpochSecond(messageList.get(position).getTimestamp()).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
//            Picasso.get().load(receiverDP).into(((screen5ReceiveMessageViewHolder)holder).dp);
        } else if (holder instanceof screen5SendImageViewHolder) {
            ((screen5SendImageViewHolder) holder).time.setText(Instant.ofEpochSecond(messageList.get(position).getTimestamp()).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            byte[] imageByteArray = Base64.decode(messageList.get(position).getTekst(), Base64.DEFAULT);

            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(imageByteArray).into(((screen5SendImageViewHolder) holder).image);

            ((screen5SendImageViewHolder) holder).relativeLayout.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getResources().getString(R.string.obrisi));

                // Set up the buttons
//                String nesto = messageList.get(holder.getAdapterPosition()).getKey();
                builder.setPositiveButton(context.getResources().getString(R.string.obrisi), (dialog, which) -> {
                    reference.child(messageList.get(holder.getAdapterPosition()).getKey()).removeValue();
                    messageList.remove(holder.getAdapterPosition());
                    notifyDataSetChanged();
                });
                builder.setNeutralButton(context.getResources().getString(R.string.odustani_update), (dialog, which) -> dialog.cancel());

                builder.show();
                return true;
            });
        } else if (holder instanceof screen5ReceiveImageViewHolder) {
            ((screen5ReceiveImageViewHolder) holder).time.setText(Instant.ofEpochSecond(messageList.get(position).getTimestamp()).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            byte[] imageByteArray = Base64.decode(messageList.get(position).getTekst(), Base64.DEFAULT);

            Glide.with(context.getApplicationContext()).asBitmap()
                    .load(imageByteArray).into(((screen5ReceiveImageViewHolder) holder).image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.getSenderId().equals(mAuth.getUid())) { // Ako je korisnik poslao poruku
            if (message.isImage()) {
                return R.layout.send_image_row; // Poslana slika
            } else {
                return R.layout.send_message_row; // Tekstualna poruka poslana
            }
        } else { // Ako je poruku poslao drugi korisnik
            if (message.isImage()) {
                return R.layout.receive_image_row; // Primljena slika
            } else {
                return R.layout.receive_message_row; // Tekstualna poruka primljena
            }
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
            relativeLayout = itemView.findViewById(R.id.rl_receive_image);
            image = itemView.findViewById(R.id.image);
            dp = itemView.findViewById(R.id.dp);
        }
    }
}
package com.example.knjigomat.chat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.knjigomat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.screen4ViewHolder> implements Filterable {
    private Context context;
    private List<Chat> chatList;
    private List<Chat> filteredList;
    private ContactsFragment fragment;

    private Map<String, String> userNamesMap;

    public ContactsAdapter(Context context, List<Chat> chatList, ContactsFragment fragment) {
        this.context = context;
        this.chatList = new ArrayList<>(chatList);  // Avoid referencing the same list
        this.filteredList = new ArrayList<>(chatList);
        this.fragment = fragment;
        userNamesMap = new HashMap<>();
    }

    @Override
    public ContactsAdapter.screen4ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.chat_row, parent, false);
        return new screen4ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.screen4ViewHolder holder, int position) {
        Chat chatItem = filteredList.get(position);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        List<String> participants = chatItem.getParticipants();

        for (String participant : participants) {
            if (!participant.equals(currentUserId)) {
                final String receiverId = participant;

                // 🔹 Dohvati ime i prezime korisnika iz Firestore-a
                FirebaseFirestore.getInstance().collection("users")
                        .document(receiverId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String ime = documentSnapshot.getString("ime");
                                String prezime = documentSnapshot.getString("prezime");
                                holder.name.setText(ime + " " + prezime);
                                userNamesMap.put(receiverId, ime + " " + prezime);
                            } else {
                                holder.name.setText(context.getResources().getString(R.string.nepoznat_korisnik));
                            }
                        })
                        .addOnFailureListener(e -> {
                            holder.name.setText(context.getResources().getString(R.string.greska_dohvacanje));
                        });

                // 🔹 Postavi klik event za Chat
                holder.rl_chat.setOnClickListener(view -> {
                    Intent intent = new Intent(context, ConversationActivity.class);
                    intent.putExtra("name", holder.name.getText().toString());
                    intent.putExtra("receiverID", receiverId);
                    context.startActivity(intent);
                });

                Glide.with(context).load(R.drawable.icon_account)
                        .into(holder.dp);

                String timeSent = Instant.ofEpochSecond(chatItem.getMessages().get(chatItem.getMessages().size() - 1).getTimestamp()).atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                holder.time.setText(timeSent);

                if (chatItem.getMessages().get(chatItem.getMessages().size() - 1).isImage()) {
                    holder.message.setText(context.getResources().getString(R.string.poslana_slika));
                } else {
                    String lastMessage = chatItem.getMessages().get(chatItem.getMessages().size() - 1).getTekst();
                    if (lastMessage.length() >= 10) {
                        holder.message.setText(lastMessage.substring(0, 15) + " ...");
                    } else {
                        holder.message.setText(" ...");
                    }
                }
                break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String key = constraint.toString().toLowerCase().trim();
                List<Chat> filteredResults = new ArrayList<>();

                if (key.isEmpty()) {
                    filteredResults.addAll(chatList);
                } else {
                    for (Chat row : chatList) {
                        for (String participant : row.getParticipants()) {
                            if (!participant.equals(currentUserId) && userNamesMap.containsKey(participant)) {
                                String fullName = userNamesMap.get(participant);
                                if (fullName.toLowerCase().contains(key)) {
                                    filteredResults.add(row);
                                    break; // Ako se pronađe podudaranje, nema potrebe provjeravati dalje
                                }
                            }
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredResults;
                filterResults.count = filteredResults.size();
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList.clear();
                if (results.values != null) {
                    filteredList.addAll((List<Chat>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }


    public void updateChatList(List<Chat> newChats) {
        chatList.clear();
        chatList.addAll(newChats);
        filteredList.clear();
        filteredList.addAll(newChats);
        notifyDataSetChanged();
    }

    public class screen4ViewHolder extends RecyclerView.ViewHolder {
        TextView name, message, time;
        RelativeLayout rl_chat;
        ImageView online_status, read_status, dp;

        public screen4ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
            time = itemView.findViewById(R.id.time);
            rl_chat = itemView.findViewById(R.id.rl_chat);
//            online_status = itemView.findViewById(R.id.online_status);
//            read_status = itemView.findViewById(R.id.read_status);
            dp = itemView.findViewById(R.id.dp);
        }
    }
}

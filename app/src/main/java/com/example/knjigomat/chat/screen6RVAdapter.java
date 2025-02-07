package com.example.knjigomat.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.knjigomat.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class screen6RVAdapter extends RecyclerView.Adapter<screen6RVAdapter.screen6ViewHolder> {
    Context context;
    List<contact> contactList;
    FirebaseDatabase database;
    DatabaseReference myRef;
    List<Account> accounts;
    fragment_screen6 fragment;

    public screen6RVAdapter(Context context, List<contact> contactList, fragment_screen6 fragment) {
        this.context = context;
        this.contactList = contactList;
        this.fragment = fragment;
        database = FirebaseDatabase.getInstance();
        accounts = new ArrayList<>();
        myRef = database.getReference("Accounts");
        myRef.keepSynced(true);

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                accounts.add(snapshot.getValue(Account.class));
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {

            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    @Override
    public screen6ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.contact_row, null, false);
        return new screen6ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(screen6ViewHolder holder, int position) {
        holder.name.setText(contactList.get(position).getName());
        holder.number.setText(contactList.get(position).getNumber());
        Picasso.get().load(contactList.get(position).getDp()).into(holder.dp);

        holder.rl_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < accounts.size(); ++i) {
                    if (accounts.get(i).getPhoneNumber().equals(contactList.get(holder.getAdapterPosition()).getNumber())) {
//                            Intent intent = new Intent(context, screen5.class);
//                            intent.putExtra("name", accounts.get(i).getFirstName() + " " + accounts.get(i).getLastName());
//                            intent.putExtra("receiverID", accounts.get(i).getID());
//                            fragment.applicationNotMinimized();
//                            context.startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class screen6ViewHolder extends RecyclerView.ViewHolder {
        TextView name, number;
        RelativeLayout rl_contact;
        ImageView dp;

        public screen6ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            number = itemView.findViewById(R.id.number);
            rl_contact = itemView.findViewById(R.id.rl_contact);
            dp = itemView.findViewById(R.id.dp);
        }
    }
}

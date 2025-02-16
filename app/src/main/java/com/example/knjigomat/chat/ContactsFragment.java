package com.example.knjigomat.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knjigomat.R;
import com.example.knjigomat.ui.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ContactsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ContactsAdapter rvAdapter;
    private List<Chat> chatList;
    private DatabaseReference chatsRef;
    private FirebaseAuth mAuth;
    private EditText searchEditText;
    private String currentUserId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        // Firebase authentication
        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        // Firebase database references
        chatsRef = FirebaseDatabase.getInstance().getReference("Chats");
        chatsRef.keepSynced(true);

        // Initialize views
        recyclerView = view.findViewById(R.id.rv_chats);
        searchEditText = view.findViewById(R.id.search_edit_text);

        // Initialize RecyclerView
        chatList = new ArrayList<>();
        rvAdapter = new ContactsAdapter(getActivity(), chatList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(rvAdapter);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(70));

        // Load Chat data
        loadUserChats();

        // Search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rvAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        view.findViewById(R.id.back_button).setOnClickListener(v -> {
//            getActivity().onBackPressed();
            Intent k = new Intent(this.getActivity(), MainActivity.class);
            startActivity(k);
        });

        return view;
    }

    private void loadUserChats() {
        chatsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                if (snapshot.child("Participants").hasChild(currentUserId)) {
                    List<String> participants = new ArrayList<>();
                    for (DataSnapshot participantSnapshot : snapshot.child("Participants").getChildren()) {
                        participants.add(participantSnapshot.getKey());
                    }

                    List<Message> msgList = new ArrayList<>();

                    for (DataSnapshot messageSnapshot : snapshot.child("Messages").getChildren()) {
                        msgList.add(messageSnapshot.getValue(Message.class));
                    }
                    msgList.sort((o1, o2) -> (int) (Math.max(o1.getTimestamp(), o2.getTimestamp())));

                    Chat cht = new Chat(snapshot.getKey(), msgList, participants);
                    chatList.add(cht);
                    rvAdapter.updateChatList(chatList);
                }
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
}
package com.example.knjigomat.chat;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class fragment_screen4 extends Fragment {
    RecyclerView recyclerView;
    screen4RVAdapter rvAdapter;
    List<chat> chatList;
    //    ImageView newMessage;
    DatabaseReference myRef, myRef1;
    FirebaseAuth mAuth;
    List<Account> accounts;
    EditText searchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen4, container, false);

        recyclerView = view.findViewById(R.id.rv_chats);
//        newMessage = view.findViewById(R.id.new_message);
        searchEditText = view.findViewById(R.id.search_edit_text);
        myRef = FirebaseDatabase.getInstance().getReference("Messages");
        myRef.keepSynced(true);
        myRef1 = FirebaseDatabase.getInstance().getReference("Accounts");
        myRef1.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        accounts = new ArrayList<>();

        chatList = new ArrayList<>();

        myRef1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                accounts.add(snapshot.getValue(Account.class));
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
                Account account = snapshot.getValue(Account.class);
                for (int i = 0; i < accounts.size(); ++i) {
                    if (accounts.get(i).getID().equals(account.getID())) {
                        accounts.set(i, account);
                        break;
                    }
                }
                for (int i = 0; i < chatList.size(); ++i) {
                    if (chatList.get(i).getId().equals(account.getID())) {
//                        chatList.get(i).setState(account.getState());
//                        chatList.get(i).setLastSeenTime(account.getLastSeenTime());
//                        chatList.get(i).setLastSeenDate(account.getLastSeenDate());
                        rvAdapter.notifyDataSetChanged();
                        break;
                    }
                }
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

//        myRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded( DataSnapshot snapshot, String previousChildName) {
//                Message msg = snapshot.getValue(message.class);
//                if (msg.getSenderID().equals(mAuth.getUid()) || msg.getReceiverID().equals(mAuth.getUid())) {
//                    String tempMsg = msg.getMessage();
//                    if (tempMsg.length() > 26) {
//                        tempMsg = tempMsg.substring(0, 26) + "...";
//                    } else if (tempMsg.equals("")) {
//                        if (msg.getSenderID().equals(mAuth.getUid())) {
//                            tempMsg = "You sent a photo";
//                        } else if (msg.getReceiverID().equals(mAuth.getUid())) {
//                            tempMsg = "User sent a photo";
//                        }
//                    }
//                    boolean updated = false;
//                    for (int i = 0; i < chatList.size(); ++i) {
//                        if (chatList.get(i).getId().equals(msg.getSenderID()) ||
//                                chatList.get(i).getId().equals(msg.getReceiverID())) {
//                            chatList.get(i).setMessage(tempMsg);
//                            chatList.get(i).setTime(msg.getTime());
//                            updated = true;
//                        }
//                    }
//                    if (!updated) {
//                        if (msg.getSenderID().equals(mAuth.getUid())) {
//                            String name = "", dp = "", state = "", lastSeenTime = "", lastSeenDate = "";
//                            for (int i = 0; i < accounts.size(); ++i) {
//                                if (accounts.get(i).getID().equals(msg.getReceiverID())) {
//                                    name = accounts.get(i).getFirstName() + " " + accounts.get(i).getLastName();
//                                    dp = accounts.get(i).getDp();
//                                    state = accounts.get(i).getState();
//                                    lastSeenTime = accounts.get(i).getLastSeenTime();
//                                    lastSeenDate = accounts.get(i).getLastSeenDate();
//                                }
//                            }
//                            chatList.add(new chat(msg.getReceiverID(), name, tempMsg, msg.getTime(),
//                                    true, dp, state, lastSeenTime, lastSeenDate));
//                        } else if (msg.getReceiverID().equals(mAuth.getUid())) {
//                            String name = "", dp = "", state = "", lastSeenTime = "", lastSeenDate = "";
//                            for (int i = 0; i < accounts.size(); ++i) {
//                                if (accounts.get(i).getID().equals(msg.getSenderID())) {
//                                    name = accounts.get(i).getFirstName() + " " + accounts.get(i).getLastName();
//                                    dp = accounts.get(i).getDp();
//                                    state = accounts.get(i).getState();
//                                    lastSeenTime = accounts.get(i).getLastSeenTime();
//                                    lastSeenDate = accounts.get(i).getLastSeenDate();
//                                }
//                            }
//                            chatList.add(new chat(msg.getSenderID(), name, tempMsg, msg.getTime(),
//                                    true, dp, state, lastSeenTime, lastSeenDate));
//                        }
//                    }
//                    rvAdapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved( DataSnapshot snapshot, String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//
//            }
//        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        rvAdapter = new screen4RVAdapter(getActivity(), chatList, fragment_screen4.this);
        recyclerView.setAdapter(rvAdapter);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(70));

//        newMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((MainActivity) v.getContext()).getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.main_fragment_container, ReadBookFragment.class, null)
////                .addToBackStack(null)
//                        .commit();
////                ((FragmentManager) getActivity()).changeViewPager(1);
//            }
//        });

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

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((fragmentsContainer) getActivity()).changeImageColorToBlue(0);
    }

    public void applicationNotMinimized() {
//        ((fragmentsContainer) getActivity()).minimized = false;
    }
}
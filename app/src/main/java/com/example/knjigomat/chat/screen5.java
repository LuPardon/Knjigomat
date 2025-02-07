package com.example.knjigomat.chat;

// Uvoz potrebnih Android i Firebase klasa

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knjigomat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class screen5 extends ScreenshotDetectionActivity {
    // Definicija UI elemenata
    ImageView backButton, cameraImage, sendMessage;
    RecyclerView recyclerView;
    screen5RVAdapter adapter;
    List<Message> messageList;
    TextView name, onlineStatus;
    EditText message;
    FirebaseDatabase database;
    DatabaseReference myRef, myRef1;
    String receiverID;
    FirebaseAuth mAuth;
    Account receiverAccount;
    boolean minimized = true;
    String chatID = "", currentUserID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen5);

        // Inicijalizacija UI elemenata
        backButton = findViewById(R.id.back_button);
        sendMessage = findViewById(R.id.send_icon);
        recyclerView = findViewById(R.id.rv_messages);
        name = findViewById(R.id.name);
        onlineStatus = findViewById(R.id.online_status);
        message = findViewById(R.id.message);
        cameraImage = findViewById(R.id.camera_image);

        // Inicijalizacija Firebase baze podataka
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Chats");
        myRef.keepSynced(true);
        myRef1 = database.getReference("Accounts");
        myRef1.keepSynced(true);

        // Inicijalizacija Firebase autentifikacije
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        // Inicijalizacija liste poruka i adaptera za RecyclerView
        messageList = new ArrayList<>();
        adapter = new screen5RVAdapter(screen5.this, messageList);

        // Dobivanje podataka iz Intent-a
        Intent intent = getIntent();
        name.setText(intent.getStringExtra("nameReceiver"));
        receiverID = intent.getStringExtra("receiverID");

        // 🚨 PROVJERA: Spriječi korisnika da započne chat sa sobom
        if (currentUserID.equals(receiverID)) {
            Toast.makeText(this, "Ne možete započeti razgovor sami sa sobom!", Toast.LENGTH_SHORT).show();
            finish(); // Zatvara aktivnost
            return;
        }

        // Ako korisnik nije sam sa sobom, nastavi s provjerom chata
        checkIfChatExists();

        // Praćenje promjena u Firebase bazi podataka (Accounts) // ovo nemam u bazi
//        myRef1.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
//                Account firebaseAccount = snapshot.getValue(Account.class);
//                if (firebaseAccount.getID().equals(receiverID)) {
//                    receiverAccount = firebaseAccount;
//                    adapter.setReceiverDP(firebaseAccount.getDp());
//
//                    // Postavljanje online statusa primatelja
//                    if (receiverAccount.getState().equals("online")) {
//                        onlineStatus.setText("Online now");
//                    } else {
//                        onlineStatus.setText("Last seen on " + receiverAccount.getLastSeenDate() + " " + receiverAccount.getLastSeenTime());
//                    }
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot snapshot, String previousChildName) {
//                Account firebaseAccount = snapshot.getValue(Account.class);
//                if (firebaseAccount.getID().equals(receiverID)) {
//                    receiverAccount = firebaseAccount;
//
//                    if (receiverAccount.getState().equals("online")) {
//                        onlineStatus.setText("Online now");
//                    } else {
//                        onlineStatus.setText("Last seen on " + receiverAccount.getLastSeenDate() + " " + receiverAccount.getLastSeenTime());
//                    }
//                }
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot snapshot) {
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot snapshot, String previousChildName) {
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//            }
//        });

        // Postavljanje RecyclerView-a
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(screen5.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Postavljanje onClick listenera za gumb za slanje poruka
        sendMessage.setOnClickListener(v -> {

            long unixTimestamp = Instant.now().getEpochSecond();
            Message msg = new Message(message.getText().toString(), unixTimestamp, currentUserID);

            myRef.child(chatID).child("Messages").push().setValue(msg);
//            messageList.add(msg);
//            Toast.makeText(getApplicationContext(), msg.getTekst(), Toast.LENGTH_LONG).show();
            message.setText("");
        });
    }

    private void checkIfChatExists() {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("Participants").hasChild(receiverID) && snapshot.child("Participants").hasChild(currentUserID)) {
                        chatID = snapshot.getKey();
                        break;
                    }
                }

                if (chatID.isEmpty()) {
                    createNewChat();  // Ako chat ne postoji, kreiraj novi
                } else {
                    loadMessages(chatID);  // Ako postoji, učitaj poruke
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    private void createNewChat() {
        // Kreiraj novi chat
        chatID = myRef.push().getKey(); // Generiraj jedinstveni ID chata
        if (chatID != null) {
            myRef.child(chatID).child("Participants").child(currentUserID).setValue(true);
            myRef.child(chatID).child("Participants").child(receiverID).setValue(true);
            Toast.makeText(this, chatID, Toast.LENGTH_SHORT).show();
        }
//        myRef.child(chatID).child("Messages").setValue(null);  // Možeš dodati inicijalne poruke

        // Kreiraj chat i učitaj poruke
        loadMessages(chatID);
    }

    //    private void loadMessages(String chatID) {
//        // Učitaj poruke u adapter
//
//        myRef.child(chatID).child("Messages").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                    Message msg = new Message(
////                            snapshot.child("text").getValue().toString(),
////                            Long.parseLong(snapshot.child("timestamp").getValue().toString()),
////                            snapshot.child("senderId").getKey());
//                            snapshot.child("text").getValue(String.class),
//                            snapshot.child("timestamp").getValue(Long.class),
//                            snapshot.child("senderId").getValue(String.class)
//                    );
//
//                    messageList.add(msg);
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//
////        for (DataSnapshot snapshot : myRef.child(chatID).child("Messages").get().getResult().getChildren()) {
////            Message msg = new Message(
////                    snapshot.child("text").getValue().toString(),
////                    Long.parseLong(snapshot.child("timestamp").getValue().toString()),
////                    snapshot.child("senderId").getKey()
////            );
////
////            messageList.add(msg);
////        }
//        adapter.notifyDataSetChanged();
//
//        myRef.child(chatID).child("Messages").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
//                // Pretpostavljam da koristiš Message klasu za poruke
//                Message message = dataSnapshot.getValue(Message.class);
//                if (message != null) {
//                    messageList.add(message);
//                    adapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//            }
//        });
//    }
    private void loadMessages(String chatID) {
        myRef.child(chatID).child("Messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    messageList.add(message);
                    adapter.notifyItemInserted(messageList.size() - 1);
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            }


            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    // Ažuriranje statusa korisnika (online/offline)
    private void updateUserStatus(String state) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Accounts");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String saveCurrentDate, saveCurrentTime;

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calForDate.getTime());

        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calForTime.getTime());

        reference.child(auth.getUid()).child("state").setValue(state);
        reference.child(auth.getUid()).child("lastSeenTime").setValue(saveCurrentTime);
        reference.child(auth.getUid()).child("lastSeenDate").setValue(saveCurrentDate);
    }

    // Kada se aplikacija pokrene, korisnik se označava kao online
    @Override
    public void onStart() {
        super.onStart();
        updateUserStatus("online");
        minimized = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (minimized) {
            updateUserStatus("offline");
        }
    }
}

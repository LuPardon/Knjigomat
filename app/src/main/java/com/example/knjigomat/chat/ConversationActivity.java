package com.example.knjigomat.chat;

// Uvoz potrebnih Android i Firebase klasa

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knjigomat.R;
import com.example.knjigomat.ui.activities.CameraActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConversationActivity extends Activity {
    // Definicija UI elemenata
    ImageView backButton, cameraImage, sendMessage;
    RecyclerView recyclerView;
    private static final int REQUEST_CODE_CAMERA_ACTIVITY = 300;
    List<Message> messageList;
    ConversationAdapter adapter;
    EditText message;
    FirebaseDatabase database;
    DatabaseReference myRef, myRef1;
    String receiverID;
    FirebaseAuth mAuth;
    boolean minimized = true;
    TextView fullName, onlineStatus;
    String chatID = "", currentUserID = "", naslov = "", slika_knjige = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        // Inicijalizacija UI elemenata
        backButton = findViewById(R.id.back_button);
        sendMessage = findViewById(R.id.send_icon);
        recyclerView = findViewById(R.id.rv_messages);
        fullName = findViewById(R.id.full_name);
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


        // Dobivanje podataka iz Intent-a
        Intent intent = getIntent();
        fullName.setText(intent.getStringExtra("nameReceiver"));
        receiverID = intent.getStringExtra("receiverID");
        naslov = intent.getStringExtra("naslov");
        slika_knjige = intent.getStringExtra("slika_knjige");


        // Ako korisnik nije sam sa sobom, nastavi s provjerom chata
        checkIfChatExists();

        // 🔹 Dohvati ime i prezime korisnika iz Firestore-a
        FirebaseFirestore.getInstance().collection("users")
                .document(receiverID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String ime = documentSnapshot.getString("ime");
                        String prezime = documentSnapshot.getString("prezime");
                        fullName.setText(ime + " " + prezime);
                    } else {
                        fullName.setText(this.getResources().getString(R.string.nepoznat_korisnik));
                    }
                })
                .addOnFailureListener(e -> {
                    fullName.setText(this.getResources().getString(R.string.greska_dohvacanje));
                });

        // 🔹 Postavi klik event za Chat
        fullName.setOnClickListener(view -> {
            Intent newIntent = new Intent(ConversationActivity.this, ConversationActivity.class);
            newIntent.putExtra("name", fullName.getText().toString());
            newIntent.putExtra("receiverID", receiverID);
            startActivity(newIntent);
        });


        // Postavljanje onClick listenera za gumb za cameru
        cameraImage.setOnClickListener(view -> {
            openCameraActivity();
        });


        // Postavljanje onClick listenera za gumb za slanje poruka
        sendMessage.setOnClickListener(v -> {
            long unixTimestamp = Instant.now().getEpochSecond();
            Message msg = new Message(message.getText().toString(), unixTimestamp, currentUserID, false);

            myRef.child(chatID).child("Messages").push().setValue(msg);
            message.setText("");
        });

        backButton.setOnClickListener(view -> {
            minimized = false;
            finish();
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
                    createNewChat();// Ako Chat ne postoji, kreiraj novi
                } else {
                    loadMessages(chatID);  // Ako postoji, učitaj poruke
                }
                if (naslov != null && !naslov.isEmpty()) {
                    long unixTimestamp = Instant.now().getEpochSecond();
                    Message msg = new Message(getResources().getString(R.string.upit_knjiga) + " " + naslov, unixTimestamp, currentUserID, false);
                    myRef.child(chatID).child("Messages").push().setValue(msg);


                    Message msg2 = new Message(slika_knjige, unixTimestamp, currentUserID, true);
                    myRef.child(chatID).child("Messages").push().setValue(msg2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void createNewChat() {
        // Kreiraj novi Chat
        chatID = myRef.push().getKey(); // Generiraj jedinstveni ID chata
        if (chatID != null) {
            myRef.child(chatID).child("Participants").child(currentUserID).setValue(true);
            myRef.child(chatID).child("Participants").child(receiverID).setValue(true);
            Toast.makeText(this, chatID, Toast.LENGTH_SHORT).show();
        }

        // Kreiraj Chat i učitaj poruke
        loadMessages(chatID);
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

    private void loadMessages(String chatID) {
        adapter = new ConversationAdapter(ConversationActivity.this, messageList, chatID);

        // Postavljanje RecyclerView-a
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ConversationActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        myRef.child(chatID).child("Messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                Message message = dataSnapshot.getValue(Message.class);
                if (message != null) {
                    message.setKey(dataSnapshot.getKey());
//                    message.setChatId(chatID);
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

    // Pokreni CameraActivity
    private void openCameraActivity() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CAMERA_ACTIVITY);
    }

    // Obradi vraćene podatke iz CameraActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA_ACTIVITY && resultCode == RESULT_OK && data != null) {
            String base64Image = data.getStringExtra("base64Image");
            if (base64Image != null) {
                long unixTimestamp = Instant.now().getEpochSecond();
                Message msg = new Message(base64Image, unixTimestamp, currentUserID, true);

                myRef.child(chatID).child("Messages").push().setValue(msg);
                // Prikaz stringa ili daljnja obrada
            } else {
                Toast.makeText(this, this.getResources().getString(R.string.nema_slike), Toast.LENGTH_SHORT).show();
            }
        }
    }
}

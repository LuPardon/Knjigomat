package com.example.knjigomat.chat;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class fragment_screen6 extends Fragment {
    RecyclerView recyclerView;
    screen6RVAdapter adaptor;
    List<contact> contactList;
    RelativeLayout newContact, newGroup;
    List<Account> accounts;
    DatabaseReference myRef;
    FirebaseAuth mAuth;
    ImageView dp;
    TextView name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_screen6, container, false);

        recyclerView = view.findViewById(R.id.rv_contacts);
        newContact = view.findViewById(R.id.rl_new_contact);
        newGroup = view.findViewById(R.id.rl_new_group);
        accounts = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference("Accounts");
        myRef.keepSynced(true);
        mAuth = FirebaseAuth.getInstance();
        dp = view.findViewById(R.id.dp);
        name = view.findViewById(R.id.name);

        contactList = new ArrayList<>();

        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String previousChildName) {
                Account firebaseAccount = snapshot.getValue(Account.class);
                accounts.add(firebaseAccount);
                contactList.clear();
                addPhoneContactsToList();
                adaptor.notifyDataSetChanged();

                if (firebaseAccount.getID().equals(mAuth.getUid())) {
                    name.setText(firebaseAccount.getFirstName() + " " + firebaseAccount.getLastName());
                    Picasso.get().load(firebaseAccount.getDp()).into(dp);
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

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        adaptor = new screen6RVAdapter(getActivity(), contactList, fragment_screen6.this);
        recyclerView.setAdapter(adaptor);
//        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(50));

        newContact.setOnClickListener(view1 -> Toast.makeText(getActivity(), "Clicked on new contact", Toast.LENGTH_LONG).show());
        newGroup.setOnClickListener(view12 -> Toast.makeText(getActivity(), "Clicked on new group", Toast.LENGTH_LONG).show());

        return view;
    }

    private void addPhoneContactsToList() {
        ContentResolver contentResolver = getActivity().getContentResolver();
        Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        String phoneNo;
        while (phones.moveToNext()) {
            int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            phoneNo = phones.getString(index).replace("+92", "0");

            if (phoneNo.charAt(4) == ' ') {
                StringBuilder stringBuilder = new StringBuilder(phoneNo);
                stringBuilder.deleteCharAt(4);
                phoneNo = stringBuilder.toString();
            }

            for (int i = 0; i < accounts.size(); ++i) {
                if (accounts.get(i).getPhoneNumber().equals(phoneNo) && !accounts.get(i).getID().equals(mAuth.getUid())) {
                    contactList.add(new contact(accounts.get(i).getFirstName() + " " +
                            accounts.get(i).getLastName(), phoneNo, accounts.get(i).getDp()));
                    break;
                }
            }
        }

        phones.close();
    }

    @Override
    public void onResume() {
        super.onResume();
//        ((fragmentsContainer)getActivity()).changeImageColorToBlue(1);
    }

    public void applicationNotMinimized() {
//        ((fragmentsContainer)getActivity()).minimized = false;
    }
}
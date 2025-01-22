package com.example.knjigomat.ui.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.knjigomat.R;
import com.example.knjigomat.ui.fragments.ReadBookFragment;


public class BookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Bundle b = getIntent().getExtras();

        // Zamjena trenutnog fragmenta
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, ReadBookFragment.class, b)
//                .addToBackStack(null)
                .commit();

    }
}
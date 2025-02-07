package com.example.knjigomat.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.knjigomat.R;
import com.example.knjigomat.chat.fragment_screen4;
import com.example.knjigomat.ui.fragments.AllBooksFragment;
import com.example.knjigomat.ui.fragments.MyBooksFragment;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Zamjena trenutnog fragmenta
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_container, AllBooksFragment.class, null)
//                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.moje_knjige) {
            // Zamjena trenutnog fragmenta
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, MyBooksFragment.class, null)
//                .addToBackStack(null)
                    .commit();

        } else if (id == R.id.sve_knjige) {
            // Zamjena trenutnog fragmenta
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, AllBooksFragment.class, null)
//                .addToBackStack(null)
                    .commit();

        } else if (id == R.id.chat) {
            // Zamjena trenutnog fragmenta
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, fragment_screen4.class, null)
//                .addToBackStack(null)
                    .commit();

        } else if (id == R.id.odjava) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }
}

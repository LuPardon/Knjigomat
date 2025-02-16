package com.example.knjigomat.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.knjigomat.R;
import com.example.knjigomat.chat.ContactsFragment;
import com.example.knjigomat.ui.fragments.AllBooksFragment;
import com.example.knjigomat.ui.fragments.MyBooksFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Dohvaćanje trenutno prijavljenog korisnika iz Firebase Authenticationa
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // Ako je korisnik prijavljen, prikazuje se fragment sa svim knjigama
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, AllBooksFragment.class, null)
//                .addToBackStack(null)
                    .commit();
        } else {
            // Ako korisnik nije prijavljen, preusmjerava se na ekran za prijavu
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    // Kreiranje izbornika u toolbaru
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    // Obrada odabira stavki izbornika
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.moje_knjige) {
            // Zamjena trenutnog fragmenta s fragmentom "Moje knjige"
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, MyBooksFragment.class, null)
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.sve_knjige) {
            // Zamjena trenutnog fragmenta s fragmentom "Sve knjige"
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, AllBooksFragment.class, null)
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.chat) {
            // Zamjena trenutnog fragmenta s Chat fragmentom
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, ContactsFragment.class, null)
                    .addToBackStack(null)
                    .commit();

        } else if (id == R.id.odjava) {

            // Prikaz dijaloga za potvrdu odjave korisnika
            new AlertDialog.Builder(this)
                    .setIconAttribute(android.R.attr.alertDialogIcon)
                    .setTitle(getResources().getString(R.string.logout_info))
                    .setMessage(getResources().getString(R.string.logout_app))
                    .setPositiveButton(getResources().getString(R.string.da), (dialog, which) -> {
                        // Ako korisnik potvrdi odjavu, odjavljuje se iz Firebase Authenticationa i preusmjerava na login ekran
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        finish();
                    })
                    .setNegativeButton(getResources().getString(R.string.ne), null)
                    .show();
        }
        return super.onOptionsItemSelected(item);
    }
}

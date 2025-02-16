package com.example.knjigomat.ui.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.knjigomat.R;
import com.example.knjigomat.chat.Account;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText etRegisterEmail, etRegisterPassword, etRegisterPassword2, etRegisterFirstName, etRegisterLastName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Inicijalizacija Firebase autentifikacije
        mAuth = FirebaseAuth.getInstance();

        // Pronalazak UI elemenata u layoutu
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        etRegisterPassword2 = findViewById(R.id.etRegisterPassword2);
        etRegisterFirstName = findViewById(R.id.etRegisterFirstName);
        etRegisterLastName = findViewById(R.id.etRegisterLastName);

        // Postavljanje klika na gumb za prijavu - preusmjerava korisnika na LoginActivity
        findViewById(R.id.btnLogin2).setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        // Postavljanje klika na gumb za registraciju
        findViewById(R.id.btnRegister).setOnClickListener(v -> {

            // Dohvaćanje unesenih podataka iz polja
            String email = etRegisterEmail.getText().toString().trim();
            String password = etRegisterPassword.getText().toString().trim();
            String password2 = etRegisterPassword2.getText().toString().trim();
            String ime = etRegisterFirstName.getText().toString().trim();
            String prezime = etRegisterLastName.getText().toString().trim();

            // Provjera jesu li sva polja popunjena
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(password2) || TextUtils.isEmpty(ime) || TextUtils.isEmpty(prezime)) {
                Toast.makeText(RegisterActivity.this, this.getResources().getString(R.string.popuni_polja), Toast.LENGTH_SHORT).show();
                return;
            }

            // Provjera podudaraju li se lozinke
            if (!password.equals(password2)) {
                Toast.makeText(RegisterActivity.this, this.getResources().getString(R.string.razlicita_lozinka), Toast.LENGTH_SHORT).show();
                return;
            }

            // Inicijalizacija Firestore baze podataka
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Kreiranje korisnika u Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, this.getResources().getString(R.string.registar_uspjeh), Toast.LENGTH_SHORT).show();

                            // Kreiranje objekta korisnika za pohranu u Firestore
                            Account usr = new Account(ime, prezime);

                            // Pohrana korisnika u Firestore pod kolekciju "users"
                            db.collection("users")
                                    .document(task.getResult().getUser().getUid()).set(usr)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(this, this.getResources().getString(R.string.korisnik_dodan), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(this, this.getResources().getString(R.string.korisnik_nedodan), Toast.LENGTH_SHORT).show());


                        } else {
                            Toast.makeText(RegisterActivity.this, this.getResources().getString(R.string.registar_neuspjeh), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // Kreiranje izbornika za promjenu jezika
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.language_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    // Obrada odabira jezika iz izbornika
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.croatian) {
            setLocale("hr");

        } else if (id == R.id.serbian) {
            setLocale("sr");

        } else if (id == R.id.english) {
            setLocale("en");

        }

        return super.onOptionsItemSelected(item);
    }

    // Metoda za promjenu jezika aplikacije
    private void setLocale(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        recreate();  // Ponovno učitavanje aktivnosti kako bi se promjene primijenile
    }

}
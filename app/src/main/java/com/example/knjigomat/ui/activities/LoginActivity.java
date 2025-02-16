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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.knjigomat.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText etEmail, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        // Pronalazak UI elemenata u layoutu
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        // Postavljanje klika na gumb za registraciju - preusmjerava korisnika na RegisterActivity
        findViewById(R.id.btnRegister2).setOnClickListener(v ->
        {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        // Postavljanje klika na gumb za prijavu
        findViewById(R.id.btnLogin).setOnClickListener(v -> {

            // Dohvaćanje unesenih podataka iz polja
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
//                String email = "lucija.barisic24@gmail.com";
//                String password = "Smokvica63";

            // Provjera jesu li sva polja popunjena
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(LoginActivity.this, this.getResources().getString(R.string.popuni_polja), Toast.LENGTH_SHORT).show();
                return;
            }

            // Prijava korisnika pomoću Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, this.getResources().getString(R.string.prijava_uspjesna), Toast.LENGTH_SHORT).show();

                            // Preusmjeravanje korisnika na glavnu aktivnost aplikacije
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, this.getResources().getString(R.string.prijava_neuspjesna), Toast.LENGTH_SHORT).show();
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
        recreate(); // Ponovno učitavanje aktivnosti kako bi se promjene primijenile
    }

    // Metoda koja presreće pritisak tipke "back" i prikazuje dijalog za izlaz
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIconAttribute(android.R.attr.alertDialogIcon)


                .setTitle(getResources().getString(R.string.logout_info))  // Naslov dijaloga
                .setMessage(getResources().getString(R.string.logout_app))  // Poruka u dijalogu
                .setPositiveButton(getResources().getString(R.string.da), (dialog, which) -> finish())
                .setNegativeButton(getResources().getString(R.string.ne), null)
                .show();
    }

}
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
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private EditText etRegisterEmail, etRegisterPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        findViewById(R.id.btnLogin2).setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        findViewById(R.id.btnRegister).setOnClickListener(v -> {
            String email = etRegisterEmail.getText().toString().trim();
            String password = etRegisterPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(RegisterActivity.this, "Popunite sva polja", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Registracija uspješna!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registracija neuspješna!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater findMenuItems = getMenuInflater();
        findMenuItems.inflate(R.menu.language_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

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


    private void setLocale(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        recreate();
    }

}
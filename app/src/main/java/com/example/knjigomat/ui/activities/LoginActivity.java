package com.example.knjigomat.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.knjigomat.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
        private FirebaseAuth mAuth;
        private EditText etEmail, etPassword;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

            mAuth = FirebaseAuth.getInstance();
            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);

            findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email = etEmail.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    email = "lucija.barisic24@gmail.com";
                    password = "Smokvica63";

                    if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                        Toast.makeText(LoginActivity.this, "Popunite sva polja", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Prijava uspješna!", Toast.LENGTH_SHORT).show();


                                    startActivity(new Intent(LoginActivity.this, PocetnaActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Prijava neuspješna!", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            });
        }


}
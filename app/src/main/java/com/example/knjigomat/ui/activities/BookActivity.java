package com.example.knjigomat.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;

import com.example.knjigomat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class BookActivity extends AppCompatActivity {

    private EditText etBookTitle, etBookAuthor, etBookGenre,etBookLocation,etBookYear,etBookLanguage, etBookDescription,etBookBinding,etBookPublisher,etBookPages;
    private Switch switch1;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            //Log.e("user1",uid);
        } else {
            // No user is signed in
        }
        etBookTitle = findViewById(R.id.etBookTitle);
        etBookAuthor = findViewById(R.id.etBookAuthor);
        etBookGenre = findViewById(R.id.etBookGenre);
        etBookLocation = findViewById(R.id.etBookLocation);
        etBookYear = findViewById(R.id.etBookYear);
        etBookLanguage = findViewById(R.id.etBookLanguage);
        etBookDescription = findViewById(R.id.etBookDescription);
        etBookBinding = findViewById(R.id.etBookBinding);
        etBookPublisher = findViewById(R.id.etBookPublisher);
        etBookPages = findViewById(R.id.etBookPages);
        imageView = findViewById(R.id.imageView);
        switch1 = findViewById(R.id.switch1);

        // Dohvaćanje Bundle-a iz Intenta
        Bundle b = getIntent().getExtras();
        if (b != null) {
            String vlasnikID = b.getString("vlasnikID", ""); // Zadana vrijednost je prazna ako nije postavljena
            String autor = b.getString("autor", "");
            int brojStranica = b.getInt("brojStranica", -1);
            boolean dostupno = b.getBoolean("dostupno", false);
            int godinaIzdanja = b.getInt("godinaIzdanja", -1);
            String jezikIzdanja = b.getString("jezikIzdanja", "");
            String lokacija = b.getString("lokacija", "");
            String nakladnik = b.getString("nakladnik", "");
            String naslov = b.getString("naslov", "");
            String opis = b.getString("opis", "");
            String slika = b.getString("slika", "");
            String uvez = b.getString("uvez", "");
            String zanr = b.getString("zanr", "");

            etBookTitle.setText(naslov);
            etBookAuthor.setText(autor);
            etBookGenre.setText(zanr);
            etBookLocation.setText(lokacija);
            etBookYear.setText(""+godinaIzdanja);
            etBookLanguage.setText(jezikIzdanja);
            etBookDescription.setText(opis);
            etBookPages.setText(""+brojStranica);
            etBookBinding.setText(uvez);
            etBookPublisher.setText(nakladnik);
            switch1.setChecked(dostupno);
            Picasso.get().load(slika).into(imageView);
        }
        else{ Log.e("BookActivity", "Bundle je prazan!");}
    }
}
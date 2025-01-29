package com.example.knjigomat.ui.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.knjigomat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


public class ReadBookFragment extends Fragment {

    private EditText etBookTitle, etBookAuthor, etBookGenre, etBookLocation, etBookYear, etBookLanguage, etBookDescription, etBookBinding, etBookPublisher, etBookPages;
    private Switch switch1;
    private ImageView imageView;
    private Button btnKontaktBook, btnUpdateBook, btnDeleteBook;
    private String knjigaID, slika;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read_book, container, false);

        Bundle b = this.getArguments();
        // Dohvaćanje Bundle-a iz Intenta
        if (b == null) {
            Toast.makeText(getContext(), "Podaci neispravno poslani", Toast.LENGTH_LONG).show();
            return view;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        etBookTitle = view.findViewById(R.id.etBookTitle);
        etBookAuthor = view.findViewById(R.id.etBookAuthor);
        etBookGenre = view.findViewById(R.id.etBookGenre);
        etBookLocation = view.findViewById(R.id.etBookLocation);
        etBookYear = view.findViewById(R.id.etBookYear);
        etBookLanguage = view.findViewById(R.id.etBookLanguage);
        etBookDescription = view.findViewById(R.id.etBookDescription);
        etBookBinding = view.findViewById(R.id.etBookBinding);
        etBookPublisher = view.findViewById(R.id.etBookPublisher);
        etBookPages = view.findViewById(R.id.etBookPages);

        imageView = view.findViewById(R.id.imageView);

        switch1 = view.findViewById(R.id.switch1);

        btnKontaktBook = view.findViewById(R.id.btnKontaktBook);
        btnUpdateBook = view.findViewById(R.id.btnUpdateBook);
        btnDeleteBook = view.findViewById(R.id.btnDeleteBook);

        //Dohvaćanje i prikaz ažurirane knjige
        knjigaID = b.getString("knjigaID", "");

        // Provjera korisnika
            String vlasnikID = b.getString("vlasnikID", "");
            if (user.getUid().equals(vlasnikID)) {
                // Prikaz gumba za ažuriranje i brisanje
                btnKontaktBook.setVisibility(View.GONE);
                btnUpdateBook.setVisibility(View.VISIBLE);
                btnDeleteBook.setVisibility(View.VISIBLE);

                btnUpdateBook.setOnClickListener(v -> {
//                    Toast.makeText(getContext(), "Ažurirana knjiga.",Toast.LENGTH_LONG).show();

                    b.putString("autor", etBookAuthor.getText().toString());
                    b.putInt("brojStranica", Integer.parseInt(etBookPages.getText().toString()));
                    b.putBoolean("dostupno", switch1.isChecked());
                    b.putInt("godinaIzdanja", Integer.parseInt(etBookYear.getText().toString()));
                    b.putString("jezikIzdanja", etBookLanguage.getText().toString());
                    b.putString("lokacija", etBookLocation.getText().toString());
                    b.putString("nakladnik", etBookPublisher.getText().toString());
                    b.putString("naslov", etBookTitle.getText().toString());
                    b.putString("opis", etBookDescription.getText().toString());
                    b.putString("zanr", etBookGenre.getText().toString());
                    b.putString("uvez", etBookBinding.getText().toString());
                    b.putString("slika", (!TextUtils.isEmpty(slika)) ? slika : "https://media.istockphoto.com/id/183890264/photo/upright-red-book-with-clipping-path.jpg?s=612x612&w=0&k=20&c=zm6sEPnc4zK4MNj307pm3tzgxTbex2sOnb1Ip5hglaA=");
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_fragment_container, UpdateBookFragment.class, b)
                            .addToBackStack("read")
                            .commit();
                });

                btnDeleteBook.setOnClickListener(v -> Toast.makeText(getContext(), "Obrisana knjiga",Toast.LENGTH_LONG).show());
            }
            else
            {
                // Prikaz gumba za kontakt
                btnKontaktBook.setVisibility(View.VISIBLE);
                btnUpdateBook.setVisibility(View.GONE);
                btnDeleteBook.setVisibility(View.GONE);

                btnKontaktBook.setOnClickListener(v -> Toast.makeText(getContext(), "Ovdje će biti chat između osoba",Toast.LENGTH_LONG).show());

            }

        return view;
    }
    private void populateFields(DocumentSnapshot snapshot) {
        String naslov = snapshot.getString("naslov");
        String autor = snapshot.getString("autor");
        String zanr = snapshot.getString("zanr");
        String lokacija = snapshot.getString("lokacija");
        Long godinaIzdanja = snapshot.getLong("godinaIzdanja");
        String jezikIzdanja = snapshot.getString("jezikIzdanja");
        String opis = snapshot.getString("opis");
        Long brojStranica = snapshot.getLong("brojStranica");
        String uvez = snapshot.getString("uvez");
        String nakladnik = snapshot.getString("nakladnik");
        Boolean dostupno = snapshot.getBoolean("dostupno");
        slika = snapshot.getString("slika");

        if (naslov != null) etBookTitle.setText(naslov);
        if (autor != null) etBookAuthor.setText(autor);
        if (zanr != null) etBookGenre.setText(zanr);
        if (lokacija != null) etBookLocation.setText(lokacija);
        if (godinaIzdanja != null) etBookYear.setText(String.valueOf(godinaIzdanja));
        if (jezikIzdanja != null) etBookLanguage.setText(jezikIzdanja);
        if (opis != null) etBookDescription.setText(opis);
        if (brojStranica != null) etBookPages.setText(String.valueOf(brojStranica));
        if (uvez != null) etBookBinding.setText(uvez);
        if (nakladnik != null) etBookPublisher.setText(nakladnik);
        if (dostupno != null)
            switch1.setChecked(dostupno);
        if (slika != null && !slika.isEmpty()) {
            Picasso.get().load(slika).into(imageView);
        } else {
            Picasso.get().load("https://media.istockphoto.com/id/183890264/photo/upright-red-book-with-clipping-path.jpg?s=612x612&w=0&k=20&c=zm6sEPnc4zK4MNj307pm3tzgxTbex2sOnb1Ip5hglaA=").into(imageView);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("knjige").document(knjigaID).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        populateFields(snapshot);
                    } else {
                        Toast.makeText(getContext(), "Knjiga nije pronađena.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Greška prilikom dohvaćanja podataka.", Toast.LENGTH_SHORT).show();
                });
    }
}
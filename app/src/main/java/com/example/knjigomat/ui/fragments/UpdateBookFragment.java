package com.example.knjigomat.ui.fragments;

import android.os.Bundle;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UpdateBookFragment extends Fragment {

    
    private EditText etUpdateBookTitle, etUpdateBookAuthor, etUpdateBookGenre, etUpdateBookLocation,
            etUpdateBookYear, etUpdateBookLanguage, etUpdateBookDescription, etUpdateBookBinding, etUpdateBookPublisher, etUpdateBookPages;
    private Button btnSaveChanges, btnCancelUpdate;
    private Switch updateSwitch1;
    private ImageView updateImageView;
    
    public UpdateBookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_update_book, container, false);

        // Dohvaćanje podataka iz Bundle-a
        Bundle args = getArguments();
        if (args == null) {
            Toast.makeText(getContext(), "Pogreška u učitavanju podataka.", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnCancelUpdate = view.findViewById(R.id.btnCancelUpdate);

        String knjigaID = args.getString("knjigaID", "");
        populateFields(args, view);

        // Postavljanje listenera za gumb "Spremi promjene"
        btnSaveChanges.setOnClickListener(v -> {
            // Spremanje promjena

            // Napravi mapu polja za ažuriranje
            Map<String, Object> updates = new HashMap<>();
            updates.put("autor", etUpdateBookAuthor.getText().toString());
            updates.put("brojStranica", Integer.parseInt(etUpdateBookPages.getText().toString()));
            updates.put("dostupno", updateSwitch1.isChecked());
            updates.put("godinaIzdanja", Integer.parseInt(etUpdateBookYear.getText().toString()));
            updates.put("jezikIzdanja", etUpdateBookLanguage.getText().toString());
            updates.put("lokacija", etUpdateBookLocation.getText().toString());
            updates.put("nakladnik", etUpdateBookPublisher.getText().toString());
            updates.put("naslov", etUpdateBookTitle.getText().toString());
            updates.put("opis", etUpdateBookDescription.getText().toString());
            updates.put("zanr", etUpdateBookGenre.getText().toString());
            updates.put("uvez", etUpdateBookBinding.getText().toString());

            db.collection("knjige")
                    .document(knjigaID)
                    .set(updates, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        // Uspješno ažurirano
                        Toast.makeText(getContext(), "Podaci su uspješno ažirirani.", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();

                    })
                    .addOnFailureListener(e -> {
                        // Greška
                        Toast.makeText(getContext(), "Greška prilikom ažuriranja.", Toast.LENGTH_SHORT).show();
                    });
        });

        // Postavljanje listenera za gumb "Odustani"
        btnCancelUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Povratak bez spremanja
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void populateFields(Bundle args, View view) {
//        String vlasnikID = args.getString("vlasnikID", ""); // Zadana vrijednost je prazna ako nije postavljena
        String autor = args.getString("autor", "");
        int brojStranica = args.getInt("brojStranica", -1);
        boolean dostupno = args.getBoolean("dostupno", false);
        int godinaIzdanja = args.getInt("godinaIzdanja", -1);
        String jezikIzdanja = args.getString("jezikIzdanja", "");
        String lokacija = args.getString("lokacija", "");
        String nakladnik = args.getString("nakladnik", "");
        String opis = args.getString("opis", "");
        String slika = args.getString("slika", "https://media.istockphoto.com/id/183890264/photo/upright-red-book-with-clipping-path.jpg?s=612x612&w=0&k=20&c=zm6sEPnc4zK4MNj307pm3tzgxTbex2sOnb1Ip5hglaA=");
        String uvez = args.getString("uvez", "");
        String zanr = args.getString("zanr", "");

        String naslov = args.getString("naslov", "");

        // Povezivanje s XML-om
        etUpdateBookTitle = view.findViewById(R.id.etUpdateBookTitle);
        etUpdateBookAuthor = view.findViewById(R.id.etUpdateBookAuthor);
        etUpdateBookGenre = view.findViewById(R.id.etUpdateBookGenre);
        etUpdateBookLocation = view.findViewById(R.id.etUpdateBookLocation);
        etUpdateBookYear = view.findViewById(R.id.etUpdateBookYear);
        etUpdateBookLanguage = view.findViewById(R.id.etUpdateBookLanguage);
        etUpdateBookDescription = view.findViewById(R.id.etUpdateBookDescription);
        etUpdateBookBinding = view.findViewById(R.id.etUpdateBookBinding);
        etUpdateBookPublisher = view.findViewById(R.id.etUpdateBookPublisher);
        etUpdateBookPages = view.findViewById(R.id.etUpdateBookPages);

        updateImageView = view.findViewById(R.id.updateImageView);

        updateSwitch1 = view.findViewById(R.id.updateSwitch1);

        etUpdateBookTitle.setText(naslov);

//        etUpdateBookTitle.setText(args.getString("naslov"));

        etUpdateBookAuthor.setText(autor);
        etUpdateBookGenre.setText(zanr);
        etUpdateBookLocation.setText(lokacija);
        etUpdateBookYear.setText("" + godinaIzdanja);
        etUpdateBookLanguage.setText(jezikIzdanja);
        etUpdateBookDescription.setText(opis);
        etUpdateBookPages.setText("" + brojStranica);
        etUpdateBookBinding.setText(uvez);
        etUpdateBookPublisher.setText(nakladnik);
        updateSwitch1.setChecked(dostupno);
        Picasso.get().load(slika).into(updateImageView);
    }
}

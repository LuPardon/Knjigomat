package com.example.knjigomat.ui.fragments;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddBookFragment extends Fragment {
    static final int Image_Capture_Code = 1337;
    View view;
    private EditText etBookTitle, etBookAuthor, etBookGenre, etBookLocation, etBookYear, etBookLanguage,
            etBookDescription, etBookBinding, etBookPublisher, etBookPages;
    private Button btnAddBook, btnCancelAdd;
    private ImageView addImageView;
    private Switch AddSwitch1;

    public AddBookFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_book, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        etBookTitle = view.findViewById(R.id.etAddBookTitle);
        etBookAuthor = view.findViewById(R.id.etAddBookAuthor);
        etBookGenre = view.findViewById(R.id.etAddBookGenre);
        etBookPublisher = view.findViewById(R.id.etAddBookPublisher);
        etBookLocation = view.findViewById(R.id.etAddBookLocation);
        AddSwitch1 = view.findViewById(R.id.AddSwitch1);
        etBookYear = view.findViewById(R.id.etAddBookYear);
        etBookLanguage = view.findViewById(R.id.etAddBookLanguage);
        etBookPages = view.findViewById(R.id.etAddBookPages);
        etBookBinding = view.findViewById(R.id.etAddBookBinding);
        etBookDescription = view.findViewById(R.id.etAddBookDescription);

        btnAddBook = view.findViewById(R.id.btnAddBook);
        btnCancelAdd = view.findViewById(R.id.btnCancelAdd);

        addImageView = view.findViewById(R.id.AddImageView);
        addImageView.setOnClickListener(v -> {
            Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cInt, Image_Capture_Code);
        });


        btnAddBook.setOnClickListener(v -> {
            // Prikupljanje podataka iz polja
            Map<String, Object> newBook = new HashMap<>();
            newBook.put("naslov", etBookTitle.getText().toString());
            newBook.put("autor", etBookAuthor.getText().toString());
            newBook.put("zanr", etBookGenre.getText().toString());
            newBook.put("nakladnik", etBookPublisher.getText().toString());
            newBook.put("dostupno", AddSwitch1.isChecked());
            newBook.put("lokacija", etBookLocation.getText().toString());
            newBook.put("godinaIzdanja", Integer.parseInt(etBookYear.getText().toString()));
            newBook.put("jezikIzdanja", etBookLanguage.getText().toString());
            newBook.put("opis", etBookDescription.getText().toString());
            newBook.put("uvez", etBookBinding.getText().toString());
            newBook.put("brojStranica", Integer.parseInt(etBookPages.getText().toString()));
            newBook.put("vlasnikID", FirebaseAuth.getInstance().getCurrentUser().getUid());
            newBook.put("slika", "https://media.istockphoto.com/id/183890264/photo/upright-red-book-with-clipping-path.jpg?s=612x612&w=0&k=20&c=zm6sEPnc4zK4MNj307pm3tzgxTbex2sOnb1Ip5hglaA=");

            // Spremanje u Firestore
            db.collection("knjige")
                    .add(newBook)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(getContext(), "Knjiga uspješno dodana.", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Greška pri dodavanju knjige.", Toast.LENGTH_SHORT).show());
        });

        btnCancelAdd.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                ((ImageView) view.findViewById(R.id.AddImageView)).setImageBitmap(bp);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getContext(), "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }
}
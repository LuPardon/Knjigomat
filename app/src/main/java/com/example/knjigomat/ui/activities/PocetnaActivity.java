package com.example.knjigomat.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knjigomat.R;
import com.example.knjigomat.books.Book;
import com.example.knjigomat.books.BookAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PocetnaActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;
    private List<Book> originalBookList;
    private SearchView searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pocetna);

        // Inicijalizacija Firebase Firestore-a
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Inicijalizacija popisa knjiga
        bookList = new ArrayList<>();
        originalBookList = new ArrayList<>();

        // Postavljanje RecyclerView-a
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookAdapter(this, bookList);
        recyclerView.setAdapter(adapter);

        // Dohvaćanje podataka iz Firestore-a
        db.collection("knjige").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Book book = document.toObject(Book.class);
                if (book != null) {
                    book.setKnjigaID(document.getId());
                    bookList.add(book);
                    originalBookList.add(book); // Spremanje originalne liste za filtriranje
                }
            }
            adapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Greška pri dohvaćanju podataka", Toast.LENGTH_SHORT).show();
        });

        // Inicijalizacija SearchView-a
        searchBar = findViewById(R.id.searchView);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Ovdje ne radimo ništa na submit
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText); // Pozivanje metode za filtriranje
                return true;
            }
        });
    }

    // Metoda za filtriranje knjiga prema unesenom tekstu
    private void filterBooks(String text) {
        List<Book> filteredList = new ArrayList<>();
        for (Book book : originalBookList) {
            if (book.getNaslov().toLowerCase().contains(text.toLowerCase())||book.getAutor().toLowerCase().contains(text.toLowerCase()))
            {
                filteredList.add(book);
            }
        }
        bookList.clear();
        bookList.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }
}

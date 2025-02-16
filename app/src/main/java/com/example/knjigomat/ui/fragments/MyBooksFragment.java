package com.example.knjigomat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knjigomat.R;
import com.example.knjigomat.books.Book;
import com.example.knjigomat.books.BookAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;


public class MyBooksFragment extends Fragment {
    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private List<Book> bookList;
    private List<Book> originalBookList;
    private SearchView searchBar;
    private Button btnAddNewBook;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_books, container, false);

        // Inicijalizacija popisa knjiga
        bookList = new ArrayList<>();
        originalBookList = new ArrayList<>();

        // Postavljanje RecyclerView-a
        recyclerView = view.findViewById(R.id.recyclerViewMyBooks);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BookAdapter(requireContext(), bookList);
        recyclerView.setAdapter(adapter);

        // Inicijalizacija gumba za dodavanje nove knjige
        btnAddNewBook = view.findViewById(R.id.btnAddNewBook);


        // Inicijalizacija SearchView-a
        searchBar = view.findViewById(R.id.searchViewMyBooks);
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

        btnAddNewBook.setOnClickListener(v -> {
            // Otvaranje AddBookFragment-a
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_fragment_container, AddBookFragment.class, null)
                    .addToBackStack("my_books")
                    .commit();
        });

        return view;
    }

    // Metoda za filtriranje knjiga prema unesenom tekstu
    private void filterBooks(String text) {
        List<Book> filteredList = new ArrayList<>();
        for (Book book : originalBookList) {
            if (book.getNaslov().toLowerCase().contains(text.toLowerCase()) || book.getAutor().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(book);
            }
        }
        bookList.clear();
        bookList.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        bookList.clear();
        originalBookList.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Dohvaćanje podataka iz Firestore-a
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("knjige").whereEqualTo("vlasnikID", user.getUid()).get().addOnSuccessListener(queryDocumentSnapshots -> {
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
            Toast.makeText(getContext(), this.getResources().getString(R.string.greska_dohvacanje), Toast.LENGTH_SHORT).show();
        });
    }
}
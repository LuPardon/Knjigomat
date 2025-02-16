package com.example.knjigomat.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.knjigomat.R;
import com.example.knjigomat.books.Book;
import com.example.knjigomat.books.BookAdapter;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;


public class AllBooksFragment extends Fragment {
    private RecyclerView recyclerView;

    // Deklaracija UI elemenata
    private BookAdapter adapter;
    private List<Book> bookList; // Lista knjiga za prikaz
    private List<Book> originalBookList; // Originalna lista za filtriranje
    private SearchView searchBar;
    private Spinner zanr_dropdown, nakladnik_dropdown, lokacija_dropdown, dostupno_dropdown, godinaIzdanja_dropdown, jezikIzdanja_dropdown, brojStranica_dropdown, uvez_dropdown;

    // Liste za podatke u Spinnerima
    private List<String> zanrovi, nakladnici, lokacije, dostupno, godineIzdanja, jeziciIzdanja, brojStranica, uvezi;

    // Pohrana odabranih filtera
    private String odabraniZanr = "", odabraniNakladnik = "", odabranaLokacija = "", odabranaDostupnost = "", odabranaGodina = "", odabraniJezik = "", odabraniBrStr = "", odabraniUvez = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_books, container, false);

        // Inicijalizacija RecyclerView-a
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicijalizacija popisa knjiga
        bookList = new ArrayList<>();
        originalBookList = new ArrayList<>();
        zanrovi = new ArrayList<>();
        nakladnici = new ArrayList<>();
        lokacije = new ArrayList<>();
        dostupno = new ArrayList<>(Arrays.asList(getResources().getString(R.string.dostupno), getResources().getString(R.string.ne), getResources().getString(R.string.da)));
        godineIzdanja = new ArrayList<>();
        jeziciIzdanja = new ArrayList<>();
        brojStranica = new ArrayList<>();
        uvezi = new ArrayList<>();

        // Postavljanje RecyclerView-a
        // Postavljanje adaptera za RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new BookAdapter(requireContext(), bookList);
        recyclerView.setAdapter(adapter);

        // Inicijalizacija SearchView-a
        searchBar = view.findViewById(R.id.searchView);
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Ovdje ne radimo ništa na submit
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText); // Pozivanje metode za filtriranje knjiga
                return true;
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Inicijalizacija dropdown filtera
        zanr_dropdown = view.findViewById(R.id.zanr_spinner);
        nakladnik_dropdown = view.findViewById(R.id.nakladnik_spinner);
        lokacija_dropdown = view.findViewById(R.id.lokacija_spinner);
        dostupno_dropdown = view.findViewById(R.id.dostupno_spinner);
        godinaIzdanja_dropdown = view.findViewById(R.id.godinaIzdanja_spinner);
        jezikIzdanja_dropdown = view.findViewById(R.id.jezikIzdanja_spinner);
        brojStranica_dropdown = view.findViewById(R.id.brojStranica_spinner);
        uvez_dropdown = view.findViewById(R.id.uvez_spinner);

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
        // Čišćenje podataka prije ponovnog dohvaćanja
        bookList.clear();
        originalBookList.clear();
        zanrovi.clear();
        nakladnici.clear();
        lokacije.clear();
        godineIzdanja.clear();
        jeziciIzdanja.clear();
        brojStranica.clear();
        uvezi.clear();


        // Inicijalizacija Firebase Firestore-a
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Dohvaćanje podataka iz Firestore-a
        db.collection("knjige").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot document : queryDocumentSnapshots) {
                Book book = document.toObject(Book.class);
                if (book != null) {
                    // Provjeri postoji li već vrijednost u listi prije dodavanja
                    if (!zanrovi.contains(book.getZanr())) {
                        zanrovi.add(book.getZanr());
                    }
                    if (!nakladnici.contains(book.getNakladnik())) {
                        nakladnici.add(book.getNakladnik());
                    }
                    if (!lokacije.contains(book.getLokacija())) {
                        lokacije.add(book.getLokacija());
                    }
                    if (!godineIzdanja.contains(Integer.toString(book.getGodinaIzdanja()))) {
                        godineIzdanja.add(Integer.toString(book.getGodinaIzdanja()));
                    }
                    if (!jeziciIzdanja.contains(book.getJezikIzdanja().trim().toLowerCase(Locale.ROOT))) {
                        jeziciIzdanja.add(book.getJezikIzdanja().trim().toLowerCase(Locale.ROOT));
                    }
                    if (!uvezi.contains(book.getUvez())) {
                        uvezi.add(book.getUvez());
                    }
                    if (!brojStranica.contains(Integer.toString(book.getBrojStranica()))) {
                        brojStranica.add(Integer.toString(book.getBrojStranica()));
                    }

                    book.setKnjigaID(document.getId());
                    bookList.add(book);
                    originalBookList.add(book); // Spremanje originalne liste za filtriranje
                }
            }

            // Sortiranje popisa
            Collections.sort(zanrovi);
            Collections.sort(nakladnici);
            Collections.sort(lokacije);
            Collections.sort(godineIzdanja);
            Collections.sort(jeziciIzdanja);
            Collections.sort(uvezi);
            Collections.sort(brojStranica);

            // Dodavanje početne opcije u dropdown menije
            zanrovi.add(0, getResources().getString(R.string.zanr));
            nakladnici.add(0, getResources().getString(R.string.nakladnik));
            lokacije.add(0, getResources().getString(R.string.lokacija));
            godineIzdanja.add(0, getResources().getString(R.string.godina_izdanja));
            jeziciIzdanja.add(0, getResources().getString(R.string.jezik_izdanja));
            brojStranica.add(0, getResources().getString(R.string.broj_stranica));
            uvezi.add(0, getResources().getString(R.string.uvez));

            adapter.notifyDataSetChanged();

            // Postavljanje adaptera za Spinner za žanr
            ArrayAdapter<String> zanr_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, zanrovi);
            zanr_dropdown.setAdapter(zanr_adapter);

            // Postavljanje listenera za odabir žanra
            zanr_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    odabraniZanr = parent.getSelectedItem().toString(); // Spremanje odabranog žanra
                    FilterBooks(); // Pozivanje metode za filtriranje
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

            // Ponovljeni postupak za ostale filtre (nakladnik, lokacija, dostupnost, godina izdanja, jezik izdanja, broj stranica, uvez)
            ArrayAdapter<String> nakladnik_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nakladnici);
            nakladnik_dropdown.setAdapter(nakladnik_adapter);

            nakladnik_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    odabraniNakladnik = parent.getSelectedItem().toString();
                    FilterBooks();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            ArrayAdapter<String> lokacija_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, lokacije);
            lokacija_dropdown.setAdapter(lokacija_adapter);

            lokacija_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    odabranaLokacija = parent.getSelectedItem().toString();
                    FilterBooks();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            ArrayAdapter<String> dostupno_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, dostupno);
            dostupno_dropdown.setAdapter(dostupno_adapter);

            dostupno_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    odabranaDostupnost = parent.getSelectedItem().toString();
                    FilterBooks();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            ArrayAdapter<String> godinaIzdanja_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, godineIzdanja);
            godinaIzdanja_dropdown.setAdapter(godinaIzdanja_adapter);

            godinaIzdanja_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    odabranaGodina = parent.getSelectedItem().toString();
                    FilterBooks();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            ArrayAdapter<String> jezikIzdanja_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, jeziciIzdanja);
            jezikIzdanja_dropdown.setAdapter(jezikIzdanja_adapter);

            jezikIzdanja_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    odabraniJezik = parent.getSelectedItem().toString();
                    FilterBooks();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            ArrayAdapter<String> brojStranica_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, brojStranica);
            brojStranica_dropdown.setAdapter(brojStranica_adapter);

            brojStranica_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    odabraniBrStr = parent.getSelectedItem().toString();
                    FilterBooks();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            ArrayAdapter<String> uvez_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, uvezi);
            uvez_dropdown.setAdapter(uvez_adapter);

            uvez_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    odabraniUvez = parent.getSelectedItem().toString();
                    FilterBooks();
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });

        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), this.getResources().getString(R.string.greska_dohvacanje), Toast.LENGTH_SHORT).show();
        });
    }

    // Metoda za filtriranje podataka
    private void FilterBooks() {
        bookList.clear(); // Resetiranje liste knjiga
        bookList.addAll(originalBookList); // Vraćanje originalne liste knjiga

        //ŽANR
        if (!odabraniZanr.isEmpty() && odabraniZanr != getResources().getString(R.string.zanr)) {
            List<Book> filteredList = new ArrayList<>();
            for (Book book : bookList) {
                if (book.getZanr() == odabraniZanr) {
                    filteredList.add(book);
                }
            }
            bookList.clear();
            bookList.addAll(filteredList);
        }
        //NAKLADNIK
        if (!odabraniNakladnik.isEmpty() && odabraniNakladnik != getResources().getString(R.string.nakladnik)) {
            List<Book> filteredList = new ArrayList<>();
            for (Book book : bookList) {
                if (book.getNakladnik() == odabraniNakladnik) {
                    filteredList.add(book);
                }
            }
            bookList.clear();
            bookList.addAll(filteredList);
        }
        //LOKACIJA
        if (!odabranaLokacija.isEmpty() && odabranaLokacija != getResources().getString(R.string.lokacija)) {
            List<Book> filteredList = new ArrayList<>();
            for (Book book : bookList) {
                if (book.getLokacija() == odabranaLokacija) {
                    filteredList.add(book);
                }
            }
            bookList.clear();
            bookList.addAll(filteredList);
        }
        //DOSTUPNO
        if (!odabranaDostupnost.isEmpty() && odabranaDostupnost != getResources().getString(R.string.dostupno)) {
            List<Book> filteredList = new ArrayList<>();
            boolean dostupnost = getResources().getString(R.string.da).equalsIgnoreCase(odabranaDostupnost);
            for (Book book : bookList) {
                if (book.getDostupno() == dostupnost) {
                    filteredList.add(book);
                }
            }
            bookList.clear();
            bookList.addAll(filteredList);
        }

        //GODINA IZDANJA
        if (odabranaGodina != null && !odabranaGodina.isEmpty() && odabranaGodina != getResources().getString(R.string.godina_izdanja)) {
            List<Book> filteredList = new ArrayList<>();
            for (Book book : bookList) {
                if (book.getGodinaIzdanja() == Integer.parseInt(odabranaGodina)) {
                    filteredList.add(book);
                }
            }
            bookList.clear();
            bookList.addAll(filteredList);
        }

        //JEZIK IZDANJA
        if (!odabraniJezik.isEmpty() && odabraniJezik != getResources().getString(R.string.jezik_izdanja)) {
            List<Book> filteredList = new ArrayList<>();
            for (Book book : bookList) {
                if (book.getJezikIzdanja().toLowerCase(Locale.ROOT).trim().equals(odabraniJezik.toLowerCase(Locale.ROOT).trim())) {
                    filteredList.add(book);
                }
            }
            bookList.clear();
            bookList.addAll(filteredList);
        }

        //BROJ STRANICA
        if (!odabraniBrStr.isEmpty() && odabraniBrStr != getResources().getString(R.string.broj_stranica) && !brojStranica.isEmpty()) {
            List<Book> filteredList = new ArrayList<>();
            for (Book book : bookList) {
                if (book.getBrojStranica() == Integer.parseInt(odabraniBrStr)) {
                    filteredList.add(book);
                }
            }
            bookList.clear();
            bookList.addAll(filteredList);
        }

        //UVEZ
        if (!odabraniUvez.isEmpty() && odabraniUvez != getResources().getString(R.string.uvez)) {
            List<Book> filteredList = new ArrayList<>();
            for (Book book : bookList) {
                if (book.getUvez() == odabraniUvez) {
                    filteredList.add(book);
                }
            }
            bookList.clear();
            bookList.addAll(filteredList);
        }

        adapter.notifyDataSetChanged(); // Ažuriranje prikaza podataka
    }
}

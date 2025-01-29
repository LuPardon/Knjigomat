package com.example.knjigomat.books;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.knjigomat.R;
import com.example.knjigomat.ui.activities.MainActivity;
import com.example.knjigomat.ui.fragments.ReadBookFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private Context context;
    private List<Book> bookList;

    public BookAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_book, parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = bookList.get(position);

        // Postavljanje podataka u ViewHolder
        holder.bookTitle.setText(book.getNaslov());
        holder.bookAuthor.setText("Autor: " + book.getAutor());
        String slikica = book.getSlika() != null ? book.getSlika() : "https://media.istockphoto.com/id/183890264/photo/upright-red-book-with-clipping-path.jpg?s=612x612&w=0&k=20&c=zm6sEPnc4zK4MNj307pm3tzgxTbex2sOnb1Ip5hglaA=";
        Picasso.get().load(slikica).into(holder.bookImage); // Koristite Glide ili Picasso za slike

        // Klik na "Posudi" gumb
        holder.btnContact.setOnClickListener(v -> {
            // TODO: Dodaj logiku za posudbu knjige
            Log.d("BookAdapter", "Kliknuto na Kontaktiraj za knjigu: " + book.getNaslov());
        });

        // Klik na cijeli element (otvaranje detalja knjige)
        holder.itemView.setOnClickListener(v -> {
            Log.d("click", "Kliknuto");

            Bundle b = new Bundle();
            b.putString("knjigaID", book.getKnjigaID());
            b.putString("vlasnikID", book.getVlasnikID());
            ((MainActivity) context).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_fragment_container, ReadBookFragment.class, b)
//                .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    // ViewHolder klasa za upravljanje prikazom podataka
    public static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView bookTitle, bookAuthor;
        ImageView bookImage;
        Button btnContact;

        public BookViewHolder(View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.bookTitle);
            bookAuthor = itemView.findViewById(R.id.bookAuthor);
            bookImage = itemView.findViewById(R.id.bookImage);
            btnContact = itemView.findViewById(R.id.btnContact);
        }
    }

}

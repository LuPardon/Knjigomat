package com.example.knjigomat.books;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.knjigomat.R;
import com.example.knjigomat.ui.activities.BookActivity;
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
        Picasso.get().load(book.getSlika()).into(holder.bookImage); // Koristite Glide ili Picasso za slike

        // Klik na "Posudi" gumb
        holder.btnContact.setOnClickListener(v -> {
            // TODO: Dodaj logiku za posudbu knjige
            Log.d("BookAdapter", "Kliknuto na Kontaktiraj za knjigu: " + book.getNaslov());
        });

        // Klik na cijeli element (otvaranje detalja knjige)
        holder.itemView.setOnClickListener(v -> {
            Log.d("click", "Kliknuto");
            Intent intent = new Intent(context, BookActivity.class);

            // Slanje podataka o knjizi u novu aktivnost
            intent.putExtra("vlasnikID", book.getVlasnikID());
            intent.putExtra("autor", book.getAutor());
            intent.putExtra("brojStranica", book.getBrojStranica());
            intent.putExtra("dostupno", book.getDostupno());
            intent.putExtra("godinaIzdanja", book.getGodinaIzdanja());
            intent.putExtra("jezikIzdanja", book.getJezikIzdanja());
            intent.putExtra("lokacija", book.getLokacija());
            intent.putExtra("nakladnik", book.getNakladnik());
            intent.putExtra("naslov", book.getNaslov());
            intent.putExtra("opis", book.getOpis());
            intent.putExtra("slika", book.getSlika());
            intent.putExtra("uvez", book.getUvez());
            intent.putExtra("zanr", book.getZanr());


            context.startActivity(intent);
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

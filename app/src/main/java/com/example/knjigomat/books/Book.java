package com.example.knjigomat.books;

public class Book {
        private String naslov, autor, opis, jezikIzdanja, lokacija, nakladnik, slika, uvez, zanr, vlasnikID;
        private int brojStranica,godinaIzdanja;
        private boolean dostupno;

        public Book() {}

        public Book(String naslov, String autor, String opis,int brojStranica, boolean dostupno, int godinaIzdanja,String jezikIzdanja,String lokacija, String nakladnik, String slika, String uvez, String zanr, String vlasnikID) {
            this.naslov = naslov;
            this.autor = autor;
            this.opis = opis;
            this.brojStranica = brojStranica;
            this.dostupno = dostupno;
            this.godinaIzdanja = godinaIzdanja;
            this.jezikIzdanja = jezikIzdanja;
            this.lokacija = lokacija;
            this.nakladnik = nakladnik;
            this.slika = slika;
            this.uvez = uvez;
            this.zanr = zanr;
            this.vlasnikID = vlasnikID;
        }

        public String getNaslov() { return naslov; }
        public String getAutor() { return autor; }
        public String getOpis() { return opis;}
        public int getBrojStranica() { return brojStranica;}
        public boolean getDostupno() { return dostupno;}
        public int getGodinaIzdanja() { return godinaIzdanja;}
        public String getJezikIzdanja() { return jezikIzdanja;}
        public String getLokacija() { return lokacija;}
        public String getNakladnik() { return nakladnik;}
        public String getSlika() { return slika;}
        public String getUvez() { return uvez;}
        public String getZanr() { return zanr;}
        public String getVlasnikID() { return vlasnikID;}
    }

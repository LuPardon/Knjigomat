package com.example.knjigomat.chat;

public class Message {

    private String tekst, key, senderId;
    private long timestamp;  // Dodan timestamp za sortiranje poruka po vremenu

    public Message() {
        // Prazan konstruktor potreban za Firebase
    }

    public Message(String tekst, long timestamp, String senderId) {
        this.tekst = tekst;
        this.senderId = senderId;
        this.timestamp = timestamp;
    }

    // Getter i setter metode za sve atribute
    public String getTekst() {
        return tekst;
    }

    public void setTekst(String tekst) {
        this.tekst = tekst;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

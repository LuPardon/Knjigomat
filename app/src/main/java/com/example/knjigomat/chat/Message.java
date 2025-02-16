package com.example.knjigomat.chat;

public class Message {

    private String tekst;
    private String key;
    private String senderId;
    private boolean isImage;
    private String chatId;
    private long timestamp;  // Dodan timestamp za sortiranje poruka po vremenu

    //    public Message(String tekst, long timestamp, String senderId) {
//        this.tekst = tekst;
//        this.senderId = senderId;
//        this.timestamp = timestamp;
//    }
    public Message(String tekst, long timestamp, String senderId, boolean isImage) {
        this.tekst = tekst;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.isImage = isImage;
    }

    public Message() {
        // Prazan konstruktor potreban za Firebase
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public boolean isImage() {
        return isImage;
    }

    public void setImage(boolean image) {
        isImage = image;
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

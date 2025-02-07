package com.example.knjigomat.chat;

import java.util.List;

public class chat {
    private String id;               // Jedinstveni ID chata
    private List<Message> messages;   // Lista poruka
    private List<String> participants; // Lista korisnika u chatu

    public chat() {
        // Prazan konstruktor potreban za Firebase
    }

    public chat(String id, List<Message> messages, List<String> participants) {
        this.id = id;
        this.messages = messages;
        this.participants = participants;
    }

    // Getter i setter metode
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<String> getParticipants() {
        return participants;
    }

    public void setParticipants(List<String> participants) {
        this.participants = participants;
    }
}

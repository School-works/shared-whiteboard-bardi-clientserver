package com.example;

public class Message {
    int id;
    String author;
    String text;
    public int getId() {
        return id;
    }
    public String getAuthor() {
        return author;
    }
    public String getText() {
        return text;
    }
    public Message(int id, String author, String text) {
        this.id = id;
        this.author = author;
        this.text = text;
    }

   
}

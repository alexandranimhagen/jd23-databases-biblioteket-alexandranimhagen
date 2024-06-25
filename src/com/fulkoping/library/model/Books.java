package com.fulkoping.library.model;

public class Books {
    private int id;
    private String title;
    private String author;
    private boolean available;

    public Books(int id, String title, String author, boolean available) {
        this.id = this.id;
        this.title = this.title;
        this.author = this.author;
        this.available = this.available;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setGenre(String genre) {
    }
}

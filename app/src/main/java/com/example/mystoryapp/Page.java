package com.example.mystoryapp;

public class Page {
    private String text;
    private String url;

    public Page() {
        // Default constructor required for calls to DataSnapshot.getValue(Page.class)
    }

    public Page(String text, String url) {
        this.text = text;
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

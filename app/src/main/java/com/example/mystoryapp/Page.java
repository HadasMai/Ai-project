/**
 * Page - A model class representing a page in a storybook.
 * Each page contains text content, an image URL, a style, and a unique page ID.
 */

package com.example.mystoryapp;

public class Page {
    private String text;     // The text content of the page
    private String url;      // The URL of the image associated with the page
    private String style;    // The style of the page (e.g., illustration style, not yet used in this class)
    private String pageId;   // The unique identifier for the page 

    public Page() {
        // Default constructor 
    }


    /**
     * Constructor to initialize the page with text and image URL.
     * @param text The text content of the page.
     * @param url The URL of the image associated with the page.
     */
    public Page(String text, String url) {
        this.text = text;
        this.url = url;

    }

     /**
     * @return The text content of the page.
     */
    public String getText() {
        return text;
    }

   /**
     * Sets the text content of the page.
     * @param text The text content to be set for the page.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return The URL of the image associated with the page.
     */
    public String getUrl() {
        return url;
    }

     /**
     * Sets the URL of the image associated with the page.
     * @param url The image URL to be set for the page.
     */
    public void setUrl(String url) {
        this.url = url;
    }
}

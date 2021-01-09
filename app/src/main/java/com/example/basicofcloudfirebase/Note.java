package com.example.basicofcloudfirebase;

import com.google.firebase.firestore.Exclude;

public class Note {

    private String title;
    private String description;
    private String docId;
    private int priority;


    public Note() {
        // Default Constructor with no argument...
    }

    public Note(String title, String description,int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    @Exclude
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}

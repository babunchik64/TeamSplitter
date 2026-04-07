package com.app.teamsplitter.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "players")
public class Player {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String photoPath;
    private String comment;

    // Constructor
    public Player(String name, String photoPath, String comment) {
        this.name = name;
        this.photoPath = photoPath;
        this.comment = comment;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhotoPath() { return photoPath; }
    public String getComment() { return comment; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPhotoPath(String photoPath) { this.photoPath = photoPath; }
    public void setComment(String comment) { this.comment = comment; }
}
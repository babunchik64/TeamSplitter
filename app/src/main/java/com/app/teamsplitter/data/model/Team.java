package com.app.teamsplitter.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "teams")
public class Team {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String color;

    // Constructor
    public Team(String name, String color) {
        this.name = name;
        this.color = color;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getColor() { return color; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setColor(String color) { this.color = color; }
}
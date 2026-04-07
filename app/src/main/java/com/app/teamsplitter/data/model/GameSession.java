package com.app.teamsplitter.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "game_sessions")
public class GameSession {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private long date; // храним как timestamp

    // Constructor
    public GameSession(long date) {
        this.date = date;
    }

    // Getters
    public int getId() { return id; }
    public long getDate() { return date; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setDate(long date) { this.date = date; }
}
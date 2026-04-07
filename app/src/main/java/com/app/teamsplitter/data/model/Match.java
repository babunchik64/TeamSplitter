package com.app.teamsplitter.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "matches",
        foreignKeys = {
                @ForeignKey(
                        entity = GameSession.class,
                        parentColumns = "id",
                        childColumns = "sessionId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Team.class,
                        parentColumns = "id",
                        childColumns = "homeTeamId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Team.class,
                        parentColumns = "id",
                        childColumns = "awayTeamId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class Match {

    public enum Status {
        PLANNED,
        IN_PROGRESS,
        FINISHED
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int sessionId;
    private int homeTeamId;
    private int awayTeamId;
    private int homeScore;
    private int awayScore;
    private Status status;

    // Constructor
    public Match(int sessionId, int homeTeamId, int awayTeamId) {
        this.sessionId = sessionId;
        this.homeTeamId = homeTeamId;
        this.awayTeamId = awayTeamId;
        this.homeScore = 0;
        this.awayScore = 0;
        this.status = Status.PLANNED;
    }

    // Getters
    public int getId() { return id; }
    public int getSessionId() { return sessionId; }
    public int getHomeTeamId() { return homeTeamId; }
    public int getAwayTeamId() { return awayTeamId; }
    public int getHomeScore() { return homeScore; }
    public int getAwayScore() { return awayScore; }
    public Status getStatus() { return status; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public void setHomeTeamId(int homeTeamId) { this.homeTeamId = homeTeamId; }
    public void setAwayTeamId(int awayTeamId) { this.awayTeamId = awayTeamId; }
    public void setHomeScore(int homeScore) { this.homeScore = homeScore; }
    public void setAwayScore(int awayScore) { this.awayScore = awayScore; }
    public void setStatus(Status status) { this.status = status; }
}
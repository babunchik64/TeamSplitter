package com.app.teamsplitter.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "session_teams",
        foreignKeys = {
                @ForeignKey(
                        entity = GameSession.class,
                        parentColumns = "id",
                        childColumns = "sessionId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class SessionTeam {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private int sessionId;
    private String teamName;
    private String teamColor;

    public SessionTeam(int sessionId, String teamName, String teamColor) {
        this.sessionId = sessionId;
        this.teamName = teamName;
        this.teamColor = teamColor;
    }

    public int getId() { return id; }
    public int getSessionId() { return sessionId; }
    public String getTeamName() { return teamName; }
    public String getTeamColor() { return teamColor; }

    public void setId(int id) { this.id = id; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public void setTeamColor(String teamColor) { this.teamColor = teamColor; }
}
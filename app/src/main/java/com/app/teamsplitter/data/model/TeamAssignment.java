package com.app.teamsplitter.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "team_assignments",
        primaryKeys = {"sessionId", "playerId"},
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
                        childColumns = "teamId",
                        onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Player.class,
                        parentColumns = "id",
                        childColumns = "playerId",
                        onDelete = ForeignKey.CASCADE
                )
        }
)
public class TeamAssignment {

    private int sessionId;
    private int teamId;
    private int playerId;

    // Constructor
    public TeamAssignment(int sessionId, int teamId, int playerId) {
        this.sessionId = sessionId;
        this.teamId = teamId;
        this.playerId = playerId;
    }

    // Getters
    public int getSessionId() { return sessionId; }
    public int getTeamId() { return teamId; }
    public int getPlayerId() { return playerId; }

    // Setters
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
}
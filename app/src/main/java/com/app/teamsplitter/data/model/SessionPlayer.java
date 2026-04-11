package com.app.teamsplitter.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "session_players",
        primaryKeys = {"sessionId", "playerId"},
        foreignKeys = {
                @ForeignKey(
                        entity = GameSession.class,
                        parentColumns = "id",
                        childColumns = "sessionId",
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
public class SessionPlayer {

    private int sessionId;
    private int playerId;
    private int teamId; // 0 = не в команде

    public SessionPlayer(int sessionId, int playerId, int teamId) {
        this.sessionId = sessionId;
        this.playerId = playerId;
        this.teamId = teamId;
    }

    public int getSessionId() { return sessionId; }
    public int getPlayerId() { return playerId; }
    public int getTeamId() { return teamId; }

    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public void setTeamId(int teamId) { this.teamId = teamId; }
}
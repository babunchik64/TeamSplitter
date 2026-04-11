package com.app.teamsplitter.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;

@Entity(
        tableName = "match_lineups",
        primaryKeys = {"matchId", "playerId"},
        foreignKeys = {
                @ForeignKey(
                        entity = Match.class,
                        parentColumns = "id",
                        childColumns = "matchId",
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
public class MatchLineup {

    private int matchId;
    private int playerId;
    private boolean isHome; // true = домашняя команда, false = гостевая

    public MatchLineup(int matchId, int playerId, boolean isHome) {
        this.matchId = matchId;
        this.playerId = playerId;
        this.isHome = isHome;
    }

    public int getMatchId() { return matchId; }
    public int getPlayerId() { return playerId; }
    public boolean isHome() { return isHome; }

    public void setMatchId(int matchId) { this.matchId = matchId; }
    public void setPlayerId(int playerId) { this.playerId = playerId; }
    public void setHome(boolean home) { isHome = home; }
}
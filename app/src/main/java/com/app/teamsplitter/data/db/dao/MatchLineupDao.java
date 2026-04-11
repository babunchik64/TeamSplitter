package com.app.teamsplitter.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.teamsplitter.data.model.MatchLineup;
import com.app.teamsplitter.data.model.Player;

import java.util.List;

@Dao
public interface MatchLineupDao {

    @Insert
    void insert(MatchLineup lineup);

    @Query("SELECT players.* FROM players " +
            "INNER JOIN match_lineups ON players.id = match_lineups.playerId " +
            "WHERE match_lineups.matchId = :matchId AND match_lineups.isHome = 1")
    LiveData<List<Player>> getHomePlayersByMatch(int matchId);

    @Query("SELECT players.* FROM players " +
            "INNER JOIN match_lineups ON players.id = match_lineups.playerId " +
            "WHERE match_lineups.matchId = :matchId AND match_lineups.isHome = 0")
    LiveData<List<Player>> getAwayPlayersByMatch(int matchId);

    @Query("DELETE FROM match_lineups WHERE matchId = :matchId")
    void deleteByMatch(int matchId);
}
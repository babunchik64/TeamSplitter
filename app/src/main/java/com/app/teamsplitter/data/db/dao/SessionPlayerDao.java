package com.app.teamsplitter.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.teamsplitter.data.model.Player;
import com.app.teamsplitter.data.model.SessionPlayer;

import java.util.List;

@Dao
public interface SessionPlayerDao {

    @Insert
    void insert(SessionPlayer sessionPlayer);

    @Query("SELECT * FROM session_players WHERE sessionId = :sessionId")
    LiveData<List<SessionPlayer>> getBySession(int sessionId);

    @Query("SELECT players.* FROM players " +
            "INNER JOIN session_players ON players.id = session_players.playerId " +
            "WHERE session_players.sessionId = :sessionId")
    LiveData<List<Player>> getPlayersBySession(int sessionId);

    @Query("DELETE FROM session_players WHERE sessionId = :sessionId")
    void deleteBySession(int sessionId);
}
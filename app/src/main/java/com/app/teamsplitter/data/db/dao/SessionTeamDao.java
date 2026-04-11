package com.app.teamsplitter.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.teamsplitter.data.model.SessionTeam;

import java.util.List;

@Dao
public interface SessionTeamDao {

    @Insert
    long insert(SessionTeam sessionTeam);

    @Query("SELECT * FROM session_teams WHERE sessionId = :sessionId")
    LiveData<List<SessionTeam>> getBySession(int sessionId);

    @Query("SELECT * FROM session_teams WHERE sessionId = :sessionId")
    List<SessionTeam> getBySessionSync(int sessionId);

    @Query("DELETE FROM session_teams WHERE sessionId = :sessionId")
    void deleteBySession(int sessionId);
}
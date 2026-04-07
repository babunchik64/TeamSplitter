package com.app.teamsplitter.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.app.teamsplitter.data.model.Player;
import com.app.teamsplitter.data.model.TeamAssignment;

import java.util.List;

@Dao
public interface TeamAssignmentDao {

    @Query("SELECT * FROM team_assignments WHERE sessionId = :sessionId")
    LiveData<List<TeamAssignment>> getBySession(int sessionId);

    @Query("SELECT * FROM team_assignments WHERE sessionId = :sessionId AND teamId = :teamId")
    LiveData<List<TeamAssignment>> getBySessionAndTeam(int sessionId, int teamId);

    @Query(
            "SELECT players.* FROM players " +
                    "INNER JOIN team_assignments ON players.id = team_assignments.playerId " +
                    "WHERE team_assignments.sessionId = :sessionId " +
                    "AND team_assignments.teamId = :teamId"
    )
    LiveData<List<Player>> getPlayersByTeamAndSession(int sessionId, int teamId);

    @Insert
    void insert(TeamAssignment assignment);

    @Delete
    void delete(TeamAssignment assignment);

    @Query("DELETE FROM team_assignments WHERE sessionId = :sessionId")
    void deleteBySession(int sessionId);
}
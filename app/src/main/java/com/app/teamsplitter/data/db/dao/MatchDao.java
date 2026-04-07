package com.app.teamsplitter.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.app.teamsplitter.data.model.Match;

import java.util.List;

@Dao
public interface MatchDao {

    @Query("SELECT * FROM matches WHERE sessionId = :sessionId")
    LiveData<List<Match>> getBySession(int sessionId);

    @Query("SELECT * FROM matches WHERE id = :id")
    LiveData<Match> getById(int id);

    @Query("SELECT * FROM matches ORDER BY id DESC")
    LiveData<List<Match>> getAll();

    @Insert
    long insert(Match match); // возвращает id созданного матча

    @Update
    void update(Match match);

    @Delete
    void delete(Match match);
}
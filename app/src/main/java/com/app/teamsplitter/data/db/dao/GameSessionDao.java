package com.app.teamsplitter.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.app.teamsplitter.data.model.GameSession;

import java.util.List;

@Dao
public interface GameSessionDao {

    @Query("SELECT * FROM game_sessions ORDER BY date DESC")
    LiveData<List<GameSession>> getAll();

    @Query("SELECT * FROM game_sessions WHERE id = :id")
    LiveData<GameSession> getById(int id);

    @Insert
    long insert(GameSession session); // возвращает id созданной сессии

    @Update
    void update(GameSession session);

    @Delete
    void delete(GameSession session);
}
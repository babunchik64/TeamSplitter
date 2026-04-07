package com.app.teamsplitter.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.app.teamsplitter.data.model.Player;

import java.util.List;

@Dao
public interface PlayerDao {

    @Query("SELECT * FROM players ORDER BY name ASC")
    LiveData<List<Player>> getAll();

    @Query("SELECT * FROM players WHERE id = :id")
    LiveData<Player> getById(int id);

    @Insert
    void insert(Player player);

    @Update
    void update(Player player);

    @Delete
    void delete(Player player);
}
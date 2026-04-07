package com.app.teamsplitter.data.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.app.teamsplitter.data.model.Team;

import java.util.List;

@Dao
public interface TeamDao {

    @Query("SELECT * FROM teams")
    LiveData<List<Team>> getAll();

    @Query("SELECT * FROM teams WHERE id = :id")
    LiveData<Team> getById(int id);

    @Insert
    void insert(Team team);

    @Update
    void update(Team team);

    @Delete
    void delete(Team team);
}
package com.app.teamsplitter.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.app.teamsplitter.data.db.AppDatabase;
import com.app.teamsplitter.data.db.dao.PlayerDao;
import com.app.teamsplitter.data.model.Player;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlayerRepository {

    private final PlayerDao playerDao;
    private final ExecutorService executor;

    public PlayerRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        playerDao = db.playerDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Player>> getAll() {
        return playerDao.getAll();
    }

    public LiveData<Player> getById(int id) {
        return playerDao.getById(id);
    }

    public void insert(Player player) {
        executor.execute(() -> playerDao.insert(player));
    }

    public void update(Player player) {
        executor.execute(() -> playerDao.update(player));
    }

    public void delete(Player player) {
        executor.execute(() -> playerDao.delete(player));
    }
}
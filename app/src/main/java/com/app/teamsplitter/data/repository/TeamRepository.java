package com.app.teamsplitter.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.app.teamsplitter.data.db.AppDatabase;
import com.app.teamsplitter.data.db.dao.TeamDao;
import com.app.teamsplitter.data.model.Team;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TeamRepository {

    private final TeamDao teamDao;
    private final ExecutorService executor;

    public TeamRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        teamDao = db.teamDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Team>> getAll() {
        return teamDao.getAll();
    }

    public LiveData<Team> getById(int id) {
        return teamDao.getById(id);
    }

    public void insert(Team team) {
        executor.execute(() -> teamDao.insert(team));
    }

    public void update(Team team) {
        executor.execute(() -> teamDao.update(team));
    }

    public void delete(Team team) {
        executor.execute(() -> teamDao.delete(team));
    }
}
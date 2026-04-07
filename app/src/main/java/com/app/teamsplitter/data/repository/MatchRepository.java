package com.app.teamsplitter.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.app.teamsplitter.data.db.AppDatabase;
import com.app.teamsplitter.data.db.dao.MatchDao;
import com.app.teamsplitter.data.model.Match;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MatchRepository {

    private final MatchDao matchDao;
    private final ExecutorService executor;

    public MatchRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        matchDao = db.matchDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<Match>> getAll() {
        return matchDao.getAll();
    }

    public LiveData<List<Match>> getBySession(int sessionId) {
        return matchDao.getBySession(sessionId);
    }

    public LiveData<Match> getById(int id) {
        return matchDao.getById(id);
    }

    public void insert(Match match) {
        executor.execute(() -> matchDao.insert(match));
    }

    public void update(Match match) {
        executor.execute(() -> matchDao.update(match));
    }

    public void delete(Match match) {
        executor.execute(() -> matchDao.delete(match));
    }

    // Обновить счёт матча
    public void updateScore(Match match, int homeScore, int awayScore) {
        executor.execute(() -> {
            match.setHomeScore(homeScore);
            match.setAwayScore(awayScore);
            match.setStatus(Match.Status.FINISHED);
            matchDao.update(match);
        });
    }
}
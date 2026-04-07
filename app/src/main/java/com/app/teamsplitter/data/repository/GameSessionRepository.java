package com.app.teamsplitter.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.app.teamsplitter.data.db.AppDatabase;
import com.app.teamsplitter.data.db.dao.GameSessionDao;
import com.app.teamsplitter.data.db.dao.TeamAssignmentDao;
import com.app.teamsplitter.data.model.GameSession;
import com.app.teamsplitter.data.model.Player;
import com.app.teamsplitter.data.model.TeamAssignment;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameSessionRepository {

    private final GameSessionDao gameSessionDao;
    private final TeamAssignmentDao teamAssignmentDao;
    private final ExecutorService executor;

    public GameSessionRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        gameSessionDao = db.gameSessionDao();
        teamAssignmentDao = db.teamAssignmentDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<GameSession>> getAll() {
        return gameSessionDao.getAll();
    }

    public LiveData<GameSession> getById(int id) {
        return gameSessionDao.getById(id);
    }

    public void insert(GameSession session) {
        executor.execute(() -> gameSessionDao.insert(session));
    }

    public void update(GameSession session) {
        executor.execute(() -> gameSessionDao.update(session));
    }

    public void delete(GameSession session) {
        executor.execute(() -> gameSessionDao.delete(session));
    }

    // Назначить игрока в команду
    public void assignPlayer(TeamAssignment assignment) {
        executor.execute(() -> teamAssignmentDao.insert(assignment));
    }

    // Убрать игрока из команды
    public void removeAssignment(TeamAssignment assignment) {
        executor.execute(() -> teamAssignmentDao.delete(assignment));
    }

    // Получить игроков команды в сессии
    public LiveData<List<Player>> getPlayersByTeamAndSession(int sessionId, int teamId) {
        return teamAssignmentDao.getPlayersByTeamAndSession(sessionId, teamId);
    }

    // Очистить все назначения сессии
    public void clearAssignments(int sessionId) {
        executor.execute(() -> teamAssignmentDao.deleteBySession(sessionId));
    }
}
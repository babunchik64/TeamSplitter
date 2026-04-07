package com.app.teamsplitter.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.teamsplitter.data.model.GameSession;
import com.app.teamsplitter.data.model.Player;
import com.app.teamsplitter.data.model.TeamAssignment;
import com.app.teamsplitter.data.repository.GameSessionRepository;

import java.util.List;

public class GameSessionViewModel extends AndroidViewModel {

    private final GameSessionRepository repository;
    private final LiveData<List<GameSession>> allSessions;

    public GameSessionViewModel(@NonNull Application application) {
        super(application);
        repository = new GameSessionRepository(application);
        allSessions = repository.getAll();
    }

    public LiveData<List<GameSession>> getAllSessions() {
        return allSessions;
    }

    public LiveData<GameSession> getById(int id) {
        return repository.getById(id);
    }

    public void insert(GameSession session) {
        repository.insert(session);
    }

    public void update(GameSession session) {
        repository.update(session);
    }

    public void delete(GameSession session) {
        repository.delete(session);
    }

    public void assignPlayer(TeamAssignment assignment) {
        repository.assignPlayer(assignment);
    }

    public void removeAssignment(TeamAssignment assignment) {
        repository.removeAssignment(assignment);
    }

    public LiveData<List<Player>> getPlayersByTeamAndSession(int sessionId, int teamId) {
        return repository.getPlayersByTeamAndSession(sessionId, teamId);
    }

    public void clearAssignments(int sessionId) {
        repository.clearAssignments(sessionId);
    }
}
package com.app.teamsplitter.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.teamsplitter.data.model.GameSession;
import com.app.teamsplitter.data.model.Match;
import com.app.teamsplitter.data.model.Player;
import com.app.teamsplitter.data.model.SessionPlayer;
import com.app.teamsplitter.data.model.SessionTeam;
import com.app.teamsplitter.data.repository.HistoryRepository;

import java.util.List;

public class HistoryViewModel extends AndroidViewModel {

    private final HistoryRepository repository;
    private final LiveData<List<GameSession>> allSessions;

    public HistoryViewModel(@NonNull Application application) {
        super(application);
        repository = new HistoryRepository(application);
        allSessions = repository.getAllSessions();
    }

    public LiveData<List<GameSession>> getAllSessions() {
        return allSessions;
    }

    public LiveData<List<SessionTeam>> getTeamsBySession(int sessionId) {
        return repository.getTeamsBySession(sessionId);
    }

    public LiveData<List<Player>> getPlayersBySession(int sessionId) {
        return repository.getPlayersBySession(sessionId);
    }

    public LiveData<List<Match>> getMatchesBySession(int sessionId) {
        return repository.getMatchesBySession(sessionId);
    }

    public LiveData<List<SessionPlayer>> getSessionPlayersBySession(int sessionId) {
        return repository.getSessionPlayersBySession(sessionId);
    }

    public void saveSession(
            List<Player> presentPlayers,
            List<String> teamNames,
            List<List<Player>> teamPlayersList,
            HistoryRepository.SaveSessionCallback callback
    ) {
        repository.saveSession(presentPlayers, teamNames, teamPlayersList, callback);
    }



    public void addMatch(Match match, HistoryRepository.SaveMatchCallback callback) {
        repository.addMatch(match, callback);
    }

    public void updateMatch(Match match) {
        repository.updateMatch(match);
    }

    public void deleteMatch(Match match) {
        repository.deleteMatch(match);
    }

    public void deleteSession(GameSession session) {
        repository.deleteSession(session);
    }
}
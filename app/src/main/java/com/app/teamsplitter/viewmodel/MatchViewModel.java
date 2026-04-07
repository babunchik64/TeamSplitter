package com.app.teamsplitter.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.teamsplitter.data.model.Match;
import com.app.teamsplitter.data.repository.MatchRepository;

import java.util.List;

public class MatchViewModel extends AndroidViewModel {

    private final MatchRepository repository;
    private final LiveData<List<Match>> allMatches;

    public MatchViewModel(@NonNull Application application) {
        super(application);
        repository = new MatchRepository(application);
        allMatches = repository.getAll();
    }

    public LiveData<List<Match>> getAllMatches() {
        return allMatches;
    }

    public LiveData<List<Match>> getBySession(int sessionId) {
        return repository.getBySession(sessionId);
    }

    public LiveData<Match> getById(int id) {
        return repository.getById(id);
    }

    public void insert(Match match) {
        repository.insert(match);
    }

    public void update(Match match) {
        repository.update(match);
    }

    public void delete(Match match) {
        repository.delete(match);
    }

    public void updateScore(Match match, int homeScore, int awayScore) {
        repository.updateScore(match, homeScore, awayScore);
    }
}
package com.app.teamsplitter.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.teamsplitter.data.model.Team;
import com.app.teamsplitter.data.repository.TeamRepository;

import java.util.List;

public class TeamViewModel extends AndroidViewModel {

    private final TeamRepository repository;
    private final LiveData<List<Team>> allTeams;

    public TeamViewModel(@NonNull Application application) {
        super(application);
        repository = new TeamRepository(application);
        allTeams = repository.getAll();
    }

    public LiveData<List<Team>> getAllTeams() {
        return allTeams;
    }

    public LiveData<Team> getById(int id) {
        return repository.getById(id);
    }

    public void insert(Team team) {
        repository.insert(team);
    }

    public void update(Team team) {
        repository.update(team);
    }

    public void delete(Team team) {
        repository.delete(team);
    }
}
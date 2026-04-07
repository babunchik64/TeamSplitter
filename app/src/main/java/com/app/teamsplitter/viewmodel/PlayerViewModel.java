package com.app.teamsplitter.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.teamsplitter.data.model.Player;
import com.app.teamsplitter.data.repository.PlayerRepository;

import java.util.List;

public class PlayerViewModel extends AndroidViewModel {

    private final PlayerRepository repository;
    private final LiveData<List<Player>> allPlayers;

    public PlayerViewModel(@NonNull Application application) {
        super(application);
        repository = new PlayerRepository(application);
        allPlayers = repository.getAll();
    }

    public LiveData<List<Player>> getAllPlayers() {
        return allPlayers;
    }

    public LiveData<Player> getById(int id) {
        return repository.getById(id);
    }

    public void insert(Player player) {
        repository.insert(player);
    }

    public void update(Player player) {
        repository.update(player);
    }

    public void delete(Player player) {
        repository.delete(player);
    }
}
package com.app.teamsplitter.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.app.teamsplitter.data.db.AppDatabase;
import com.app.teamsplitter.data.db.dao.GameSessionDao;
import com.app.teamsplitter.data.db.dao.MatchDao;
import com.app.teamsplitter.data.db.dao.MatchLineupDao;
import com.app.teamsplitter.data.db.dao.SessionPlayerDao;
import com.app.teamsplitter.data.db.dao.SessionTeamDao;
import com.app.teamsplitter.data.model.GameSession;
import com.app.teamsplitter.data.model.Match;
import com.app.teamsplitter.data.model.MatchLineup;
import com.app.teamsplitter.data.model.Player;
import com.app.teamsplitter.data.model.SessionPlayer;
import com.app.teamsplitter.data.model.SessionTeam;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryRepository {

    private final GameSessionDao gameSessionDao;
    private final SessionTeamDao sessionTeamDao;
    private final SessionPlayerDao sessionPlayerDao;
    private final MatchDao matchDao;
    private final MatchLineupDao matchLineupDao;
    private final ExecutorService executor;

    public HistoryRepository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        gameSessionDao = db.gameSessionDao();
        sessionTeamDao = db.sessionTeamDao();
        sessionPlayerDao = db.sessionPlayerDao();
        matchDao = db.matchDao();
        matchLineupDao = db.matchLineupDao();
        executor = Executors.newSingleThreadExecutor();
    }

    // Сохранить сессию со всеми командами и игроками
    public void saveSession(
            List<Player> presentPlayers,
            List<String> teamNames,
            List<List<Player>> teamPlayersList,
            SaveSessionCallback callback
    ) {
        executor.execute(() -> {
            // 1. Создаём сессию
            long sessionId = gameSessionDao.insert(new GameSession(System.currentTimeMillis()));

            // 2. Сохраняем команды
            for (int i = 0; i < teamNames.size(); i++) {
                SessionTeam sessionTeam = new SessionTeam((int) sessionId, teamNames.get(i), "");
                long teamId = sessionTeamDao.insert(sessionTeam);

                // 3. Сохраняем игроков команды
                List<Player> players = teamPlayersList.get(i);
                for (Player player : players) {
                    sessionPlayerDao.insert(new SessionPlayer((int) sessionId, player.getId(), (int) teamId));
                }
            }

            // 4. Сохраняем игроков без команды
            for (Player player : presentPlayers) {
                boolean inTeam = false;
                for (List<Player> teamPlayers : teamPlayersList) {
                    if (teamPlayers.stream().anyMatch(p -> p.getId() == player.getId())) {
                        inTeam = true;
                        break;
                    }
                }
                if (!inTeam) {
                    sessionPlayerDao.insert(new SessionPlayer((int) sessionId, player.getId(), 0));
                }
            }

            if (callback != null) {
                callback.onSaved((int) sessionId);
            }
        });
    }

    // Получить все сессии
    public LiveData<List<GameSession>> getAllSessions() {
        return gameSessionDao.getAll();
    }

    // Получить команды сессии
    public LiveData<List<SessionTeam>> getTeamsBySession(int sessionId) {
        return sessionTeamDao.getBySession(sessionId);
    }

    // Получить игроков сессии
    public LiveData<List<Player>> getPlayersBySession(int sessionId) {
        return sessionPlayerDao.getPlayersBySession(sessionId);
    }

    public LiveData<List<SessionPlayer>> getSessionPlayersBySession(int sessionId) {
        return sessionPlayerDao.getBySession(sessionId);
    }

    // Получить матчи сессии
    public LiveData<List<Match>> getMatchesBySession(int sessionId) {
        return matchDao.getBySession(sessionId);
    }

    // Добавить матч
    public void addMatch(Match match, SaveMatchCallback callback) {
        executor.execute(() -> {
            long matchId = matchDao.insert(match);
            if (callback != null) callback.onSaved((int) matchId);
        });
    }

    // Обновить матч
    public void updateMatch(Match match) {
        executor.execute(() -> matchDao.update(match));
    }

    // Удалить матч
    public void deleteMatch(Match match) {
        executor.execute(() -> matchDao.delete(match));
    }

    // Удалить сессию
    public void deleteSession(GameSession session) {
        executor.execute(() -> gameSessionDao.delete(session));
    }

    public interface SaveSessionCallback {
        void onSaved(int sessionId);
    }

    public interface SaveMatchCallback {
        void onSaved(int matchId);
    }
}
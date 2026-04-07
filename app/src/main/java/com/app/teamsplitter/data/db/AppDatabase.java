package com.app.teamsplitter.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.app.teamsplitter.data.db.dao.GameSessionDao;
import com.app.teamsplitter.data.db.dao.MatchDao;
import com.app.teamsplitter.data.db.dao.PlayerDao;
import com.app.teamsplitter.data.db.dao.TeamAssignmentDao;
import com.app.teamsplitter.data.db.dao.TeamDao;
import com.app.teamsplitter.data.model.GameSession;
import com.app.teamsplitter.data.model.Match;
import com.app.teamsplitter.data.model.Player;
import com.app.teamsplitter.data.model.Team;
import com.app.teamsplitter.data.model.TeamAssignment;

@Database(
        entities = {
                Player.class,
                Team.class,
                GameSession.class,
                TeamAssignment.class,
                Match.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // DAO методы
    public abstract PlayerDao playerDao();
    public abstract TeamDao teamDao();
    public abstract GameSessionDao gameSessionDao();
    public abstract TeamAssignmentDao teamAssignmentDao();
    public abstract MatchDao matchDao();

    // Singleton паттерн
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "teamsplitter_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}
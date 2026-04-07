package com.app.teamsplitter;

import android.app.Application;

import com.app.teamsplitter.data.db.AppDatabase;

public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppDatabase.getInstance(this);
    }
}
package com.agadimi.agplayer.app;

import android.app.Application;

import com.agadimi.agplayer.dagger.components.*;
import com.agadimi.agplayer.dagger.components.*;
import com.agadimi.agplayer.dagger.modules.AppModule;

import timber.log.Timber;

public class App extends Application
{
    public AppComponent appComponent;

    @Override
    public void onCreate()
    {
        super.onCreate();
        //dagger
        appComponent = DaggerAppComponent.builder().withApplication(this).build();

        //plant logger
        Timber.plant(new Timber.DebugTree());

    }
}
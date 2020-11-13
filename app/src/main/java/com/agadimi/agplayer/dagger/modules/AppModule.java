package com.agadimi.agplayer.dagger.modules;

import android.app.Application;
import android.content.ContentUris;
import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule
{

    @Singleton
    @Provides
    public Context provideApplicationContext(Application application)
    {
        return application;
    }
}

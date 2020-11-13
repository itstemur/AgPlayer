package com.agadimi.agplayer.dagger.components;

import android.app.Application;

import com.agadimi.agplayer.dagger.modules.AppModule;
import com.agadimi.agplayer.ui.activities.HomeActivity;
import com.agadimi.agplayer.ui.activities.PlayerActivity;
import com.agadimi.agplayer.ui.fragments.FileListFragment;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = AppModule.class)
public interface AppComponent
{
    void inject(HomeActivity homeActivity);

    void inject(PlayerActivity playerActivity);

    void inject(FileListFragment Application);


    @Component.Builder
    interface Builder
    {
        AppComponent build();

        @BindsInstance
        Builder withApplication(Application application);
    }
}

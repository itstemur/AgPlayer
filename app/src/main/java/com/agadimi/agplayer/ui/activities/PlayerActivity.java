package com.agadimi.agplayer.ui.activities;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agadimi.agplayer.R;
import com.agadimi.agplayer.app.App;
import com.agadimi.agplayer.databinding.ActivityPlayerBinding;
import com.agadimi.agplayer.models.VideoFile;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.util.Util;

import timber.log.Timber;

public class PlayerActivity extends AppCompatActivity
{
    private ActivityPlayerBinding binding;
    private SimpleExoPlayer exoPlayer;

    private Uri theFile;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //dagger
        ((App) getApplication()).appComponent.inject(this);

        //view binding
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        handleIntent();
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (Util.SDK_INT >= 24)
        {
            initializePlayer();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || exoPlayer == null))
        {
            initializePlayer();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (Util.SDK_INT < 24)
        {
            releasePlayer();
        }
    }

    @Override
    public void onStop()
    {
        super.onStop();
        if (Util.SDK_INT >= 24)
        {
            releasePlayer();
        }
    }

    private void handleIntent()
    {
        try
        {
            Bundle bundle = getIntent().getExtras();
            theFile = Uri.parse(bundle.getString(VideoFile.INTENT_KEY));
        }
        catch (Exception e)
        {
            Timber.e(e);
        }
    }

    private void initializePlayer()
    {
        exoPlayer = new SimpleExoPlayer.Builder(this).build();
        binding.playerView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(theFile);
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(currentWindow, playbackPosition);
        exoPlayer.prepare();
        exoPlayer.play();
    }

    private void releasePlayer()
    {
        if (exoPlayer != null)
        {
            playWhenReady = exoPlayer.getPlayWhenReady();
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi()
    {
        binding.playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}

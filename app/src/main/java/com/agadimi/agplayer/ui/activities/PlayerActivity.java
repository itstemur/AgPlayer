package com.agadimi.agplayer.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.agadimi.agplayer.R;
import com.agadimi.agplayer.app.App;
import com.agadimi.agplayer.common.DoubleClickListener;
import com.agadimi.agplayer.common.SeekBar;
import com.agadimi.agplayer.databinding.ActivityPlayerBinding;
import com.agadimi.agplayer.models.VideoFile;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RendererCapabilities;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;

import timber.log.Timber;

public class PlayerActivity extends AppCompatActivity implements Player.EventListener, Runnable, SeekBar.OnProgressChangedListener
{
    private ActivityPlayerBinding binding;
    private SimpleExoPlayer exoPlayer;
    private DefaultTrackSelector defaultTrackSelector;
    private Handler progressHandler;

    private VideoFile theFile;
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
        setupClicksAndStuff();


        handleIntent();
        displayContent();
    }

    private void setupClicksAndStuff()
    {
        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.playerOverlay.setOnClickListener(new DoubleClickListener()
        {
            @Override
            public void onDoubleClick()
            {
                togglePlayer();
            }
        });
        binding.playPauseBtn.setOnClickListener(v -> togglePlayer());
        binding.lockBtn.setOnClickListener(v -> {
        });
        binding.seekbar.setOnProgressChangedListener(this);
    }

    private void togglePlayer()
    {
        if (exoPlayer != null)
        {
            if (exoPlayer.isPlaying())
            {
                exoPlayer.pause();
            }
            else
            {
                exoPlayer.play();
            }
        }
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
            theFile = (VideoFile) bundle.getParcelable(VideoFile.INTENT_KEY);
        }
        catch (Exception e)
        {
            Timber.e(e);
        }
    }

    private void displayContent()
    {
        binding.fileNameTv.setText(theFile.getName());
    }

    private void initializePlayer()
    {
        progressHandler = new Handler();

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.CONTENT_TYPE_MOVIE)
                .build();

        defaultTrackSelector = new DefaultTrackSelector(this);

        exoPlayer = new SimpleExoPlayer.Builder(this)
                .setTrackSelector(defaultTrackSelector)
                .build();
        exoPlayer.addListener(this);
        exoPlayer.setAudioAttributes(audioAttributes, true);
        binding.playerView.setPlayer(exoPlayer);
        MediaItem mediaItem = MediaItem.fromUri(theFile.getUri());
        exoPlayer.setMediaItem(mediaItem);
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(currentWindow, playbackPosition);
        exoPlayer.prepare();
        exoPlayer.play();

//        binding.subtitleView.setCues(exoPlayer.getCurrentCues());

//        exoPlayer.getPlaybackLooper()

//        Timber.d("%d", exoPlayer.getCurrentTrackSelections().length);
//        for(int i = 0; i < exoPlayer.getCurrentTrackSelections().length; i++){
//            exoPlayer.getCurrentTrackSelections().get(i).getSelectedFormat()
//
//
//            String format = exoPlayer.getCurrentTrackSelections().get(i).getSelectedFormat().sampleMimeType;
//            String lang = exoPlayer.getCurrentTrackSelections().get(i).getSelectedFormat().language;
//            String id = exoPlayer.getCurrentTrackSelections().get(i).getSelectedFormat().id;
//            String label = exoPlayer.getCurrentTrackSelections().get(i).getSelectedFormat().label;
//
//            String format = exoPlayer.getCurrentTrackGroups().get(i).getFormat(0).sampleMimeType;
//            String lang = exoPlayer.getCurrentTrackGroups().get(i).getFormat(0).language;
//            String id = exoPlayer.getCurrentTrackGroups().get(i).getFormat(0).id;
//            String label = exoPlayer.getCurrentTrackGroups().get(i).getFormat(0).label;
//            Timber.d("%s, %s, %s, %s", id, lang, label, format);
//            System.out.println(exoPlayer.getCurrentTrackGroups().get(i).getFormat(0));
//            if(format.contains("audio") && id != null && lang != null){
//                //System.out.println(lang + " " + id);
//                audioLanguages.add(new Pair<>(id, lang));
//            }
//        }


//        new TrackSelectionDialogBuilder(this, "Tracks", defaultTrackSelector, 0)
//                .build().show();
    }

    private String trackTypeToName(int type)
    {
        switch (type)
        {
            case C.TRACK_TYPE_UNKNOWN:
                return "TRACK_TYPE_UNKNOWN";
            case C.TRACK_TYPE_DEFAULT:
                return "TRACK_TYPE_DEFAULT";
            case C.TRACK_TYPE_AUDIO:
                return "TRACK_TYPE_AUDIO";
            case C.TRACK_TYPE_VIDEO:
                return "TRACK_TYPE_VIDEO";
            case C.TRACK_TYPE_TEXT:
                return "TRACK_TYPE_TEXT";
            case C.TRACK_TYPE_METADATA:
                return "TRACK_TYPE_METADATA";
            case C.TRACK_TYPE_CAMERA_MOTION:
                return "TRACK_TYPE_CAMERA_MOTION";
            case C.TRACK_TYPE_NONE:
                return "TRACK_TYPE_NONE";
            default:
                return "UNKNOWN";
        }
    }

    private void releasePlayer()
    {
        progressHandler.removeCallbacks(this);

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

    @Override
    public void onPlayWhenReadyChanged(boolean playWhenReady, int reason)
    {
        this.playWhenReady = playWhenReady;
    }

    @Override
    public void onIsPlayingChanged(boolean isPlaying)
    {
        if (isPlaying)
        {
            binding.playPauseBtn.setImageResource(R.drawable.ic_pause);
            Timber.d("log tracks clicked");
            MappingTrackSelector.MappedTrackInfo mappedTrackInfo = Assertions.checkNotNull(defaultTrackSelector.getCurrentMappedTrackInfo());
            DefaultTrackSelector.Parameters parameters = defaultTrackSelector.getParameters();

            for (int rendererIndex = 0; rendererIndex < mappedTrackInfo.getRendererCount(); rendererIndex++)
            {
                int trackType = mappedTrackInfo.getRendererType(rendererIndex);
                if (trackType == C.TRACK_TYPE_AUDIO)
                {
                    TrackGroupArray trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex);
                    Boolean isRendererDisabled = parameters.getRendererDisabled(rendererIndex);
                    DefaultTrackSelector.SelectionOverride selectionOverride = parameters.getSelectionOverride(rendererIndex, trackGroupArray);

                    Timber.d("------------------------------------------------------Track item " + rendererIndex + "------------------------------------------------------");
                    Timber.d("track type: " + trackTypeToName(trackType));
                    Timber.d("track group array: " + new Gson().toJson(trackGroupArray));
                    for (int groupIndex = 0; groupIndex < trackGroupArray.length; groupIndex++)
                    {
                        for (int trackIndex = 0; trackIndex < trackGroupArray.get(groupIndex).length; trackIndex++)
                        {
                            String trackName = new DefaultTrackNameProvider(getResources()).getTrackName(trackGroupArray.get(groupIndex).getFormat(trackIndex));
                            Boolean isTrackSupported = mappedTrackInfo.getTrackSupport(rendererIndex, groupIndex, trackIndex) == RendererCapabilities.FORMAT_HANDLED;
//                            Timber.d("track item " + groupIndex + ": trackName: " + trackName + ", isTrackSupported: " + isTrackSupported);
                            Timber.d("%d =>  label: %s, name: %s", groupIndex, trackGroupArray.get(groupIndex).getFormat(trackIndex).label, trackName);
                        }
                    }
                    Timber.d("isRendererDisabled: " + isRendererDisabled);
                    Timber.d("selectionOverride: " + new Gson().toJson(selectionOverride));
                }
            }

            progressHandler.post(this);
        }
        else
        {
            binding.playPauseBtn.setImageResource(R.drawable.ic_play);
        }
    }

    @Override
    public void onPlaybackStateChanged(int state)
    {
        Timber.d("Player state: %s", getPlayerState());

        switch (state)
        {
            case Player.STATE_IDLE:
                break;
            case Player.STATE_BUFFERING:
                break;
            case Player.STATE_READY:
                break;
            case Player.STATE_ENDED:
                onVideoEnded();
                break;
        }
    }

    @Override
    public void run()
    {
        if (exoPlayer.isPlaying())
        {
//            Timber.d("Duration: %d, Current position: %d", exoPlayer.getDuration(), exoPlayer.getCurrentPosition());
            binding.seekbar.setProgress((float) exoPlayer.getCurrentPosition() / (float) exoPlayer.getDuration());
            progressHandler.postDelayed(this, 100);
//            Timber.d("Player state: %s", getPlayerState());
        }
    }

    private String getPlayerState()
    {
        switch (exoPlayer.getPlaybackState())
        {
            case Player.STATE_IDLE:
                return "STATE_IDEL";
            case Player.STATE_BUFFERING:
                return "STATE_BUFFERING";
            case Player.STATE_READY:
                return "STATE_READY";
            case Player.STATE_ENDED:
                return "STATE_ENDED";
        }

        return "";
    }

    @Override
    public void onProgressChanged(float progress)
    {
        Timber.d("Position manually set.");
        if (exoPlayer.getPlaybackState() == Player.STATE_READY)
        {
            long position = (long) (progress * exoPlayer.getDuration());
            Timber.d("Picked position: %d", position);
            exoPlayer.seekTo(currentWindow, position);
        }
    }

    public void onVideoBuffering()
    {

    }

    public void onVideoEnded()
    {
        binding.seekbar.setProgress(1);
        //play next or finish activity
//        if (!BuildConfig.DEBUG)
//        {
//            finish();
//        }
    }
}

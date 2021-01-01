package com.agadimi.agplayer.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.agadimi.agplayer.R;
import com.agadimi.agplayer.app.App;
import com.agadimi.agplayer.common.PlayerOverlayTouchHandler;
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
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.MappingTrackSelector;
import com.google.android.exoplayer2.ui.DefaultTrackNameProvider;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;

import java.util.List;

import timber.log.Timber;

import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT;

public class PlayerActivity extends AppCompatActivity implements Player.EventListener, Runnable, SeekBar.OnProgressChangedListener, TextOutput
{
    private ActivityPlayerBinding binding;
    private SimpleExoPlayer exoPlayer;
    private DefaultTrackSelector defaultTrackSelector;
    private Handler progressHandler;
    //    private Handler controlsVisibilityHandler;
    private VideoFile theFile;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private long controlsShowedAt = 0;
    private boolean isControlsVisible = true;
    private boolean isControlsLocked = false;


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

        if (savedInstanceState != null)
        {
            playWhenReady = savedInstanceState.getBoolean("play_when_ready", playWhenReady);
            currentWindow = savedInstanceState.getInt("window_index", currentWindow);
            playbackPosition = savedInstanceState.getLong("playback_position", playbackPosition);
        }

        handleIntent();
        displayContent();
    }

    private void setupClicksAndStuff()
    {
        binding.backBtn.setOnClickListener(v -> onBackPressed());
        binding.playerOverlay.setOnClickListener(new PlayerOverlayTouchHandler()
        {
            @Override
            public void onDoubleClick()
            {
                if (!isControlsLocked) // if controls are locked, then ignore double click
                {
                    togglePlayer();
                }
            }

            @Override
            public void onClick(View v)
            {
                super.onClick(v);
                if (isControlsVisible)
                {
                    hideControls();
                }
                else
                {
                    showControls();
                }
            }
        });
        binding.playPauseBtn.setOnClickListener(v -> togglePlayer());
        binding.lockBtn.setOnClickListener(v -> {
            hideControls();
            isControlsLocked = true;
            showControls();
        });
        binding.unlockBtn.setOnClickListener(v -> {
            hideControls();
            isControlsLocked = false;
            showControls();
        });
        binding.seekbar.setOnProgressChangedListener(this);
        binding.resizeBtn.setOnClickListener(v -> {
            int currentResizeMode = binding.playerView.getResizeMode() + 1;
            if (currentResizeMode > 4) currentResizeMode = RESIZE_MODE_FIT;
            binding.playerView.setResizeMode(currentResizeMode);
        });
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState)
    {
        outState.putBoolean("play_when_ready", exoPlayer.getPlayWhenReady());
        outState.putInt("window_index", exoPlayer.getCurrentWindowIndex());
        outState.putLong("playback_position", exoPlayer.getCurrentPosition());
        super.onSaveInstanceState(outState);
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
        binding.playerView.setPlayer(exoPlayer);
        binding.playerView.getSubtitleView().setVisibility(View.GONE);
        binding.subtitleView.setFixedTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        exoPlayer.addListener(this);
        exoPlayer.addTextOutput(this);
        exoPlayer.setAudioAttributes(audioAttributes, true);
        MediaItem mediaItem = MediaItem.fromUri(theFile.getUri());//.buildUpon().setSubtitles().build();
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
                onVideoBuffering();
                break;
            case Player.STATE_READY:
                refreshDuration();
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
            long currentPos = exoPlayer.getCurrentPosition();
            if (isControlsVisible && currentPos - 5000 > controlsShowedAt)
            {
                hideControls();
            }
            binding.seekbar.setProgress((float) exoPlayer.getCurrentPosition() / (float) exoPlayer.getDuration());
            refreshCurrentPosition();
            progressHandler.postDelayed(this, 100);
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
    public void onProgressDragStarted()
    {
        Timber.i("Progress drag started");
        if (exoPlayer.isPlaying()) exoPlayer.pause();
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

    @Override
    public void onProgressDragStopped()
    {
        Timber.i("Progress drag stopped");
        exoPlayer.play();
    }

    public void onVideoBuffering()
    {
        showControls();
    }

    public void onVideoEnded()
    {
        binding.seekbar.setProgress(1);
        //play next or finish activity
//        if (!BuildConfig.DEBUG)
//        {
        finish();
//        }
    }

    @Override
    public void onCues(List<Cue> cues)
    {
        binding.subtitleView.setCues(cues);
    }

    public void showControls()
    {
        if (isControlsLocked)
        {
            binding.unlockBtn.setVisibility(View.VISIBLE);
        }
        else
        {
            binding.topControlPanel.setVisibility(View.VISIBLE);
            binding.centerControlPanel.setVisibility(View.VISIBLE);
            binding.bottomControlPanel.setVisibility(View.VISIBLE);
        }
        isControlsVisible = true;
        controlsShowedAt = exoPlayer.getCurrentPosition();
        Timber.i("Controls showed up at: %d", controlsShowedAt);
    }

    public void hideControls()
    {
        if (isControlsLocked)
        {
            binding.unlockBtn.setVisibility(View.GONE);
        }
        else
        {
            binding.topControlPanel.setVisibility(View.GONE);
            binding.centerControlPanel.setVisibility(View.GONE);
            binding.bottomControlPanel.setVisibility(View.GONE);
        }
        isControlsVisible = false;
    }

    private void refreshDuration()
    {
        binding.durationTv.setText(VideoFile.convertMillisToTime(exoPlayer.getDuration()));
    }

    private void refreshCurrentPosition()
    {
        binding.currentPositionTv.setText(VideoFile.convertMillisToTime(exoPlayer.getCurrentPosition()));
    }


}

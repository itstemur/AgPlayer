package com.agadimi.agplayer.models;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.concurrent.TimeUnit;

public class VideoFile extends SimpleFile
{
    public static final String INTENT_KEY = "the_video";

    private Uri uri;
    private int duration;
    private int size;
    private Bitmap thumbnail;

    public Uri getUri()
    {
        return uri;
    }

    public void setUri(Uri uri)
    {
        this.uri = uri;
    }

    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    public String getVideoLength()
    {
        return String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes(duration),
                TimeUnit.MILLISECONDS.toSeconds(duration) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
        );
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public Bitmap getThumbnail()
    {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail)
    {
        this.thumbnail = thumbnail;
    }
}

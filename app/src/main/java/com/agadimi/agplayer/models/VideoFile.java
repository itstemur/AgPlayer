package com.agadimi.agplayer.models;

import android.net.Uri;

public class VideoFile extends SimpleFile
{
    public static final String INTENT_KEY = "the_video";

    private Uri uri;
    private int duration;
    private int size;

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

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }
}

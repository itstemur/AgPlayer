package com.agadimi.agplayer.models;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.concurrent.TimeUnit;

public class VideoFile extends SimpleFile implements Parcelable
{
    public static final String INTENT_KEY = "the_video";

    private Uri uri;
    private int duration;
    private int size;
    private Bitmap thumbnail;

    public VideoFile()
    {
    }

    protected VideoFile(Parcel parcel)
    {
        setName(parcel.readString());
        setPath(parcel.readString());
        setType(parcel.readByte());
        this.duration = parcel.readInt();
        this.size = parcel.readInt();
        this.uri = Uri.parse(parcel.readString());
    }

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
        return convertMillisToTime(duration);
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

    @Override
    public int describeContents()
    {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(getName());
        dest.writeString(getPath());
        dest.writeByte(getType());
        dest.writeInt(duration);
        dest.writeInt(size);
        dest.writeString(uri.toString());
    }

    public static final Parcelable.Creator<VideoFile> CREATOR = new Parcelable.Creator<VideoFile>()
    {
        public VideoFile createFromParcel(Parcel in)
        {
            return new VideoFile(in);
        }

        public VideoFile[] newArray(int size)
        {
            return new VideoFile[size];
        }
    };

    public static String convertMillisToTime(long millis)
    {
        if (millis > 3600000)
        {
            return String.format(
                    "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
            );
        }
        else
        {
            return String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(millis),
                    TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1)
            );
        }
    }
}

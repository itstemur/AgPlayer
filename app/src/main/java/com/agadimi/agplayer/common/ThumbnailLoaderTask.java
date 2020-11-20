package com.agadimi.agplayer.common;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import java.util.logging.ConsoleHandler;

import timber.log.Timber;

public class ThumbnailLoaderTask implements Runnable
{
    private String path;
    private int position;
    private ThumbnailTaskResultListener listener;

    public ThumbnailLoaderTask(String path, ThumbnailTaskResultListener listener, int position)
    {
        this.path = path;
        this.position = position;
        this.listener = listener;
    }

    // TODO: 11/20/20 ThumbnailUtils.createVideoThumbnail is deprecated from API 29
    @Override
    public void run()
    {
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> listener.onFinished(bitmap, position));


        //        try
//        {
//
//            return ThumbnailUtils.createVideoThumbnail(new File(uri.getPath()), thumbnailSize, null);
////            return context.getContentResolver().loadThumbnail(uri, MediaStore.Video.Thumbnails., null);
//        }
//        catch (Exception e)
//        {
//            Timber.e(e);
//        }

//        return null;

    }
}

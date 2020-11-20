package com.agadimi.agplayer.common;

import android.graphics.Bitmap;

public interface ThumbnailTaskResultListener
{
    void onFinished(Bitmap bitmap, int position);
}

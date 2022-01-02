package com.agadimi.agplayer.common;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public abstract class PlayerOverlayTouchHandler implements View.OnTouchListener
{
    private static final byte DRAG_RIGHT_VERTICAL = 1;
    private static final byte DRAG_LEFT_VERTICAL = 2;
    private static final byte DRAG_HORIZONTAL = 3;

    private long actionDownAt = 0;
    private long timestampLastClick = 0;
    private byte dragType = 0;
    private float dragInitialX, dragInitialY;
    private int deviceWidth = 0, deviceHeight = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                actionDownAt = SystemClock.elapsedRealtime();
                dragInitialX = event.getX();
                dragInitialY = event.getY();
                onDragStarted();
                break;
            case MotionEvent.ACTION_MOVE:
                int dw = getDeviceWidth(v.getContext());
                int dh = getDeviceHeight(v.getContext());
                float xChange = Math.abs(dragInitialX - event.getX());
                float yChange = Math.abs(dragInitialY - event.getY());
                if (dragType == 0)
                {
                    if (xChange > yChange && xChange > 100) // horizontal drag
                    {
                        dragType = DRAG_HORIZONTAL;
                    }
                    else if (yChange > xChange && yChange > 100) // vertical drag
                    {
                        if (dragInitialX > dw / 2)
                        {
                            dragType = DRAG_RIGHT_VERTICAL;
                        }
                        else
                        {
                            dragType = DRAG_LEFT_VERTICAL;
                        }
                    }
                }
                else
                {
                    switch (dragType)
                    {
                        case DRAG_HORIZONTAL:
                            onHorizontalDrag((event.getX() - dragInitialX) / dw);
                            break;
                        case DRAG_LEFT_VERTICAL:
                            onLeftVerticalDrag((float) ((event.getY() - dragInitialY) / (dh * 0.75)));
                            break;
                        case DRAG_RIGHT_VERTICAL:
                            onRightVerticalDrag((float) ((event.getY() - dragInitialY) / (dh * 0.75)));
                            break;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (dragType == 0 && SystemClock.elapsedRealtime() - actionDownAt < 300)
                {
                    onOverlayClick(v);
                }
                else
                {
                    dragType = 0;
                }
                break;
            default:
                return false;
        }

        return true;
    }


    public void onDragStarted()
    {
    }

    public void onRightVerticalDrag(float movement)
    {
    }

    public void onLeftVerticalDrag(float movement)
    {
    }

    public void onHorizontalDrag(float movement)
    {
    }

    public void onOverlayClick(View v)
    {
        v.performClick();
        if ((SystemClock.elapsedRealtime() - timestampLastClick) < 200)
        {
            onOverlayDoubleClick(v);
        }
        timestampLastClick = SystemClock.elapsedRealtime();
    }

    public void onOverlayDoubleClick(View v)
    {
    }


    private int getDeviceWidth(Context context)
    {
        if (deviceWidth > 0) return deviceWidth;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        return deviceWidth;
    }

    private int getDeviceHeight(Context context)
    {
        if (deviceHeight > 0) return deviceHeight;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        return deviceHeight;
    }
}
package com.agadimi.agplayer.common;

import android.os.SystemClock;
import android.view.View;

public abstract class PlayerOverlayTouchHandler implements View.OnClickListener
{
//    private long timestampLastClick = 0;
//
//    @Override
//    public boolean onTouch(View v, MotionEvent event)
//    {
//        switch (event.getAction())
//        {
//            case MotionEvent.ACTION_DOWN:
//
//                break;
//            case MotionEvent.ACTION_MOVE:
//                break;
//            case MotionEvent.ACTION_UP:
//
//                break;
//            default:
//                return false;
//        }
//
//        return true;
//    }
//
//    public void onClick(View v)
//    {
////        if ((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis)
////        {
////            onDoubleClick();
////        }
////        timestampLastClick = SystemClock.elapsedRealtime();
//    }
//
//    public void onDoubleClick()
//    {
//    }
//
//    public void onRightVerticalDrag()
//    {
//    }


    // The time in which the second tap should be done in order to qualify as
    // a double click
    private static final long DEFAULT_QUALIFICATION_SPAN = 200;
    private long doubleClickQualificationSpanInMillis;
    private long timestampLastClick;

    public PlayerOverlayTouchHandler()
    {
        doubleClickQualificationSpanInMillis = DEFAULT_QUALIFICATION_SPAN;
        timestampLastClick = 0;
    }

    public PlayerOverlayTouchHandler(long doubleClickQualificationSpanInMillis)
    {
        this.doubleClickQualificationSpanInMillis = doubleClickQualificationSpanInMillis;
        timestampLastClick = 0;
    }

    @Override
    public void onClick(View v)
    {
        if ((SystemClock.elapsedRealtime() - timestampLastClick) < doubleClickQualificationSpanInMillis)
        {
            onDoubleClick();
        }
        timestampLastClick = SystemClock.elapsedRealtime();
    }

    public abstract void onDoubleClick();
}
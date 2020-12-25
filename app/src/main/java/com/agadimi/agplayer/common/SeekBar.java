package com.agadimi.agplayer.common;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.agadimi.agplayer.R;

/**
 * TODO: document your custom view class.
 */
public class SeekBar extends View
{

    private Paint trackPaint;
    private Paint thumbPaint;
    private int trackHeight;
    private int thumbRadius;
    private int trackColor;
    private int thumbColor;

    private int contentWidth;
    private int contentHeight;
    private int verticalCenter;
    private boolean movingManually = false;

    private float progress;
    private OnProgressChangedListener onProgressChangedListener;

    public SeekBar(Context context)
    {
        super(context);
        init(null, 0);
    }

    public SeekBar(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(attrs, 0);
    }

    public SeekBar(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle)
    {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.SeekBar, defStyle, 0);

        trackColor = a.getColor(R.styleable.SeekBar_trackColor, Color.WHITE);
        thumbColor = a.getColor(R.styleable.SeekBar_thumbColor, Color.WHITE);
        trackHeight = a.getDimensionPixelSize(R.styleable.SeekBar_trackHeight, 5);
        thumbRadius = a.getDimensionPixelSize(R.styleable.SeekBar_thumbRadius, 10);
        progress = a.getFloat(R.styleable.SeekBar_progress, 0);
        a.recycle();

        trackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        thumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        invalidateColors();
    }

    private void invalidateColors()
    {
        trackPaint.setColor(Color.WHITE);
        thumbPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        contentWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        contentHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        verticalCenter = contentHeight / 2;

        //draw
        drawTrack(canvas);
        drawThumb(canvas);
    }

    private void drawTrack(Canvas canvas)
    {
        canvas.drawRoundRect(
                getPaddingLeft(),
                verticalCenter - (trackHeight / 2),
                getPaddingLeft() + contentWidth,
                verticalCenter + (trackHeight / 2),
                trackHeight / 2,
                trackHeight / 2,
                trackPaint
        );
    }

    private void drawThumb(Canvas canvas)
    {
        int thumbCenterX = (int) (progress * (contentWidth - (2 * thumbRadius))) + thumbRadius;
        canvas.drawCircle(thumbCenterX, verticalCenter, movingManually ? thumbRadius * 1.5f : thumbRadius, thumbPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                movingManually = true;
                setProgress(event.getX() / contentWidth, true);
                break;
            case MotionEvent.ACTION_MOVE:
                setProgress(event.getX() / contentWidth, true);
                break;
            case MotionEvent.ACTION_UP:
                movingManually = false;
                invalidate();
                break;
            default:
                return false;
        }

        return true;
    }


    public int getTrackHeight()
    {
        return trackHeight;
    }

    public void setTrackHeight(int trackHeight)
    {
        this.trackHeight = trackHeight;
        invalidate();
    }

    public int getThumbRadius()
    {
        return thumbRadius;
    }

    public void setThumbRadius(int thumbRadius)
    {
        this.thumbRadius = thumbRadius;
        invalidate();
    }

    public float getProgress()
    {
        return progress;
    }

    public void setProgress(float progress)
    {
        setProgress(progress, false);
    }

    public void setProgress(float progress, boolean invokeListener)
    {
        if (progress < 0)
        {
            this.progress = 0;
        }
        else if (progress > 1)
        {
            this.progress = 1;
        }
        else
        {
            this.progress = progress;
        }
        invalidate();

        if (invokeListener && onProgressChangedListener != null)
        {
            onProgressChangedListener.onProgressChanged(this.progress);
        }
    }

    public int getTrackColor()
    {
        return trackColor;
    }

    public void setTrackColor(int trackColor)
    {
        this.trackColor = trackColor;
        invalidateColors();
        invalidate();
    }

    public int getThumbColor()
    {
        return thumbColor;
    }

    public void setThumbColor(int thumbColor)
    {
        this.thumbColor = thumbColor;
        invalidateColors();
        invalidate();
    }

    public OnProgressChangedListener getOnProgressChangedListener()
    {
        return onProgressChangedListener;
    }

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener)
    {
        this.onProgressChangedListener = onProgressChangedListener;
    }

    public interface OnProgressChangedListener
    {
        void onProgressChanged(float progress);
    }
}
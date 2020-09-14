package com.theflopguyproductions.ticktrack.ui.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.theflopguyproductions.ticktrack.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;

public class AnalogClock extends View {

    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mTimeZone == null && Intent.ACTION_TIMEZONE_CHANGED.equals(intent.getAction())) {
                final String tz = intent.getStringExtra("time-zone");
                mTime = Calendar.getInstance(TimeZone.getTimeZone(tz));
            }
            onTimeChanged();
        }
    };

    private Runnable mClockTick = new Runnable() {
        @Override
        public void run() {
            onTimeChanged();

            if (mEnableSeconds) {
                final long now = System.currentTimeMillis();
                final long delay = SECOND_IN_MILLIS - now % SECOND_IN_MILLIS;
                postDelayed(this, delay);
            }
        }
    };

    private Drawable mDial;
    private Drawable mHourHand;
    private Drawable mMinuteHand;
    private Drawable mSecondHand;

    private Calendar mTime;
    private String mDescFormat;
    private TimeZone mTimeZone;
    private boolean mEnableSeconds = true;

    private int hourHandDrawable = -1;
    private int minuteHandDrawable = -1;
    private int dialDrawable = -1;

    private Context context;

    public AnalogClock(Context context) {
        this(context, null);
        this.context = context;
    }

    public AnalogClock(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public void setupClockDrawables(int hour_hand, int minute_hand, int dial_clock){
        this.hourHandDrawable = hour_hand;
        this.minuteHandDrawable = minute_hand;
        this.dialDrawable = dial_clock;

        final Resources r = context.getResources();
        final TypedArray a = context.obtainStyledAttributes(null, R.styleable.AnalogClock);
        mDial = a.getDrawable(R.styleable.AnalogClock_dial);
//        setupClockDrawables(hour_hand, minute_hand, dial_clock);
        if (mDial == null) {
//            if(dialDrawable==-1){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    mDial = ContextCompat.getDrawable(context, R.drawable.transparent_dial_unordinary);
//                } else {
//                    mDial = r.getDrawable(R.drawable.transparent_dial_unordinary);
//                }
//            }
//            else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mDial = ContextCompat.getDrawable(context, dialDrawable);
            } else {
                mDial = r.getDrawable(dialDrawable);
            }
//            }
        }
        mHourHand = a.getDrawable(R.styleable.AnalogClock_hour);
        if (mHourHand == null) {
//            if(hourHandDrawable==-1){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    mHourHand = ContextCompat.getDrawable(context, R.drawable.white_hour_hand_unordinary);
//                } else {
//                    mHourHand = r.getDrawable(R.drawable.white_hour_hand_unordinary);
//                }
//            }
//            else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mHourHand = ContextCompat.getDrawable(context, hourHandDrawable);
            } else {
                mHourHand = r.getDrawable(hourHandDrawable);
            }
//            }

        }
        mMinuteHand = a.getDrawable(R.styleable.AnalogClock_minute);
        if (mMinuteHand == null) {
//            if(minuteHandDrawable==-1){
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    mMinuteHand = ContextCompat.getDrawable(context, R.drawable.white_minute_hand_unordinary);
//                } else {
//                    mMinuteHand = r.getDrawable(R.drawable.white_minute_hand_unordinary);
//                }
//            }
//            else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMinuteHand = ContextCompat.getDrawable(context, minuteHandDrawable);
            } else {
                mMinuteHand = r.getDrawable(minuteHandDrawable);
            }
//            }
        }
        mSecondHand = a.getDrawable(R.styleable.AnalogClock_second);
        if (mSecondHand == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mSecondHand = ContextCompat.getDrawable(context, R.drawable.ic_second_w_center);
            } else {
                mSecondHand = r.getDrawable(R.drawable.ic_second_w_center);
            }
        }
        initDrawable(context, mDial);
        initDrawable(context, mHourHand);
        initDrawable(context, mMinuteHand);
        initDrawable(context, mSecondHand);
    }
    public AnalogClock(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final Resources r = context.getResources();
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnalogClock);
        mTime = Calendar.getInstance();
        mDescFormat = ((SimpleDateFormat) DateFormat.getTimeFormat(context)).toLocalizedPattern();
        mEnableSeconds = a.getBoolean(R.styleable.AnalogClock_showSecondHand, false);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        final IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getContext().registerReceiver(mIntentReceiver, filter);
        mTime = Calendar.getInstance(mTimeZone != null ? mTimeZone : TimeZone.getDefault());
        onTimeChanged();
        if (mEnableSeconds) {
            mClockTick.run();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        getContext().unregisterReceiver(mIntentReceiver);
        removeCallbacks(mClockTick);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int minWidth = Math.max(mDial.getIntrinsicWidth(), getSuggestedMinimumWidth());
        final int minHeight = Math.max(mDial.getIntrinsicHeight(), getSuggestedMinimumHeight());
        setMeasuredDimension(getDefaultSize(minWidth, widthMeasureSpec),
                getDefaultSize(minHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int w = getWidth();
        final int h = getHeight();

        final int saveCount = canvas.save();
        canvas.translate(w / 2f, h / 2f);
        final float scale = Math.min((float) w / mDial.getIntrinsicWidth(),
                (float) h / mDial.getIntrinsicHeight());
        if (scale < 1f) {
            canvas.scale(scale, scale, 0f, 0f);
        }
        mDial.draw(canvas);

        final float hourAngle = mTime.get(Calendar.HOUR) * 30f;
        canvas.rotate(hourAngle, 0f, 0f);
        mHourHand.draw(canvas);

        final float minuteAngle = mTime.get(Calendar.MINUTE) * 6f;
        canvas.rotate(minuteAngle - hourAngle, 0f, 0f);
        mMinuteHand.draw(canvas);

        if (mEnableSeconds) {
            final float secondAngle = mTime.get(Calendar.SECOND) * 6f;
            canvas.rotate(secondAngle - minuteAngle, 0f, 0f);
            mSecondHand.draw(canvas);
        }
        canvas.restoreToCount(saveCount);
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return mDial == who
                || mHourHand == who
                || mMinuteHand == who
                || mSecondHand == who
                || super.verifyDrawable(who);
    }

    private void initDrawable(Context context, Drawable drawable) {
        final int midX = drawable.getIntrinsicWidth() / 2;
        final int midY = drawable.getIntrinsicHeight() / 2;
        drawable.setBounds(-midX, -midY, midX, midY);
    }

    public void onTimeChanged() {
        mTime.setTimeInMillis(System.currentTimeMillis());
        setContentDescription(DateFormat.format(mDescFormat, mTime));
        invalidate();
    }

    public void setTimeZone(String id) {
        mTimeZone = TimeZone.getTimeZone(id);
        mTime.setTimeZone(mTimeZone);
        onTimeChanged();
    }

}

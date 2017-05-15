package com.baozs.demos.sqlbritedemo2017.listview;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.widget.ListView;
import android.widget.OverScroller;

import static android.R.attr.duration;
import static android.R.interpolator.linear;
import static android.support.v4.widget.ViewDragHelper.INVALID_POINTER;

/**
 * Created by vashzhong on 2017/5/15.
 */

public class MyAutoPullDownListView extends ListView {

    public MyAutoPullDownListView(Context context) {
        super(context);
        init(context, null);
    }

    public MyAutoPullDownListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
    }

    public void overScrollDown() {
        post(new Runnable() {
            @Override
            public void run() {
                final MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_DOWN, getWidth() / 2, getHeight() / 2, 0);
                dispatchTouchEvent(event);
                event.recycle();
            }
        });

        postDelayed(new Runnable() {
            @Override
            public void run() {
                final MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_MOVE, getWidth() / 2, getHeight() / 2, 0);
                dispatchTouchEvent(event);
                event.recycle();
            }
        }, 50);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                final MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_MOVE, getWidth() / 2, getHeight() / 2 + 400, 0);
                dispatchTouchEvent(event);
                event.recycle();
            }
        }, 100);

        postDelayed(new Runnable() {
            @Override
            public void run() {
                final MotionEvent event = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis(), MotionEvent.ACTION_UP, getWidth() / 2, getHeight() / 2 + 400, 0);
                dispatchTouchEvent(event);
                event.recycle();
            }
        }, 3000);
    }
}

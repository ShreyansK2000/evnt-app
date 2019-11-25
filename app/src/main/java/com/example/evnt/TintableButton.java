package com.example.evnt;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

import de.hdodenhof.circleimageview.CircleImageView;

public class TintableButton extends CircleImageView {

    private boolean mIsSelected;

    public TintableButton(Context context) {
        super(context);
    }

    public TintableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TintableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && !mIsSelected) {
            setColorFilter(0x99000000);
            mIsSelected = true;
        } else if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) && mIsSelected) {
            setColorFilter(Color.TRANSPARENT);
            mIsSelected = false;
        }

        return super.onTouchEvent(event);
    }
}

package com.example.evnt;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.AnimationUtils;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A class created purely to aid styling of the imageView that houses the app
 * logo on the complex logic screen. Just makes the image feel like a button
 * (in addition to the animation)
 */
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

    /**
     * Apply a color filter when the image is touched, and the remove it.
     * Adds a button like effect
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && !mIsSelected) {
            setColorFilter(0x99000000);
            this.startAnimation(AnimationUtils.loadAnimation(this.getContext(), R.anim.image_click));
            mIsSelected = true;
        } else if ((event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL) && mIsSelected) {
            setColorFilter(Color.TRANSPARENT);
            mIsSelected = false;
        }

        return super.onTouchEvent(event);
    }
}

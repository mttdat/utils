package com.elevationviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class RelativeLayoutElevation extends RelativeLayout {
    private Context context;

    // Attributes.
    private int myElevation = 0;
    private int myBackgroundColor = -1;
    private int myBackgroundImage = -1;
    private int shadowColor = -1;
    private int radius = 0;
    private int myGravity = 0;   // Bottom.

    public RelativeLayoutElevation(Context context) {
        super(context);

        this.context = context;
    }

    public RelativeLayoutElevation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        parseAttributes(attrs);

        if(myBackgroundColor != -1) {
            initBackgroundColor();
        }else {
            initBackgroundImage();
        }
    }

    public RelativeLayoutElevation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.context = context;

        parseAttributes(attrs);

        if(myBackgroundColor != -1) {
            initBackgroundColor();
        }else {
            initBackgroundImage();
        };
    }

    private void parseAttributes(final AttributeSet attrs) {
        final TypedArray arr = context.obtainStyledAttributes(attrs,
                R.styleable.RelativeLayoutElevation);
        try {
            myElevation = arr.getDimensionPixelSize(R.styleable.RelativeLayoutElevation_myElevation, 0);
            myBackgroundColor = arr.getResourceId(R.styleable.RelativeLayoutElevation_backgroundColor, -1);
            myBackgroundImage = arr.getResourceId(R.styleable.RelativeLayoutElevation_backgroundImage, -1);
            shadowColor = arr.getResourceId(R.styleable.RelativeLayoutElevation_shadowColor, -1);
            radius = arr.getDimensionPixelSize(R.styleable.RelativeLayoutElevation_radius, 0);
            myGravity = arr.getInt(R.styleable.RelativeLayoutElevation_myGravity, 0);
        } finally {
            arr.recycle();
        }
    }

    public void initBackgroundImage() {
        setBackground(ViewUtils.generateBackgroundDrawableWithShadow(
                this, myBackgroundImage, radius, shadowColor, myElevation, myGravity));
    }

    public void initBackgroundColor() {
        setBackground(ViewUtils.generateBackgroundSolidWithShadow(
                this, myBackgroundColor, radius, shadowColor, myElevation, myGravity));
    }

    /* Get - set */

    public void setMyElevation(int myElevation) {
        this.myElevation = myElevation;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getMyElevation() {
        return myElevation;
    }

    public int getMyBackgroundColor() {
        return myBackgroundColor;
    }

    public void setMyBackgroundColor(int myBackgroundColor) {
        this.myBackgroundColor = myBackgroundColor;
    }

    public int getMyBackgroundImage() {
        return myBackgroundImage;
    }

    public void setMyBackgroundImage(int myBackgroundImage) {
        this.myBackgroundImage = myBackgroundImage;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
    }

    public int getMyGravity() {
        return myGravity;
    }

    public void setMyGravity(int myGravity) {
        this.myGravity = myGravity;
    }
}

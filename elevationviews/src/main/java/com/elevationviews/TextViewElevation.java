package com.elevationviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import androidx.annotation.Nullable;

public class TextViewElevation extends androidx.appcompat.widget.AppCompatTextView {

    private final String TAG = "TextViewElevation";

    private Context context;

    // Attributes.
    private int myElevation = 0;
    private int myBackgroundColor = -1;
    private int myBackgroundImage = -1;
    private int shadowColor = -1;
    private int radius = 0;
    private int myGravity = 0;   // Bottom.

    public TextViewElevation(Context context) {
        super(context);

        this.context = context;
    }

    public TextViewElevation(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        parseAttributes(attrs);

        if(myBackgroundColor != -1) {
            initBackgroundColor();
        }else {
            initBackgroundImage();
        }
    }

    public TextViewElevation(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                R.styleable.TextViewElevation);
        try {
            myElevation = arr.getDimensionPixelSize(R.styleable.TextViewElevation_myElevation, 0);
            myBackgroundColor = arr.getResourceId(R.styleable.TextViewElevation_backgroundColor, -1);
            myBackgroundImage = arr.getResourceId(R.styleable.TextViewElevation_backgroundImage, -1);
            shadowColor = arr.getResourceId(R.styleable.TextViewElevation_shadowColor, -1);
            radius = arr.getDimensionPixelSize(R.styleable.TextViewElevation_radius, 0);
            myGravity = arr.getInt(R.styleable.TextViewElevation_myGravity, 0);

            String customFont = arr.getString(R.styleable.TextViewElevation_myFont);
            setCustomFont(customFont);
        } finally {
            arr.recycle();
        }
    }

    public void setCustomFont(String asset) {
        Typeface tf = null;
        try {
            tf = Typeface.createFromAsset(context.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: " + e.getMessage());
        }

        setTypeface(tf, Typeface.NORMAL);

//        setPadding(0, ViewUtils.dpToPx(3), 0, ViewUtils.dpToPx(3));

        setIncludeFontPadding(false);
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

package mttdat.viewplus;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;

import androidx.appcompat.widget.AppCompatTextView;

public class TextViewPlus extends AppCompatTextView {
    private static final String TAG = "TextViewPlus";
    private float graphicHeight = 0, graphicWidth = 0, customSizePx = 0, customSizePt = 0;

    public TextViewPlus(Context context) {
        super(context);
    }

    public TextViewPlus(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        graphicHeight = a.getFloat(R.styleable.TextViewPlus_customGraphicHeight, 0f);
        graphicWidth = a.getFloat(R.styleable.TextViewPlus_customGraphicWidth, 0f);

        a.recycle();

        setCustomFont(context, attrs);
        setCustomSize(context, attrs);
        setCustomPadding(context, attrs);
    }

    public TextViewPlus(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        graphicHeight = a.getFloat(R.styleable.TextViewPlus_customGraphicHeight, 0f);
        graphicWidth = a.getFloat(R.styleable.TextViewPlus_customGraphicWidth, 0f);
        a.recycle();

        setCustomFont(context, attrs);
        setCustomSize(context, attrs);
        setCustomPadding(context, attrs);
    }

    private void setCustomPadding(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        float cusPadding = a.getFloat(R.styleable.TextViewPlus_customPadding, 0f);
        float cusPaddingLeft = a.getFloat(R.styleable.TextViewPlus_customPaddingLeft, 0f);
        float cusPaddingRight = a.getFloat(R.styleable.TextViewPlus_customPaddingRight, 0f);
        float cusPaddingTop = a.getFloat(R.styleable.TextViewPlus_customPaddingTop, 0f);
        float cusPaddingBottom = a.getFloat(R.styleable.TextViewPlus_customPaddingBottom, 0f);

        int paddingLeft = 0;
        int paddingRight = 0;
        int paddingTop = 0;
        int paddingBottom = 0;

        float ratioW = (float) getContext().getResources().getDisplayMetrics().widthPixels / graphicWidth;
        float ratioH = (float) getContext().getResources().getDisplayMetrics().heightPixels / graphicHeight;

        if(cusPadding != 0f) {
            paddingLeft = (int) (cusPadding * ratioW);
            paddingRight = (int) (cusPadding * ratioW);
            paddingTop = (int) (cusPadding * ratioH);
            paddingBottom = (int) (cusPadding * ratioH);
        }

        if(cusPaddingLeft != 0f) {
            paddingLeft = (int) (cusPaddingLeft * ratioW);
        }

        if(cusPaddingRight != 0f) {
            paddingRight = (int) (cusPaddingRight * ratioW);
        }

        if(cusPaddingTop != 0f) {
            paddingTop = (int) (cusPaddingTop * ratioH);
        }

        if(cusPaddingBottom != 0f) {
            paddingBottom = (int) (cusPaddingBottom * ratioH);
        }

        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);

        a.recycle();
    }

    public void setCustomPadding(int cusPaddingLeft, int cusPaddingTop, int cusPaddingBottom, int cusPaddingRight){
        int paddingLeft = 0;
        int paddingRight = 0;
        int paddingTop = 0;
        int paddingBottom = 0;

        float ratioW = (float) getContext().getResources().getDisplayMetrics().widthPixels / graphicWidth;
        float ratioH = (float) getContext().getResources().getDisplayMetrics().heightPixels / graphicHeight;

        if(cusPaddingLeft != 0f) {
            paddingLeft = (int) (cusPaddingLeft * ratioW);
        }

        if(cusPaddingRight != 0f) {
            paddingRight = (int) (cusPaddingRight * ratioW);
        }

        if(cusPaddingTop != 0f) {
            paddingTop = (int) (cusPaddingTop * ratioH);
        }

        if(cusPaddingBottom != 0f) {
            paddingBottom = (int) (cusPaddingBottom * ratioH);
        }

        setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
    }

    private void setCustomSize(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        float customSize = a.getFloat(R.styleable.TextViewPlus_customSize, 0f);
        customSizePx = a.getFloat(R.styleable.TextViewPlus_customSizePx, 0f);
        customSizePt = a.getFloat(R.styleable.TextViewPlus_customSizePt, 0f);

        setCustomSize(customSize);

        a.recycle();
    }

    public void setCustomSize(float customSize){
        if(customSize != 0f) {
            float sizePixel = (float) getContext().getResources().getDisplayMetrics().heightPixels * (customSizePx / graphicHeight) / customSizePt;

            setTextSize(TypedValue.COMPLEX_UNIT_PX, customSize * sizePixel);
        }
    }

    private void setCustomFont(Context ctx, AttributeSet attrs) {
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.TextViewPlus);
        String customFont = a.getString(R.styleable.TextViewPlus_customFont);
        setCustomFont(ctx, customFont);
        a.recycle();
    }

    public boolean setCustomFont(Context ctx, String asset) {
        Typeface tf = null;
        try {
        tf = Typeface.createFromAsset(ctx.getAssets(), asset);
        } catch (Exception e) {
            Log.e(TAG, "Could not get typeface: " + e.getMessage());
            return false;
        }

        setTypeface(tf, getTypeface().getStyle());

        setPadding(0, 0, 0, 0);
        setIncludeFontPadding(false);

        return true;
    }
}
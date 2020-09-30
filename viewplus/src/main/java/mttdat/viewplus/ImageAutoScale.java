package mttdat.viewplus;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class ImageAutoScale extends AppCompatImageView {

    private float heightPerWidth;
    private float widthPerHeight;
    private int height, width;
//    private Path path;
//    private RectF rect;
//    private float radius;

    public ImageAutoScale(Context context) {
        super(context);

        heightPerWidth = -1;
        widthPerHeight = -1;
//        init();
    }

    public ImageAutoScale(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        parseAttributes(context, attrs);

//        init();
    }

//    private void init() {
//        path = new Path();
//    }

    /**
     * Parses the attributes.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    private void parseAttributes(final Context context, final AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.ImageAutoScale);
        try {
            heightPerWidth = a.getFloat(
                    R.styleable.ImageAutoScale_heightPerWidth, -1f);

            widthPerHeight = a.getFloat(
                    R.styleable.ImageAutoScale_widthPerHeight, -1f);

//            radius = a.getFloat(
//                    R.styleable.ImageAutoScale_radius, -1f);

        } finally {
            a.recycle();
        }
    }

    public ImageAutoScale(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // Get temporary width and height.
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // Measure actual height base on mode, because onMeasure() gets called many times.
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(height, heightSize);
        } else {
            //Be whatever you want
//            height = desiredHeight;
        }

        // Measure actual width base on mode.
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(width, widthSize);
        } else {
            //Be whatever you want
//                width = desiredWidth;
        }

        if((widthPerHeight != -1 && heightPerWidth != -1) ||
                (widthPerHeight == -1 && heightPerWidth == -1)    ){
            // Do nothing.
        }else if(heightPerWidth != -1){
            height = (int) (width * heightPerWidth);
        }else { // widthPerHeight != -1
            width = (int) (this.height * widthPerHeight);
        }

        setMeasuredDimension(width, height);
    }

    public float getWidthPerHeight() {
        return widthPerHeight;
    }

    public void setWidthPerHeight(float widthPerHeight) {
        this.widthPerHeight = widthPerHeight;
        this.heightPerWidth = -1;
    }

    public float getHeightPerWidth() {
        return heightPerWidth;
    }

    public void setHeightPerWidth(float heightPerWidth) {
        this.heightPerWidth = heightPerWidth;
        this.widthPerHeight = -1;
    }

    //    @Override
//    protected void onDraw(Canvas canvas) {
//
//        if(radius != -1){
//            rect = new RectF(0, 0, this.getWidth(), this.getHeight());
//            path.addRoundRect(rect, radius, radius, Path.Direction.CW);
//            canvas.clipPath(path);
//            super.onDraw(canvas);
//        }
//    }
}

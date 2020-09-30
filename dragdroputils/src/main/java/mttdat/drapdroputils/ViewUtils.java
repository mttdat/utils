package mttdat.drapdroputils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

public class ViewUtils {

    public static void hideSoftKeyboard(View view, Context context) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showSoftKeyboard(View view, Context context) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }

        return -1;
    }

    /*
        It doesn't work on all devices, but most.
        It can work even the screen is not drawn yet, because it's read from dimen file.
    * */
    public static int getStatusBarHeightLegacy(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getStatusBarHeight(Activity activity){
        Rect rectangle = new Rect();
        Window window = activity.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);

        return rectangle.top;   // This's statusBarHeight.

        // Get the title bar height...

//        int contentViewTop =
//                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
//        int titleBarHeight = contentViewTop - statusBarHeight;
//
//        return titleBarHeight;
    }

    public static boolean hasNavigationBar(){
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);
        if (!hasBackKey || !hasHomeKey) {//there's a navigation bar
            return true;
        }

        return false;
    }

    public static void disableTouchTheft(View view) {

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                view.getParent().requestDisallowInterceptTouchEvent(true);
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        view.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                return false;
            }
        });
    }

    public static void pushAppInBackground(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        return;
    }

    public static int getScreenHeight(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.heightPixels;
    }

    public static int getScreenWidth(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        return displayMetrics.widthPixels;
    }

    // @textSize: unit px.
    public static int getWidthFromText(Context context, String text, float textSize, String textFont){
        Rect bounds = new Rect();
        Paint paint = new Paint();

        if(textFont != null) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), textFont);
            paint.setTypeface(tf);
        }

        paint.setTextSize(textSize/* * Resources.getSystem().getDisplayMetrics().scaledDensity*/);
        paint.getTextBounds(text, 0, text.length(), bounds);

        return (int) paint.measureText(text);   // More correct.
//        return bounds.width();
    }

    // @textSize: unit px.
    public static int getHeightFromText(Context context, String text, float textSize, String textFont){
        Rect bounds = new Rect();
        Paint paint = new Paint();

        if(textFont != null) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), textFont);
            paint.setTypeface(tf);
        }

        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);

        return bounds.height();
    }

    // @textSize: unit px.
    public static int[] getWidthHeightFromText(Context context, String text, float textSize, String textFont){
        Rect bounds = new Rect();
        Paint paint = new Paint();

        if(textFont != null) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), textFont);
            paint.setTypeface(tf);
        }

        paint.setTextSize(textSize);
        paint.getTextBounds(text, 0, text.length(), bounds);

        return new int[]{(int) paint.measureText(text), bounds.height()};
    }

    public static boolean has_nav_bar(Resources resources) {
        int id = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        return id > 0 && resources.getBoolean(id);
    }

    public static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity)cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper)cont).getBaseContext());

        return null;
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }

    public static float dpToPx(float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density + 0.5f;
    }

    public static float pxToSp(float px) {
        return px / Resources.getSystem().getDisplayMetrics().scaledDensity;
    }

    public static float spToPx(float sp) {
        return sp * Resources.getSystem().getDisplayMetrics().scaledDensity;
    }

    public static float customSizeToPx(float customSizePx, float customSizePt, float graphicHeight, float customSize){
        return customSize * (float) Resources.getSystem().getDisplayMetrics().heightPixels * (customSizePx / graphicHeight) / customSizePt;
    }

    public static float customPaddingHorizontalToPx(float padding, float graphic){
        return padding * (float) Resources.getSystem().getDisplayMetrics().widthPixels / graphic;
    }

    public static float customPaddingVerticalToPx(float padding, float graphic){
        return padding * (float) Resources.getSystem().getDisplayMetrics().heightPixels / graphic;
    }

    // Get left position relative to the root layout of Activity.
    public static int getRelativeLeft(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getLeft();
        else
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
    }

    // Get right position relative to the root layout of Activity.
    public static int getRelativeRight(View myView) {
        return getRelativeLeft(myView) + myView.getWidth();
    }

    // Get top position relative to the root layout of Activity.
    public static int getRelativeTop(View myView) {
        if (myView.getParent() == myView.getRootView())
            return myView.getTop();
        else
            return myView.getTop() + getRelativeTop((View) myView.getParent());
    }

    // Get bottom position relative to the root layout of Activity.
    public static int getRelativeBottom(View myView) {
        return getRelativeTop(myView) + myView.getHeight();
    }

    public static boolean isFullScreen(Activity activity) {
        int flg = activity.getWindow().getAttributes().flags;
        boolean flag = false;
        if ((flg & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
            flag = true;
        }
        return flag;
    }

    // Type can be: drawable, ...
    public static int getResourceIdFromName(Context context, String name){
        return getResourceIdFromName(context, name, "drawable");
    }

    public static int getResourceIdFromName(Context context, String name, String type){
        return context.getResources().getIdentifier(name, type, context.getPackageName());
    }

    public static boolean isInView(MotionEvent ev, View... views) {
        Rect rect = new Rect();
        for (View v : views) {
            v.getGlobalVisibleRect(rect);
            if (rect.contains((int) ev.getX(), (int) ev.getY()))
                return true;
        }
        return false;
    }

    public static boolean isInView(int x, int y, View... views) {
        Rect rect = new Rect();
        for (View v : views) {
            v.getGlobalVisibleRect(rect);

            int offsetGlobalY = getStatusBarHeight((Activity) v.getContext());
            rect.bottom += offsetGlobalY;
            rect.top += offsetGlobalY;

            if (rect.contains(x, y))
                return true;
        }
        return false;
    }

    public static boolean isViewFullyVisible(View view){
        Rect rect = new Rect();
        if(view.getGlobalVisibleRect(rect)
                && view.getHeight() == rect.height()
                && view.getWidth() == rect.width() ) {
            // view is fully visible on screen.
            return true;
        }

        return false;
    }

    public static boolean isViewPartlyVisible(View view){
        Rect rect = new Rect();

        // view is partly visible on screen.
        return view.getGlobalVisibleRect(rect)
                && (rect.height() <= Math.ceil(view.getHeight() * view.getScaleY()) && rect.height() > 0)
                && (rect.width() <= Math.ceil(view.getWidth() * view.getScaleX()) && rect.width() > 0);

    }

    public static boolean isViewOverlapping(View firstView, View secondView) {
        int[] firstPosition = new int[2];
        int[] secondPosition = new int[2];

        firstView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        firstView.getLocationOnScreen(firstPosition);
        secondView.getLocationOnScreen(secondPosition);

        int r = firstView.getMeasuredWidth() + firstPosition[0];
        int l = secondPosition[0];
        return r >= l && (r != 0 && l != 0);
    }

    public static Bitmap takeScreenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();

        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight, width, height  - statusBarHeight);
        view.destroyDrawingCache();
        return b;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public static Bitmap getBitmapFromView(View v, int expectedWidth, int expectedHeight) {

        Bitmap bitmap = Bitmap.createBitmap(expectedWidth, expectedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmapFromViewNotDraw(View v, int expectedWidth, int expectedHeight) {

        v.measure(expectedWidth, expectedHeight);
        Bitmap bitmap = Bitmap.createBitmap(expectedWidth, expectedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.layout(0, 0, expectedWidth, expectedHeight);
        v.draw(canvas);
        return bitmap;
    }
}

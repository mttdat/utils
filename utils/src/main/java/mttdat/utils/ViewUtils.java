package mttdat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.core.content.res.ResourcesCompat;

import mttdat.b.utils.BuildConfig;

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

    public static int getHeightFromTextV2(Context context, String text, int textSizePx, String textFont,
                                          float lineSpacingAdd, float lineSpacingMul, boolean includeFontPadding,
                                          int widthBound){

        TextPaint myTextPaint = new TextPaint();
        myTextPaint.setAntiAlias(true);
        myTextPaint.setTextSize(textSizePx);

        if(textFont != null) {
            Typeface tf = Typeface.createFromAsset(context.getAssets(), textFont);
            myTextPaint.setTypeface(tf);
        }

        StaticLayout.Builder builder = StaticLayout.Builder.obtain(text, 0, text.length(), myTextPaint, widthBound)
                .setAlignment(Layout.Alignment.ALIGN_NORMAL)
                .setLineSpacing(lineSpacingAdd, lineSpacingMul)
                .setIncludePad(includeFontPadding);

        StaticLayout myStaticLayout = builder.build();

        return myStaticLayout.getHeight();
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

    public static int getColor(Context context, int colorRes){
        return ResourcesCompat.getColor(context.getResources(), colorRes, null);
    }

    public static boolean isInView(MotionEvent ev, View... views) {
        Rect rect = new Rect();
        for (View v : views) {
            v.getGlobalVisibleRect(rect);

            int offsetGlobalY = getStatusBarHeight((Activity) v.getContext());
            rect.bottom += offsetGlobalY;
            rect.top += offsetGlobalY;

            if (rect.contains((int) ev.getRawX(), (int) ev.getRawY()))
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

    public static Drawable getDrawableFromId(Context context, int id) {
        return  ResourcesCompat.getDrawable(context.getResources(), id, null);
    }

    public static Bitmap getBitmapFromDrawable(Context context, int resDrawable) {
        return BitmapFactory.decodeResource(context.getResources(), resDrawable);
    }

    /**
     * README: Must be called on UI-Thread (main thread), so post to a handler if needed
     *
     * NOTES:
     * 		- Avoid using wrap_content on ScrollView!
     * 		- If you have center or right/bottom gravity, you should re-layout all nodes, not only the wrap_content: just call the method with the boolean set to true
     *
     * @author Eric, April 2014
     */

    /**
     * Does what a proper requestLayout() should do about layout_width or layout_height = "wrap_content"
     *
     * Warning: if the subTreeRoot itself has a "wrap_content" layout param, the size will be computed without boundaries maximum size.
     * 			If you do have limits, consider either passing the parent, or calling the method with the size parameters (View.MeasureSpec)
     *
     * @param subTreeRoot  root of the sub tree you want to recompute
     */
    public static final void wrapContentAgain( ViewGroup subTreeRoot )
    {
        wrapContentAgain( subTreeRoot, false, View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED );
    }
    /** Same but allows re-layout of all views, not only those with "wrap_content". Necessary for "center", "right", "bottom",... */
    public static final void wrapContentAgain( ViewGroup subTreeRoot, boolean relayoutAllNodes )
    {
        wrapContentAgain( subTreeRoot, relayoutAllNodes, View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED );
    }
    /**
     * Same as previous, but with given size in case subTreeRoot itself has layout_width or layout_height = "wrap_content"
     */
    public static void wrapContentAgain( ViewGroup subTreeRoot, boolean relayoutAllNodes,
                                         int subTreeRootWidthMeasureSpec, int subTreeRootHeightMeasureSpec  ) {
        Log.d("wrapContentAgain", "+++ LayoutWrapContentUpdater wrapContentAgain on subTreeRoot=[" + subTreeRoot + "], with w="
                + subTreeRootWidthMeasureSpec + " and h=" + subTreeRootHeightMeasureSpec);

        if (BuildConfig.DEBUG && !("main".equals(Thread.currentThread().getName()))) {
            throw new AssertionError("Assertion failed");
        }

        if (subTreeRoot == null)
            return;
        ViewGroup.LayoutParams layoutParams = subTreeRoot.getLayoutParams();

        // --- First, we force measure on the subTree
        int widthMeasureSpec = subTreeRootWidthMeasureSpec;

        // When LayoutParams.MATCH_PARENT and Width > 0, we apply measured width to avoid getting dimensions too big
        if (layoutParams.width != ViewGroup.LayoutParams.WRAP_CONTENT && subTreeRoot.getWidth() > 0)
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(subTreeRoot.getWidth(), View.MeasureSpec.EXACTLY);
        int heightMeasureSpec = subTreeRootHeightMeasureSpec;

        // When LayoutParams.MATCH_PARENT and Height > 0, we apply measured height to avoid getting dimensions too big
        if (layoutParams.height != ViewGroup.LayoutParams.WRAP_CONTENT && subTreeRoot.getHeight() > 0)
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(subTreeRoot.getHeight(), View.MeasureSpec.EXACTLY);

        // This measure recursively the whole sub-tree
        subTreeRoot.measure(widthMeasureSpec, heightMeasureSpec);

        // --- Then recurse on all children to correct the sizes
        recurseWrapContent(subTreeRoot, relayoutAllNodes);

        // --- RequestLayout to finish properly
        subTreeRoot.requestLayout();
    }


    /**
     * Internal method to recurse on view tree. Tag you View nodes in XML layouts to read the logs more easily
     */
    private static void recurseWrapContent( View nodeView, boolean relayoutAllNodes )
    {
        // Does not recurse when visibility GONE
        if ( nodeView.getVisibility() == View.GONE ) {
            // nodeView.layout( nodeView.getLeft(), nodeView.getTop(), 0, 0 );		// No need
            return;
        }

        ViewGroup.LayoutParams layoutParams = nodeView.getLayoutParams();
        boolean isWrapWidth  = ( layoutParams.width  == ViewGroup.LayoutParams.WRAP_CONTENT ) || relayoutAllNodes;
        boolean isWrapHeight = ( layoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT ) || relayoutAllNodes;

        if ( isWrapWidth || isWrapHeight ) {

            boolean changed = false;
            int right  = nodeView.getRight();
            int bottom = nodeView.getBottom();

            if ( isWrapWidth  && nodeView.getMeasuredWidth() > 0 ) {
                right = nodeView.getLeft() + nodeView.getMeasuredWidth();
                changed = true;
                Log.v("wrapContentAgain", "+++ LayoutWrapContentUpdater recurseWrapContent set Width to "+ nodeView.getMeasuredWidth() +" of node Tag="+ nodeView.getTag() +" ["+ nodeView +"]");
            }
            if ( isWrapHeight && nodeView.getMeasuredHeight() > 0 ) {
                bottom = nodeView.getTop() + nodeView.getMeasuredHeight();
                changed = true;
                Log.v("wrapContentAgain", "+++ LayoutWrapContentUpdater recurseWrapContent set Height to "+ nodeView.getMeasuredHeight() +" of node Tag="+ nodeView.getTag() +" ["+ nodeView +"]");
            }

            if (changed) {
                nodeView.layout( nodeView.getLeft(), nodeView.getTop(), right, bottom );
                // FIXME: Adjust left & top position when gravity = "center" / "bottom" / "right"
            }
        }

        // --- Recurse
        if ( nodeView instanceof ViewGroup ) {
            ViewGroup nodeGroup = (ViewGroup)nodeView;
            for (int i = 0; i < nodeGroup.getChildCount(); i++) {
                recurseWrapContent( nodeGroup.getChildAt(i), relayoutAllNodes );
            }
        }
        return;
    }
}

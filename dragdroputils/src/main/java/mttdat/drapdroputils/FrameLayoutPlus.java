package mttdat.drapdroputils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/* Because default overlay can't not adjust width/ height, this custom view will be use as
 * a dummy overlay. */
public class FrameLayoutPlus extends FrameLayout {

    private class ViewInfo{
        ViewGroup parent;
        ViewGroup.LayoutParams layoutParams;
        float x, y;  // X, y is an animated value, it has the highest priority of position.

        public ViewInfo(ViewGroup parent, ViewGroup.LayoutParams layoutParams, float x, float y) {
            this.parent = parent;
            this.layoutParams = layoutParams;
            this.x = x;
            this.y = y;
        }
    }

    private class MyHashMap<X, Y> extends LinkedHashMap<View, ViewInfo> {

        public ViewInfo put(View key, ViewGroup parent, ViewGroup.LayoutParams layoutParams, float x, float y){
            ViewInfo viewInfo = new ViewInfo(parent, layoutParams, x, y);

            return super.put(key, viewInfo);
        }
    }

    public MyHashMap<View, ViewInfo> stack; // This contains the view and its parent when you remove the view
                                            // to add into this frame layout.

    public FrameLayoutPlus(@NonNull Context context) {
        super(context);
        init();
    }

    public FrameLayoutPlus(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FrameLayoutPlus(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        stack = new MyHashMap<>();
    }

    public void addViewAsOverlay(final View v){

        int[] location = {0,0};
        v.getLocationOnScreen(location);

        final int newX = location[0] - ViewUtils.getRelativeLeft(this);
        final int newY = location[1] - ViewUtils.getRelativeTop(this);

        LayoutParams layoutParams = new LayoutParams(v.getWidth(), v.getHeight());
        layoutParams.leftMargin = newX;
        layoutParams.topMargin  = newY;

        stack.put(v, (ViewGroup) v.getParent(), v.getLayoutParams(), v.getX(), v.getY());
        ((ViewGroup) v.getParent()).removeView(v);

        this.addView(v, layoutParams);

        v.post(new Runnable() {
            @Override
            public void run() {
                v.setX(newX);
                v.setY(newY);
            }
        });
    }

    public void addViewsAsOverlay(ArrayList<View> views){

        for(final View v : views) {

            int[] location = {0,0};
            v.getLocationOnScreen(location);

            final int newX = location[0] - ViewUtils.getRelativeLeft(this);
            final int newY = location[1] - ViewUtils.getRelativeTop(this);

            LayoutParams layoutParams = new LayoutParams(v.getWidth(), v.getHeight());
            layoutParams.leftMargin = newX;
            layoutParams.topMargin = newY;

            stack.put(v, (ViewGroup) v.getParent(), v.getLayoutParams(), v.getX(), v.getY());
            ((ViewGroup) v.getParent()).removeView(v);

            this.addView(v, layoutParams);

            // After adding view to overlay successfully.
            v.post(new Runnable() {
                @Override
                public void run() {
                    v.setX(newX);
                    v.setY(newY);
                }
            });
        }
    }

    public void returnBackAddedView(final View view){
        if(stack.containsKey(view)){

            final ViewInfo viewInfo = stack.get(view);

            // If parent still exists.
            if(viewInfo.parent != null){
                this.removeView(view);

                viewInfo.parent.addView(view, viewInfo.layoutParams);

                view.requestLayout();

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setX(viewInfo.x);
                        view.setY(viewInfo.y);
                    }
                });

                stack.remove(view);
            }
        }
    }

    // Notice: because the view was affected by old attribute params when its in an old parent,
    // adding it into a new parent will generate new position (x, y) based on what new set of attributes
    // PLUS old attribute.
    // It will be: -gravity -margin + x/y + new attr.
    // If can or your view change (e.g: text change), we will set position x, y we want must be easier.
    public void returnBackAddedView(final View view, final float x, final float y){
        if(stack.containsKey(view)){

            final ViewInfo viewInfo = stack.get(view);

            // If parent still exists.
            if(viewInfo.parent != null){
                this.removeView(view);

                viewInfo.parent.addView(view, viewInfo.layoutParams);
                view.requestLayout();

                // After adding view to overlay successfully.
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setX(x);
                        view.setY(y);
                    }
                });

                stack.remove(view);
            }
        }
    }

    public void returnBackAllAddedView(){
        Iterator<MyHashMap.Entry<View, ViewInfo>> iterator = stack.entrySet().iterator();

        while (iterator.hasNext()){
            MyHashMap.Entry<View, ViewInfo> entry = iterator.next();

            final ViewInfo viewInfo = entry.getValue();
            final View view = entry.getKey();

            // If parent still exists.
            if(viewInfo.parent != null){
                this.removeView(view);

                viewInfo.parent.addView(view, viewInfo.layoutParams);

                view.requestLayout();

                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.setX(viewInfo.x);
                        view.setY(viewInfo.y);
                    }
                });

                iterator.remove();
            }
        }
    }

    public void clear(){
        this.removeAllViews();
        stack.clear();
    }
}

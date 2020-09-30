package mttdat.utils;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.TextView;

import androidx.core.math.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class AnimatedViewUtils {

    private LinkedHashMap<String, AnimatedView> listAnimatedView;

    public enum Property{
        X,
        Y,
        WIDTH,
        HEIGHT,
        ALPHA,
        TEXT_SIZE,
        TEXT_COLOR,
        TEXT_SPACE,
        TEXT_SPACING_LETTER,
        TEXT_SPACING_LINE_MULTIPLIER,
        TEXT_SPACING_LINE_EXTRA
    }

    public interface OnAnimatedViewUpdateListener{
        void onReachTarget();
        void onReachOrigin();
    }

    public static final Property[] PROPERTIES = {
            Property.X, Property.Y, Property.WIDTH, Property.HEIGHT, Property.ALPHA, Property.TEXT_SIZE, Property.TEXT_COLOR
    };

    private OnAnimatedViewUpdateListener onAnimatedViewUpdateListener;

    public AnimatedViewUtils(){
        listAnimatedView = new LinkedHashMap<>();
    }

    public void put(String key, AnimatedView sharedView){
        listAnimatedView.put(key, sharedView);
    }

    public void remove(String key){
        if(listAnimatedView.containsKey(key)){
            listAnimatedView.remove(key);
        }
    }

    public void remove(){
        listAnimatedView.clear();
    }

    // It means it's all added to overlay successfully to animate.
    public boolean isSharedViewReady(){
        for(AnimatedView sharedView :
                listAnimatedView.values())  {

            // If animated view has X property, check if it was added to overlay.
            if(sharedView.containProperty(Property.X)){

                // If a view is not ready.
                if(sharedView.getView().getX() != sharedView.getOriginalX()){

                    // Overlay is not ready.
                    return false;
                }
            }
        }

        return true;
    }

    public boolean isSharedViewReady(Property property){
        for(AnimatedView sharedView :
                listAnimatedView.values())  {

            // If animated view has a property, check if it was added to overlay.
            if(sharedView.containProperty(property)){

                float curVal = -1;
                float expectedVal = sharedView.getOriginal(property);

                switch (property){
                    case X:
                        curVal = sharedView.getView().getX();
                        break;
                    case Y:
                        curVal = sharedView.getView().getY();
                        break;
                }

                // If a view is not ready.
                if(curVal != expectedVal){

                    // Overlay is not ready.
                    return false;
                }
            }
        }

        return true;
    }

    public void updateAnimatedView(float offset){

        boolean isReachOrigin = false, isReachTarget = false;

        for(AnimatedView animatedView :
                listAnimatedView.values())  {

            View view = animatedView.getView();

            for(Property property : AnimatedViewUtils.PROPERTIES) {

                if (isNotChange(property, animatedView)) {
                    continue;
                }

                float val = getValue(animatedView.isInverse(property) ? -offset : offset, property, animatedView);
                isReachOrigin = val == animatedView.getOriginal(property);
                isReachTarget = val == animatedView.getTarget(property);

                switch (property) {
                    case X:
                        view.setX(val);
                        break;

                    case Y:
                        view.setY(val);
                        break;

                    case WIDTH:
                        view.getLayoutParams().width = (int) val;
                        break;

                    case HEIGHT:
                        view.getLayoutParams().height = (int) val;
                        break;

                    case ALPHA:
                        view.setAlpha(val);
                        view.invalidate();
                        break;

                    case TEXT_SIZE:
                        ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
                        view.invalidate();
                        break;

                    case TEXT_COLOR:
//                        ((TextView) animatedView).setTextColor((int) getValue(sharedView.isInverse(property) ? -offset : offset, property, sharedView));
                        break;
                }
            }

            view.requestLayout();
        }

        if(onAnimatedViewUpdateListener != null) {
            if(isReachOrigin){
                onAnimatedViewUpdateListener.onReachOrigin();
            }

            if(isReachTarget){
                onAnimatedViewUpdateListener.onReachTarget();
            }
        }
    }

    // Primary view is used to check if the animation reaches target or origin status.
    public void updateAnimatedView(float offset, String primaryViewKey){
        boolean isReachOrigin = false, isReachTarget = false;

        for (LinkedHashMap.Entry<String, AnimatedView> entry : listAnimatedView.entrySet()){

            AnimatedView animatedView = entry.getValue();

            View view = animatedView.getView();

            for(Property property : AnimatedViewUtils.PROPERTIES) {

                if (isNotChange(property, animatedView)) {
                    continue;
                }

                float val = getValue(animatedView.isInverse(property) ? -offset : offset, property, animatedView);

                if(entry.getKey().equals(primaryViewKey)) {
                    isReachOrigin = val == animatedView.getOriginal(property);
                    isReachTarget = val == animatedView.getTarget(property);
                }

                switch (property) {
                    case X:
                        view.setX(val);
                        break;

                    case Y:
                        view.setY(val);
                        break;

                    case WIDTH:
                        view.getLayoutParams().width = (int) val;
                        break;

                    case HEIGHT:
                        view.getLayoutParams().height = (int) val;
                        break;

                    case ALPHA:
                        view.setAlpha(val);
                        view.invalidate();
                        break;

                    case TEXT_SIZE:
                        ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, val);
                        view.invalidate();
                        break;

                    case TEXT_COLOR:
//                        ((TextView) animatedView).setTextColor((int) getValue(sharedView.isInverse(property) ? -offset : offset, property, sharedView));
                        break;
                }
            }

            view.requestLayout();
        }

        if(onAnimatedViewUpdateListener != null) {
            if(isReachOrigin){
                onAnimatedViewUpdateListener.onReachOrigin();
            }

            if(isReachTarget){
                onAnimatedViewUpdateListener.onReachTarget();
            }
        }
    }

    public void updateAnimatedViewLegacy(float offset){
        for(AnimatedView sharedView :
                listAnimatedView.values())  {

            View animatedView = sharedView.getView();

            for(Property property : AnimatedViewUtils.PROPERTIES) {

                if (isNotChange(property, sharedView)) {
                    continue;
                }

                switch (property) {
                    case X:
                        animatedView.setX(getValueLegacy(sharedView.isInverse(property) ? -offset : offset, property, sharedView));
                        break;

                    case Y:
                        animatedView.setY(getValueLegacy(sharedView.isInverse(property) ? -offset : offset, property, sharedView));
                        break;

                    case WIDTH:
                        animatedView.getLayoutParams().width = (int) getValueLegacy(sharedView.isInverse(property) ? -offset : offset, property, sharedView);
                        break;

                    case HEIGHT:
                        animatedView.getLayoutParams().height = (int) getValueLegacy(sharedView.isInverse(property) ? -offset : offset, property, sharedView);
                        break;

                    case ALPHA:
                        animatedView.setAlpha(getValueLegacy(sharedView.isInverse(property) ? -offset : offset, property, sharedView));
                        animatedView.invalidate();
                        break;

                    case TEXT_SIZE:
                        ((TextView) animatedView).setTextSize(TypedValue.COMPLEX_UNIT_PX, getValueLegacy(sharedView.isInverse(property) ? -offset : offset, property, sharedView));
                        animatedView.invalidate();
                        break;

                    case TEXT_COLOR:
//                        ((TextView) animatedView).setTextColor((int) getValue(sharedView.isInverse(property) ? -offset : offset, property, sharedView));
                        break;
                }
            }

            animatedView.requestLayout();
        }
    }

    private boolean isNotChange(Property property, AnimatedView sharedView){
        return !sharedView.containProperty(property) || sharedView.getRange(property) <= Float.MIN_NORMAL;
    }

    // If your value is decreasing by offset, use a negative offset; and otherwise.
    // (Increase value from current value).
    private float getValue(float offset, Property property, AnimatedView sharedView){

        float curVal = 0;

        switch (property){
            case X:
                curVal = sharedView.getView().getX();
                break;
            case Y:
                curVal = sharedView.getView().getY();
                break;
            case WIDTH:
                curVal = sharedView.getView().getWidth();
                break;
            case HEIGHT:
                curVal = sharedView.getView().getHeight();
                break;
            case ALPHA:
                curVal = sharedView.getView().getAlpha();
                break;
            case TEXT_SIZE:
                curVal = ((TextView) sharedView.getView()).getTextSize();
                break;
            case TEXT_COLOR:

//                int curColor = ((TextView) sharedView.getView()).getCurrentTextColor();
//                Color.red(curColor)

                break;
            default:
                return -1;
        }

        return MathUtils.clamp(curVal + offset * sharedView.getRange(property) / sharedView.getMaxRange(property), sharedView.getMin(property), sharedView.getMax(property));
    }

    // If your value is decreasing by offset, use a negative offset; and otherwise.
    // (Increase value from original value).
    private float getValueLegacy(float offset, Property property, AnimatedView sharedView){
        return MathUtils.clamp(sharedView.getOriginal(property) + offset * sharedView.getRange(property) / sharedView.getMaxRange(property), sharedView.getMin(property), sharedView.getMax(property));
    }

    public void animateToTarget(Animator.AnimatorListener animatorListener){
        AnimatorSet animatorSet = new AnimatorSet();

        ArrayList<Animator> animators = new ArrayList<>();

        for(AnimatedView sharedView :
                listAnimatedView.values())  {

            final View animatedView = sharedView.getView();

            for(Property property : AnimatedViewUtils.PROPERTIES){

                if(isNotChange(property, sharedView)){
                    continue;
                }

                Animator animator = null;

                switch (property){
                    case X:
                        animator = ObjectAnimator.ofFloat(animatedView, "x", animatedView.getX(), sharedView.getTarget(property));
                        break;

                    case Y:
                        animator = ObjectAnimator.ofFloat(animatedView, "y", animatedView.getY(), sharedView.getTarget(property));
                        break;

                    case WIDTH:
                        animator = ValueAnimator.ofInt(animatedView.getWidth(), (int) sharedView.getTarget(property));
                        ((ValueAnimator)animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                animatedView.getLayoutParams().width = (int) valueAnimator.getAnimatedValue();

                                // Request layout after change dimension.
                                animatedView.requestLayout();
                            }
                        });
                        break;

                    case HEIGHT:
                        animator = ValueAnimator.ofInt(animatedView.getHeight(), (int) sharedView.getTarget(property));
                        ((ValueAnimator)animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                animatedView.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();

                                // Request layout after change dimension.
                                animatedView.requestLayout();
                            }
                        });
                        break;

                    case ALPHA:
                        animator = ObjectAnimator.ofFloat(animatedView, "alpha", animatedView.getAlpha(), sharedView.getTarget(property));
                        break;

                    case TEXT_SIZE:

                        animator = ValueAnimator.ofFloat(((TextView) animatedView).getTextSize(), sharedView.getTarget(property));
                        ((ValueAnimator)animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ((TextView) animatedView).setTextSize(TypedValue.COMPLEX_UNIT_PX, (Float) valueAnimator.getAnimatedValue());

                                // Request layout after change dimension.
                                animatedView.requestLayout();
                            }
                        });

                        break;

                    case TEXT_COLOR:
//                        ((TextView) animatedView).setTextColor((int) getValue(sharedView.isInverse(property) ? -offset : offset, property, sharedView));
                        break;
                }

                animators.add(animator);
            }
        }

        animatorSet.playTogether(animators);

        if(animatorListener != null) {
            animatorSet.addListener(animatorListener);
        }

        animatorSet.start();
    }

    public void animateToTarget(Animator.AnimatorListener animatorListener, Interpolator interpolator){
        AnimatorSet animatorSet = new AnimatorSet();

        ArrayList<Animator> animators = new ArrayList<>();

        for(AnimatedView sharedView :
                listAnimatedView.values())  {

            final View animatedView = sharedView.getView();

            for(Property property : AnimatedViewUtils.PROPERTIES){

                if(isNotChange(property, sharedView)){
                    continue;
                }

                Animator animator = null;

                switch (property){
                    case X:
                        animator = ObjectAnimator.ofFloat(animatedView, "x", animatedView.getX(), sharedView.getTarget(property));
                        break;

                    case Y:
                        animator = ObjectAnimator.ofFloat(animatedView, "y", animatedView.getY(), sharedView.getTarget(property));
                        break;

                    case WIDTH:
                        animator = ValueAnimator.ofInt(animatedView.getWidth(), (int) sharedView.getTarget(property));
                        ((ValueAnimator)animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                animatedView.getLayoutParams().width = (int) valueAnimator.getAnimatedValue();

                                // Request layout after change dimension.
                                animatedView.requestLayout();
                            }
                        });
                        break;

                    case HEIGHT:
                        animator = ValueAnimator.ofInt(animatedView.getHeight(), (int) sharedView.getTarget(property));
                        ((ValueAnimator)animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                animatedView.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();

                                // Request layout after change dimension.
                                animatedView.requestLayout();
                            }
                        });
                        break;

                    case ALPHA:
                        animator = ObjectAnimator.ofFloat(animatedView, "alpha", animatedView.getAlpha(), sharedView.getTarget(property));
                        break;

                    case TEXT_SIZE:

                        animator = ValueAnimator.ofFloat(((TextView) animatedView).getTextSize(), sharedView.getTarget(property));
                        ((ValueAnimator)animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ((TextView) animatedView).setTextSize(TypedValue.COMPLEX_UNIT_PX, (Float) valueAnimator.getAnimatedValue());

                                // Request layout after change dimension.
                                animatedView.requestLayout();
                            }
                        });

                        break;

                    case TEXT_COLOR:
//                        ((TextView) animatedView).setTextColor((int) getValue(sharedView.isInverse(property) ? -offset : offset, property, sharedView));
                        break;
                }

                animators.add(animator);
            }
        }

        animatorSet.playTogether(animators);

        if(animatorListener != null) {
            animatorSet.addListener(animatorListener);
        }

        if(interpolator != null) {
            animatorSet.setInterpolator(interpolator);
        }

        animatorSet.start();
    }

    public void animateToOrigin(Animator.AnimatorListener animatorListener){
        AnimatorSet animatorSet = new AnimatorSet();

        ArrayList<Animator> animators = new ArrayList<>();

        for(AnimatedView sharedView :
                listAnimatedView.values())  {

            final View animatedView = sharedView.getView();

            for(Property property : AnimatedViewUtils.PROPERTIES){

                if(isNotChange(property, sharedView)){
                    continue;
                }

                Animator animator = null;

                switch (property){
                    case X:
                        animator = ObjectAnimator.ofFloat(animatedView, "x", animatedView.getX(), sharedView.getOriginal(property));
                        break;

                    case Y:
                        animator = ObjectAnimator.ofFloat(animatedView, "y", animatedView.getY(), sharedView.getOriginal(property));
                        break;

                    case WIDTH:
                        animator = ValueAnimator.ofInt(animatedView.getWidth(), (int) sharedView.getOriginal(property));
                        ((ValueAnimator)animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                animatedView.getLayoutParams().width = (int) valueAnimator.getAnimatedValue();

                                // Request layout after change dimension.
                                animatedView.requestLayout();
                            }
                        });
                        break;

                    case HEIGHT:
                        animator = ValueAnimator.ofInt(animatedView.getHeight(), (int) sharedView.getOriginal(property));
                        ((ValueAnimator)animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                animatedView.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();

                                // Request layout after change dimension.
                                animatedView.requestLayout();
                            }
                        });
                        break;

                    case ALPHA:
                        animator = ObjectAnimator.ofFloat(animatedView, "alpha", animatedView.getAlpha(), sharedView.getOriginal(property));
                        break;

                    case TEXT_SIZE:

                        animator = ValueAnimator.ofFloat(((TextView) animatedView).getTextSize(), sharedView.getOriginal(property));
                        ((ValueAnimator)animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ((TextView) animatedView).setTextSize(TypedValue.COMPLEX_UNIT_PX, (Float) valueAnimator.getAnimatedValue());

                                // Request layout after change dimension.
                                animatedView.requestLayout();
                            }
                        });

                        break;

                    case TEXT_COLOR:
//                        ((TextView) animatedView).setTextColor((int) getValue(sharedView.isInverse(property) ? -offset : offset, property, sharedView));
                        break;
                }

                animators.add(animator);
            }
        }

        animatorSet.playTogether(animators);

        if(animatorListener != null) {
            animatorSet.addListener(animatorListener);
        }

        animatorSet.start();
    }

    public void animateToOrigin(Animator.AnimatorListener animatorListener, Interpolator interpolator){
        AnimatorSet animatorSet = new AnimatorSet();

        ArrayList<Animator> animators = new ArrayList<>();

        for(AnimatedView sharedView :
                listAnimatedView.values())  {

            final View animatedView = sharedView.getView();

            for(Property property : AnimatedViewUtils.PROPERTIES){

                if(isNotChange(property, sharedView)){
                    continue;
                }

                Animator animator = null;

                switch (property){
                    case X:
                        animator = ObjectAnimator.ofFloat(animatedView, "x", animatedView.getX(), sharedView.getOriginal(property));
                        break;

                    case Y:
                        animator = ObjectAnimator.ofFloat(animatedView, "y", animatedView.getY(), sharedView.getOriginal(property));
                        break;

                    case WIDTH:
                        animator = ValueAnimator.ofInt(animatedView.getWidth(), (int) sharedView.getOriginal(property));
                        ((ValueAnimator)animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                animatedView.getLayoutParams().width = (int) valueAnimator.getAnimatedValue();

                                // Request layout after change dimension.
                                animatedView.requestLayout();
                            }
                        });
                        break;

                    case HEIGHT:
                        animator = ValueAnimator.ofInt(animatedView.getHeight(), (int) sharedView.getOriginal(property));
                        ((ValueAnimator)animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                animatedView.getLayoutParams().height = (int) valueAnimator.getAnimatedValue();

                                // Request layout after change dimension.
                                animatedView.requestLayout();
                            }
                        });
                        break;

                    case ALPHA:
                        animator = ObjectAnimator.ofFloat(animatedView, "alpha", animatedView.getAlpha(), sharedView.getOriginal(property));
                        break;

                    case TEXT_SIZE:

                        animator = ValueAnimator.ofFloat(((TextView) animatedView).getTextSize(), sharedView.getOriginal(property));
                        ((ValueAnimator)animator).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                ((TextView) animatedView).setTextSize(TypedValue.COMPLEX_UNIT_PX, (Float) valueAnimator.getAnimatedValue());

                                // Request layout after change dimension.
                                animatedView.requestLayout();
                            }
                        });

                        break;

                    case TEXT_COLOR:
//                        ((TextView) animatedView).setTextColor((int) getValue(sharedView.isInverse(property) ? -offset : offset, property, sharedView));
                        break;
                }

                animators.add(animator);
            }
        }

        animatorSet.playTogether(animators);

        if(animatorListener != null) {
            animatorSet.addListener(animatorListener);
        }

        if(interpolator != null) {
            animatorSet.setInterpolator(interpolator);
        }

        animatorSet.start();
    }

    // Which animated view and which its property we used to check "shouldBackToOrigin".
    public boolean shouldBackToOrigin(String id, Property property){

        AnimatedView sharedView = listAnimatedView.get(id);

        float curValue = 0;

        switch (property) {
            case X:
                curValue = sharedView.getView().getX();
                break;

            case Y:
                curValue = sharedView.getView().getY();
                break;

            case WIDTH:
                curValue = sharedView.getView().getWidth();
                break;

            case HEIGHT:
                curValue = sharedView.getView().getHeight();
                break;

            case ALPHA:
                curValue = sharedView.getView().getAlpha();
                break;

            case TEXT_SIZE:
                curValue = ((TextView) sharedView.getView()).getTextSize();
                break;

            case TEXT_COLOR:
//                        ((TextView) animatedView).setTextColor((int) getValue(sharedView.isInverse(property) ? -offset : offset, property, sharedView));
                break;
        }

        return (Math.abs(curValue - sharedView.getOriginal(property)) < Math.abs(curValue - sharedView.getTarget(property)));
    }

    /* ****** Get - set ****** */

    public HashMap<String, AnimatedView> getListAnimatedView() {
        return listAnimatedView;
    }

    public void setOnAnimatedViewUpdateListener(OnAnimatedViewUpdateListener onAnimatedViewUpdateListener) {
        this.onAnimatedViewUpdateListener = onAnimatedViewUpdateListener;
    }
}

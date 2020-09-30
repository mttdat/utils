package mttdat.drapdroputils;

import android.view.View;
import android.widget.TextView;

import java.util.HashMap;

public class AnimatedView {

    /* Properties */

    private float maxRange = 800f;  // Unit: px.

    private View view;

    private boolean isDirectionTouchNegative;  // Direction touch moving is positive or negative.

    private HashMap<AnimatedViewUtils.Property, AnimatedProperty> properties;

    public AnimatedView(View originalView, View targetView) {

        this.view = originalView;

        properties = new HashMap<>();

        for(AnimatedViewUtils.Property property : AnimatedViewUtils.PROPERTIES){

            AnimatedProperty animatedProperty = null;
            float originalValue = 0, targetValue = 0;
            int[] locationOriginalView = {0,0}, locationTargetView = {0,0};

            switch (property){
                case X:

                    originalView.getLocationOnScreen(locationOriginalView);
                    targetView.getLocationOnScreen(locationTargetView);

                    originalValue = locationOriginalView[0];
                    targetValue = locationTargetView[0];

                    break;

                case Y:

                    originalView.getLocationOnScreen(locationOriginalView);
                    targetView.getLocationOnScreen(locationTargetView);

                    originalValue = locationOriginalView[1];
                    targetValue = locationTargetView[1];

                    break;

                case WIDTH:

                    originalValue = originalView.getWidth();
                    targetValue = targetView.getWidth();

                    break;

                case HEIGHT:

                    originalValue = originalView.getHeight();
                    targetValue = targetView.getHeight();

                    break;

                case ALPHA:

                    originalValue = originalView.getAlpha();
                    targetValue = targetView.getAlpha();

                    break;

                case TEXT_SIZE:

                    if(!(originalView instanceof TextView) || !(targetView instanceof TextView)){
                        break;
                    }

                    originalValue = ((TextView) originalView).getTextSize();
                    targetValue = ((TextView) targetView).getTextSize();

                    break;

                case TEXT_COLOR:

                    if(!(originalView instanceof TextView) || !(targetView instanceof TextView)){
                        break;
                    }

                    originalValue = ((TextView) originalView).getCurrentTextColor();
                    targetValue = ((TextView) targetView).getCurrentTextColor();

                    break;
            }

            animatedProperty = new AnimatedProperty(
                    property,
                    originalValue,
                    targetValue,
                    targetValue < originalValue);

            properties.put(property, animatedProperty);
        }
    }

    public AnimatedView(View originalView, View targetView,  View overlayView) {

        this.view = originalView;

        properties = new HashMap<>();

        for(AnimatedViewUtils.Property property : AnimatedViewUtils.PROPERTIES){

            AnimatedProperty animatedProperty;
            float originalValue = 0, targetValue = 0;
            int[] locationOriginalView = {0,0}, locationTargetView = {0,0}, locationOverlayView = {0,0};

            switch (property){
                case X:

                    originalView.getLocationOnScreen(locationOriginalView);
                    targetView.getLocationOnScreen(locationTargetView);
                    overlayView.getLocationOnScreen(locationOverlayView);

                    originalValue = locationOriginalView[0] - locationOverlayView[0];
                    targetValue = locationTargetView[0] - locationOverlayView[0];

                    break;

                case Y:

                    originalView.getLocationOnScreen(locationOriginalView);
                    targetView.getLocationOnScreen(locationTargetView);
                    overlayView.getLocationOnScreen(locationOverlayView);

                    originalValue = locationOriginalView[1] - locationOverlayView[1];
                    targetValue = locationTargetView[1] - locationOverlayView[1];

                    break;

                case WIDTH:

                    originalValue = originalView.getWidth();
                    targetValue = targetView.getWidth();

                    break;

                case HEIGHT:

                    originalValue = originalView.getHeight();
                    targetValue = targetView.getHeight();

                    break;

                case ALPHA:

                    originalValue = originalView.getAlpha();
                    targetValue = targetView.getAlpha();

                    break;

                case TEXT_SIZE:

                    if(!(originalView instanceof TextView) || !(targetView instanceof TextView)){
                        break;
                    }

                    originalValue = ((TextView) originalView).getTextSize();
                    targetValue = ((TextView) targetView).getTextSize();

                    break;

                case TEXT_COLOR:

                    if(!(originalView instanceof TextView) || !(targetView instanceof TextView)){
                        break;
                    }

                    originalValue = ((TextView) originalView).getCurrentTextColor();
                    targetValue = ((TextView) targetView).getCurrentTextColor();

                    break;
            }

            animatedProperty = new AnimatedProperty(
                    property,
                    originalValue,
                    targetValue,
                    targetValue < originalValue);

            properties.put(property, animatedProperty);
        }
    }

    public AnimatedView(View view, AnimatedProperty... animatedProperties) {

        this.view = view;

        properties = new HashMap<>();

        for (AnimatedProperty animatedProperty : animatedProperties){
            properties.put(animatedProperty.property, animatedProperty);
        }

//        for(AnimatedViewUtils.Property property : AnimatedViewUtils.PROPERTIES){
//
//            // Set dummy property if it was not set.
//            if(!properties.containsKey(property)){
//                properties.put(property, new AnimatedProperty(property, 0, 0));
//            }
//        }
    }

    public AnimatedView(View originalView, View targetView, boolean isDirectionTouchNegative) {

        this.view = originalView;

        this.isDirectionTouchNegative = isDirectionTouchNegative;

        properties = new HashMap<>();

        for(AnimatedViewUtils.Property property : AnimatedViewUtils.PROPERTIES){

            AnimatedProperty animatedProperty = null;
            float originalValue = 0, targetValue = 0;
            int[] locationOriginalView = {0,0}, locationTargetView = {0,0};

            switch (property){
                case X:

                    originalView.getLocationOnScreen(locationOriginalView);
                    originalView.getLocationOnScreen(locationTargetView);

                    originalValue = locationOriginalView[0];
                    targetValue = locationTargetView[0];

                    break;

                case Y:

                    originalView.getLocationOnScreen(locationOriginalView);
                    originalView.getLocationOnScreen(locationTargetView);

                    originalValue = locationOriginalView[1];
                    targetValue = locationTargetView[1];

                    break;

                case WIDTH:

                    originalValue = originalView.getWidth();
                    targetValue = targetView.getWidth();

                    break;

                case HEIGHT:

                    originalValue = originalView.getHeight();
                    targetValue = targetView.getHeight();

                    break;

                case ALPHA:

                    originalValue = originalView.getAlpha();
                    targetValue = targetView.getAlpha();

                    break;

                case TEXT_SIZE:

                    if(!(originalView instanceof TextView) || !(targetView instanceof TextView)){
                        break;
                    }

                    originalValue = ((TextView) originalView).getTextSize();
                    targetValue = ((TextView) targetView).getTextSize();

                    break;

                case TEXT_COLOR:

                    if(!(originalView instanceof TextView) || !(targetView instanceof TextView)){
                        break;
                    }

                    originalValue = ((TextView) originalView).getCurrentTextColor();
                    targetValue = ((TextView) targetView).getCurrentTextColor();

                    break;
            }

            animatedProperty = new AnimatedProperty(
                    property,
                    originalValue,
                    targetValue,
                    isDirectionTouchNegative ? (targetValue > originalValue) : (targetValue < originalValue));

            properties.put(property, animatedProperty);
        }
    }

    /* ***************************************** */

    public View getView() {
        return view;
    }

    public AnimatedView setMaxRange(float maxRange){
        this.maxRange = maxRange;
        return this;
    }

    public AnimatedView setMaxRange(float maxRange, AnimatedViewUtils.Property property){
        properties.get(property).maxRange = maxRange;
        return this;
    }

    public float getMaxRange() {
        return maxRange;
    }

    public float getMaxRange(AnimatedViewUtils.Property property) {
        return properties.get(property).maxRange != -1 ? properties.get(property).maxRange : this.maxRange;
    }

    public boolean containProperty(AnimatedViewUtils.Property property){
        return properties.containsKey(property);
    }

    public AnimatedView removeProperty(AnimatedViewUtils.Property property){
        properties.remove(property);

        return this;
    }

    /* ***************************************** */

    public float getOriginalX() {
        return properties.get(AnimatedViewUtils.Property.X).originalValue;
    }

    public float getOriginalY() {
        return properties.get(AnimatedViewUtils.Property.Y).originalValue;
    }

    public int getOriginalWidth() {
        return (int) properties.get(AnimatedViewUtils.Property.WIDTH).originalValue;
    }

    public int getOriginalHeight() {
        return (int) properties.get(AnimatedViewUtils.Property.HEIGHT).originalValue;
    }

    public float getOriginalAlpha() {
        return (int) properties.get(AnimatedViewUtils.Property.ALPHA).originalValue;
    }

    public float getOriginal(AnimatedViewUtils.Property property){

        return properties.get(property).originalValue;
    }

    /* ***************************************** */

    public float getTargetX() {
        return properties.get(AnimatedViewUtils.Property.X).targetValue;
    }

    public float getTargetY() {
        return properties.get(AnimatedViewUtils.Property.Y).targetValue;
    }

    public int getTargetWidth() {
        return (int) properties.get(AnimatedViewUtils.Property.WIDTH).targetValue;
    }

    public int getTargetHeight() {
        return (int) properties.get(AnimatedViewUtils.Property.HEIGHT).targetValue;
    }

    public float getTargetAlpha() {
        return properties.get(AnimatedViewUtils.Property.ALPHA).targetValue;
    }

    public float getTarget(AnimatedViewUtils.Property property){
        return properties.get(property).targetValue;
    }

    /* ***************************************** */

    public float getRangeX() {
        return properties.get(AnimatedViewUtils.Property.X).range;
    }

    public float getRangeY() {
        return properties.get(AnimatedViewUtils.Property.Y).range;
    }

    public int getRangeWidth() {
        return (int) properties.get(AnimatedViewUtils.Property.WIDTH).range;
    }

    public int getRangeHeight() {
        return (int) properties.get(AnimatedViewUtils.Property.HEIGHT).range;
    }

    public float getRangeAlpha() {
        return properties.get(AnimatedViewUtils.Property.ALPHA).range;
    }

    public float getRange(AnimatedViewUtils.Property property){
        return properties.get(property).range;
    }

    /* ***************************************** */

    public float getMinX() {
        return properties.get(AnimatedViewUtils.Property.X).min;
    }

    public float getMinY() {
        return properties.get(AnimatedViewUtils.Property.Y).min;
    }

    public int getMinWidth() {
        return (int) properties.get(AnimatedViewUtils.Property.WIDTH).min;
    }

    public int getMinHeight() {
        return (int) properties.get(AnimatedViewUtils.Property.HEIGHT).min;
    }

    public float getMinAlpha() {
        return properties.get(AnimatedViewUtils.Property.ALPHA).min;
    }

    public float getMin(AnimatedViewUtils.Property property){
        return properties.get(property).min;
    }

    /* ***************************************** */

    public float getMaxX() {
        return properties.get(AnimatedViewUtils.Property.X).max;
    }

    public float getMaxY() {
        return properties.get(AnimatedViewUtils.Property.Y).max;
    }

    public int getMaxWidth() {
        return (int) properties.get(AnimatedViewUtils.Property.WIDTH).max;
    }

    public int getMaxHeight() {
        return (int) properties.get(AnimatedViewUtils.Property.HEIGHT).max;
    }

    public float getMaxAlpha() {
        return properties.get(AnimatedViewUtils.Property.ALPHA).max;
    }

    public float getMax(AnimatedViewUtils.Property property){
        return properties.get(property).max;
    }

    /* ***************************************** */

    public AnimatedView setInverseX(boolean inverse) {

        properties.get(AnimatedViewUtils.Property.X).isInverse = inverse;

        return this;
    }

    public AnimatedView setInverseY(boolean inverse) {

        properties.get(AnimatedViewUtils.Property.Y).isInverse = inverse;

        return this;
    }

    public AnimatedView setInverseWidth(boolean inverse) {

        properties.get(AnimatedViewUtils.Property.WIDTH).isInverse = inverse;

        return this;
    }

    public AnimatedView setInverseHeight(boolean inverse) {
        properties.get(AnimatedViewUtils.Property.HEIGHT).isInverse = inverse;

        return this;
    }

    public AnimatedView setInverseAlpha(boolean inverse) {
        properties.get(AnimatedViewUtils.Property.ALPHA).isInverse = inverse;

        return this;
    }

    public AnimatedView setInverse(AnimatedViewUtils.Property property, boolean inverse) {

        properties.get(property).isInverse = inverse;

        return this;
    }

    /* ***************************************** */

    public boolean isInverseX() {
        return properties.get(AnimatedViewUtils.Property.X).isInverse;
    }

    public boolean isInverseY() {
        return properties.get(AnimatedViewUtils.Property.Y).isInverse;
    }

    public boolean isInverseWidth() {
        return properties.get(AnimatedViewUtils.Property.WIDTH).isInverse;
    }

    public boolean isInverseHeight() {
        return properties.get(AnimatedViewUtils.Property.HEIGHT).isInverse;
    }

    public boolean isInverseAlpha() {
        return properties.get(AnimatedViewUtils.Property.ALPHA).isInverse;
    }

    public boolean isInverse(AnimatedViewUtils.Property property) {
        return properties.get(property).isInverse;
    }
}

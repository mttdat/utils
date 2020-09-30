package com.elevationviews;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

public class ViewUtils {

    public static Drawable generateBackgroundSolidWithShadow(View view,
                                                             @ColorRes int backgroundColor,
                                                             int cornerRadiusValue,
                                                             @ColorRes int shadowColor,
                                                             int elevationValue,
                                                             int shadowGravity) {

        int backgroundColorValue = ContextCompat.getColor(view.getContext(), backgroundColor);

        float[] outerRadius = {cornerRadiusValue, cornerRadiusValue, cornerRadiusValue,
                cornerRadiusValue, cornerRadiusValue, cornerRadiusValue, cornerRadiusValue,
                cornerRadiusValue};

        /* Create shadow. */
        ShapeDrawable shadowDrawable = new ShapeDrawable();

        // Create padding.
        Rect shapeDrawablePadding = new Rect();
        shapeDrawablePadding.left = elevationValue;
        shapeDrawablePadding.right = elevationValue;
        shapeDrawablePadding.top = elevationValue;
        shapeDrawablePadding.bottom = elevationValue;
        shadowDrawable.setPadding(shapeDrawablePadding);    // This padding will affect real editable view's area.


        // Get paint of shadow drawable to custom (e.g: add shadow and make its color transparent).
        Paint mPaint = shadowDrawable.getPaint();
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setAntiAlias(true);  // This is compulsory to use color transparent.

        // Set shadow.
        if(shadowColor != -1) {
            int shadowColorValue = ContextCompat.getColor(view.getContext(),shadowColor);

            int dy;
            switch (shadowGravity) {
                case Gravity.CENTER:
                    dy = 0;
                    break;
                case Gravity.TOP:
                    dy = -1 * elevationValue / 2;
                    break;
                default:
                case Gravity.BOTTOM:
                    dy = elevationValue / 2;
                    break;
            }
            mPaint.setShadowLayer(
                    8,                          // Blur radius.
                    0,                          // Dx.
                    dy,                         // Dy.
                    shadowColorValue);          // Shadow color.
        }

        // This is compulsory to use shadow layer.
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Set shape of shadow.
        shadowDrawable.setShape(new RoundRectShape(outerRadius, null, null));

        /* Create background. */

        // Background is solid color.
        ShapeDrawable backgroundDrawable = new ShapeDrawable();
        backgroundDrawable.getPaint().setColor(backgroundColorValue);
        backgroundDrawable.setShape(new RoundRectShape(outerRadius, null, null));

        // Merge drawable as a list item_flow_list.
        LayerDrawable drawable = new LayerDrawable(new Drawable[]{shadowDrawable, backgroundDrawable});
        drawable.setLayerInset(0, elevationValue, elevationValue, elevationValue, elevationValue);
        drawable.setLayerInset(1, 2, 2, 2, 2);    // Offset of subsequent is based on the preceding layer.

        return drawable;
    }

    public static Drawable generateBackgroundDrawableWithShadow(View view,
                                                                @DrawableRes int backgroundDrawableRes,
                                                                int cornerRadiusValue,
                                                                @ColorRes int shadowColor,
                                                                int elevationValue,
                                                                int shadowGravity) {

        float[] outerRadius = {cornerRadiusValue, cornerRadiusValue, cornerRadiusValue,
                cornerRadiusValue, cornerRadiusValue, cornerRadiusValue, cornerRadiusValue,
                cornerRadiusValue};

        /* Create shadow. */
        ShapeDrawable shadowDrawable = new ShapeDrawable();

        // Create padding.
        Rect shapeDrawablePadding = new Rect();
        shapeDrawablePadding.left = elevationValue;
        shapeDrawablePadding.right = elevationValue;
        shapeDrawablePadding.top = elevationValue;
        shapeDrawablePadding.bottom = elevationValue;
        shadowDrawable.setPadding(shapeDrawablePadding);    // This padding will affect real editable view's area.


        // Get paint of shadow drawable to custom (e.g: add shadow and make its color transparent).
        Paint mPaint = shadowDrawable.getPaint();
        mPaint.setColor(Color.TRANSPARENT);
        mPaint.setAntiAlias(true);  // This is compulsory to use color transparent.

        // Set shadow.
        if(shadowColor != -1) {
            int shadowColorValue = ContextCompat.getColor(view.getContext(),shadowColor);

            int dy;
            switch (shadowGravity) {
                case 2:
                    dy = 0;
                    break;
                case 1:
                    dy = -1 * elevationValue / 2;
                    break;
                default:
                case 0:
                    dy = elevationValue / 2;
                    break;
            }
            mPaint.setShadowLayer(
                    8,                          // Blur radius.
                    0,                          // Dx.
                    dy,                         // Dy.
                    shadowColorValue);          // Shadow color.
        }

        // This is compulsory to use shadow layer.
        view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // Set shape of shadow.
        shadowDrawable.setShape(new RoundRectShape(outerRadius, null, null));


        /* Create background. */

        // Background is a drawable.
        Drawable backgroundDrawable = view.getContext().getResources().getDrawable(backgroundDrawableRes);

        // Merge drawable as a list item_flow_list.
        LayerDrawable drawable = new LayerDrawable(new Drawable[]{shadowDrawable, backgroundDrawable});
        drawable.setLayerInset(0, elevationValue, elevationValue, elevationValue, elevationValue);
        drawable.setLayerInset(1, 2, 2, 2, 2);    // Offset of subsequent is based on the preceding layer.

        return drawable;
    }

    public static int dpToPx(int dp) {
        return Math.round(dp * Resources.getSystem().getDisplayMetrics().density);
    }
}

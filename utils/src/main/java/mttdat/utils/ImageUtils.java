package mttdat.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.io.IOException;

public class ImageUtils {
    public static void loadImageFromURL(ImageView view, String url){
        Picasso.get().load(url).into(view);
    }

    public static void loadImageFromURLv2(ImageView view, String url, int width, int height){
        Picasso.get().load(url).resize(width, height).into(view);
    }

    public static void loadImageFromURL(Target target, String url){
        Picasso.get().load(url).into(target);
    }

    public static void loadImageFromResLocal(ImageView view, int resDrawable){
        Picasso.get().load(resDrawable).into(view);
    }

    public static void loadImageFromResLocal(ImageView view, int resDrawable, Transformation transformation){
        Picasso.get().load(resDrawable).transform(transformation).into(view);
    }

    public static void loadImageFromResLocalv2(ImageView view, int resDrawable, int width, int height){
        Picasso.get().load(resDrawable).resize(width, height).into(view);
    }

    public static Bitmap getImageFromURL(String url, int expectedWidth, int expectedHeight){
        try {
            return Picasso.get().load(url).resize(expectedWidth, expectedHeight).get();
        } catch (IOException e) {
        }

        return null;
    }

    public static void loadImageFromURL(ImageView view, String url, int placeHolderRes, int errorRes){
        Picasso.get()
                .load(url)
                .placeholder(placeHolderRes)
                .error(errorRes)
                .into(view);
    }

    public static void loadImageFromURL(ImageView view, String url, int placeHolderRes, int errorRes, Transformation transformation){
        Picasso.get()
                .load(url)
                .placeholder(placeHolderRes)
                .error(errorRes)
                .transform(transformation)
                .into(view);
    }
}
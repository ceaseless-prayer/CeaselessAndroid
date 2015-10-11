package org.theotech.ceaselessandroid.util;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by uberx on 10/10/15.
 */
public class PicassoUtils {
    public static void load(Context context, ImageView imageView, int imageResId, int placeholderResId) {
        load(context, imageView, imageResId, placeholderResId, null);
    }

    public static void load(Context context, ImageView imageView, int imageResId, int placeholderResId, Callback callback) {
        Picasso.with(context).load(imageResId).placeholder(placeholderResId).fit().noFade().into(imageView, callback);
    }

    public static void load(Context context, ImageView imageView, String path, int placeholderResId) {
        load(context, imageView, path, placeholderResId, null);
    }

    public static void load(Context context, ImageView imageView, String path, int placeholderResId, Callback callback) {
        Picasso.with(context).load(path).placeholder(placeholderResId).fit().noFade().into(imageView, callback);
    }

    public static void load(Context context, ImageView imageView, Uri path, int placeholderResId) {
        load(context, imageView, path, placeholderResId, null);
    }

    public static void load(Context context, ImageView imageView, Uri path, int placeholderResId, Callback callback) {
        Picasso.with(context).load(path).placeholder(placeholderResId).fit().noFade().into(imageView, callback);
    }
}

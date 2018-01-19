package com.berwin.cocoadialog.utils;

import android.content.Context;

public class DensityUtil {

    /**
     * Change dip to the pixels.
     * @param context The context instance.
     * @param dpValue The value of the dip.
     * @return The value of pixels.
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * Change pixels to the dip.
     * @param context The context instance.
     * @param pxValue The value of pixels.
     * @return The value of dip.
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}

package com.lyentech.lib.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;

public class ScreenUtils {
    public static int dp2px(Context context, float dpValue) {
        return (int) ((float) ((int) context.getResources().getDisplayMetrics().density) * dpValue + 0.5F);
    }

    public static int sp2px(Context context, float spValue) {
        float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5F);
    }


    private static Point point = new Point();

    /**
     * 获取屏幕宽度
     *
     * @param activity Activity
     * @return ScreenWidth
     */
    public static int getScreenWidth(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        if (display != null) {
            display.getSize(point);
            return point.x;
        }
        return 0;
    }

    public static int getWidth(Context context) {
        int w=context.getResources().getDisplayMetrics().widthPixels;
        return w;
    }

    /**
     * 获取屏幕高度
     *
     * @param activity Activity
     * @return ScreenHeight
     */
    public static int getScreenHeight(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        if (display != null) {
            display.getSize(point);
            return point.y - getStatusBarHeight(activity);
        }
        return 0;
    }

    /**
     * 计算statusBar高度
     *
     * @param activity Activity
     * @return statusBar高度
     */
    public static int getStatusBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 计算navigationBar高度
     *
     * @param activity navigationBar高度
     * @return navigationBar高度
     */
    public static int getNavigationBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }


    public static boolean isNavigationBarShow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(activity).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static float realScreenArg(Application application, int index) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        int width4 = displayMetrics.widthPixels;
        int height4 = displayMetrics.heightPixels;
        float densityDpi4 = displayMetrics.densityDpi;
        float density4 = displayMetrics.density;
        if (index == 0) {
            return width4;
        } else if (index == 1) {
            return height4;
        } else if (index == 2) {
            return densityDpi4;
        } else {
            return density4;
        }
    }
}

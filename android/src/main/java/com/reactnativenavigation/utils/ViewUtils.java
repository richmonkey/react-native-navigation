package com.reactnativenavigation.utils;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewTreeObserver;
import com.reactnativenavigation.params.AppStyle;

public class ViewUtils {
    public static void runOnPreDraw(final View view, final Runnable task) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (!view.getViewTreeObserver().isAlive()) {
                    return true;
                }
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                task.run();
                return true;
            }
        });
    }

    public static void tintDrawable(Drawable drawable, int tint, boolean enabled) {
        drawable.setColorFilter(new PorterDuffColorFilter(enabled ? tint :
                AppStyle.appStyle.titleBarDisabledButtonColor.getColor(),
                PorterDuff.Mode.SRC_IN));
    }

    public static int generateViewId() {
        return View.generateViewId();
    }


}


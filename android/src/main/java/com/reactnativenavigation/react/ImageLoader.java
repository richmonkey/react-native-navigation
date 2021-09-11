package com.reactnativenavigation.react;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.controllers.NavigationCommandsHandler;

public class ImageLoader {
    private static final String FILE_SCHEME = "file";

    public static Drawable loadImage(String iconSource) {
        if (NavigationCommandsHandler.navigationApplication.isDebug()) {
            return JsDevImageLoader.loadIcon(iconSource);
        } else {
            Uri uri = Uri.parse(iconSource);
            if (isLocalFile(uri)) {
                return loadFile(uri);
            } else {
                return loadResource(iconSource);
            }
        }
    }

    private static boolean isLocalFile(Uri uri) {
        return FILE_SCHEME.equals(uri.getScheme());
    }

    private static Drawable loadFile(Uri uri) {
        return new BitmapDrawable(NavigationCommandsHandler.application.getResources(), uri.getPath());
    }

    private static Drawable loadResource(String iconSource) {
        return ResourceDrawableIdHelper.instance.getResourceDrawable(NavigationCommandsHandler.application, iconSource);
    }
}

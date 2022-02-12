package com.reactnativenavigation.params;

import android.os.Bundle;
import androidx.annotation.ColorInt;

public class StyleParams {
    public static class Color {
        @ColorInt
        private Integer color = null;

        public Color() {
            color = null;
        }

        public Color(Integer color) {
            this.color = color;
        }

        public boolean hasColor() {
            return color != null;
        }

        @ColorInt
        public int getColor() {
            if (!hasColor()) {
                throw new RuntimeException("Color undefined");
            }
            return color;
        }

        public static Color parse(Bundle bundle, String key) {
            return bundle.containsKey(key) ? new Color(bundle.getInt(key)) : new Color();
        }
    }

    /*
    public boolean topBarHidden;
    public boolean topBarElevationShadowEnabled;
    public boolean topTabsHidden;
    public boolean titleBarHideOnScroll;
    public boolean topBarTransparent;
    public boolean backButtonHidden;*/

    public Color statusBarColor;
    public boolean drawScreenBelowTopBar;
    public boolean titleBarHidden;
    public Color topBarColor;
    public boolean topBarTranslucent;
    public Color titleBarTitleColor;
    public Color titleBarSubtitleColor;
    public Color titleBarButtonColor;
    public Color titleBarDisabledButtonColor;
    public Color screenBackgroundColor;
    public Color navigationBarColor;

    public StyleParams() {
        statusBarColor = new Color();
        topBarColor = new Color();
        titleBarTitleColor = new Color();
        titleBarSubtitleColor = new Color();
        titleBarButtonColor = new Color();
        titleBarDisabledButtonColor = new Color();
        screenBackgroundColor = new Color();
        navigationBarColor = new Color();
    }
}

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

    public Color statusBarColor;
    public Color topBarColor;
    public boolean topBarHidden;
    public boolean topBarElevationShadowEnabled;
    public boolean topTabsHidden;
    public boolean drawScreenBelowTopBar;
    public boolean titleBarHidden;
    public boolean titleBarHideOnScroll;
    public boolean topBarTransparent;
    public boolean topBarTranslucent;
    public Color titleBarTitleColor;
    public Color titleBarSubtitleColor;
    public Color titleBarButtonColor;
    public Color titleBarDisabledButtonColor;
    public boolean backButtonHidden;
    public Color screenBackgroundColor;
    public Color navigationBarColor;
}

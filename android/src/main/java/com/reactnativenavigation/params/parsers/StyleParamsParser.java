package com.reactnativenavigation.params.parsers;

import android.graphics.Color;
import android.os.Bundle;

import com.reactnativenavigation.params.AppStyle;
import com.reactnativenavigation.params.StyleParams;

public class StyleParamsParser {
    private Bundle params;

    public StyleParamsParser(Bundle params) {
        if (params == null) {
            this.params = new Bundle();
        } else {
            this.params = params;
        }
    }

    public StyleParams parse() {
        StyleParams result = new StyleParams();
        result.statusBarColor = getColor("statusBarColor", getDefaultStatusBarColor());
        result.topBarColor = getColor("topBarColor", getDefaultTopBarColor());
        result.titleBarHideOnScroll = getBoolean("titleBarHideOnScroll", getDefaultTitleBarHideOnScroll());
        result.topBarHidden = getBoolean("topBarHidden", getDefaultTopBarHidden());
        result.topBarTransparent = getBoolean("topBarTransparent", getDefaultTopBarTransparent());
        result.drawScreenBelowTopBar = params.getBoolean("drawBelowTopBar", getDefaultScreenBelowTopBar());
        if (result.topBarTransparent) {
            result.drawScreenBelowTopBar = false;
        }
        result.titleBarHidden = getBoolean("titleBarHidden", getDefaultTopBarHidden());
        result.topBarElevationShadowEnabled = getBoolean("topBarElevationShadowEnabled", getDefaultTopBarElevationShadowEnabled());
        result.titleBarTitleColor = getColor("titleBarTitleColor", getDefaultTitleBarColor());
        result.topBarTranslucent = getBoolean("topBarTranslucent", getDefaultTopBarTranslucent());
        result.titleBarSubtitleColor = getColor("titleBarSubtitleColor", getDefaultSubtitleBarColor());
        result.titleBarButtonColor = getColor("titleBarButtonColor", getTitleBarButtonColor());
        result.titleBarDisabledButtonColor = getColor("titleBarDisabledButtonColor", getTitleBarDisabledButtonColor());
        result.backButtonHidden = getBoolean("backButtonHidden", getDefaultBackButtonHidden());
        result.topTabsHidden = getBoolean("topTabsHidden", getDefaultTopTabsHidden());
        result.screenBackgroundColor = getColor("screenBackgroundColor", getDefaultScreenBackgroundColor());
        result.navigationBarColor = getColor("navigationBarColor", getDefaultNavigationColor());

        return result;
    }



    private StyleParams.Color getDefaultNavigationColor() {
        return AppStyle.appStyle == null ? new StyleParams.Color() : AppStyle.appStyle.navigationBarColor;
    }

    private boolean getDefaultScreenBelowTopBar() {
        return AppStyle.appStyle != null && AppStyle.appStyle.drawScreenBelowTopBar;
    }

    private StyleParams.Color getDefaultScreenBackgroundColor() {
        return AppStyle.appStyle != null ? AppStyle.appStyle.screenBackgroundColor : getColor("screenBackgroundColor", new StyleParams.Color());
    }

    private boolean getDefaultTopTabsHidden() {
        return AppStyle.appStyle != null && AppStyle.appStyle.topTabsHidden;
    }

    private boolean getDefaultBackButtonHidden() {
        return AppStyle.appStyle != null && AppStyle.appStyle.backButtonHidden;
    }

    private StyleParams.Color getDefaultTitleBarColor() {
        return AppStyle.appStyle == null ? new StyleParams.Color() : AppStyle.appStyle.titleBarTitleColor;
    }

    private StyleParams.Color getDefaultSubtitleBarColor() {
        return AppStyle.appStyle == null ? new StyleParams.Color() : AppStyle.appStyle.titleBarSubtitleColor;
    }

    private StyleParams.Color getTitleBarButtonColor() {
        return AppStyle.appStyle == null ? new StyleParams.Color() : AppStyle.appStyle.titleBarButtonColor;
    }

    private StyleParams.Color getTitleBarDisabledButtonColor() {
        return AppStyle.appStyle == null ? new StyleParams.Color(Color.LTGRAY) : AppStyle.appStyle.titleBarDisabledButtonColor;
    }

    private boolean getDefaultTopBarTransparent() {
        return AppStyle.appStyle != null && AppStyle.appStyle.topBarTransparent;
    }

    private boolean getDefaultTopBarHidden() {
        return AppStyle.appStyle != null && AppStyle.appStyle.topBarHidden;
    }

    private boolean getDefaultTopBarElevationShadowEnabled() {
        return AppStyle.appStyle == null || AppStyle.appStyle.topBarElevationShadowEnabled;
    }

    private boolean getDefaultTopBarTranslucent() {
        return AppStyle.appStyle != null && AppStyle.appStyle.topBarTranslucent;
    }

    private boolean getDefaultTitleBarHideOnScroll() {
        return AppStyle.appStyle != null && AppStyle.appStyle.titleBarHideOnScroll;
    }

    private StyleParams.Color getDefaultTopBarColor() {
        return AppStyle.appStyle == null ? new StyleParams.Color() : AppStyle.appStyle.topBarColor;
    }

    private StyleParams.Color getDefaultStatusBarColor() {
        return AppStyle.appStyle == null ? new StyleParams.Color() : AppStyle.appStyle.statusBarColor;
    }

    private boolean getBoolean(String key, boolean defaultValue) {
        return params.containsKey(key) ? params.getBoolean(key) : defaultValue;
    }

    private StyleParams.Color getColor(String key, StyleParams.Color defaultColor) {
        StyleParams.Color color = StyleParams.Color.parse(params, key);
        if (color.hasColor()) {
            return color;
        } else {
            return defaultColor != null && defaultColor.hasColor() ? defaultColor : color;
        }
    }

    private int getInt(String key, int defaultValue) {
        return params.containsKey(key) ? params.getInt(key) : defaultValue;
    }
}

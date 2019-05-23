package com.reactnativenavigation.params.parsers;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;

import com.reactnativenavigation.params.NavigationParams;
import com.reactnativenavigation.params.ScreenParams;
import com.reactnativenavigation.react.ImageLoader;

import java.util.Set;

public class ScreenParamsParser extends Parser {
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";
    private static final String KEY_SCREEN_ID = "screenId";
    private static final String KEY_NAVIGATION_PARAMS = "navigationParams";
    private static final String STYLE_PARAMS = "styleParams";
    private static final String TOP_TABS = "topTabs";
    private static final String OVERRIDE_BACK_PRESS = "overrideBackPress";
    private static final String PASS_PROPS = "passProps";

    @SuppressWarnings("ConstantConditions")
    public static ScreenParams parse(Bundle params) {
        ScreenParams result = new ScreenParams();
        result.screenId = params.getString(KEY_SCREEN_ID);
        assertKeyExists(params, KEY_NAVIGATION_PARAMS);
        result.navigationParams = new NavigationParams(params.getBundle(KEY_NAVIGATION_PARAMS));

        result.styleParams = new StyleParamsParser(params.getBundle(STYLE_PARAMS)).parse();

        result.title = params.getString(KEY_TITLE);
        result.subtitle = params.getString(KEY_SUBTITLE);
        result.rightButtons = ButtonParser.parseRightButton(params);
        result.overrideBackPressInJs = params.getBoolean(OVERRIDE_BACK_PRESS, false);
        result.leftButton = ButtonParser.parseLeftButton(params);
        result.tabLabel = getTabLabel(params);
        result.tabIcon = getTabIcon(params);

        result.animateScreenTransitions = params.getBoolean("animated", true);

        result.passProps = params.getBundle(PASS_PROPS);
        covnertPassProps(result.passProps);

        return result;
    }

    //Parcelable[] -> Bundle[]
    private static void covnertPassProps(Bundle passProps) {
        if (passProps != null) {
            Set<String> keys = passProps.keySet();
            for (String k : keys) {
                Parcelable[] v = passProps.getParcelableArray(k);
                if (v != null) {
                    Bundle[] bundles = new Bundle[v.length];
                    int i;
                    for (i = 0; i < v.length; i++) {
                        Parcelable p = v[i];
                        if (p instanceof Bundle) {
                            bundles[i] = (Bundle)p;
                        } else {
                            break;
                        }
                    }

                    if (i == v.length) {
                        passProps.putParcelableArray(k, bundles);
                    }
                }
            }
        }
    }

    private static Drawable getTabIcon(Bundle params) {
        Drawable tabIcon = null;
        if (hasKey(params, "icon")) {
            tabIcon = ImageLoader.loadImage(params.getString("icon"));
        }
        return tabIcon;
    }

    private static String getTabLabel(Bundle params) {
        String tabLabel = null;
        if (hasKey(params, "label")) {
            tabLabel = params.getString("label");
        }
        return tabLabel;
    }

}

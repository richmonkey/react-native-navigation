package com.reactnativenavigation.params;


import android.os.Bundle;

import java.util.List;

public class ScreenParams {
    public String screenId;
    public String title;
    public String subtitle;
    public NavigationParams navigationParams;
    public List<TitleBarButtonParams> rightButtons;
    public TitleBarLeftButtonParams leftButton;

//    public StyleParams.Color screenBackgroundColor;
//    public StyleParams.Color topBarColor;
//    public StyleParams.Color titleBarButtonColor;
//    public StyleParams.Color titleBarDisabledButtonColor;
//    public boolean drawScreenBelowTopBar;

    public StyleParams styleParams;

    public Bundle passProps;

    public String getScreenInstanceId() {
        return navigationParams.screenInstanceId;
    }

    public String getNavigatorId() {
        return navigationParams.navigatorId;
    }

    public String getNavigatorEventId() {
        return navigationParams.navigatorEventId;
    }
}

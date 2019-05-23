package com.reactnativenavigation.layouts;

import android.view.View;


import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.screens.Screen;
import com.reactnativenavigation.views.LeftButtonOnClickListener;

import java.util.List;

public interface Layout extends LeftButtonOnClickListener {
    View asView();

    boolean onBackPressed();

    void setTopBarVisible(String screenInstanceId, boolean hidden, boolean animated);

    void setTitleBarTitle(String screenInstanceId, String title);

    void setTitleBarSubtitle(String screenInstanceId, String subtitle);

    void setTitleBarRightButtons(String screenInstanceId, String navigatorEventId, List<TitleBarButtonParams> titleBarButtons);

    void setTitleBarLeftButton(String screenInstanceId, String navigatorEventId, TitleBarLeftButtonParams titleBarLeftButtonParams);

    Screen getCurrentScreen();

    void destroy();
}

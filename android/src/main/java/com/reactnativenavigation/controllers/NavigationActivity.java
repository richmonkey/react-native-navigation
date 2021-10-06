package com.reactnativenavigation.controllers;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.Window;
import com.reactnativenavigation.params.StyleParams;
import com.reactnativenavigation.params.parsers.StyleParamsParser;
import com.reactnativenavigation.params.ActivityParams;
import com.reactnativenavigation.params.AppStyle;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.screens.Screen;
import com.reactnativenavigation.views.LeftButtonOnClickListener;
import com.reactnativenavigation.views.TitleBar;

import java.util.List;


public class NavigationActivity extends ReactNavigationActivity implements LeftButtonOnClickListener {
    private static final String SCREEN_STATE_ACTIVE = "active";
    private static final String SCREEN_STATE_BACKGROUND = "background";

    public static long _id = 0;
    static NavigationActivity currentActivity;

    private ActivityParams activityParams;
    private Screen screen;
    public TitleBar titleBar;

    public static String uniqueId(String prefix) {
        synchronized (NavigationActivity.class) {
            long newId = ++_id;
            return String.format("%s%d", (prefix != null ? prefix : ""), newId);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set default app style
        AppStyle.setAppStyle(new StyleParamsParser(null).parse());
        activityParams = NavigationCommandsHandler.parseActivityParams(getIntent());
        NavigationCommandsHandler.registerNavigationActivity(this, activityParams.screenParams.navigationParams.navigatorId);
        NavigationCommandsHandler.registerActivity(this, activityParams.screenParams.navigationParams.screenInstanceId);

        setStatusBarColor(activityParams.screenParams.styleParams.statusBarColor);
        setNavigationBarColor(activityParams.screenParams.styleParams.navigationBarColor);
        createLayout();

        String mainComponentName = activityParams.screenParams.screenId;
        if (mainComponentName != null) {
            screen.contentView.startReactApplication(mReactInstanceManager, mainComponentName, getLaunchOptions());
        }
    }

    protected @Nullable Bundle getLaunchOptions() {
        Bundle bundle = activityParams.screenParams.navigationParams.toBundle();
        if (activityParams.screenParams.passProps != null) {
            bundle.putAll(activityParams.screenParams.passProps);
        }
        return bundle;
    }

    private void createLayout() {
        screen = new Screen(this, activityParams.screenParams, this);
        screen.setStyle();
        screen.setVisibility(View.VISIBLE);
        if (hasBackgroundColor()) {
            screen.setBackgroundColor(AppStyle.appStyle.screenBackgroundColor.getColor());
        }
        setContentView(screen);
        titleBar = screen.titleBar;
        mReactRootView = screen.contentView;
    }

    private boolean hasBackgroundColor() {
        return AppStyle.appStyle.screenBackgroundColor != null &&
               AppStyle.appStyle.screenBackgroundColor.hasColor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity = this;
        NavigationCommandsHandler.navigationApplication.getEventEmitter().sendNavigatorScreenEvent(SCREEN_STATE_ACTIVE, activityParams.screenParams.getNavigatorEventId());
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentActivity = null;
        NavigationCommandsHandler.navigationApplication.getEventEmitter().sendNavigatorScreenEvent(SCREEN_STATE_BACKGROUND, activityParams.screenParams.getNavigatorEventId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NavigationCommandsHandler.unregisterNavigationActivity(this, activityParams.screenParams.navigationParams.navigatorId);
        NavigationCommandsHandler.unregisterActivity(activityParams.screenParams.navigationParams.screenInstanceId);
    }

    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            mReactInstanceManager.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (getReactNativeHost().hasInstance()) {
            getReactNativeHost().getReactInstanceManager().onNewIntent(intent);
            return;
        }
        super.onNewIntent(intent);
    }

    @Override
    public boolean onTitleBarBackButtonClick() {
        onBackPressed();
        return true;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setStatusBarColor(StyleParams.Color statusBarColor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;
        final Window window = this.getWindow();
        if (statusBarColor.hasColor()) {
            window.setStatusBarColor(statusBarColor.getColor());
        } else {
            window.setStatusBarColor(Color.BLACK);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setNavigationBarColor(StyleParams.Color navigationBarColor) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return;

        final Window window = this.getWindow();
        if (navigationBarColor.hasColor()) {
            window.setNavigationBarColor(navigationBarColor.getColor());
        } else {
            window.setNavigationBarColor(Color.BLACK);
        }
    }

    //TODO all these setters should be combined to something like setStyle
    public void setTopBarVisible(String screenId, boolean visible, boolean animate) {
        titleBar.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    public void setTitleBarTitle(String screenId, String title) {
        titleBar.setTitle(title);
    }

    public void setTitleBarSubtitle(String screenId, String subtitle) {
        titleBar.setSubtitle(subtitle);
    }

    public void setTitleBarRightButtons(String screenId, String navigatorEventId, List<TitleBarButtonParams> titleBarButtons) {
        screen.setButtonColorFromScreen(titleBarButtons);
        titleBar.setRightButtons(titleBarButtons, navigatorEventId);
    }

    void setTitleBarButtons(String screenInstanceId, final String navigatorEventId, final List<TitleBarButtonParams> titleBarButtons) {
        this.setTitleBarRightButtons(screenInstanceId, navigatorEventId, titleBarButtons);
    }

    public void setTitleBarLeftButton(String screenId, String navigatorEventId,
                                      TitleBarLeftButtonParams titleBarLeftButtonParams) {
        titleBar.setLeftButton(titleBarLeftButtonParams,this, navigatorEventId);
    }

    public void enableRightButton(String screenId, boolean enabled) {
        titleBar.enableRightButton(enabled);
    }
}

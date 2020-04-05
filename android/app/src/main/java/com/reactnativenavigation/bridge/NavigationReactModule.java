package com.reactnativenavigation.bridge;

import android.os.Bundle;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.reactnativenavigation.controllers.NavigationCommandsHandler;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.params.parsers.TitleBarButtonParamsParser;
import com.reactnativenavigation.params.parsers.TitleBarLeftButtonParamsParser;

import java.util.List;

/**
 * The basic abstract components we will expose:
 * BottomTabs (app) - boolean
 * TopBar (per screen)
 * - TitleBar
 * - - RightButtons
 * - - LeftButton
 * - TopTabs (segmented control / view pager tabs)
 * DeviceStatusBar (app) (colors are per screen)
 * AndroidNavigationBar (app) (colors are per screen)
 * SideMenu (app) - boolean, (menu icon is screen-based)
 */
public class NavigationReactModule extends ReactContextBaseJavaModule {
    public static final String NAME = "NavigationReactModule";

    public NavigationReactModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return NAME;
    }


    @ReactMethod
    public void registerNavigatorButtons(String screenId, ReadableArray rightButtonsParams, ReadableMap leftButtonParams) {
        Bundle rightButtons = null;
        if (rightButtonsParams != null) {
            rightButtons = BundleConverter.toBundle(rightButtonsParams);
        }
        Bundle leftButton = null;
        if (leftButtonParams != null) {
            leftButton = BundleConverter.toBundle(leftButtonParams);
        }
        NavigationCommandsHandler.registerNavigatorButtons(screenId, rightButtons, leftButton);
    }

    @ReactMethod
    public void setScreenTitleBarTitle(String screenInstanceId, String title) {
        NavigationCommandsHandler.setScreenTitleBarTitle(screenInstanceId, title);
    }

    @ReactMethod
    public void setScreenTitleBarSubtitle(String screenInstanceId, String subtitle) {
        NavigationCommandsHandler.setScreenTitleBarSubtitle(screenInstanceId, subtitle);
    }

    @ReactMethod
    public void setScreenButtons(String screenInstanceId, String navigatorEventId,
                                 ReadableArray rightButtonsParams, ReadableMap leftButtonParams, ReadableMap fab) {
        if (rightButtonsParams != null) {
            setScreenTitleBarRightButtons(screenInstanceId, navigatorEventId, rightButtonsParams);
        }
        if (leftButtonParams != null) {
            setScreenTitleBarLeftButton(screenInstanceId, navigatorEventId, leftButtonParams);
        }
    }

    @ReactMethod
    public void enableRightButton(String screenInstanceId, boolean enabled) {
        NavigationCommandsHandler.enableRightButton(screenInstanceId, enabled);
    }

    private void setScreenTitleBarRightButtons(String screenInstanceId, String navigatorEventId, ReadableArray rightButtonsParams) {
        List<TitleBarButtonParams> rightButtons = new TitleBarButtonParamsParser()
                .parseButtons(BundleConverter.toBundle(rightButtonsParams));
        NavigationCommandsHandler.setScreenTitleBarRightButtons(screenInstanceId, navigatorEventId, rightButtons);
    }

    private void setScreenTitleBarLeftButton(String screenInstanceId, String navigatorEventId, ReadableMap leftButtonParams) {
        TitleBarLeftButtonParams leftButton = new TitleBarLeftButtonParamsParser()
                .parseSingleButton(BundleConverter.toBundle(leftButtonParams));
        NavigationCommandsHandler.setScreenTitleBarLeftButtons(screenInstanceId, navigatorEventId, leftButton);
    }


    @ReactMethod
    public void toggleTopBarVisible(final ReadableMap params) {
    }

    @ReactMethod
    public void setTopBarVisible(String screenInstanceId, boolean hidden, boolean animated) {
        NavigationCommandsHandler.setTopBarVisible(screenInstanceId, hidden, animated);
    }

    @ReactMethod
    public void setResult(String screenInstanceId, ReadableMap res) {
        Bundle bundle = BundleConverter.toBundle(res);
        NavigationCommandsHandler.setScreenResult(screenInstanceId, bundle);
    }


    @ReactMethod
    public void push(final ReadableMap params) {
        boolean portraitOnlyMode = false;
        boolean landscapeOnlyMode = false;
        String navigatorID = "";
        String screen = "";
        if (params.hasKey("portraitOnlyMode")) {
            portraitOnlyMode = params.getBoolean("portraitOnlyMode");
        }

        if (params.hasKey(("landscapeOnlyMode"))) {
            landscapeOnlyMode = params.getBoolean("landscapeOnlyMode");
        }

        if (params.hasKey("navigatorID")) {
            navigatorID = params.getString("navigatorID");
        }
        if (params.hasKey("component")) {
            screen = params.getString("component");
        }
        Bundle bundle = BundleConverter.toBundle(params);
        if (params.hasKey("passProps")) {
            ReadableMap passProps = params.getMap("passProps");
            Bundle props = BundleConverter.passPropsToBundle(passProps);
            bundle.putBundle("passProps", props);
        }
        NavigationCommandsHandler.push(bundle, portraitOnlyMode, landscapeOnlyMode, navigatorID, screen);
    }

    @ReactMethod
    public void pop(final ReadableMap params) {
        NavigationCommandsHandler.pop(BundleConverter.toBundle(params));
    }

    @ReactMethod
    public void popToRoot(final ReadableMap params) {
        NavigationCommandsHandler.popToRoot(BundleConverter.toBundle(params));
    }

    @ReactMethod
    public void showModal(final ReadableMap params) {
        boolean portraitOnlyMode = false;
        boolean landscapeOnlyMode = false;
        String screen = "";
        if (params.hasKey("portraitOnlyMode")) {
            portraitOnlyMode = params.getBoolean("portraitOnlyMode");
        }

        if (params.hasKey(("landscapeOnlyMode"))) {
            landscapeOnlyMode = params.getBoolean("landscapeOnlyMode");
        }
        if (params.hasKey("component")) {
            screen = params.getString("component");
        }
        Bundle bundle = BundleConverter.toBundle(params);
        if (params.hasKey("passProps")) {
            ReadableMap passProps = params.getMap("passProps");
            Bundle props = BundleConverter.passPropsToBundle(passProps);
            bundle.putBundle("passProps", props);
        }
        NavigationCommandsHandler.showModal(bundle, portraitOnlyMode, landscapeOnlyMode, screen);
    }

    @ReactMethod
    public void dismissTopModal() {
        NavigationCommandsHandler.dismissTopModal();
    }


}

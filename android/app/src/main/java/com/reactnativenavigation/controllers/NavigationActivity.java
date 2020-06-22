package com.reactnativenavigation.controllers;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactDelegate;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactRootView;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.reactnativenavigation.params.ScreenParams;
import com.reactnativenavigation.params.StyleParams;
import com.reactnativenavigation.params.parsers.StyleParamsParser;
import com.reactnativenavigation.react.NavigationApplication;
import com.reactnativenavigation.params.ActivityParams;
import com.reactnativenavigation.params.AppStyle;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.screens.Screen;
import com.reactnativenavigation.views.LeftButtonOnClickListener;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class NavigationActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler,
        PermissionAwareActivity, LeftButtonOnClickListener {


    public static long _id = 0;

    public static String uniqueId(String prefix) {
        synchronized (NavigationActivity.class) {
            long newId = ++_id;
            return String.format("%s%d", (prefix != null ? prefix : ""), newId);
        }
    }
    /**
     * Although we start multiple activities, we make sure to pass Intent.CLEAR_TASK | Intent.NEW_TASK
     * So that we actually have only 1 instance of the activity running at one time.
     * We hold the currentActivity (resume->pause) so we know when we need to destroy the javascript context
     * (when currentActivity is null, ie pause and destroy was called without resume).
     * This is somewhat weird, and in the future we better use a single activity with changing contentView similar to ReactNative impl.
     * Along with that, we should handle commands from the bridge using onNewIntent
     */
    static NavigationActivity currentActivity;

    private ActivityParams activityParams;
    private RelativeLayout layout;
    private Screen screen;
    @Nullable protected PermissionListener mPermissionListener;

    private ReactDelegate mReactDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!NavigationApplication.instance.isReactContextInitialized()) {
            NavigationApplication.instance.startReactContextOnceInBackgroundAndExecuteJS();
            return;
        }

        //set default app style
        AppStyle.setAppStyle(new StyleParamsParser(null).parse());
        activityParams = NavigationCommandsHandler.parseActivityParams(getIntent());
        NavigationCommandsHandler.registerNavigationActivity(this, activityParams.screenParams.navigationParams.navigatorId);
        NavigationCommandsHandler.registerActivity(this, activityParams.screenParams.navigationParams.screenInstanceId);

        setStatusBarColor(activityParams.screenParams.styleParams.statusBarColor);
        setNavigationBarColor(activityParams.screenParams.styleParams.navigationBarColor);


        createLayout();

        String mainComponentName = activityParams.screenParams.screenId;
        mReactDelegate =
                new ReactDelegate(this, getReactNativeHost(), mainComponentName, getLaunchOptions()) {
                    @Override
                    protected ReactRootView createRootView() {
                        return screen.contentView;
                    }
                };

        if (mainComponentName != null) {
            mReactDelegate.loadApp(mainComponentName);
        }
    }

    protected @Nullable Bundle getLaunchOptions() {
        Bundle bundle = activityParams.screenParams.navigationParams.toBundle();
        if (activityParams.screenParams.passProps != null) {
            bundle.putAll(activityParams.screenParams.passProps);
        }
        return bundle;
    }


    protected ReactNativeHost getReactNativeHost() {
        return ((ReactApplication) getApplication()).getReactNativeHost();
    }


    private void createLayout() {
        layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);

        screen = new Screen(this, activityParams.screenParams, this);
        screen.setVisibility(View.INVISIBLE);
        layout.addView(screen, layout.getChildCount() - 1, lp);
        screen.setStyle();
        screen.setVisibility(View.VISIBLE);

        if (hasBackgroundColor()) {
            layout.setBackgroundColor(AppStyle.appStyle.screenBackgroundColor.getColor());
        }
        setContentView(layout);
    }


    private boolean hasBackgroundColor() {
        return AppStyle.appStyle.screenBackgroundColor != null &&
               AppStyle.appStyle.screenBackgroundColor.hasColor();
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentActivity = this;
        mReactDelegate.onHostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentActivity = null;
        mReactDelegate.onHostPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NavigationCommandsHandler.unregisterNavigationActivity(this, activityParams.screenParams.navigationParams.navigatorId);
        NavigationCommandsHandler.unregisterActivity(activityParams.screenParams.navigationParams.screenInstanceId);
        mReactDelegate.onHostDestroy();
    }


    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (mReactDelegate.onBackPressed()) {
            return;
        }
        super.onBackPressed();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mReactDelegate.onActivityResult(requestCode, resultCode, data, true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (getReactNativeHost().hasInstance()
                && getReactNativeHost().getUseDeveloperSupport()
                && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            event.startTracking();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mReactDelegate.shouldShowDevMenuOrReload(keyCode, event) || super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (getReactNativeHost().hasInstance()
                && getReactNativeHost().getUseDeveloperSupport()
                && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD) {
            getReactNativeHost().getReactInstanceManager().showDevOptionsDialog();
            return true;
        }

        return super.onKeyLongPress(keyCode, event);
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
        screen.topBarVisibilityAnimator.setVisible(visible, animate);
    }

    public void setTitleBarTitle(String screenId, String title) {
        screen.topBar.setTitle(title);
    }

    public void setTitleBarSubtitle(String screenId, String subtitle) {
        screen.topBar.setSubtitle(subtitle);
    }

    public void setTitleBarRightButtons(String screenId, String navigatorEventId, List<TitleBarButtonParams> titleBarButtons) {
        screen.setButtonColorFromScreen(titleBarButtons);
        screen.topBar.setTitleBarRightButtons(navigatorEventId, titleBarButtons);
    }
    void setTitleBarButtons(String screenInstanceId, final String navigatorEventId, final List<TitleBarButtonParams> titleBarButtons) {
        this.setTitleBarRightButtons(screenInstanceId, navigatorEventId, titleBarButtons);
    }

    public void setTitleBarLeftButton(String screenId, String navigatorEventId,
                                      TitleBarLeftButtonParams titleBarLeftButtonParams) {
        screen.topBar.setTitleBarLeftButton(navigatorEventId,
                this,
                titleBarLeftButtonParams,
                activityParams.screenParams.overrideBackPressInJs);
    }

    public void enableRightButton(String screenId, boolean enabled) {
        screen.topBar.enableRightButton(enabled);
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        mPermissionListener = listener;
        requestPermissions(permissions, requestCode);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mPermissionListener != null && mPermissionListener.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            mPermissionListener = null;
        }
    }

}

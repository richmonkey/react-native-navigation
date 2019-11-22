package com.reactnativenavigation.controllers;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.reactnativenavigation.params.parsers.StyleParamsParser;
import com.reactnativenavigation.react.NavigationApplication;
import com.reactnativenavigation.layouts.Layout;
import com.reactnativenavigation.layouts.LayoutFactory;
import com.reactnativenavigation.params.ActivityParams;
import com.reactnativenavigation.params.AppStyle;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.react.JsDevReloadHandler;
import java.util.List;

public class NavigationActivity extends AppCompatActivity implements DefaultHardwareBackBtnHandler,
        PermissionAwareActivity {

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
    private Layout layout;
    @Nullable protected PermissionListener mPermissionListener;

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

        createLayout();
    }


    private void createLayout() {
        layout = LayoutFactory.create(this, activityParams);
        if (hasBackgroundColor()) {
            layout.asView().setBackgroundColor(AppStyle.appStyle.screenBackgroundColor.getColor());
        }
        setContentView(layout.asView());
    }

    private boolean hasBackgroundColor() {
        return AppStyle.appStyle.screenBackgroundColor != null &&
               AppStyle.appStyle.screenBackgroundColor.hasColor();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isFinishing() || !NavigationApplication.instance.isReactContextInitialized()) {
            return;
        }

        currentActivity = this;
        NavigationApplication.instance.getReactGateway().onResumeActivity(this, this);
    }


    @Override
    protected void onPause() {
        super.onPause();
        currentActivity = null;
        NavigationApplication.instance.getReactGateway().onPauseActivity();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        NavigationCommandsHandler.unregisterNavigationActivity(this, activityParams.screenParams.navigationParams.navigatorId);
        NavigationCommandsHandler.unregisterActivity(activityParams.screenParams.navigationParams.screenInstanceId);

        destroyLayouts();
        super.onDestroy();
    }

    private void destroyLayouts() {
        if (layout != null) {
            layout.destroy();
            layout = null;
        }
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        if (layout != null && !layout.onBackPressed()) {
            NavigationApplication.instance.getReactGateway().onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        NavigationApplication.instance.getReactGateway().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return JsDevReloadHandler.onKeyUp(getCurrentFocus(), keyCode) || super.onKeyUp(keyCode, event);
    }


    //TODO all these setters should be combined to something like setStyle
    void setTopBarVisible(String screenInstanceId, boolean hidden, boolean animated) {
        layout.setTopBarVisible(screenInstanceId, hidden, animated);
    }


    void setTitleBarTitle(String screenInstanceId, String title) {
        layout.setTitleBarTitle(screenInstanceId, title);
    }

    public void setTitleBarSubtitle(String screenInstanceId, String subtitle) {
        layout.setTitleBarSubtitle(screenInstanceId, subtitle);
    }

    void setTitleBarButtons(String screenInstanceId, String navigatorEventId, List<TitleBarButtonParams> titleBarButtons) {
        layout.setTitleBarRightButtons(screenInstanceId, navigatorEventId, titleBarButtons);
    }

    void setTitleBarLeftButton(String screenInstanceId, String navigatorEventId, TitleBarLeftButtonParams titleBarLeftButton) {
        layout.setTitleBarLeftButton(screenInstanceId, navigatorEventId, titleBarLeftButton);
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

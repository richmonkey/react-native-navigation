package com.reactnativenavigation.controllers;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.modules.core.PermissionAwareActivity;
import com.facebook.react.modules.core.PermissionListener;
import com.reactnativenavigation.params.ScreenParams;
import com.reactnativenavigation.params.parsers.StyleParamsParser;
import com.reactnativenavigation.react.NavigationApplication;
import com.reactnativenavigation.params.ActivityParams;
import com.reactnativenavigation.params.AppStyle;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.react.JsDevReloadHandler;
import com.reactnativenavigation.screens.Screen;
import com.reactnativenavigation.screens.ScreenFactory;
import com.reactnativenavigation.utils.Task;
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
        layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);

        Screen initialScreen = ScreenFactory.create(this, activityParams.screenParams, this);
        initialScreen.setVisibility(View.INVISIBLE);
        layout.addView(initialScreen, layout.getChildCount() - 1, lp);
        screen = initialScreen;
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
            screen.destroy();
            layout.removeView(screen);
            layout = null;
        }
    }

    @Override
    public void invokeDefaultOnBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        ScreenParams currentScreen = screen.screenParams;
        if (currentScreen.overrideBackPressInJs) {
            NavigationApplication.instance.getEventEmitter().sendNavigatorEvent("backPress", currentScreen.getNavigatorEventId());
            return;
        }
        this.finish();
        return;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        NavigationApplication.instance.getReactGateway().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return JsDevReloadHandler.onKeyUp(getCurrentFocus(), keyCode) || super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onTitleBarBackButtonClick() {
        onBackPressed();
        return true;
    }


    //TODO all these setters should be combined to something like setStyle
    void setTopBarVisible(String screenInstanceId, final boolean visible, final boolean animated) {
        performOnScreen(screenInstanceId, new Task<Screen>() {
            @Override
            public void run(Screen param) {
                param.setTopBarVisible(visible, animated);
            }
        });
    }


    void setTitleBarTitle(String screenInstanceId, final String title) {
        performOnScreen(screenInstanceId, new Task<Screen>() {
            @Override
            public void run(Screen param) {
                param.setTitleBarTitle(title);
            }
        });
    }

    public void setTitleBarSubtitle(String screenInstanceId, final String subtitle) {
        performOnScreen(screenInstanceId, new Task<Screen>() {
            @Override
            public void run(Screen param) {
                param.setTitleBarSubtitle(subtitle);
            }
        });
    }

    void setTitleBarButtons(String screenInstanceId, final String navigatorEventId, final List<TitleBarButtonParams> titleBarButtons) {
        performOnScreen(screenInstanceId, new Task<Screen>() {
            @Override
            public void run(Screen param) {
                param.setTitleBarRightButtons(navigatorEventId, titleBarButtons);
            }
        });
    }

    void setTitleBarLeftButton(String screenInstanceId, final String navigatorEventId, final TitleBarLeftButtonParams titleBarLeftButton) {
        performOnScreen(screenInstanceId, new Task<Screen>() {
            @Override
            public void run(Screen param) {
                param.setTitleBarLeftButton(navigatorEventId, NavigationActivity.this, titleBarLeftButton);
            }
        });
    }

    void enableRightButton(String screenInstanceId, final boolean enabled) {
        performOnScreen(screenInstanceId, new Task<Screen>() {
            @Override
            public void run(Screen param) {
                param.enableRightButton(enabled);
            }
        });
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


    private void performOnScreen(String screenInstanceId, Task<Screen> task) {
        if (screen.getScreenInstanceId().equals(screenInstanceId)) {
            task.run(screen);
            return;
        }
    }


}

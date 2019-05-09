package com.reactnativenavigation.layouts;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import com.facebook.react.bridge.Callback;
import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.events.EventBus;
import com.reactnativenavigation.events.ScreenChangedEvent;
import com.reactnativenavigation.params.ContextualMenuParams;
import com.reactnativenavigation.params.ScreenParams;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.screens.Screen;
import com.reactnativenavigation.screens.ScreenFactory;
import com.reactnativenavigation.utils.Task;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class SingleScreenLayout extends RelativeLayout implements Layout {

    private final AppCompatActivity activity;
    protected final ScreenParams screenParams;

    Screen screen;

    public SingleScreenLayout(AppCompatActivity activity, ScreenParams screenParams) {
        super(activity);
        this.activity = activity;
        this.screenParams = screenParams;
        createLayout();
    }

    private void createLayout() {
        createStack();
        sendScreenChangedEventAfterInitialPush();
    }

    private RelativeLayout getScreenStackParent() {
        return this;
    }

    private void createStack() {
        LayoutParams lp = new LayoutParams(MATCH_PARENT, MATCH_PARENT);

        Screen initialScreen = ScreenFactory.create(activity, screenParams, this);
        initialScreen.setVisibility(View.INVISIBLE);

        addScreenBeforeSnackbarAndFabLayout(initialScreen, lp);
        screen = initialScreen;
        show();
    }

    private void addScreenBeforeSnackbarAndFabLayout(Screen screen, LayoutParams layoutParams) {
        RelativeLayout parent = getScreenStackParent();
        parent.addView(screen, parent.getChildCount() - 1, layoutParams);
    }


    private void sendScreenChangedEventAfterInitialPush() {
        if (screenParams.topTabParams != null) {
            EventBus.instance.post(new ScreenChangedEvent(screenParams.topTabParams.get(0)));
        } else {
            EventBus.instance.post(new ScreenChangedEvent(screenParams));
        }
    }


    @Override
    public boolean onBackPressed() {
        ScreenParams currentScreen = screen.screenParams;
        if (currentScreen.overrideBackPressInJs) {
            NavigationApplication.instance.getEventEmitter().sendNavigatorEvent("backPress", currentScreen.getNavigatorEventId());
            return true;
        }
        this.activity.finish();
        return true;
    }

    @Override
    public void destroy() {
        screen.destroy();
        RelativeLayout parent = getScreenStackParent();
        parent.removeView(screen);
    }


    @Override
    public void setTopBarVisible(String screenInstanceID, final boolean visible, final boolean animate) {
        performOnScreen(screenInstanceID, new Task<Screen>() {
            @Override
            public void run(Screen param) {
                param.setTopBarVisible(visible, animate);
            }
        });
    }

    @Override
    public void setTitleBarTitle(String screenInstanceId, final String title) {
        performOnScreen(screenInstanceId, new Task<Screen>() {
            @Override
            public void run(Screen param) {
                param.setTitleBarTitle(title);
            }
        });
    }

    @Override
    public void setTitleBarSubtitle(String screenInstanceId, final String subtitle) {
        performOnScreen(screenInstanceId, new Task<Screen>() {
            @Override
            public void run(Screen param) {
                param.setTitleBarSubtitle(subtitle);
            }
        });
    }

    @Override
    public View asView() {
        return this;
    }

    @Override
    public void setTitleBarRightButtons(String screenInstanceId, final String navigatorEventId,
                                        final List<TitleBarButtonParams> titleBarRightButtons) {
        performOnScreen(screenInstanceId, new Task<Screen>() {
            @Override
            public void run(Screen param) {
                param.setTitleBarRightButtons(navigatorEventId, titleBarRightButtons);
            }
        });
    }

    @Override
    public void setTitleBarLeftButton(String screenInstanceId, final String navigatorEventId, final TitleBarLeftButtonParams titleBarLeftButtonParams) {
        performOnScreen(screenInstanceId, new Task<Screen>() {
            @Override
            public void run(Screen param) {
                param.setTitleBarLeftButton(navigatorEventId, SingleScreenLayout.this, titleBarLeftButtonParams);
            }
        });
    }


    @Override
    public void showContextualMenu(String screenInstanceId, final ContextualMenuParams params, final Callback onButtonClicked) {
        performOnScreen(screenInstanceId, new Task<Screen>() {
            @Override
            public void run(Screen screen) {
                screen.showContextualMenu(params, onButtonClicked);
            }
        });
    }

    @Override
    public void dismissContextualMenu(String screenInstanceId) {
        performOnScreen(screenInstanceId, new Task<Screen>() {
            @Override
            public void run(Screen screen) {
                screen.dismissContextualMenu();
            }
        });
    }

    @Override
    public Screen getCurrentScreen() {
        return screen;
    }

    @Override
    public boolean onTitleBarBackButtonClick() {
        return onBackPressed();
    }


    private void performOnScreen(String screenInstanceId, Task<Screen> task) {
        if (screen.getScreenInstanceId().equals(screenInstanceId)) {
            task.run(screen);
            return;
        }
    }

    public void show() {
        screen.setStyle();
        screen.setVisibility(View.VISIBLE);
    }

    public void hide() {
        screen.setVisibility(View.INVISIBLE);
    }

}

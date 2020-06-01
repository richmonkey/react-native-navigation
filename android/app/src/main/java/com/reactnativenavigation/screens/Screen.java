package com.reactnativenavigation.screens;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.RelativeLayout;
import com.reactnativenavigation.animation.VisibilityAnimator;
import com.reactnativenavigation.params.ScreenParams;
import com.reactnativenavigation.params.StyleParams;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.utils.ViewUtils;
import com.reactnativenavigation.views.ContentView;
import com.reactnativenavigation.views.LeftButtonOnClickListener;
import com.reactnativenavigation.views.TopBar;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class Screen extends RelativeLayout {

    public interface OnDisplayListener {
        void onDisplay();
    }

    public final ScreenParams screenParams;
    public TopBar topBar;
    public final LeftButtonOnClickListener leftButtonOnClickListener;
    public VisibilityAnimator topBarVisibilityAnimator;
    public final StyleParams styleParams;
    public ContentView contentView;

    public Screen(AppCompatActivity activity, ScreenParams screenParams, LeftButtonOnClickListener leftButtonOnClickListener) {
        super(activity);
        this.screenParams = screenParams;
        styleParams = screenParams.styleParams;
        this.leftButtonOnClickListener = leftButtonOnClickListener;
        createViews();
    }

    public void setStyle() {
        topBar.setStyle(styleParams);
        if (styleParams.screenBackgroundColor.hasColor()) {
            setBackgroundColor(styleParams.screenBackgroundColor.getColor());
        }
    }

    private void createViews() {
        createAndAddTopBar();
        createTitleBar();
        createContent();
    }

    private void createTitleBar() {
        addTitleBarButtons();
        topBar.setTitle(screenParams.title);
        topBar.setSubtitle(screenParams.subtitle);
    }

    private void addTitleBarButtons() {
        setButtonColorFromScreen(screenParams.rightButtons);
        if (screenParams.leftButton != null) {
            screenParams.leftButton.setColorFromScreenStyle(screenParams.styleParams.titleBarButtonColor);
        }
        topBar.addTitleBarAndSetButtons(screenParams.rightButtons,
                screenParams.leftButton,
                leftButtonOnClickListener,
                screenParams.getNavigatorEventId(),
                screenParams.overrideBackPressInJs);
    }

    private void createAndAddTopBar() {
        createTopBar();
        addTopBar();
    }

    protected void createTopBar() {
        topBar = new TopBar(getContext());
    }

    private void addTopBar() {
        createTopBarVisibilityAnimator();
        addView(topBar, new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
    }

    private void createTopBarVisibilityAnimator() {
        ViewUtils.runOnPreDraw(topBar, new Runnable() {
            @Override
            public void run() {
                if (topBarVisibilityAnimator == null) {
                    topBarVisibilityAnimator = new VisibilityAnimator(topBar,
                            VisibilityAnimator.HideDirection.Up,
                            topBar.getHeight());
                }
            }
        });
    }


    public void setButtonColorFromScreen(List<TitleBarButtonParams> titleBarButtonParams) {
        if (titleBarButtonParams == null) {
            return;
        }

        for (TitleBarButtonParams titleBarButtonParam : titleBarButtonParams) {
            titleBarButtonParam.setColorFromScreenStyle(screenParams.styleParams.titleBarButtonColor);
        }
    }

    protected void createContent() {
        contentView = new ContentView(getContext(), screenParams.screenId,
                screenParams.navigationParams, screenParams.passProps);
        addView(contentView, 0, createLayoutParams());
    }

    protected LayoutParams createLayoutParams() {
        LayoutParams params = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        if (screenParams.styleParams.drawScreenBelowTopBar) {
            params.addRule(BELOW, topBar.getId());
        }
        return params;
    }
}

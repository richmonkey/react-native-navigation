package com.reactnativenavigation.screens;

import androidx.appcompat.app.AppCompatActivity;
import android.widget.RelativeLayout;
import com.facebook.react.ReactRootView;
import com.reactnativenavigation.params.ScreenParams;
import com.reactnativenavigation.params.StyleParams;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.utils.ViewUtils;
import com.reactnativenavigation.views.LeftButtonOnClickListener;
import com.reactnativenavigation.views.TitleBar;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class Screen extends RelativeLayout {
    public final ScreenParams screenParams;
    public TitleBar titleBar;

    public final LeftButtonOnClickListener leftButtonOnClickListener;
    public final StyleParams styleParams;
    public ReactRootView contentView;

    public Screen(AppCompatActivity activity, ScreenParams screenParams, LeftButtonOnClickListener leftButtonOnClickListener) {
        super(activity);
        this.screenParams = screenParams;
        styleParams = screenParams.styleParams;
        this.leftButtonOnClickListener = leftButtonOnClickListener;
        createViews();
    }

    public void setStyle() {
        titleBar.setStyle(styleParams);
        if (styleParams.screenBackgroundColor.hasColor()) {
            setBackgroundColor(styleParams.screenBackgroundColor.getColor());
        }
    }

    private void createViews() {
        createTitleBar();
        createContent();
    }

    private void createTitleBar() {
        titleBar = new TitleBar(getContext());
        titleBar.setId(ViewUtils.generateViewId());
        titleBar.setBackgroundColor(styleParams.topBarColor.getColor());

        addView(titleBar, new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        titleBar.setRightButtons(screenParams.rightButtons, screenParams.getNavigatorEventId());
        titleBar.setLeftButton(screenParams.leftButton, leftButtonOnClickListener, screenParams.getNavigatorEventId());
        titleBar.setTitle(screenParams.title);
        titleBar.setSubtitle(screenParams.subtitle);
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
        contentView = new ReactRootView(getContext());
        addView(contentView, 0, createLayoutParams());
    }

    protected LayoutParams createLayoutParams() {
        LayoutParams params = new LayoutParams(MATCH_PARENT, MATCH_PARENT);
        if (screenParams.styleParams.drawScreenBelowTopBar) {
            params.addRule(BELOW, titleBar.getId());
        }
        return params;
    }
}

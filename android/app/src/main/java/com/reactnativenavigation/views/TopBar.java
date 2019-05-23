package com.reactnativenavigation.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.AppBarLayout;

import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.reactnativenavigation.params.StyleParams;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.utils.ViewUtils;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class TopBar extends AppBarLayout {
    protected TitleBar titleBar;
    protected FrameLayout titleBarAndContextualMenuContainer;


    public TopBar(Context context) {
        super(context);
        setId(ViewUtils.generateViewId());
        createLayout();
    }

    protected void createLayout() {
        titleBarAndContextualMenuContainer = new FrameLayout(getContext());
        addView(titleBarAndContextualMenuContainer);
    }

    public void addTitleBarAndSetButtons(List<TitleBarButtonParams> rightButtons,
                                         TitleBarLeftButtonParams leftButton,
                                         LeftButtonOnClickListener leftButtonOnClickListener,
                                         String navigatorEventId, boolean overrideBackPressInJs) {
        titleBar = createTitleBar();
        titleBarAndContextualMenuContainer.addView(titleBar, new ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        addButtons(rightButtons, leftButton, leftButtonOnClickListener, navigatorEventId, overrideBackPressInJs);
    }

    protected TitleBar createTitleBar() {
        return new TitleBar(getContext());
    }

    private void addButtons(List<TitleBarButtonParams> rightButtons, TitleBarLeftButtonParams leftButton, LeftButtonOnClickListener leftButtonOnClickListener, String navigatorEventId, boolean overrideBackPressInJs) {
        titleBar.setRightButtons(rightButtons, navigatorEventId);
        titleBar.setLeftButton(leftButton, leftButtonOnClickListener, navigatorEventId, overrideBackPressInJs);
    }

    public void setTitle(String title) {
        titleBar.setTitle(title);
    }

    public void setSubtitle(String subtitle) {
        titleBar.setSubtitle(subtitle);
    }

    public void setStyle(StyleParams styleParams) {
        if (styleParams.topBarColor.hasColor()) {
            setBackgroundColor(styleParams.topBarColor.getColor());
        }
        if (styleParams.topBarTransparent) {
            setTransparent();
        }
        setVisibility(styleParams.topBarHidden ? GONE : VISIBLE);
        titleBar.setStyle(styleParams);

        if (!styleParams.topBarElevationShadowEnabled) {
            disableElevationShadow();
        }
    }

    private void setTransparent() {
        setBackgroundColor(Color.TRANSPARENT);
        disableElevationShadow();
    }

    private void disableElevationShadow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setOutlineProvider(null);
        }
    }

    public void setTitleBarRightButtons(String navigatorEventId, List<TitleBarButtonParams> titleBarButtons) {
        titleBar.setRightButtons(titleBarButtons, navigatorEventId);
    }

    public void setTitleBarLeftButton(String navigatorEventId,
                                      LeftButtonOnClickListener leftButtonOnClickListener,
                                      TitleBarLeftButtonParams titleBarLeftButtonParams,
                                      boolean overrideBackPressInJs) {
        titleBar.setLeftButton(titleBarLeftButtonParams, leftButtonOnClickListener, navigatorEventId,
                overrideBackPressInJs);
    }
}

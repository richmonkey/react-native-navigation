package com.reactnativenavigation.views;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.reactnativenavigation.NavigationApplication;
import com.facebook.react.ReactRootView;
import com.reactnativenavigation.params.NavigationParams;
import com.reactnativenavigation.screens.Screen;
import com.reactnativenavigation.utils.ViewUtils;
import com.reactnativenavigation.views.utils.ViewMeasurer;

public class ContentView extends ReactRootView {
    private final String screenId;
    private final NavigationParams navigationParams;
    private final Bundle passProps;

    boolean isContentVisible = false;
    private Screen.OnDisplayListener onDisplayListener;
    protected ViewMeasurer viewMeasurer;

    public void setOnDisplayListener(Screen.OnDisplayListener onDisplayListener) {
        this.onDisplayListener = onDisplayListener;
    }

    public ContentView(Context context, String screenId, NavigationParams navigationParams) {
        super(context);
        this.screenId = screenId;
        this.navigationParams = navigationParams;
        this.passProps = null;
        attachToJS();
        viewMeasurer = new ViewMeasurer();
    }

    public ContentView(Context context, String screenId, NavigationParams navigationParams,
                       Bundle passProps) {
        super(context);
        this.screenId = screenId;
        this.navigationParams = navigationParams;
        this.passProps = passProps;
        attachToJS();
        viewMeasurer = new ViewMeasurer();
    }

    public void setViewMeasurer(ViewMeasurer viewMeasurer) {
        this.viewMeasurer = viewMeasurer;
    }

    private void attachToJS() {
        Bundle bundle = navigationParams.toBundle();
        if (passProps != null) {
            bundle.putAll(passProps);
        }
        startReactApplication(NavigationApplication.instance.getReactInstanceManager(),
                screenId, bundle);
    }

    public String getNavigatorEventId() {
        return navigationParams.navigatorEventId;
    }

    public void unmountReactView() {
        unmountReactApplication();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredHeight = viewMeasurer.getMeasuredHeight(heightMeasureSpec);
        setMeasuredDimension(viewMeasurer.getMeasuredWidth(widthMeasureSpec),
                measuredHeight);
    }

    @Override
    public void onViewAdded(final View child) {
        super.onViewAdded(child);
        detectContentViewVisible(child);
    }

    private void detectContentViewVisible(View child) {
        if (onDisplayListener != null) {
            ViewUtils.runOnPreDraw(child, new Runnable() {
                @Override
                public void run() {
                    if (!isContentVisible) {
                        isContentVisible = true;
                        onDisplayListener.onDisplay();
                        onDisplayListener = null;
                    }
                }
            });
        }
    }
}

package com.reactnativenavigation.views;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.controllers.NavigationCommandsHandler;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;

class LeftButton extends MaterialMenuDrawable implements View.OnClickListener {

    private static int getColor(TitleBarButtonParams params) {
        return params != null && params.color.hasColor() ?
                params.color.getColor() :
                Color.BLACK;
    }

    private TitleBarLeftButtonParams params;
    private final LeftButtonOnClickListener onClickListener;
    private final String navigatorEventId;

    LeftButton(Context context,
               TitleBarLeftButtonParams params,
               LeftButtonOnClickListener onClickListener,
               String navigatorEventId) {
        super(context, getColor(params), Stroke.THIN);
        this.params = params;
        this.onClickListener = onClickListener;
        this.navigatorEventId = navigatorEventId;
        setInitialState();
    }

    void setIconState(TitleBarLeftButtonParams params) {
        this.params = params;
        if (params.color.hasColor()) {
            setColor(params.color.getColor());
        }
        animateIconState(params.iconState);
    }

    @Override
    public void onClick(View v) {
        if (isBackButton()) {
            handleBackButtonClick();
        } else {
            sendClickEvent();
        }
    }

    private void handleBackButtonClick() {
        onClickListener.onTitleBarBackButtonClick();
    }

    private void setInitialState() {
        if (params != null) {
            setIconState(params.iconState);
        } else {
            setVisible(false);
        }
    }

    private boolean isBackButton() {
        return getIconState() == IconState.ARROW;
    }


    private void sendClickEvent() {
        NavigationCommandsHandler.navigationApplication.getEventEmitter().sendNavigatorEvent(params.eventId, navigatorEventId);
    }
}

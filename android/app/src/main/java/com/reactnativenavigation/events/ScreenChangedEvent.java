package com.reactnativenavigation.events;

import com.reactnativenavigation.params.BaseScreenParams;


public class ScreenChangedEvent implements Event {
    public static final String TYPE = "ScreenChangedEvent";

    public ScreenChangedEvent(BaseScreenParams screenParams) {

    }

    @Override
    public String getType() {
        return TYPE;
    }
}

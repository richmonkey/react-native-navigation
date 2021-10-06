package com.reactnativenavigation.bridge;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;

public class NavigationReactEventEmitter {

    private static final String KEY_EVENT_ID = "id";
    private static final String KEY_EVENT_TYPE = "type";
    private static final String KEY_NAVIGATOR_EVENT_ID = "navigatorEventID";

    private static final String KEY_SCREEN_STATE = "state";

    private static final String BUTTON_EVENT_TYPE = "NavBarButtonPress";
    private static final String SCREEN_STATE_EVENT_TYPE = "ScreenStateEvent";

    private RCTDeviceEventEmitter eventEmitter;

    public NavigationReactEventEmitter(ReactContext reactContext) {
        this.eventEmitter = reactContext.getJSModule(RCTDeviceEventEmitter.class);
    }

    public void sendNavigatorEvent(String eventId, String navigatorEventId) {
        WritableMap data = Arguments.createMap();
        data.putString(KEY_EVENT_TYPE, BUTTON_EVENT_TYPE);
        data.putString(KEY_EVENT_ID, eventId);
        data.putString(KEY_NAVIGATOR_EVENT_ID, navigatorEventId);
        eventEmitter.emit(navigatorEventId, data);
    }

    public void sendNavigatorScreenEvent(String screenState, String navigatorEventId) {
        WritableMap data = Arguments.createMap();
        data.putString(KEY_EVENT_TYPE, SCREEN_STATE_EVENT_TYPE);
        data.putString(KEY_SCREEN_STATE, screenState);
        data.putString(KEY_NAVIGATOR_EVENT_ID, navigatorEventId);
        eventEmitter.emit(navigatorEventId, data);
    }
}

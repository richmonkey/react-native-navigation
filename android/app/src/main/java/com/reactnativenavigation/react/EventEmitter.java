package com.reactnativenavigation.react;

import com.facebook.react.bridge.WritableMap;
import com.reactnativenavigation.bridge.NavigationReactEventEmitter;


public class EventEmitter implements com.reactnativenavigation.bridge.EventEmitter{
    private NavigationReactEventEmitter reactEventEmitter;

    public EventEmitter(NavigationReactEventEmitter reactEventEmitter) {
        this.reactEventEmitter = reactEventEmitter;
    }

    public void sendNavigatorEvent(String eventId, String navigatorEventId) {
        reactEventEmitter.sendNavigatorEvent(eventId, navigatorEventId);
    }

    public void sendNavigatorEvent(String eventId, String navigatorEventId, WritableMap data) {
        reactEventEmitter.sendNavigatorEvent(eventId, navigatorEventId, data);
    }

    public void sendEvent(String eventId, String navigatorEventId) {
        reactEventEmitter.sendEvent(eventId, navigatorEventId);
    }

    public void sendNavigatorEvent(String eventId, WritableMap arguments) {
        reactEventEmitter.sendEvent(eventId, arguments);
    }
}

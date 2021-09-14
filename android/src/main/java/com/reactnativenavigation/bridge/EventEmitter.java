package com.reactnativenavigation.bridge;

public interface EventEmitter {
    public void sendNavigatorEvent(String eventId, String navigatorEventId);
//    public void sendNavigatorEvent(String eventId, String navigatorEventId, WritableMap data);

}

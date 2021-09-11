package com.reactnativenavigation;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.ReactContext;
import com.reactnativenavigation.bridge.EventEmitter;

public interface NavigationApplication {
    public void runOnMainThread(Runnable runnable);
    public void runOnMainThread(Runnable runnable, long delay);
    public ReactInstanceManager getReactInstanceManager();
    public boolean hasStartedCreatingContext();
    public boolean isReactContextInitialized();
    public ReactContext getReactContext();
    public EventEmitter getEventEmitter();
    public boolean isDebug();

}

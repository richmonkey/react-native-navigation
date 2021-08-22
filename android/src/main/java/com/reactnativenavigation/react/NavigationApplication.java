package com.reactnativenavigation.react;

import android.app.Application;
import android.os.Handler;
import androidx.annotation.Nullable;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.shell.MainReactPackage;
import com.reactnativenavigation.bridge.NavigationReactEventEmitter;
import com.reactnativenavigation.bridge.NavigationReactPackage;

import java.util.ArrayList;
import java.util.List;

public abstract class NavigationApplication extends com.reactnativenavigation.NavigationApplication implements ReactApplication, ReactInstanceManager.ReactInstanceEventListener  {

    public static NavigationApplication instance;

    private ReactNativeHost mReactNativeHost;

    private EventEmitter eventEmitter;
    private Handler handler;


    private static class ReactNativeHostImpl extends ReactNativeHost {

        public ReactNativeHostImpl(Application application) {
            super(application);
        }

        @Override
        public boolean getUseDeveloperSupport() {
            return NavigationApplication.instance.isDebug();
        }

        @Override
        protected List<ReactPackage> getPackages() {
            List<ReactPackage> list = new ArrayList<>();
            list.add(new MainReactPackage());
            list.add(new NavigationReactPackage());
            addAdditionalReactPackagesIfNeeded(list);
            return list;
        }

        private void addAdditionalReactPackagesIfNeeded(List<ReactPackage> list) {
            List<ReactPackage> additionalReactPackages = NavigationApplication.instance.createAdditionalReactPackages();
            if (additionalReactPackages == null) {
                return;
            }

            for (ReactPackage reactPackage : additionalReactPackages) {
                if (reactPackage instanceof MainReactPackage)
                    throw new RuntimeException("Do not create a new MainReactPackage. This is created for you.");
                if (reactPackage instanceof NavigationReactPackage)
                    throw new RuntimeException("Do not create a new NavigationReactPackage. This is created for you.");
            }

            list.addAll(additionalReactPackages);
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        handler = new Handler(getMainLooper());
        mReactNativeHost = new ReactNativeHostImpl(this);
        mReactNativeHost.getReactInstanceManager().addReactInstanceEventListener(this);
        mReactNativeHost.getReactInstanceManager().createReactContextInBackground();

    }

    public void startReactContextOnceInBackgroundAndExecuteJS() {
        getReactInstanceManager().createReactContextInBackground();
    }

    @Override
    public void runOnMainThread(Runnable runnable) {
        handler.post(runnable);
    }

    @Override
    public void runOnMainThread(Runnable runnable, long delay) {
        handler.postDelayed(runnable, delay);
    }

    @Override
    public ReactInstanceManager getReactInstanceManager() {
        return getReactNativeHost().getReactInstanceManager();
    }


    public boolean isReactContextInitialized() {
        return getReactNativeHost().hasInstance() && getReactInstanceManager().getCurrentReactContext() != null;
    }

    public boolean hasStartedCreatingContext() {
        return getReactInstanceManager().hasStartedCreatingInitialContext();
    }

    public ReactContext getReactContext() {
        return getReactNativeHost().getReactInstanceManager().getCurrentReactContext();
    }

    @Override
    public void onReactContextInitialized(ReactContext context) {
        NavigationReactEventEmitter reactEventEmitter = new NavigationReactEventEmitter(context);
        eventEmitter = new EventEmitter(reactEventEmitter);
    }


    @Override
    public ReactNativeHost getReactNativeHost() {
        return mReactNativeHost;
    }

    @Override
    public com.reactnativenavigation.bridge.EventEmitter getEventEmitter() {
        return eventEmitter;
    }


    public abstract boolean isDebug();

    @Nullable
    public abstract List<ReactPackage> createAdditionalReactPackages();


}

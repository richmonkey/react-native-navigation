package com.reactnativenavigation.controllers;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.reactnativenavigation.NavigationApplication;
import com.reactnativenavigation.params.ActivityParams;
import com.reactnativenavigation.params.ScreenParams;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.params.parsers.ActivityParamsParser;
import com.reactnativenavigation.params.parsers.ScreenParamsParser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NavigationCommandsHandler {

    public static final String ACTIVITY_PARAMS_BUNDLE = "ACTIVITY_PARAMS_BUNDLE";

    public static ActivityParams parseActivityParams(Intent intent) {
        return ActivityParamsParser.parse(intent.getBundleExtra(NavigationCommandsHandler.ACTIVITY_PARAMS_BUNDLE));
    }

    private static HashMap<String, Bundle[]> navigatorButtons = new HashMap<>();

    private static HashMap<String, Class<?>> activitieClasses = new HashMap<>();
    private static HashMap<String, Activity> activities = new HashMap<>();

    private static Class<?> customActivityClass = null;

    static class Navigator {
        public String navigatorID;
        public ArrayList<Activity> activities;
    }

    private static HashMap<String, Navigator> navigators = new HashMap<>();

    public static Application application;
    public static NavigationApplication navigationApplication;
    public static void init(Application app) {
        application = app;
        navigationApplication = (NavigationApplication)app;
    }


    public static Bundle createParamsBundle(String screenId, Bundle passProps) {
        Bundle stylesParams = null;
        return createParamsBundle(screenId, passProps, stylesParams, "");
    }

    public static Bundle createParamsBundle(String screenId, Bundle passProps, String title) {
        Bundle stylesParams = null;
        return createParamsBundle(screenId, passProps, stylesParams, title);
    }

    public static Bundle createParamsBundle(String screenId, Bundle passProps, boolean titleBarHidden, String title) {
        Bundle styleParams = new Bundle();
        styleParams.putBoolean("titleBarHidden", titleBarHidden);
        return createParamsBundle(screenId, passProps, styleParams, title);
    }
    public static Bundle createParamsBundle(String screenId, Bundle passProps, Bundle styleParams, String title) {
        return createParamsBundle(screenId, passProps, styleParams, title, null);
    }
    public static Bundle createParamsBundle(String screenId, Bundle passProps, Bundle styleParams, String title, Bundle leftButton) {
        Bundle bundle = new Bundle();

        String navigatorId = NavigationActivity.uniqueId("_navigatorID");
        String screenInstanceId = NavigationActivity.uniqueId("_screenInstanceID");
        String navigatorEventId = screenInstanceId + "_event";

        passProps.putString("navigatorID", navigatorId);
        passProps.putString("screenInstanceID", screenInstanceId);
        passProps.putString("navigatorEventID", navigatorEventId);


        Bundle navigationParams = new Bundle();
        navigationParams.putString("screenInstanceID", screenInstanceId);
        navigationParams.putString("navigatorEventID", navigatorEventId);
        navigationParams.putString("navigatorID", navigatorId);


        Bundle[] buttons = NavigationCommandsHandler.getNavigatorButtons(screenId);
        if (buttons != null && buttons.length == 2) {
            if (buttons[0] != null) {
                bundle.putBundle("leftButton", buttons[0]);
            }
            if (buttons[1] != null) {
                bundle.putBundle("rightButtons", buttons[1]);
            }
        }
        if (leftButton != null) {
            bundle.putBundle("leftButton", leftButton);
        }

        bundle.putBundle("passProps", passProps);
        if (styleParams != null) {
            bundle.putBundle("styleParams", styleParams);
        }
        bundle.putBundle("navigationParams", navigationParams);
        bundle.putString("screenId", screenId);
        bundle.putString("navigatorID", navigatorId);
        if (!TextUtils.isEmpty(title)) {
            bundle.putString("title", title);
        }

        return bundle;
    }

    public static void registerNavigationActivity(Activity activity, String navigatorID) {
        if (!navigators.containsKey(navigatorID)) {
            Navigator nav = new Navigator();
            nav.navigatorID = navigatorID;
            nav.activities = new ArrayList<>();
            navigators.put(navigatorID, nav);
        }

        Navigator nav = navigators.get(navigatorID);
        if (!nav.activities.contains(activity)) {
            nav.activities.add(activity);
        }
    }

    public static void unregisterNavigationActivity(Activity activity, String navigatorID) {
        if (!navigators.containsKey(navigatorID)) {
            return;
        }
        Navigator nav = navigators.get(navigatorID);
        if (nav.activities.contains(activity)) {
            nav.activities.remove(activity);
        }
    }

    public static void registerActivity(Activity activity, String id) {
        if (!activities.containsKey(id)) {
            activities.put(id, activity);
        }
    }

    public static void unregisterActivity(String id) {
        if (activities.containsKey(id)) {
            activities.remove(id);
        }
    }

    public static void registerNavigatorButtons(String componentId, Bundle rightButtons, Bundle leftButton) {
        Bundle[] a = new Bundle[2];
        a[0] = leftButton;
        a[1] = rightButtons;
        navigatorButtons.put(componentId, a);
    }

    public static Bundle[] getNavigatorButtons(String componentId) {
        if (navigatorButtons.containsKey(componentId)) {
            return navigatorButtons.get(componentId);
        } else {
            return null;
        }
    }

    public static void registerActivityClass(Class<?> cls, String id) {
        activitieClasses.put(id, cls);
    }

    public static void setCustomNavigationActivityClass(Class<?> cls) {
        customActivityClass = cls;
    }

    public static void push(final Bundle screenParams, final boolean portraitOnlyMode,
                            final boolean landscapeOnlyMode, final String navigatorID,
                            final String screen) {
        navigationApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (navigators.containsKey(navigatorID)) {
                    Context context;
                    Navigator nav = navigators.get(navigatorID);
                    if (nav.activities.size() > 0) {
                        context = nav.activities.get(nav.activities.size() - 1);
                    } else {
                        context = application;
                    }

                    Intent intent;
                    if (activitieClasses.containsKey(screen)) {
                        Class<?> cls = activitieClasses.get(screen);
                        intent = new Intent(context, cls);
                        try {
                            Method method = cls.getMethod("convertBundle", Bundle.class, Intent.class);
                            method.invoke(null, screenParams, intent);
                        } catch (NoSuchMethodException e) {

                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else if (customActivityClass != null) {
                        intent = new Intent(context, customActivityClass);
                    } else if (portraitOnlyMode) {
                        intent = new Intent(context, PortraitNavigationActivity.class);
                    } else if (landscapeOnlyMode) {
                        intent = new Intent(context, LandscapeNavigationActivity.class);
                    } else {
                        intent = new Intent(context, NavigationActivity.class);
                    }
                    if (context == application) {
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                    intent.putExtra(ACTIVITY_PARAMS_BUNDLE, screenParams);
                    context.startActivity(intent);
                }
            }
        });
    }


    public static void pop(Bundle screenParams) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        final ScreenParams params = ScreenParamsParser.parse(screenParams);
        navigationApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.finish();
            }
        });
    }

    public static void popToRoot(Bundle screenParams) {
        final ScreenParams params = ScreenParamsParser.parse(screenParams);
        navigationApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                String navigatorID = params.getNavigatorId();
                if (navigators.containsKey(navigatorID)) {
                    Navigator nav = navigators.get(navigatorID);
                    if (nav.activities.size() > 1) {
                        for (int i = nav.activities.size() - 1; i > 0; i--) {
                            nav.activities.get(i).finish();
                        }
                    }
                }
            }
        });
    }

    public static void showModal(Bundle screenParams, boolean portraitOnlyMode, boolean landscapeOnlyMode, final String screen) {
        Intent intent;
        if (activitieClasses.containsKey(screen)) {
            Class<?> cls = activitieClasses.get(screen);
            intent = new Intent(application, cls);
            try {
                Method method = cls.getMethod("convertBundle", Bundle.class, Intent.class);
                method.invoke(null, screenParams, intent);
            } catch (NoSuchMethodException e) {

            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (portraitOnlyMode) {
            intent = new Intent(application, PortraitNavigationActivity.class);
        } else if (landscapeOnlyMode) {
            intent = new Intent(application, LandscapeNavigationActivity.class);
        } else {
            intent = new Intent(application, NavigationActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ACTIVITY_PARAMS_BUNDLE, screenParams);
        application.startActivity(intent);
    }

    public static void dismissTopModal() {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }
        navigationApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.finish();
            }
        });
    }

    public static void setTopBarVisible(final String screenInstanceID, final boolean hidden, final boolean animated) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        navigationApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setTopBarVisible(screenInstanceID, hidden, animated);
            }
        });
    }

    public static void setScreenTitleBarTitle(final String screenInstanceId, final String title) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        navigationApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setTitleBarTitle(screenInstanceId, title);
            }
        });
    }

    public static void setScreenTitleBarSubtitle(final String screenInstanceId, final String subtitle) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        navigationApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setTitleBarSubtitle(screenInstanceId, subtitle);
            }
        });
    }


    public static void setScreenTitleBarRightButtons(final String screenInstanceId,
                                                     final String navigatorEventId,
                                                     final List<TitleBarButtonParams> titleBarButtons) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        navigationApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setTitleBarButtons(screenInstanceId, navigatorEventId, titleBarButtons);
            }
        });
    }

    public static void setScreenTitleBarLeftButtons(final String screenInstanceId,
                                                    final String navigatorEventId,
                                                    final TitleBarLeftButtonParams titleBarButtons) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        navigationApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.setTitleBarLeftButton(screenInstanceId, navigatorEventId, titleBarButtons);
            }
        });
    }

    public static void enableRightButton(final String screenInstanceId, final boolean enabled) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        navigationApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                currentActivity.enableRightButton(screenInstanceId, enabled);
            }
        });
    }

    public static void setScreenResult(final String screenInstanceId,
                                       final Bundle result) {
        final NavigationActivity currentActivity = NavigationActivity.currentActivity;
        if (currentActivity == null) {
            return;
        }

        navigationApplication.runOnMainThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.putExtras(result);
                currentActivity.setResult(Activity.RESULT_OK, intent);
            }
        });
    }

}

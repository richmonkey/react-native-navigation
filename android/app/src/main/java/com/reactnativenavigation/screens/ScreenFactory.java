package com.reactnativenavigation.screens;

import androidx.appcompat.app.AppCompatActivity;

import com.reactnativenavigation.params.ScreenParams;
import com.reactnativenavigation.views.LeftButtonOnClickListener;

public class ScreenFactory {
    public static Screen create(AppCompatActivity activity,
                         ScreenParams screenParams,
                         LeftButtonOnClickListener leftButtonOnClickListener) {
        return new SingleScreen(activity, screenParams, leftButtonOnClickListener);
    }
}

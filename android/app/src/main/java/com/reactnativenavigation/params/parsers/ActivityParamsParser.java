package com.reactnativenavigation.params.parsers;

import android.os.Bundle;

import com.reactnativenavigation.params.ActivityParams;
import com.reactnativenavigation.params.AppStyle;

public class ActivityParamsParser extends Parser {
    public static ActivityParams parse(Bundle params) {
        ActivityParams result = new ActivityParams();


        result.screenParams = ScreenParamsParser.parse(params);


        return result;
    }
}

/*eslint-disable*/
import React, {Component} from 'react';
import {AppRegistry, NativeModules, processColor} from 'react-native';
import _ from 'lodash';
import PropRegistry from './PropRegistry';
import Navigation from './Navigation';

const resolveAssetSource = require('react-native/Libraries/Image/resolveAssetSource');
const NativeReactModule = NativeModules.NavigationReactModule;

function savePassProps(params) {
    if (params.navigationParams && params.passProps) {
        PropRegistry.save(params.navigationParams.screenInstanceID, params.passProps);
    }

    if (params.screen && params.screen.passProps) {
        PropRegistry.save(params.screen.navigationParams.screenInstanceID, params.screen.passProps);
    }
}

var newPlatformSpecific = {
    push:function(screenParams) {
        savePassProps(screenParams);
        NativeReactModule.push(screenParams);
    },

    pop:function(screenParams) {
        NativeReactModule.pop(screenParams);
    },

    popToRoot: function(screenParams) {
        NativeReactModule.popToRoot(screenParams);
    },

    toggleTopBarVisible: function(screenInstanceID, visible, animated) {
        NativeReactModule.setTopBarVisible(screenInstanceID, visible, animated);
    },

    setScreenTitleBarTitle: function(screenInstanceID, title) {
        NativeReactModule.setScreenTitleBarTitle(screenInstanceID, title);
    },

    setScreenTitleBarSubtitle: function(screenInstanceID, subtitle) {
        NativeReactModule.setScreenTitleBarSubtitle(screenInstanceID, subtitle);
    },

    setScreenButtons: function(screenInstanceID, navigatorEventID, rightButtons, leftButton, fab) {
        NativeReactModule.setScreenButtons(screenInstanceID, navigatorEventID, rightButtons, leftButton, fab);
    },

    showModal: function(screenParams) {
        savePassProps(screenParams);
        NativeReactModule.showModal(screenParams);
    },

    dismissTopModal: function() {
        NativeReactModule.dismissTopModal();
    },

    dismissAllModals: function() {
        NativeReactModule.dismissAllModals();
    },
};



function navigatorPush(navigator, params) {
    var screen = params;
    if (!screen.screen) {
        console.error('navigatorPush(params): screen must include a screen property');
        return;
    }
    addNavigatorParams(screen, navigator);
    addNavigatorButtons(screen);
    addTitleBarBackButtonIfNeeded(params);
    addNavigationStyleParams(screen);
    screen.screenId = screen.screen;
    adaptNavigationStyleToScreenStyle(screen);
    adaptNavigationParams(screen);
    params.overrideBackPress = screen.overrideBackPress;

    console.log("params:", params, params.navigationParams);
    newPlatformSpecific.push(params);
}


function navigatorPop(navigator, params) {
  addNavigatorParams(params, navigator);

  params.screenId = params.screen;
  let adapted = adaptNavigationStyleToScreenStyle(params);
  adapted = adaptNavigationParams(adapted);

  newPlatformSpecific.pop(adapted);
}

function navigatorPopToRoot(navigator, params) {
    addNavigatorParams(params, navigator);
    var adapted = adaptNavigationParams(params);
    newPlatformSpecific.popToRoot(adapted);
}



function adaptNavigationStyleToScreenStyle(screen) {
  const navigatorStyle = screen.navigatorStyle;
  if (!navigatorStyle) {
    return screen;
  }

  screen.styleParams = convertStyleParams(navigatorStyle);

  return _.omit(screen, ['navigatorStyle']);
}

function convertStyleParams(originalStyleObject) {
  if (!originalStyleObject) {
    return null;
  }

  let ret = {
    statusBarColor: processColor(originalStyleObject.statusBarColor),
    topBarColor: processColor(originalStyleObject.navBarBackgroundColor),
    topBarTransparent: originalStyleObject.navBarTransparent,
    topBarTranslucent: originalStyleObject.navBarTranslucent,
    topBarElevationShadowEnabled: originalStyleObject.topBarElevationShadowEnabled,
    collapsingToolBarImage: originalStyleObject.collapsingToolBarImage,
    collapsingToolBarCollapsedColor: processColor(originalStyleObject.collapsingToolBarCollapsedColor),
    titleBarHidden: originalStyleObject.navBarHidden,
    titleBarHideOnScroll: originalStyleObject.navBarHideOnScroll,
    titleBarTitleColor: processColor(originalStyleObject.navBarTextColor),
    titleBarSubtitleColor: processColor(originalStyleObject.navBarTextSubtitleColor),
    titleBarButtonColor: processColor(originalStyleObject.navBarButtonColor),
    titleBarDisabledButtonColor: processColor(originalStyleObject.titleBarDisabledButtonColor),
    backButtonHidden: originalStyleObject.backButtonHidden,
    topTabsHidden: originalStyleObject.topTabsHidden,
    contextualMenuStatusBarColor: processColor(originalStyleObject.contextualMenuStatusBarColor),
    contextualMenuBackgroundColor: processColor(originalStyleObject.contextualMenuBackgroundColor),
    contextualMenuButtonsColor: processColor(originalStyleObject.contextualMenuButtonsColor),

    drawBelowTopBar: !originalStyleObject.drawUnderNavBar,

    topTabTextColor: processColor(originalStyleObject.topTabTextColor),
    selectedTopTabTextColor: processColor(originalStyleObject.selectedTopTabTextColor),
    selectedTopTabIndicatorHeight: originalStyleObject.selectedTopTabIndicatorHeight,
    selectedTopTabIndicatorColor: processColor(originalStyleObject.selectedTopTabIndicatorColor),

    screenBackgroundColor: processColor(originalStyleObject.screenBackgroundColor),

    drawScreenAboveBottomTabs: !originalStyleObject.drawUnderTabBar,

    bottomTabsColor: processColor(originalStyleObject.tabBarBackgroundColor),
    bottomTabsButtonColor: processColor(originalStyleObject.tabBarButtonColor),
    bottomTabsSelectedButtonColor: processColor(originalStyleObject.tabBarSelectedButtonColor),
    bottomTabsHidden: originalStyleObject.tabBarHidden,
    bottomTabsHiddenOnScroll: originalStyleObject.bottomTabsHiddenOnScroll,
    forceTitlesDisplay: originalStyleObject.forceTitlesDisplay,
    bottomTabBadgeTextColor: processColor(originalStyleObject.bottomTabBadgeTextColor),
    bottomTabBadgeBackgroundColor: processColor(originalStyleObject.bottomTabBadgeBackgroundColor),

    navigationBarColor: processColor(originalStyleObject.navigationBarColor)
  }
  return ret;
}


function adaptNavigationParams(screen) {
  screen.navigationParams = {
    screenInstanceID: screen.screenInstanceID,
    navigatorID: screen.navigatorID,
    navigatorEventID: screen.navigatorEventID
  };
  return screen;
}


function navigatorSetButtons(navigator, navigatorEventID, _params) {
  const params = _.cloneDeep(_params);
  if (params.rightButtons) {
    params.rightButtons.forEach(function(button) {
      button.enabled = !button.disabled;
      if (button.icon) {
        const icon = resolveAssetSource(button.icon);
        if (icon) {
          button.icon = icon.uri;
        }
      }
    });
  }
  const leftButton = getLeftButton(params);
  if (leftButton) {
    if (leftButton.icon) {
      const icon = resolveAssetSource(leftButton.icon);
      if (icon) {
        leftButton.icon = icon.uri;
      }
    }
  }
  const fab = undefined
  newPlatformSpecific.setScreenButtons(navigator.screenInstanceID, navigatorEventID, params.rightButtons, leftButton, fab);
}



function navigatorSetTitle(navigator, params) {
  newPlatformSpecific.setScreenTitleBarTitle(navigator.screenInstanceID, params.title);
}

function navigatorSetSubtitle(navigator, params) {
  newPlatformSpecific.setScreenTitleBarSubtitle(navigator.screenInstanceID, params.subtitle);
}


function navigatorToggleNavBar(navigator, params) {
  const screenInstanceID = navigator.screenInstanceID;
  const visible = params.to === 'shown';
  const animated = !(params.animated === false);

  newPlatformSpecific.toggleTopBarVisible(
    screenInstanceID,
    visible,
    animated
  );
}

function showModal(params) {
  addNavigatorParams(params);
  addNavigatorButtons(params);
  addTitleBarBackButtonIfNeeded(params);
  addNavigationStyleParams(params);
  params.screenId = params.screen;
  let adapted = adaptNavigationStyleToScreenStyle(params);
  adapted = adaptNavigationParams(adapted);
  adapted.overrideBackPress = params.overrideBackPress;

  newPlatformSpecific.showModal(adapted);
}

function dismissModal() {
  newPlatformSpecific.dismissTopModal();
}

function dismissAllModals(params) {
  newPlatformSpecific.dismissAllModals();
}

function addNavigatorParams(screen, navigator = null, idx = '') {
  screen.navigatorID = navigator ? navigator.navigatorID : _.uniqueId('navigatorID') + '_nav' + idx;
  screen.screenInstanceID = _.uniqueId('screenInstanceID');
  screen.navigatorEventID = screen.screenInstanceID + '_events';
}

function addNavigatorButtons(screen) {
  const Screen = Navigation.getRegisteredScreen(screen.screen);
  screen.navigatorButtons = _.cloneDeep(Screen.navigatorButtons);

  // Get image uri from image id
  const rightButtons = getRightButtons(screen);
  if (rightButtons) {
    rightButtons.forEach(function(button) {
      button.enabled = !button.disabled;
      if (button.icon) {
        const icon = resolveAssetSource(button.icon);
        if (icon) {
          button.icon = icon.uri;
        }
      }
    });
  }

  let leftButton = getLeftButton(screen);
  if (leftButton) {
    if (leftButton.icon) {
      const icon = resolveAssetSource(leftButton.icon);
      if (icon) {
        leftButton.icon = icon.uri;
      }
    }
  }

  if (rightButtons) {
    screen.rightButtons = rightButtons;
  }
  if (leftButton) {
    screen.leftButton = leftButton;
  }
}


function addTitleBarBackButtonIfNeeded(screen) {
  const leftButton = getLeftButton(screen);
  if (!leftButton) {
    screen.leftButton = {
      id: 'back'
    }
  }
}

function getLeftButton(screen) {
  const leftButton = getLeftButtonDeprecated(screen);
  if (leftButton) {
    return leftButton;
  }

  if (screen.navigatorButtons && screen.navigatorButtons.leftButtons) {
    return screen.navigatorButtons.leftButtons[0];
  }

  if (screen.leftButtons) {
    if (_.isArray(screen.leftButtons)) {
      return screen.leftButtons[0];
    } else {
      return screen.leftButtons;
    }
  }

  return null;
}

function getLeftButtonDeprecated(screen) {
  if (screen.navigatorButtons && screen.navigatorButtons.leftButton) {
    return screen.navigatorButtons.leftButton;
  }

  return screen.leftButton;
}

function getRightButtons(screen) {
  if (screen.navigatorButtons && screen.navigatorButtons.rightButtons) {
    return screen.navigatorButtons.rightButtons;
  }

  return screen.rightButtons;
}

function addNavigationStyleParams(screen) {
  const Screen = Navigation.getRegisteredScreen(screen.screen);
  screen.navigatorStyle = Object.assign({}, screen.navigatorStyle, Screen.navigatorStyle);
}


function registerNavigatorButtons(screenID, navigatorButtons) {
    //todo
    //NativeReactModule.registerNavigatorButtons(screenID, navigatorButtons);
}

export default {
  navigatorPush,
  navigatorPop,
  navigatorPopToRoot,    
  showModal,
  dismissModal,
  dismissAllModals,
  navigatorSetButtons,
  navigatorSetTitle,
  navigatorSetSubtitle,
  navigatorToggleNavBar,
  registerNavigatorButtons    
};

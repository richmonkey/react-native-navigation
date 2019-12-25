/*eslint-disable*/
import {NativeModules} from 'react-native';
import _ from 'lodash';

const resolveAssetSource = require('react-native/Libraries/Image/resolveAssetSource');
const NativeReactModule = NativeModules.NavigationReactModule;


var newPlatformSpecific = {
    push:function(screenParams) {
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
    var params = Object.assign({}, params);
    params.screenId = params.component;
    params.navigatorID = navigator.navigatorID;

    if (params.leftButtons) {
      params.leftButton = params.leftButtons[0];
      delete(params, 'leftButtons');
    } else {
      params.leftButton = {
        id: 'back'
      }
    }
    var adapted = adaptNavigationStyleToScreenStyle(params);
    console.log("params:", adapted);
    newPlatformSpecific.push(adapted);
}


function navigatorPop(navigator, params) {
  params.navigatorID = navigator.navigatorID;
  params.screenInstanceID = navigator.screenInstanceID;
  params.navigatorEventID = navigator.navigatorEventID;
  var adapted = adaptNavigationParams(params);
  newPlatformSpecific.pop(adapted);
}

function navigatorPopToRoot(navigator, params) {
  params.navigatorID = navigator.navigatorID;
  params.screenInstanceID = navigator.screenInstanceID;
  params.navigatorEventID = navigator.navigatorEventID;
  var adapted = adaptNavigationParams(params);
  newPlatformSpecific.popToRoot(adapted);
}

function navigatorSetButtons(navigator, navigatorEventID, _params) {
  const params = _.cloneDeep(_params);
  if (params.rightButtons) {
    params.rightButtons.forEach(function(button) {
      button.enabled = !button.disabled;
    });
  }
  var leftButton = undefined;
  if (params.leftButtons) {
    params.leftButtons.forEach(function(button) {
      button.enabled = !button.disabled;
    });

    leftButton = params.leftButtons[0];
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
  var params = Object.assign({}, params);
  params.screenId = params.component;
  params.navigatorID = navigator.navigatorID;


  if (params.leftButtons) {
    params.leftButton = params.leftButtons[0];
    delete(params, 'leftButtons');
  } else {
    params.leftButton = {
      id: 'back'
    }
  }
  var adapted = adaptNavigationStyleToScreenStyle(params);
  console.log("params:", adapted);
  newPlatformSpecific.showModal(adapted);
}

function dismissModal() {
  newPlatformSpecific.dismissTopModal();
}

function dismissAllModals(params) {
  newPlatformSpecific.dismissAllModals();
}


//helper function
function adaptNavigationStyleToScreenStyle(screen) {
  const navigatorStyle = screen.style;
  if (!navigatorStyle) {
    return screen;
  }

  screen.styleParams = convertStyleParams(navigatorStyle);

  return _.omit(screen, ['style']);
}

function convertStyleParams(originalStyleObject) {
  if (!originalStyleObject) {
    return null;
  }

  let ret = {
    statusBarColor: originalStyleObject.statusBarColor,


    drawScreenAboveBottomTabs: !originalStyleObject.drawUnderTabBar,
    drawBelowTopBar: !originalStyleObject.drawUnderNavBar,

    topBarColor: originalStyleObject.navBarBackgroundColor,
    topBarTransparent: originalStyleObject.navBarTransparent,
    topBarTranslucent: originalStyleObject.navBarTranslucent,
    topBarElevationShadowEnabled: originalStyleObject.topBarElevationShadowEnabled,

    titleBarHidden: originalStyleObject.navBarHidden,
    titleBarHideOnScroll: originalStyleObject.navBarHideOnScroll,
    titleBarTitleColor: originalStyleObject.navBarTextColor,
    titleBarSubtitleColor: originalStyleObject.navBarTextSubtitleColor,
    titleBarButtonColor: originalStyleObject.navBarButtonColor,

    titleBarDisabledButtonColor: (originalStyleObject.titleBarDisabledButtonColor),

    collapsingToolBarImage: originalStyleObject.collapsingToolBarImage,
    collapsingToolBarCollapsedColor: (originalStyleObject.collapsingToolBarCollapsedColor),

    backButtonHidden: originalStyleObject.backButtonHidden,
    topTabsHidden: originalStyleObject.topTabsHidden,
    contextualMenuStatusBarColor: (originalStyleObject.contextualMenuStatusBarColor),
    contextualMenuBackgroundColor: (originalStyleObject.contextualMenuBackgroundColor),
    contextualMenuButtonsColor: (originalStyleObject.contextualMenuButtonsColor),

    topTabTextColor: (originalStyleObject.topTabTextColor),
    selectedTopTabTextColor: (originalStyleObject.selectedTopTabTextColor),
    selectedTopTabIndicatorHeight: originalStyleObject.selectedTopTabIndicatorHeight,
    selectedTopTabIndicatorColor: (originalStyleObject.selectedTopTabIndicatorColor),

    screenBackgroundColor: (originalStyleObject.screenBackgroundColor),

    bottomTabsColor: (originalStyleObject.tabBarBackgroundColor),
    bottomTabsButtonColor: (originalStyleObject.tabBarButtonColor),
    bottomTabsSelectedButtonColor: (originalStyleObject.tabBarSelectedButtonColor),
    bottomTabsHidden: originalStyleObject.tabBarHidden,
    bottomTabsHiddenOnScroll: originalStyleObject.bottomTabsHiddenOnScroll,
    forceTitlesDisplay: originalStyleObject.forceTitlesDisplay,
    bottomTabBadgeTextColor: (originalStyleObject.bottomTabBadgeTextColor),
    bottomTabBadgeBackgroundColor: (originalStyleObject.bottomTabBadgeBackgroundColor),

    navigationBarColor: (originalStyleObject.navigationBarColor)
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
};

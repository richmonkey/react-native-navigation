/*eslint-disable*/
import {NativeModules} from 'react-native';
import _ from 'lodash';

const NativeReactModule = NativeModules.NavigationReactModule;

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
    NativeReactModule.push(adapted);    
}


function navigatorPop(navigator, params) {
  params.navigatorID = navigator.navigatorID;
  params.screenInstanceID = navigator.screenInstanceID;
  params.navigatorEventID = navigator.navigatorEventID;
  var adapted = adaptNavigationParams(params);
  NativeReactModule.pop(adapted);    
}

function navigatorPopToRoot(navigator, params) {
  params.navigatorID = navigator.navigatorID;
  params.screenInstanceID = navigator.screenInstanceID;
  params.navigatorEventID = navigator.navigatorEventID;
  var adapted = adaptNavigationParams(params);
  NativeReactModule.popToRoot(adapted);    
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

  NativeReactModule.setScreenButtons(navigator.screenInstanceID, navigatorEventID, params.rightButtons, leftButton, fab);
}


function navigatorEnableRightButton(navigator, params) {
    NativeReactModule.enableRightButton(navigator.screenInstanceID, params.enabled);    
}

function navigatorEnableLeftButton(navigator, params) {
    console.warn("can't support enable/disable left button on android");
}

    
function navigatorSetTitle(navigator, params) {
  NativeReactModule.setScreenTitleBarTitle(navigator.screenInstanceID, params.title);
}

function navigatorSetSubtitle(navigator, params) {
  NativeReactModule.setScreenTitleBarSubtitle(navigator.screenInstanceID, params.subtitle);
}


function navigatorToggleNavBar(navigator, params) {
  const screenInstanceID = navigator.screenInstanceID;
  const visible = params.to === 'shown';
  const animated = !(params.animated === false);

  NativeReactModule.setTopBarVisible(screenInstanceID, visible, animated);    
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

  NativeReactModule.showModal(adapted);    
}

function dismissModal() {
  NativeReactModule.dismissTopModal();    
}

function dismissAllModals(params) {
  NativeReactModule.dismissAllModals();    
}


function registerNavigatorButtons(screenID, _params) {
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

        if (params.leftButtons.length > 0) {
            leftButton = params.leftButtons[0];
        }
    }
    NativeReactModule.registerNavigatorButtons(screenID, params.rightButtons, leftButton);  
}


function setScreenResult(screenInstanceID, result) {
    NativeReactModule.setResult(screenInstanceID, result);
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
    registerNavigatorButtons,    
    navigatorPush,
    navigatorPop,
    navigatorPopToRoot,    
    showModal,
    dismissModal,
    dismissAllModals,
    navigatorSetButtons,
    navigatorEnableRightButton,
    navigatorEnableLeftButton,    
    navigatorSetTitle,
    navigatorSetSubtitle,
    navigatorToggleNavBar,
    setScreenResult,    
};

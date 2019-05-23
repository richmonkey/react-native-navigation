/*eslint-disable*/
import Navigation from './Navigation';
var OriginalReactNative = require('react-native');
var RCCManager = OriginalReactNative.NativeModules.RCCManager;
import Controllers, {Modal} from './controllers';
const React = Controllers.hijackReact();
const {
  ControllerRegistry,
  NavigationControllerIOS,
} = React;
import _ from 'lodash';

import PropRegistry from './PropRegistry';

function _mergeScreenSpecificSettings(screenID, screenInstanceID, params) {
  const screenClass = Navigation.getRegisteredScreen(screenID);
  if (!screenClass) {
    console.error('Cannot create screen ' + screenID + '. Are you it was registered with Navigation.registerScreen?');
    return;
  }
  const navigatorStyle = Object.assign({}, screenClass.navigatorStyle);
  if (params.navigatorStyle) {
    Object.assign(navigatorStyle, params.navigatorStyle);
  }

  let navigatorEventID = screenInstanceID + '_events';
  let navigatorButtons = _.cloneDeep(screenClass.navigatorButtons);
  if (params.navigatorButtons) {
    navigatorButtons = _.cloneDeep(params.navigatorButtons);
  }
  if (navigatorButtons.leftButtons) {
    for (let i = 0; i < navigatorButtons.leftButtons.length; i++) {
      navigatorButtons.leftButtons[i].onPress = navigatorEventID;
    }
  }
  if (navigatorButtons.rightButtons) {
    for (let i = 0; i < navigatorButtons.rightButtons.length; i++) {
      navigatorButtons.rightButtons[i].onPress = navigatorEventID;
    }
  }
  return {navigatorStyle, navigatorButtons, navigatorEventID};
}

function navigatorPush(navigator, params) {
  if (!params.screen) {
    console.error('Navigator.push(params): params.screen is required');
    return;
  }
  const screenInstanceID = _.uniqueId('screenInstanceID');
  const {
    navigatorStyle,
    navigatorButtons,
    navigatorEventID
  } = _mergeScreenSpecificSettings(params.screen, screenInstanceID, params);
  const passProps = Object.assign({}, params.passProps);
  passProps.navigatorID = navigator.navigatorID;
  passProps.screenInstanceID = screenInstanceID;
  passProps.navigatorEventID = navigatorEventID;

  params.navigationParams = {
    screenInstanceID,
    navigatorStyle,
    navigatorButtons,
    navigatorEventID,
    navigatorID: navigator.navigatorID
  };

  savePassProps(params);

  Controllers.NavigationControllerIOS(navigator.navigatorID).push({
    title: params.title,
    subtitle: params.subtitle,
    titleImage: params.titleImage,
    component: params.screen,
    animated: params.animated,
    passProps: passProps,
    style: navigatorStyle,
    backButtonTitle: params.backButtonTitle,
    backButtonHidden: params.backButtonHidden,
    leftButtons: navigatorButtons.leftButtons,
    rightButtons: navigatorButtons.rightButtons
  });
}

function navigatorPop(navigator, params) {
  Controllers.NavigationControllerIOS(navigator.navigatorID).pop({
    animated: params.animated
  });
}


function navigatorPopToRoot(navigator, params) {
    Controllers.NavigationControllerIOS(navigator.navigatorID).popToRoot({
        animated: params.animated
    });    
}


function navigatorSetTitle(navigator, params) {
  Controllers.NavigationControllerIOS(navigator.navigatorID).setTitle({
    title: params.title,
    subtitle: params.subtitle,
    titleImage: params.titleImage,
    style: params.navigatorStyle
  });
}

function navigatorSetTitleImage(navigator, params) {
  Controllers.NavigationControllerIOS(navigator.navigatorID).setTitleImage({
    titleImage: params.titleImage
  });
}

function navigatorToggleNavBar(navigator, params) {
  Controllers.NavigationControllerIOS(navigator.navigatorID).setHidden({
    hidden: ((params.to === 'hidden') ? true : false),
    animated: params.animated
  });
}

function navigatorSetButtons(navigator, navigatorEventID, params) {
  if (params.leftButtons) {
    const buttons = params.leftButtons.slice(); // clone
    for (let i = 0; i < buttons.length; i++) {
      buttons[i].onPress = navigatorEventID;
    }
    Controllers.NavigationControllerIOS(navigator.navigatorID).setLeftButtons(buttons, params.animated);
  }
  if (params.rightButtons) {
    const buttons = params.rightButtons.slice(); // clone
    for (let i = 0; i < buttons.length; i++) {
      buttons[i].onPress = navigatorEventID;
    }
    Controllers.NavigationControllerIOS(navigator.navigatorID).setRightButtons(buttons, params.animated);
  }
}

function showModal(params) {
  if (!params.screen) {
    console.error('showModal(params): params.screen is required');
    return;
  }
  const controllerID = _.uniqueId('controllerID');
  const navigatorID = controllerID + '_nav';
  const screenInstanceID = _.uniqueId('screenInstanceID');
  const {
    navigatorStyle,
    navigatorButtons,
    navigatorEventID
  } = _mergeScreenSpecificSettings(params.screen, screenInstanceID, params);
  const passProps = Object.assign({}, params.passProps);
  passProps.navigatorID = navigatorID;
  passProps.screenInstanceID = screenInstanceID;
  passProps.navigatorEventID = navigatorEventID;

  params.navigationParams = {
    screenInstanceID,
    navigatorStyle,
    navigatorButtons,
    navigatorEventID,
    navigatorID: navigator.navigatorID
  };

  const Controller = Controllers.createClass({
    render: function() {
      return (
        <NavigationControllerIOS
          id={navigatorID}
          title={params.title}
          subtitle={params.subtitle}
          titleImage={params.titleImage}
          component={params.screen}
          passProps={passProps}
          style={navigatorStyle}
          leftButtons={navigatorButtons.leftButtons}
          rightButtons={navigatorButtons.rightButtons}/>
      );
    }
  });

  savePassProps(params);

  ControllerRegistry.registerController(controllerID, () => Controller);
  Modal.showController(controllerID, params.animationType);
}

function dismissModal(params) {
  Modal.dismissController(params.animationType);
}

function dismissAllModals(params) {
  Modal.dismissAllControllers(params.animationType);
}

function showLightBox(params) {
  if (!params.screen) {
    console.error('showLightBox(params): params.screen is required');
    return;
  }
  const controllerID = _.uniqueId('controllerID');
  const navigatorID = controllerID + '_nav';
  const screenInstanceID = _.uniqueId('screenInstanceID');
  const {
    navigatorStyle,
    navigatorButtons,
    navigatorEventID
  } = _mergeScreenSpecificSettings(params.screen, screenInstanceID, params);
  const passProps = Object.assign({}, params.passProps);
  passProps.navigatorID = navigatorID;
  passProps.screenInstanceID = screenInstanceID;
  passProps.navigatorEventID = navigatorEventID;

  params.navigationParams = {
    screenInstanceID,
    navigatorStyle,
    navigatorButtons,
    navigatorEventID,
    navigatorID
  };

  savePassProps(params);

  Modal.showLightBox({
    component: params.screen,
    passProps: passProps,
    style: params.style
  });
}

function dismissLightBox(params) {
  Modal.dismissLightBox();
}

function savePassProps(params) {
  //TODO this needs to be handled in a common place,
  //TODO also, all global passProps should be handled differently
  if (params.navigationParams && params.passProps) {
    PropRegistry.save(params.navigationParams.screenInstanceID, params.passProps);
  }

  if (params.screen && params.screen.passProps) {
    PropRegistry.save(params.screen.navigationParams.screenInstanceID, params.screen.passProps);
  }
}

function showContextualMenu() {
  // Android only
}

function dismissContextualMenu() {
  // Android only
}

function registerNavigatorButtons(screenID, navigatorButtons) {
    RCCManager.registerNavigatorButtons(screenID, navigatorButtons);
}

export default {
  navigatorPush,
  navigatorPop,
  navigatorPopToRoot,
  showModal,
  dismissModal,
  dismissAllModals,
  showLightBox,
  dismissLightBox,
  navigatorSetButtons,
  navigatorSetTitle,
  navigatorSetTitleImage,
  navigatorToggleNavBar,
  showContextualMenu,
  dismissContextualMenu,
  registerNavigatorButtons    
};

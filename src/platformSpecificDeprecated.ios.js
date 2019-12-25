/*eslint-disable*/
import {NativeModules} from 'react-native';
var RCCManager = NativeModules.RCCManager;

var Controllers = {
  NavigationControllerIOS: function (id) {
    return {
      push: function (params) {
        RCCManager.NavigationControllerIOS(id, "push", params);
      },
      pop: function (params) {
        RCCManager.NavigationControllerIOS(id, "pop", params);
      },
      popToRoot: function (params) {
        RCCManager.NavigationControllerIOS(id, "popToRoot", params);
      },
      setTitle: function (params) {
        RCCManager.NavigationControllerIOS(id, "setTitle", params);
      },
      setLeftButtons: function (buttons, animated = false) {
        RCCManager.NavigationControllerIOS(id, "setButtons", {buttons: buttons, side: "left", animated: animated});
      },
      setRightButtons: function (buttons, animated = false) {
        RCCManager.NavigationControllerIOS(id, "setButtons", {buttons: buttons, side: "right", animated: animated});
      },
      setHidden: function(params = {}) {
        RCCManager.NavigationControllerIOS(id, "setHidden", params);
      }
    };
  },

  Modal: {
    showLightBox: function(params) {
      RCCManager.modalShowLightBox(params);
    },
    dismissLightBox: function() {
      RCCManager.modalDismissLightBox();
    },

    showController: function(layout, animationType = 'slide-up', passProps = {}) {
      RCCManager.showController(layout, animationType, passProps);
    },

    dismissController: function(animationType = 'slide-down') {
      RCCManager.dismissController(animationType);
    },
    dismissAllControllers: function(animationType = 'slide-down') {
      RCCManager.dismissAllControllers(animationType);
    }
  },
};

const Modal = Controllers.Modal;

function navigatorPush(navigator, params) {
  Controllers.NavigationControllerIOS(navigator.navigatorID).push(params);
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
    style: params.style
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
  //ViewControllerIOS||NavigationControllerIOS
  var layout = {type:"NavigationControllerIOS", props:params};
  console.log("show modal layout:", layout);
  Modal.showController(layout, params.animationType);
}

function dismissModal(params) {
  Modal.dismissController(params.animationType);
}

function dismissAllModals(params) {
  Modal.dismissAllControllers(params.animationType);
}

function showLightBox(params) {
  Modal.showLightBox(params);
}

function dismissLightBox(params) {
  Modal.dismissLightBox();
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
};

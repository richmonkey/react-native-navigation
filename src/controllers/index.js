/*eslint-disable*/
var OriginalReactNative = require('react-native');
var RCCManager = OriginalReactNative.NativeModules.RCCManager;
var NativeAppEventEmitter = OriginalReactNative.NativeAppEventEmitter;
var utils = require('./utils');
var resolveAssetSource = require('react-native/Libraries/Image/resolveAssetSource');
var processColor = OriginalReactNative.processColor;

var _controllerRegistry = {};


function _processProperties(properties) {
  for (var property in properties) {
    if (properties.hasOwnProperty(property)) {
      if (property === 'icon' || property.endsWith('Icon') || property.endsWith('Image')) {
        properties[property] = resolveAssetSource(properties[property]);
      }
      if (property === 'color' || property.endsWith('Color')) {
        properties[property] = processColor(properties[property]);
      }
    }
  }
}


function _processButtons(buttons) {
  if (!buttons) return;
  for (var i = 0 ; i < buttons.length ; i++) {
    buttons[i] = Object.assign({}, buttons[i]);
    var button = buttons[i];
    _processProperties(button);
  }
}



var Controllers = {
  createClass: function (app) {
    return app;
  },

  hijackReact: function () {
    return {
      createElement: function(type, props) {
        var children = Array.prototype.slice.call(arguments, 2);
        var flatChildren = utils.flattenDeep(children);
        props = Object.assign({}, props);
        _processProperties(props);
        if (props['style']) {
          props['style'] = Object.assign({}, props['style']);
          _processProperties(props['style']);
        }
        return {
          'type': type.name,
          'props': props,
          'children': flatChildren
        };
      },

      ControllerRegistry: Controllers.ControllerRegistry,
      NavigationControllerIOS: {name: 'NavigationControllerIOS'},
      ViewControllerIOS: {name: 'ViewControllerIOS'},
    };
  },

  ControllerRegistry: {
    registerController: function (appKey, getControllerFunc) {
      _controllerRegistry[appKey] = getControllerFunc();
    }
  },

  NavigationControllerIOS: function (id) {
    return {
      push: function (params) {
        if (params['style']) {
          params['style'] = Object.assign({}, params['style']);
          _processProperties(params['style']);
        }
        if (params['titleImage']) {
          params['titleImage'] = resolveAssetSource(params['titleImage']);
        }
        if (params['leftButtons']) {
          _processButtons(params['leftButtons']);
        }
        if (params['rightButtons']) {
          _processButtons(params['rightButtons']);
        }
        RCCManager.NavigationControllerIOS(id, "push", params);
      },
      pop: function (params) {
        RCCManager.NavigationControllerIOS(id, "pop", params);
      },
      popToRoot: function (params) {
        RCCManager.NavigationControllerIOS(id, "popToRoot", params);
      },
      setTitle: function (params) {
        if (params['style']) {
          params['style'] = Object.assign({}, params['style']);
          _processProperties(params['style']);
        }
        if (params['titleImage']) {
          params['titleImage'] = resolveAssetSource(params['titleImage']);
        }
        RCCManager.NavigationControllerIOS(id, "setTitle", params);
      },
      resetTo: function (params) {
        if (params['style']) {
          params['style'] = Object.assign({}, params['style']);
          _processProperties(params['style']);
        }
        if (params['leftButtons']) {
          _processButtons(params['leftButtons']);
        }
        if (params['rightButtons']) {
          _processButtons(params['rightButtons']);
        }
        RCCManager.NavigationControllerIOS(id, "resetTo", params);
      },
      setLeftButtons: function (buttons, animated = false) {
        _processButtons(buttons);
        RCCManager.NavigationControllerIOS(id, "setButtons", {buttons: buttons, side: "left", animated: animated});
      },
      setRightButtons: function (buttons, animated = false) {
        _processButtons(buttons);
        RCCManager.NavigationControllerIOS(id, "setButtons", {buttons: buttons, side: "right", animated: animated});
      },
      setHidden: function(params = {}) {
        RCCManager.NavigationControllerIOS(id, "setHidden", params);
      }
    };
  },

  Modal: {
    showLightBox: function(params) {
      params['style'] = Object.assign({}, params['style']);
      _processProperties(params['style']);
      RCCManager.modalShowLightBox(params);
    },
    dismissLightBox: function() {
      RCCManager.modalDismissLightBox();
    },
    showController: function(appKey, animationType = 'slide-up', passProps = {}) {
      var controller = _controllerRegistry[appKey];
      if (controller === undefined) return;
      var layout = controller.render();
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

module.exports = Controllers;


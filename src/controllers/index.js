/*eslint-disable*/
var OriginalReactNative = require('react-native');
var RCCManager = OriginalReactNative.NativeModules.RCCManager;
var NativeAppEventEmitter = OriginalReactNative.NativeAppEventEmitter;
var utils = require('./utils');
var Constants = require('./Constants');
var resolveAssetSource = require('react-native/Libraries/Image/resolveAssetSource');
var processColor = OriginalReactNative.processColor;

var _controllerRegistry = {};

function _getRandomId() {
  return (Math.random()*1e20).toString(36);
}

function _processProperties(properties) {
  for (var property in properties) {
    if (properties.hasOwnProperty(property)) {
      if (property === 'icon' || property.endsWith('Icon') || property.endsWith('Image')) {
        properties[property] = resolveAssetSource(properties[property]);
      }
      if (property === 'color' || property.endsWith('Color')) {
        properties[property] = processColor(properties[property]);
      }
      if (property === 'buttons' || property.endsWith('Buttons')) {
        _processButtons(properties[property]);
      }
    }
  }
}

function _setListener(callbackId, func) {
  return NativeAppEventEmitter.addListener(callbackId, (...args) => func(...args));
}

function _processButtons(buttons) {
  if (!buttons) return;
  var unsubscribes = [];
  for (var i = 0 ; i < buttons.length ; i++) {
    buttons[i] = Object.assign({}, buttons[i]);
    var button = buttons[i];
    _processProperties(button);
    if (typeof button.onPress === "function") {
      var onPressId = _getRandomId();
      var onPressFunc = button.onPress;
      button.onPress = onPressId;
      var unsubscribe = _setListener(onPressId, onPressFunc);
      unsubscribes.push(unsubscribe);
    }
  }
  return function () {
    for (var i = 0 ; i < unsubscribes.length ; i++) {
      if (unsubscribes[i]) { unsubscribes[i](); }
    }
  };
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
        var unsubscribes = [];
        if (params['style']) {
          params['style'] = Object.assign({}, params['style']);
          _processProperties(params['style']);
        }
        if (params['titleImage']) {
          params['titleImage'] = resolveAssetSource(params['titleImage']);
        }
        if (params['leftButtons']) {
          var unsubscribe = _processButtons(params['leftButtons']);
          unsubscribes.push(unsubscribe);
        }
        if (params['rightButtons']) {
          var unsubscribe = _processButtons(params['rightButtons']);
          unsubscribes.push(unsubscribe);
        }
        RCCManager.NavigationControllerIOS(id, "push", params);
        return function() {
          for (var i = 0 ; i < unsubscribes.length ; i++) {
            if (unsubscribes[i]) { unsubscribes[i](); }
          }
        };
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
        var unsubscribes = [];
        if (params['style']) {
          params['style'] = Object.assign({}, params['style']);
          _processProperties(params['style']);
        }
        if (params['leftButtons']) {
          var unsubscribe = _processButtons(params['leftButtons']);
          unsubscribes.push(unsubscribe);
        }
        if (params['rightButtons']) {
          var unsubscribe = _processButtons(params['rightButtons']);
          unsubscribes.push(unsubscribe);
        }
        RCCManager.NavigationControllerIOS(id, "resetTo", params);
        return function() {
          for (var i = 0 ; i < unsubscribes.length ; i++) {
            if (unsubscribes[i]) { unsubscribes[i](); }
          }
        };
      },
      setLeftButton: function () {
        console.error('setLeftButton is deprecated, see setLeftButtons');
      },
      setLeftButtons: function (buttons, animated = false) {
        var unsubscribe = _processButtons(buttons);
        RCCManager.NavigationControllerIOS(id, "setButtons", {buttons: buttons, side: "left", animated: animated});
        return unsubscribe;
      },
      setRightButtons: function (buttons, animated = false) {
        var unsubscribe = _processButtons(buttons);
        RCCManager.NavigationControllerIOS(id, "setButtons", {buttons: buttons, side: "right", animated: animated});
        return unsubscribe;
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

  Constants: Constants
};

module.exports = Controllers;


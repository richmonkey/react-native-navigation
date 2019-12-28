import {
    NativeAppEventEmitter,
    DeviceEventEmitter,
    Platform,
    processColor    
  } from 'react-native';
import PropRegistry from './PropRegistry';
import ComponentRegistry from './ComponentRegistry';
import _ from 'lodash';
import platformSpecific from './platformSpecificDeprecated';
var resolveAssetSource = require('react-native/Libraries/Image/resolveAssetSource');

const _allNavigatorEventHandlers = {};



function _processProperties(properties) {
    for (var property in properties) {
        if (properties.hasOwnProperty(property)) {
            if (property === 'icon' || property.endsWith('Icon') || property.endsWith('Image')) {
                if (properties[property]) {
                    properties[property] = resolveAssetSource(properties[property]);
                }
            }
            if (property === 'color' || property.endsWith('Color')) {
                if (properties[property]) {
                    properties[property] = processColor(properties[property]);
                }
            }
        }
    }
}


function _processButtons(buttons) {
    //todo test
    // params.rightButtons.forEach(function(button) {
    //     button.enabled = !button.disabled;
    //     if (button.icon) {
    //       const icon = resolveAssetSource(button.icon);
    //       if (icon) {
    //         button.icon = icon.uri;
    //       }
    //     }
    if (!buttons) return;
    for (var i = 0 ; i < buttons.length ; i++) {
        buttons[i] = Object.assign({}, buttons[i]);
        var button = buttons[i];
        _processProperties(button);
    }
}


function savePassProps(params) {
    if (params.navigationParams && params.passProps) {
      PropRegistry.save(params.navigationParams.screenInstanceID, params.passProps);
    }
}
  
  
function _mergeScreenSpecificSettings(screenID, screenInstanceID, params) {
    const screenClass = ComponentRegistry.getRegisteredScreen(screenID);
    if (!screenClass) {
        console.error('Cannot create screen ' + screenID + '. Are you it was registered with ComponentRegistry.registerScreen?');
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
    const passProps = _.cloneDeep(params.passProps);
    passProps.navigatorID = navigator.navigatorID;
    passProps.screenInstanceID = screenInstanceID;
    passProps.navigatorEventID = navigatorEventID;
    params.passProps = passProps;
    
    params.navigationParams = {
        screenInstanceID,
        navigatorEventID,
        navigatorID: navigator.navigatorID
    };

    savePassProps(params);

    var p = {
        title: params.title,
        subtitle: params.subtitle,
        titleImage: params.titleImage,
        component: params.screen,
        animated: params.animated,
        passProps: passProps,
        style: navigatorStyle,
        navigationParams:params.navigationParams,
        backButtonTitle: params.backButtonTitle,
        backButtonHidden: params.backButtonHidden,
        leftButtons: navigatorButtons.leftButtons,
        rightButtons: navigatorButtons.rightButtons
    };

    params = p;
    _processProperties(params);
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

    return platformSpecific.navigatorPush(navigator, params);
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
  
    savePassProps(params);
  
    var props = {
      id:navigatorID,
      title:params.title,
      subtitle:params.subtitle,
      titleImage:params.titleImage,
      component:params.screen,
      passProps:passProps,
      style:navigatorStyle,
      navigationParams:params.navigationParams,
      leftButtons:navigatorButtons.leftButtons,
      rightButtons:navigatorButtons.rightButtons,
      animationType:params.animationType,
    };

    params = props;

    _processProperties(params);
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

    platformSpecific.showModal(params);
}

  
  
//ios only
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
  
    var p = {
      component: params.screen,
      passProps: passProps,
      style: params.style
    }
    params = p;
    if (params['style']) {
        params['style'] = Object.assign({}, params['style']);
        _processProperties(params['style']);
    }

    platformSpecific.showLightBox(params);

}


export default class Navigator {
    constructor(navigatorID, navigatorEventID, screenInstanceID) {
      this.navigatorID = navigatorID;
      this.screenInstanceID = screenInstanceID;
      this.navigatorEventID = navigatorEventID;
      this.navigatorEventHandler = null;
      this.navigatorEventSubscription = null;
    }
  
    push(params = {}) {
        return navigatorPush(this, params);
    }
  
    pop(params = {}) {
        return platformSpecific.navigatorPop(this, params);
    }
  
    popToRoot(params = {}) {
        return platformSpecific.navigatorPopToRoot(this, params);
    }    

    showModal(params = {}) {
      return showModal(params);        
    }
  
    dismissModal(params = {}) {
       return platformSpecific.dismissModal(params);        
    }
  
    dismissAllModals(params = {}) {
      return platformSpecific.dismissAllModals(params);      
    }
  
    showLightBox(params = {}) {
      showLightBox(params);
    }
  
    dismissLightBox(params = {}) {
      return platformSpecific.dismissLightBox(params);      
    }
  
    setButtons(params = {}) {
      if (params['leftButtons']) {
        _processButtons(params['leftButtons']);
      }
      if (params['rightButtons']) {
        _processButtons(params['rightButtons']);
      }
      return platformSpecific.navigatorSetButtons(this, this.navigatorEventID, params);
    }
  
    setTitle(params = {}) {
      if (params['style']) {
        params['style'] = Object.assign({}, params['style']);
        _processProperties(params['style']);
      }
      if (params['titleImage']) {
        params['titleImage'] = resolveAssetSource(params['titleImage']);
      }
      return platformSpecific.navigatorSetTitle(this, params);
    }
  
    setSubTitle(params = {}) {
      return platformSpecific.navigatorSetSubtitle(this, params);
    }
  
    setTitleImage(params = {}) {
      return platformSpecific.navigatorSetTitleImage(this, params);
    }
  
    toggleNavBar(params = {}) {
      return platformSpecific.navigatorToggleNavBar(this, params);
    }
  
    setOnNavigatorEvent(callback) {
      this.navigatorEventHandler = callback;
      if (!this.navigatorEventSubscription) {
        let Emitter = Platform.OS === 'android' ? DeviceEventEmitter : NativeAppEventEmitter;
        this.navigatorEventSubscription = Emitter.addListener(this.navigatorEventID, (event) => this.onNavigatorEvent(event));
        _allNavigatorEventHandlers[this.navigatorEventID] = (event) => this.onNavigatorEvent(event);
      }
    }
  
    handleDeepLink(params = {}) {
      if (!params.link) return;
      const event = {
        type: 'DeepLink',
        link: params.link
      };
      for (let i in _allNavigatorEventHandlers) {
        _allNavigatorEventHandlers[i](event);
      }
    }
  
    onNavigatorEvent(event) {
      if (this.navigatorEventHandler) {
        this.navigatorEventHandler(event);
      }
    }
  
    cleanup() {
      if (this.navigatorEventSubscription) {
        this.navigatorEventSubscription.remove();
        delete _allNavigatorEventHandlers[this.navigatorEventID];
      }
    }
}
  
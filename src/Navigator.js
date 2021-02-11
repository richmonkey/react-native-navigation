import {
    NativeAppEventEmitter,
    DeviceEventEmitter,
    Platform,
    processColor    
  } from 'react-native';
import PropRegistry from './PropRegistry';
import ScreenRegistry from "./ScreenRegistry";
import _ from 'lodash';
import platformSpecific from './platformSpecificDeprecated';

import {processButtons as _processButtons, processProperties as _processProperties} from './util';
var resolveAssetSource = require('react-native/Libraries/Image/resolveAssetSource');

const _allNavigatorEventHandlers = {};


function savePassProps(screenInstanceID, passProps) {
  if (screenInstanceID && passProps) {
    PropRegistry.save(screenInstanceID, passProps);
  }
}

  
function _mergeScreenSpecificSettings(screenID, screenInstanceID, params) {
    const screenClass = ScreenRegistry.getRegisteredScreen(screenID);
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

    let navigationParams = { 
      navigatorID:navigator.navigatorID,
      screenInstanceID:screenInstanceID,
      navigatorEventID:navigatorEventID,
    };

    let passProps = Object.assign({}, params.passProps, params.props, navigationParams);
    savePassProps(screenInstanceID, passProps);

    //避免通过android intent传递大的对象
    passProps = Object.assign({}, params.passProps, navigationParams);
    params.passProps = passProps;
    params.navigationParams = navigationParams;

    console.log("pass props");

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
      navigatorStyle,
      navigatorButtons,
      screenInstanceID,
      navigatorEventID,
      navigatorID: navigator.navigatorID
    };
  
    savePassProps(screenInstanceID, passProps);


    var props = {
      id:navigatorID,
      navigatorID:navigatorID,
      title:params.title,
      subtitle:params.subtitle,
      titleImage:params.titleImage,
      screenId:params.screen,
      component:params.screen,
      passProps:passProps,
      style:navigatorStyle,
      navigationParams:params.navigationParams,
      leftButtons:navigatorButtons.leftButtons,
      rightButtons:navigatorButtons.rightButtons,
      animationType:params.animationType,
      hideNavigationBar:params.hideNavigationBar,
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
  
    savePassProps(screenInstanceID, passProps);

  
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

    //enabled:true or false
    enableRightButton(params) {
        return platformSpecific.navigatorEnableRightButton(this, params);
    }

    enableLeftButton(params) {
        return platformSpecific.navigatorEnableLeftButton(this, params);
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

    setResult(result) {
        return platformSpecific.setScreenResult(this.screenInstanceID, result);
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
  

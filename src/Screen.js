/*eslint-disable*/
import React, {Component} from 'react';
import {
  NativeAppEventEmitter,
  DeviceEventEmitter,
  Platform
} from 'react-native';
import platformSpecific from './platformSpecificDeprecated';
import Navigation from './Navigation';

const _allNavigatorEventHandlers = {};

class Navigator {
  constructor(navigatorID, navigatorEventID, screenInstanceID) {
    this.navigatorID = navigatorID;
    this.screenInstanceID = screenInstanceID;
    this.navigatorEventID = navigatorEventID;
    this.navigatorEventHandler = null;
    this.navigatorEventSubscription = null;
  }

  push(params = {}) {
      return platformSpecific.navigatorPush(this, params);
  }

  pop(params = {}) {
      return platformSpecific.navigatorPop(this, params);
  }

  popToRoot(params = {}) {
      return platformSpecific.navigatorPopToRoot(this, params);
  }    

  showModal(params = {}) {
    return platformSpecific.showModal(params);        
  }

  dismissModal(params = {}) {
     return platformSpecific.dismissModal(params);        
  }

  dismissAllModals(params = {}) {
    return platformSpecific.dismissAllModals(params);      
  }

  showLightBox(params = {}) {
    return platformSpecific.showLightBox(params);      
  }

  dismissLightBox(params = {}) {
    return platformSpecific.dismissLightBox(params);      
  }

  setButtons(params = {}) {
    return platformSpecific.navigatorSetButtons(this, this.navigatorEventID, params);
  }

  setTitle(params = {}) {
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

export default class Screen extends Component {
  static navigatorStyle = {};
  static navigatorButtons = {};

  constructor(props) {
    super(props);
    if (props.navigatorID) {
      this.navigator = new Navigator(props.navigatorID, props.navigatorEventID, props.screenInstanceID);
    }
  }

  componentWillUnmount() {
    if (this.navigator) {
      this.navigator.cleanup();
      this.navigator = undefined;
    }
  }
}

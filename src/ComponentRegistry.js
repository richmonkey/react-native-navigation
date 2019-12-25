/*eslint-disable*/
import React from 'react';
import {AppRegistry} from 'react-native';
import Screen from './Screen';
import PropRegistry from './PropRegistry';

const registeredScreens = {};

function registerScreen(screenID, generator) {
    registeredScreens[screenID] = generator;
    AppRegistry.registerComponent(screenID, generator);
}

function registerComponent(screenID, generator, store = undefined, Provider = undefined) {
  if (store && Provider) {
    return _registerComponentRedux(screenID, generator, store, Provider);
  } else {
    return _registerComponentNoRedux(screenID, generator);
  }
}

function _registerComponentNoRedux(screenID, generator) {
  const generatorWrapper = function() {
    const InternalComponent = generator();
    return class extends Screen {
      static navigatorStyle = InternalComponent.navigatorStyle || {};
      static navigatorButtons = InternalComponent.navigatorButtons || {};

      constructor(props) {
        super(props);
        this.state = {
          internalProps: {...props, ...PropRegistry.load(props.screenInstanceID)}
        }
      }

      static getDerivedStateFromProps(nextProps, prevState) {
         return {
            internalProps: {...PropRegistry.load(nextProps.screenInstanceID), ...nextProps}
         }
      }
      render() {
        return (
          <InternalComponent testID={screenID} navigator={this.navigator} {...this.state.internalProps} />
        );
      }
    };
  };
  registerScreen(screenID, generatorWrapper);
  return generatorWrapper;
}

function _registerComponentRedux(screenID, generator, store, Provider) {
  const generatorWrapper = function() {
    const InternalComponent = generator();
    return class extends Screen {
      static navigatorStyle = InternalComponent.navigatorStyle || {};
      static navigatorButtons = InternalComponent.navigatorButtons || {};

      constructor(props) {
        super(props);
        this.state = {
          internalProps: {...props, ...PropRegistry.load(props.screenInstanceID)}
        }
      }

      static getDerivedStateFromProps(nextProps, prevState) {
          return {
              internalProps: {...PropRegistry.load(nextProps.screenInstanceID), ...nextProps}
          }
      }        

      render() {
        return (
          <Provider store={store}>
            <InternalComponent testID={screenID} navigator={this.navigator} {...this.state.internalProps} />
          </Provider>
        );
      }
    };
  };
  registerScreen(screenID, generatorWrapper);
  return generatorWrapper;
}

function getRegisteredScreen(screenID) {
  const generator = registeredScreens[screenID];
  if (!generator) {
    console.error(`Navigation.getRegisteredScreen: ${screenID} used but not yet registered`);
    return undefined;
  }
  return generator();
}

export default {
    getRegisteredScreen,
    registerComponent,
};

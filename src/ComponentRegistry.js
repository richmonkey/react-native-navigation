/*eslint-disable*/
import React from 'react';
import Screen from './Screen';
import PropRegistry from './PropRegistry';
import {registerScreen, getRegisteredScreen} from "./ScreenRegistry";


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

      componentWillUnmount() {
        PropRegistry.release(this.props.screenInstanceID);
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

      componentWillUnmount() {
        PropRegistry.release(this.props.screenInstanceID);
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


export default {
    getRegisteredScreen,
    registerComponent,
};

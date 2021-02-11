import {AppRegistry} from 'react-native';

const registeredScreens = {};

export function registerScreen(screenID, generator) {
    registeredScreens[screenID] = generator;
    AppRegistry.registerComponent(screenID, generator);    
}

export function getRegisteredScreen(screenID) {
    const generator = registeredScreens[screenID];
    if (!generator) {
        console.error(`Navigation.getRegisteredScreen: ${screenID} used but not yet registered`);
        return undefined;
    }
    return generator();
}

export default {
    registerScreen,
    getRegisteredScreen
};

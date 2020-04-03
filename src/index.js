import _ from 'lodash';
import ComponentRegistry  from './ComponentRegistry';
import Navigator from './Navigator';
import {processButtons} from './util';
import platformSpecific from './platformSpecificDeprecated';

function push(navigatorID, params = {}) {
    var navigator = new Navigator(navigatorID, "", "");
    navigator.push(params);
}

function showModal(params = {}) {
    var navigator = new Navigator("", "", "");
    return navigator.showModal(params);
}

function dismissModal(params = {}) {
    var navigator = new Navigator("", "", "");
    return navigator.dismissModal(params);
}

function dismissAllModals(params = {}) {
    var navigator = new Navigator("", "", "");
    return navigator.dismissAllModals(params);
}

function showLightBox(params = {}) {
    var navigator = new Navigator("", "", "");
    return navigator.showLightBox(params);
}

function dismissLightBox(params = {}) {
    var navigator = new Navigator("", "", "");
    return navigator.dismissLightBox(params);
}

function registerComponent(screenID, generator, store = undefined, provider = undefined) {
    ComponentRegistry.registerComponent(screenID, generator, store, provider);

    const screenClass = ComponentRegistry.getRegisteredScreen(screenID);    
    let navigatorButtons = _.cloneDeep(screenClass.navigatorButtons);

    var params = navigatorButtons;
    if (params['leftButtons']) {
        processButtons(params['leftButtons']);
    }
    if (params['rightButtons']) {
        processButtons(params['rightButtons']);
    }

    console.log("register navigator buttons:", screenID, params);
    platformSpecific.registerNavigatorButtons(screenID, params);
}

var Navigation = {
    getRegisteredScreen:ComponentRegistry.getRegisteredScreen,
    registerComponent:registerComponent,
    push:push,
    showModal: showModal,
    dismissModal: dismissModal,
    dismissAllModals: dismissAllModals,
    showLightBox: showLightBox,
    dismissLightBox: dismissLightBox,
};


module.exports = {
    Navigation
};



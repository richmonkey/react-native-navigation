import ComponentRegistry  from './ComponentRegistry';
import Navigator from './Navigator';

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



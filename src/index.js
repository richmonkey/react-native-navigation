import nav from './Navigation';

import platformSpecific from './platformSpecificDeprecated';

function push(navigatorID, params = {}) {
    var navigator = {
        navigatorID:navigatorID,
        navigatorEventID:"",
        screenInstanceID:"",
    };
    platformSpecific.navigatorPush(navigator, params);
}

function showModal(params = {}) {
    return platformSpecific.showModal(params);
}

function dismissModal(params = {}) {
    return platformSpecific.dismissModal(params);
}

function dismissAllModals(params = {}) {
    return platformSpecific.dismissAllModals(params);
}

function showLightBox(params = {}) {
    return platformSpecific.showLightBox(params);
}

function dismissLightBox(params = {}) {
    return platformSpecific.dismissLightBox(params);
}

function registerComponent(screenID, generator, store = undefined, provider = undefined) {
    const com = generator();
    const buttons = com.navigatorButtons;
    if (buttons) {
        platformSpecific.registerNavigatorButtons(screenID, buttons);
    }
    nav.registerComponent(screenID, generator, store, provider);
}

var Navigation = {
    getRegisteredScreen:nav.getRegisteredScreen,
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



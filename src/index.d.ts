
export interface Navigation {
    registerComponent(screenID, generator, store?, provider?);
    push(navigatorID, params);
    showModal(params);
    dismissModal(params);
    dismissAllModals(params);
    showLightBox(params);
    dismissLightBox(params);
}


interface PushParam {
    title?:string;
    screen:string;
    navigatorStyle?:{navBarHidden};
    passProps;
}
export interface Navigator {
    push(param:PushParam);
    pop();
    setOnNavigatorEvent(cb);
    dismissModal();
}

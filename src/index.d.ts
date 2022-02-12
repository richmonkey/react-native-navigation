
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
    navigatorStyle?:{
        navBarHidden?,
        tabBarHidden?,
        statusBarHidden?
    };
    passProps;
}
export interface Navigator {
    push(param:PushParam);
    pop();
    setOnNavigatorEvent(cb);
    dismissModal();
}

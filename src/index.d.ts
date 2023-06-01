
export interface Navigation {
    registerComponent(screenID:string, generator:()=>any, store?, provider?):void;
    push(navigatorID:string, params: PushParam):void;
    showModal(params:ModalParam):void;
    dismissModal(params):void;
    dismissAllModals(params):void;
    showLightBox(params):void;
    dismissLightBox(params):void;
}

interface ModalParam {
    title:string,
    screen:string, 
    navigatorStyle?:{
        navBarHidden?:boolean,
        tabBarHidden?:boolean,
        statusBarHidden?:boolean,
    }, 
    passProps:any,
}

interface PushParam {
    title?:string;
    screen:string;
    navigatorStyle?:{
        navBarHidden?:boolean,
        tabBarHidden?:boolean,
        statusBarHidden?:boolean,
    };
    passProps:any;
}
export interface Navigator {
    push(param:PushParam):void;
    pop():void;
    setOnNavigatorEvent(cb:(event:{id:string, type:string})=>void):void;
    dismissModal():void;
}

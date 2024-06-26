#import "RCCNavigationController.h"
#import "RCCViewController.h"
#import "RCCManager.h"
#import <React/RCTEventDispatcher.h>
#import <React/RCTConvert.h>
#import <objc/runtime.h>
#import "RCCTitleViewHelper.h"



@implementation RCCNavigationController

NSString const *CALLBACK_ASSOCIATED_KEY = @"RCCNavigationController.CALLBACK_ASSOCIATED_KEY";
NSString const *CALLBACK_ASSOCIATED_ID = @"RCCNavigationController.CALLBACK_ASSOCIATED_ID";

- (instancetype)initWithRootViewController:(UIViewController *)rootViewController componentID:(NSString*)id{
    self = [super initWithRootViewController:rootViewController];
    if (self) {
        self.componentID = id;
        self.delegate = self;
        self.navigationBar.translucent = NO; // default
        
        
        if (self.componentID.length > 0)
        {
            [[RCCManager sharedInstance] registerController:self componentId:self.componentID componentType:@"NavigationControllerIOS"];
        }
        
    }
    return self;
}

- (instancetype)initWithProps:(NSDictionary *)props globalProps:(NSDictionary*)globalProps bridge:(RCTBridge *)bridge
{
    NSString *component = props[@"component"];
    if (!component) return nil;
    
    NSDictionary *passProps = props[@"passProps"];
    NSDictionary *navigatorStyle = props[@"style"];
    
    UIViewController *viewController = nil;
    
    Class cls = [[RCCManager sharedInstance] getComponent:component];
    if (cls) {
        viewController = [[cls alloc] initWithComponent:component passProps:passProps navigatorStyle:navigatorStyle bridge:bridge];
    } else {
        viewController = [[RCCViewController alloc] initWithComponent:component passProps:passProps navigatorStyle:navigatorStyle bridge:bridge];
    }
    
    if (!viewController) return nil;
    
    NSArray *leftButtons = props[@"leftButtons"];
    if (leftButtons)
    {
        [self setButtons:leftButtons viewController:viewController side:@"left" animated:NO];
    }
    
    NSArray *rightButtons = props[@"rightButtons"];
    if (rightButtons)
    {
        [self setButtons:rightButtons viewController:viewController side:@"right" animated:NO];
    }
    
    self = [super initWithRootViewController:viewController];
    if (!self) return nil;
    self.delegate = self;
    
    self.navigationBar.translucent = NO; // default
    
    [self processTitleView:viewController
                     props:props
                     style:navigatorStyle];
    
    
    // register the controller if we have an id
     NSString *componentId = props[@"id"];
     if (componentId.length > 0)
     {
         self.componentID = componentId;
         [[RCCManager sharedInstance] registerController:self componentId:self.componentID componentType:@"NavigationControllerIOS"];
     }
    
    
    return self;
}

-(void)dealloc {
    if (self.componentID.length > 0) {
        [[RCCManager sharedInstance] unregisterController:self];
    }
}

- (void)push:(NSString*)component
       props:(NSDictionary*)props
       title:(NSString*)title
tabBarHidden:(BOOL)tabbarHidden
      bridge:(RCTBridge*)bridge {
    NSDictionary *params = @{
        @"passProps":props,
        @"title":title,
        @"component":component,
        @"style":@{@"tabBarHidden":@(tabbarHidden)}
    };
    [self performAction:@"push" actionParams:params bridge:bridge];
}

- (void)performAction:(NSString*)performAction actionParams:(NSDictionary*)actionParams bridge:(RCTBridge *)bridge {
    BOOL animated = actionParams[@"animated"] ? [actionParams[@"animated"] boolValue] : YES;
    
    // push
    if ([performAction isEqualToString:@"push"])
    {
        NSString *component = actionParams[@"component"];
        if (!component) return;
        
        NSDictionary *passProps = actionParams[@"passProps"];
        NSDictionary *navigatorStyle = actionParams[@"style"];
        
        // merge the navigatorStyle of our parent
        if ([self.topViewController isKindOfClass:[RCCViewController class]])
        {
            RCCViewController *parent = (RCCViewController*)self.topViewController;
            NSMutableDictionary *mergedStyle = [NSMutableDictionary dictionaryWithDictionary:parent.navigatorStyle];
            
            // there are a few styles that we don't want to remember from our parent (they should be local)
            [mergedStyle removeObjectForKey:@"navBarHidden"];
            [mergedStyle removeObjectForKey:@"statusBarHidden"];
            [mergedStyle removeObjectForKey:@"navBarHideOnScroll"];
            [mergedStyle removeObjectForKey:@"drawUnderNavBar"];
            [mergedStyle removeObjectForKey:@"drawUnderTabBar"];
            [mergedStyle removeObjectForKey:@"statusBarBlur"];
            [mergedStyle removeObjectForKey:@"navBarBlur"];
            [mergedStyle removeObjectForKey:@"navBarTranslucent"];
            [mergedStyle removeObjectForKey:@"statusBarHideWithNavBar"];
            [mergedStyle removeObjectForKey:@"statusBarTextColorSchemeSingleScreen"];
            
            [mergedStyle addEntriesFromDictionary:navigatorStyle];
            navigatorStyle = mergedStyle;
        }
        
        UIViewController *viewController;
        
        Class cls = [[RCCManager sharedInstance] getComponent:component];
        if (cls) {
            viewController = [[cls alloc] initWithComponent:component passProps:passProps navigatorStyle:navigatorStyle bridge:bridge];
        } else {
            viewController = [[RCCViewController alloc] initWithComponent:component passProps:passProps navigatorStyle:navigatorStyle bridge:bridge];
        }

        [self processTitleView:viewController
                         props:actionParams
                         style:navigatorStyle];
        
        NSString *backButtonTitle = actionParams[@"backButtonTitle"];
        if (backButtonTitle)
        {
            UIBarButtonItem *backItem = [[UIBarButtonItem alloc] initWithTitle:backButtonTitle
                                                                         style:UIBarButtonItemStylePlain
                                                                        target:nil
                                                                        action:nil];
            
            self.topViewController.navigationItem.backBarButtonItem = backItem;
        }
        else
        {
            self.topViewController.navigationItem.backBarButtonItem = nil;
        }
        
        NSNumber *backButtonHidden = actionParams[@"backButtonHidden"];
        BOOL backButtonHiddenBool = backButtonHidden ? [backButtonHidden boolValue] : NO;
        if (backButtonHiddenBool)
        {
            viewController.navigationItem.hidesBackButton = YES;
        }
        
        NSArray *leftButtons = actionParams[@"leftButtons"];
        if (leftButtons)
        {
            [self setButtons:leftButtons viewController:viewController side:@"left" animated:NO];
        }
        
        NSArray *rightButtons = actionParams[@"rightButtons"];
        if (rightButtons)
        {
            [self setButtons:rightButtons viewController:viewController side:@"right" animated:NO];
        }

        
        [self pushViewController:viewController animated:animated];
        return;
    }
    
    // pop
    if ([performAction isEqualToString:@"pop"])
    {
        [self popViewControllerAnimated:animated];
        return;
    }
    
    // popToRoot
    if ([performAction isEqualToString:@"popToRoot"])
    {
        [self popToRootViewControllerAnimated:animated];
        return;
    }
    
    // resetTo
    if ([performAction isEqualToString:@"resetTo"])
    {
        NSString *component = actionParams[@"component"];
        if (!component) return;
        
        NSDictionary *passProps = actionParams[@"passProps"];
        NSDictionary *navigatorStyle = actionParams[@"style"];
        
        RCCViewController *viewController = [[RCCViewController alloc] initWithComponent:component passProps:passProps navigatorStyle:navigatorStyle bridge:bridge];
        
        [self processTitleView:viewController
                         props:actionParams
                         style:navigatorStyle];
        NSArray *leftButtons = actionParams[@"leftButtons"];
        if (leftButtons)
        {
            [self setButtons:leftButtons viewController:viewController side:@"left" animated:NO];
        }
        
        NSArray *rightButtons = actionParams[@"rightButtons"];
        if (rightButtons)
        {
            [self setButtons:rightButtons viewController:viewController side:@"right" animated:NO];
        }
        
        BOOL animated = actionParams[@"animated"] ? [actionParams[@"animated"] boolValue] : YES;
        
        [self setViewControllers:@[viewController] animated:animated];
        return;
    }
    
    // setButtons
    if ([performAction isEqualToString:@"setButtons"])
    {
        NSArray *buttons = actionParams[@"buttons"];
        BOOL animated = actionParams[@"animated"] ? [actionParams[@"animated"] boolValue] : YES;
        NSString *side = actionParams[@"side"] ? actionParams[@"side"] : @"left";
        
        [self setButtons:buttons viewController:self.topViewController side:side animated:animated];
        return;
    }
    
    //enable or disable left/right button
    if ([performAction isEqualToString:@"enableButton"]) {
        BOOL enabled = [actionParams[@"enabled"] boolValue];
        NSString *side = [actionParams objectForKey:@"side"];
        if ([side isEqualToString:@"left"]) {
            self.topViewController.navigationItem.leftBarButtonItem.enabled = enabled;
        } else if ([side isEqualToString:@"right"]) {
            self.topViewController.navigationItem.rightBarButtonItem.enabled = enabled;
        }
        return;
    }
    // setTitle
    if ([performAction isEqualToString:@"setTitle"] || [performAction isEqualToString:@"setTitleImage"])
    {
        NSDictionary *navigatorStyle = actionParams[@"style"];
        [self processTitleView:self.topViewController
                         props:actionParams
                         style:navigatorStyle];
        return;
    }
    
    // toggleNavBar
    if ([performAction isEqualToString:@"setHidden"]) {
        NSNumber *animated = actionParams[@"animated"];
        BOOL animatedBool = animated ? [animated boolValue] : YES;
        
        NSNumber *setHidden = actionParams[@"hidden"];
        
        RCCViewController *topViewController = ((RCCViewController*)self.topViewController);
        topViewController.navigatorStyle[@"navBarHidden"] = setHidden;
        [topViewController setNavBarVisibilityChange:animatedBool];
        
    }
}

-(void)onButtonPress:(UIBarButtonItem*)barButtonItem
{
    NSString *callbackId = objc_getAssociatedObject(barButtonItem, &CALLBACK_ASSOCIATED_KEY);
    if (!callbackId) return;
    NSString *buttonId = objc_getAssociatedObject(barButtonItem, &CALLBACK_ASSOCIATED_ID);
    [[[RCCManager sharedInstance] getBridge].eventDispatcher sendAppEventWithName:callbackId body:@
     {
         @"type": @"NavBarButtonPress",
         @"id": buttonId ? buttonId : [NSNull null]
     }];
}

-(void)setButtons:(NSArray*)buttons viewController:(UIViewController*)viewController side:(NSString*)side animated:(BOOL)animated
{
    NSMutableArray *barButtonItems = [NSMutableArray new];
    for (NSDictionary *button in buttons)
    {
        NSString *title = button[@"title"];
        UIImage *iconImage = nil;
        id icon = button[@"icon"];
        if (icon) iconImage = [RCTConvert UIImage:icon];
        
        UIBarButtonItem *barButtonItem;
        if (iconImage)
        {
            barButtonItem = [[UIBarButtonItem alloc] initWithImage:iconImage style:UIBarButtonItemStylePlain target:self action:@selector(onButtonPress:)];
        }
        else if (title)
        {
            barButtonItem = [[UIBarButtonItem alloc] initWithTitle:title style:UIBarButtonItemStylePlain target:self action:@selector(onButtonPress:)];
        }
        else continue;
        objc_setAssociatedObject(barButtonItem, &CALLBACK_ASSOCIATED_KEY, button[@"onPress"], OBJC_ASSOCIATION_RETAIN_NONATOMIC);
        [barButtonItems addObject:barButtonItem];
        
        NSString *buttonId = button[@"id"];
        if (buttonId)
        {
            objc_setAssociatedObject(barButtonItem, &CALLBACK_ASSOCIATED_ID, buttonId, OBJC_ASSOCIATION_RETAIN_NONATOMIC);
        }
        
        NSNumber *disabled = button[@"disabled"];
        BOOL disabledBool = disabled ? [disabled boolValue] : NO;
        if (disabledBool) {
            [barButtonItem setEnabled:NO];
        }
        
        NSNumber *disableIconTintString = button[@"disableIconTint"];
        BOOL disableIconTint = disableIconTintString ? [disableIconTintString boolValue] : NO;
        if (disableIconTint) {
            [barButtonItem setImage:[barButtonItem.image imageWithRenderingMode:UIImageRenderingModeAlwaysOriginal]];
        }
        
        NSString *testID = button[@"testID"];
        if (testID)
        {
            barButtonItem.accessibilityIdentifier = testID;
        }
    }
    
    if ([side isEqualToString:@"left"])
    {
        [viewController.navigationItem setLeftBarButtonItems:barButtonItems animated:animated];
    }
    
    if ([side isEqualToString:@"right"])
    {
        [viewController.navigationItem setRightBarButtonItems:barButtonItems animated:animated];
    }
}


-(void)processTitleView:(UIViewController*)viewController
                  props:(NSDictionary*)props
                  style:(NSDictionary*)style
{
    
    RCCTitleViewHelper *titleViewHelper = [[RCCTitleViewHelper alloc] init:viewController
                                                      navigationController:self
                                                                     title:props[@"title"]
                                                                  subtitle:props[@"subtitle"]
                                                            titleImageData:props[@"titleImage"]];
    
    [titleViewHelper setup:style];
    
}

- (UIStatusBarStyle)preferredStatusBarStyle {
    return [self.topViewController preferredStatusBarStyle];
}


#pragma mark - UINavigationControllerDelegate


-(void)navigationController:(UINavigationController *)navigationController willShowViewController:(UIViewController *)viewController animated:(BOOL)animated {
    [viewController setNeedsStatusBarAppearanceUpdate];
}


@end

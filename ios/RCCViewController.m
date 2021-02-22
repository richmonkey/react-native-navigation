#import "RCCViewController.h"
#import "RCCNavigationController.h"

#import <React/RCTRootView.h>
#import "RCCManager.h"
#import <React/RCTConvert.h>

const NSInteger BLUR_STATUS_TAG = 78264801;
const NSInteger BLUR_NAVBAR_TAG = 78264802;
const NSInteger TRANSPARENT_NAVBAR_TAG = 78264803;

static volatile int64_t _id = 0;

@interface RCCViewController() <UIGestureRecognizerDelegate>
@property (nonatomic) BOOL _hidesBottomBarWhenPushed;
@property (nonatomic) BOOL _statusBarHideWithNavBar;
@property (nonatomic) BOOL _statusBarHidden;
@property (nonatomic) BOOL _statusBarTextColorSchemeLight;
@property (nonatomic, strong) NSDictionary *originalNavBarImages;
@property (nonatomic, strong) UIImageView *navBarHairlineImageView;
@property (nonatomic, weak) id <UIGestureRecognizerDelegate> originalInteractivePopGestureDelegate;

@property(nonatomic) NSMutableDictionary *props;
@property(nonatomic) RCTBridge *bridge;
@property(nonatomic, copy) NSString *component;

@property(nonatomic, copy) NSString *componentID;

@end

@implementation RCCViewController

+(NSString*)uniqueId:(NSString*)prefix {
    //todo atomic int
    @synchronized(self) {
        int64_t newId = ++_id;
        return [NSString stringWithFormat:@"%@%lld", prefix ? prefix : @"", newId];
    }
}

-(UIImageView *)navBarHairlineImageView {
    if (!_navBarHairlineImageView) {
        _navBarHairlineImageView = [self findHairlineImageViewUnder:self.navigationController.navigationBar];
    }
    return _navBarHairlineImageView;
}


- (instancetype)initWithComponent:(NSString *)component passProps:(NSDictionary *)passProps navigatorStyle:(NSDictionary*)navigatorStyle bridge:(RCTBridge *)bridge
{
    self = [super init];
    if (self) {
        self.props = [NSMutableDictionary dictionaryWithDictionary:passProps];
        self.bridge = bridge;
        self.component = component;

        self.navigatorStyle = [NSMutableDictionary dictionaryWithDictionary:navigatorStyle];
        [self setStyleOnInit];
        
        NSString *componentID = [self.props objectForKey:@"screenInstanceID"];
        if (componentID.length > 0) {
            self.componentID = componentID;
             [[RCCManager sharedInstance] registerController:self componentId:self.componentID componentType:@"ViewControllerIOS"];
        }
    }


    return self;
}

- (void)dealloc
{
    self.view = nil;
    if (self.componentID.length > 0) {
        [[RCCManager sharedInstance] unregisterController:self];
    }
}



- (void)loadView
{
    RCTRootView *reactView = [[RCTRootView alloc] initWithBridge:self.bridge moduleName:self.component initialProperties:self.props];
    self.view = reactView;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    if (@available(iOS 13.0,*)) {
        self.view.backgroundColor = [UIColor systemBackgroundColor];
    }
    self.edgesForExtendedLayout = UIRectEdgeNone; // default
    self.automaticallyAdjustsScrollViewInsets = NO; // default
}




- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
}


// only styles that can't be set on willAppear should be set here
- (void)setStyleOnInit
{
    NSNumber *tabBarHidden = self.navigatorStyle[@"tabBarHidden"];
    BOOL tabBarHiddenBool = tabBarHidden ? [tabBarHidden boolValue] : NO;
    if (tabBarHiddenBool)
    {
        self._hidesBottomBarWhenPushed = YES;
    }
    else
    {
        self._hidesBottomBarWhenPushed = NO;
    }
    
    NSNumber *statusBarHideWithNavBar = self.navigatorStyle[@"statusBarHideWithNavBar"];
    BOOL statusBarHideWithNavBarBool = statusBarHideWithNavBar ? [statusBarHideWithNavBar boolValue] : NO;
    if (statusBarHideWithNavBarBool)
    {
        self._statusBarHideWithNavBar = YES;
    }
    else
    {
        self._statusBarHideWithNavBar = NO;
    }
    
    NSNumber *statusBarHidden = self.navigatorStyle[@"statusBarHidden"];
    BOOL statusBarHiddenBool = statusBarHidden ? [statusBarHidden boolValue] : NO;
    if (statusBarHiddenBool)
    {
        self._statusBarHidden = YES;
    }
    else
    {
        self._statusBarHidden = NO;
    }
}

- (BOOL)hidesBottomBarWhenPushed
{
    if (!self._hidesBottomBarWhenPushed) return NO;
    return (self.navigationController.topViewController == self);
}

- (BOOL)prefersStatusBarHidden
{
    if (self._statusBarHidden)
    {
        return YES;
    }
    if (self._statusBarHideWithNavBar)
    {
        return self.navigationController.isNavigationBarHidden;
    }
    else
    {
        return NO;
    }
}

- (void)setNavBarVisibilityChange:(BOOL)animated {
    [self.navigationController setNavigationBarHidden:[self.navigatorStyle[@"navBarHidden"] boolValue] animated:animated];
}


- (UIStatusBarStyle)preferredStatusBarStyle
{
    if (self._statusBarTextColorSchemeLight)
    {
        return UIStatusBarStyleLightContent;
    }
    else
    {
        return UIStatusBarStyleDefault;
    }
}

- (UIImageView *)findHairlineImageViewUnder:(UIView *)view {
    if ([view isKindOfClass:UIImageView.class] && view.bounds.size.height <= 1.0) {
        return (UIImageView *)view;
    }
    for (UIView *subview in view.subviews) {
        UIImageView *imageView = [self findHairlineImageViewUnder:subview];
        if (imageView) {
            return imageView;
        }
    }
    return nil;
}


#pragma mark - NewRelic

- (NSString*) customNewRelicInteractionName
{
    NSString *interactionName = nil;
    
    if (self.view != nil && [self.view isKindOfClass:[RCTRootView class]])
    {
        NSString *moduleName = ((RCTRootView*)self.view).moduleName;
        if(moduleName != nil)
        {
            interactionName = [NSString stringWithFormat:@"RCCViewController: %@", moduleName];
        }
    }
    
    if (interactionName == nil)
    {
        interactionName = [NSString stringWithFormat:@"RCCViewController with title: %@", self.title];
    }
    
    return interactionName;
}


@end

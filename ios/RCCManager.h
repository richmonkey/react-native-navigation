#import <Foundation/Foundation.h>
#import <React/RCTBridgeModule.h>
#import <UIKit/UIKit.h>

@interface RCCManager : NSObject

+ (instancetype)sharedInstance;


-(void)initBridgeWithBundleURL:(NSURL *)bundleURL;
-(void)initBridgeWithBundleURL:(NSURL *)bundleURL launchOptions:(NSDictionary *)launchOptions;
-(RCTBridge*)getBridge;
-(UIWindow*)getAppWindow;

-(void)registerController:(UIViewController*)controller componentId:(NSString*)componentId componentType:(NSString*)componentType;
-(id)getControllerWithId:(NSString*)componentId componentType:(NSString*)componentType;
-(void)unregisterController:(UIViewController*)vc;
-(void)clearModuleRegistry;


-(void)registerComponent:(NSString*)component class:(Class)cls;
-(Class)getComponent:(NSString*)component;

-(void)registerComponentNavigatorButtons:(NSString*)component navigatorButtons:(NSDictionary*)buttons;
-(NSDictionary*)getComponentNavigatorButtons:(NSString*)component;
@end

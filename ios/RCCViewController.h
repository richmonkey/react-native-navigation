#import <UIKit/UIKit.h>
#import <React/RCTBridge.h>

extern NSString* const RCCViewControllerCancelReactTouchesNotification;

@interface RCCViewController : UIViewController

@property (nonatomic) NSMutableDictionary *navigatorStyle;
@property (nonatomic) BOOL navBarHidden;


- (instancetype)initWithProps:(NSDictionary *)props globalProps:(NSDictionary *)globalProps bridge:(RCTBridge *)bridge;
- (instancetype)initWithComponent:(NSString *)component passProps:(NSDictionary *)passProps navigatorStyle:(NSDictionary*)navigatorStyle globalProps:(NSDictionary *)globalProps bridge:(RCTBridge *)bridge;
- (void)setStyleOnInit;
- (void)setNavBarVisibilityChange:(BOOL)animated;
@end



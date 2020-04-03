#import <UIKit/UIKit.h>
#import <React/RCTBridge.h>


@protocol RCCViewControllerDelegate <NSObject>
-(void)onResult:(NSDictionary*)result;
@end

@interface RCCViewController : UIViewController

+(NSString*)uniqueId:(NSString*)prefix;

@property (nonatomic) NSMutableDictionary *navigatorStyle;
@property (nonatomic) BOOL navBarHidden;
@property (nonatomic, weak) id<RCCViewControllerDelegate> delegate;


- (instancetype)initWithComponent:(NSString *)component passProps:(NSDictionary *)passProps navigatorStyle:(NSDictionary*)navigatorStyle bridge:(RCTBridge *)bridge;
- (void)setStyleOnInit;
- (void)setNavBarVisibilityChange:(BOOL)animated;
@end



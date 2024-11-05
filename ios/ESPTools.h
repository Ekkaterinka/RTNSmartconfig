#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN
@interface ESPTools : NSObject

+ (nullable NSString *)getCurrentWiFiSsid;
+ (nullable NSString *)getCurrentBSSID;

NS_ASSUME_NONNULL_END
@end

#import "RTNSmartconfig.h"

@implementation RTNSmartconfig

RCT_EXPORT_MODULE()

- (void)add:(double)a b:(double)b resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    NSNumber *result = [[NSNumber alloc] initWithInteger:a+b];
    resolve(result);
}

- (void) getConnectedInfo:(RCTResponseSenderBlock) successCallback errorCallback:(RCTResponseErrorBlock)errorCallback {
    NetworkStatus networkStatus = [[ESPReachability reachabilityForInternetConnection] currentReachabilityStatus];
    if (networkStatus == ReachableViaWiFi) {
        NSDictionary *wifiDic = [NSDictionary dictionaryWithObjectsAndKeys:
                                 ESPTools.getCurrentWiFiSsid, @"ssid",
                                 ESPTools.getCurrentBSSID,@"bssid",
                                 @"Connected", @"state",
                                 nil];
        
        successCallback(@[[NSNull null],wifiDic]);
    } else {
        NSDictionary *wifiDic = @{
            @"state":@"NotConnected"
        };
        successCallback(@[wifiDic,[NSNull null]]);
    }
}

- (void) checkLocation:(RCTResponseSenderBlock) successCallback errorCallback:(RCTResponseErrorBlock)errorCallback {

    NSString* message = @"";

    if (@available(iOS 13.0, *)) {
        CLLocationManager* cllocation = [[CLLocationManager alloc] init];

        switch ([CLLocationManager authorizationStatus]) {
            case kCLAuthorizationStatusDenied:
            case kCLAuthorizationStatusRestricted: {
                UIViewController* rootController = [UIApplication sharedApplication].delegate.window.rootViewController;

                UIAlertController* alert = [UIAlertController alertControllerWithTitle:@"У приложения нет доступа к геопозиции."
                                                                               message:@"Разрешите доступ к Вашей геопозиции в настройках устройства."
                                                                        preferredStyle:UIAlertControllerStyleAlert];

                UIAlertAction* settingsAction = [UIAlertAction actionWithTitle:@"Перейти в настройки"
                                                                         style:UIAlertActionStyleDefault
                                                                       handler:^(UIAlertAction* action)
                                                 {
                    NSURL *settingsURL = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
                    UIApplication *application = [UIApplication sharedApplication];
                    [application openURL:settingsURL options:@{} completionHandler:nil];
                }];

                [alert addAction:settingsAction];

                UIAlertAction* cancelAction = [UIAlertAction actionWithTitle:@"Позже"
                                                                       style:UIAlertActionStyleCancel
                                                                     handler:^(UIAlertAction* action)
                                               {
                    NSString *message = [NSString stringWithFormat:@"NOT_GRANTED"];
                    successCallback(@[message,[NSNull null]]);
                }];

                [alert addAction:cancelAction];

                [rootController presentViewController:alert animated:YES completion:nil];
                [NSTimer scheduledTimerWithTimeInterval:5 repeats:NO block:^(NSTimer * _Nonnull timer) {
                    [rootController dismissViewControllerAnimated:YES completion:nil];
                }];


                break;}
            case kCLAuthorizationStatusNotDetermined: {
                [cllocation requestWhenInUseAuthorization];
                message = [NSString stringWithFormat:@"NOT_DETERMINATED"];
                successCallback(@[message,[NSNull null]]);
                break;}
            case kCLAuthorizationStatusAuthorizedAlways:
            case kCLAuthorizationStatusAuthorizedWhenInUse:{
                message = [NSString stringWithFormat:@"GRANTED"];
                successCallback(@[[NSNull null],message]);
                break;}
            default:
                break;
        }
    }
}

- (void)startEspTouch:(NSString *)apSsid
              apBssid:(NSString *)apBssid
           apPassword:(NSString *)apPassword
     success_callback:(RCTResponseSenderBlock)success_callback
        fail_callback:(RCTResponseSenderBlock)fail_callback {
    
}

- (void)stopEspTouch:(RCTResponseSenderBlock)successCallback
        failCallback:(RCTResponseSenderBlock)failCallback {
    
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeRTNSmartconfigSpecJSI>(params);
}

@end

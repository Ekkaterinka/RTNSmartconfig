#import "RTNSmartconfig.h"

@implementation RTNSmartconfig

RCT_EXPORT_MODULE()

- (void)checkLocation: (RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
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
                    reject(@"checkLocation",@"NOT_GRANTED", nil);
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
                reject(@"checkLocation",@"NOT_DETERMINATED", nil);
                break;}
            case kCLAuthorizationStatusAuthorizedAlways:
            case kCLAuthorizationStatusAuthorizedWhenInUse:{
                message = [NSString stringWithFormat:@"GRANTED"];
                resolve(message);
                break;}
            default:
                break;
        }
    }
}

- (void)getConnectedInfo:(RCTResponseSenderBlock)successCallback failCallback:(RCTResponseSenderBlock)failCallback {
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

- (void)startEspTouch:(NSString *)apSsid apBssid:(NSString *)apBssid apPassword:(NSString *)apPassword resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {

}

- (void)stopEspTouch:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {

}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeRTNSmartconfigSpecJSI>(params);
}

@end

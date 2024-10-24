#import "RTNSmartconfig.h"

@implementation RTNSmartconfig

RCT_EXPORT_MODULE()

- (void)add:(double)a b:(double)b resolve:(RCTPromiseResolveBlock)resolve reject:(RCTPromiseRejectBlock)reject {
    NSNumber *result = [[NSNumber alloc] initWithInteger:a+b];
    resolve(result);
}

- (void) getConnectedInfo:(CDVInvokedUrlCommand *)command{
    NetworkStatus networkStatus = [[ESPReachability reachabilityForInternetConnection] currentReachabilityStatus];
    if (networkStatus == ReachableViaWiFi) {
        NSDictionary *wifiDic = [NSDictionary dictionaryWithObjectsAndKeys:
                                 ESPTools.getCurrentWiFiSsid, @"ssid",
                                 ESPTools.getCurrentBSSID,@"bssid",
                                 @"Connected", @"state",
                                 nil];

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:wifiDic];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

    } else {
        NSDictionary *wifiDic = @{
            @"state":@"NotConnected"
        };

        CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:wifiDic];
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
}

- (void) checkLocation:(RCTResponseSenderBlock) successCallback (RCTResponseErrorBlock) errorCallback{

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
                    errorCallback = [message];

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
                errorCallback = [message];
                break;}
            case kCLAuthorizationStatusAuthorizedAlways:
            case kCLAuthorizationStatusAuthorizedWhenInUse:{
                message = [NSString stringWithFormat:@"GRANTED"];
                successCallback = [message];
                break;}
            default:
                break;
        }
    }
}

- (std::shared_ptr<facebook::react::TurboModule>)getTurboModule:
    (const facebook::react::ObjCTurboModule::InitParams &)params
{
    return std::make_shared<facebook::react::NativeRTNSmartconfigSpecJSI>(params);
}

@end

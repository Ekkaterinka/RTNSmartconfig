
//  ESPTools.m
//  rtn-smartconfig
//
//  Created by Ilya Skriblovsky on 24.10.2024.
//

#import <Foundation/Foundation.h>
#import "ESPTools.h"
#import <ifaddrs.h>
#import <arpa/inet.h>
#import <net/if.h>
#import <SystemConfiguration/CaptiveNetwork.h>

#define IOS_CELLULAR    @"pdp_ip0"
#define IOS_WIFI        @"en0"
#define IOS_VPN         @"utun0"
#define IP_ADDR_IPv4    @"ipv4"
#define IP_ADDR_IPv6    @"ipv6"

@implementation ESPTools

+ (nullable NSString *)getCurrentWiFiSsid {
    NSArray *ifs = (__bridge_transfer id)CNCopySupportedInterfaces();
    id info = nil;
    for (NSString *ifnam in ifs) {
        info = (__bridge_transfer id)CNCopyCurrentNetworkInfo((__bridge CFStringRef)ifnam);
        if (info && [info count]) {
            break;
        }
    }
    // Key: BSSID, SSID, SSIDDATA
    return [(NSDictionary*)info objectForKey:@"SSID"];
}

+ (nullable NSString *)getCurrentBSSID {
    NSArray *ifs = (__bridge_transfer id)CNCopySupportedInterfaces();
    id info = nil;
    for (NSString *ifnam in ifs) {
        info = (__bridge_transfer id)CNCopyCurrentNetworkInfo((__bridge CFStringRef)ifnam);
        if (info && [info count]) {
            break;
        }
    }
    // Key: BSSID, SSID, SSIDDATA
    return [(NSDictionary*)info objectForKey:@"BSSID"];
}

@end

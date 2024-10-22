import {TurboModule, TurboModuleRegistry} from "react-native";

export interface Spec extends TurboModule {
    add(a: number, b: number): Promise<number>;

    checkLocation(
        successCallback: (success: string) => void,
        failCallback: (error: string) => void
    ): void;

    getConnectedInfo(successCallback: (result: {
        ip: string,
        is5G: boolean,
        ssid: string,
        bssid: string,
        state: string
    }) => void, failCallback: (error: string) => void): void;

    startEspTouch(
        apSsid: string,
        apBssid: string,
        apPassword: string,
        success_callback: (success: { bssid: string; ip: string }) => void,
        fail_callback: (error: string) => void): void;

    stopEspTouch(
        successCallback: (success: string) => void,
        failCallback: (error: string) => void
    ): void;
}

export default TurboModuleRegistry.get<Spec>("RTNSmartconfig") as Spec | null;

import {TurboModule, TurboModuleRegistry} from "react-native";

export interface Spec extends TurboModule {
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
        apPassword: string): Promise<string>;

    stopEspTouch(): Promise<string>;
}

export default TurboModuleRegistry.get<Spec>("RTNSmartconfig") as Spec | null;

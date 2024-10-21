import { TurboModule, TurboModuleRegistry } from "react-native";

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
  //
  // scan(options: any, successCallback: (success: string) => void, failCallbackfailCallback: (error: string) => void): void,
  //
  // connect(ssid: string, password: string, successCallback: (success: string) => void, failCallback: (error: string) => void): void,
  //
  // disconnect(ssid: string, successCallback: (success: string) => void, failCallback: (error: string) => void): void
}

export default TurboModuleRegistry.get<Spec>("RTNSmartconfig") as Spec | null;

import UIKit
import Capacitor

@objc(BluetoothMeshPlugin)
public class BlueToothMeshPlugin: CAPPlugin, CAPBridgedPlugin {
  public let identifier = "BluetoothMeshPlugin"
  public let jsName = "BluetoothMesh"
  public let pluginMethods: [CAPPluginMethod] = [
    CAPPluginMethod(name: "init", returnType: CAPPluginReturnPromise),
  ]

  private let videoStream;

  @objc func init(_ call: CAPPluginCall) {
    call.resolve();
  }
}

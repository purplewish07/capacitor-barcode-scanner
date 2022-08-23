import Foundation
import Capacitor
import AVFoundation


@objc(BarcodeScannerPlugin)
public class BarcodeScannerPlugin: CAPPlugin, BarcodeScannerDelegate {
    
    var call: CAPPluginCall?
    var barcodeScanner: BarcodeScannerViewController?

    @objc func scan(_ call: CAPPluginCall) {
        call.keepAlive = true
        self.call = call
        
        if let isSim = bridge?.isSimEnvironment, isSim || !UIImagePickerController.isSourceTypeAvailable(UIImagePickerController.SourceType.camera) {
          call.reject("Camera not available while running in Simulator")
          return
        }
        
        DispatchQueue.main.async {
            self.barcodeScanner = BarcodeScannerViewController()
            self.barcodeScanner!.delegate = self;
         }
        
        AVCaptureDevice.requestAccess(for: .video) { granted in
            if granted {
              DispatchQueue.main.async {

                self.bridge?.viewController?.present(self.barcodeScanner!, animated: true, completion: nil)
                
              }
            } else {
                call.reject("User denied access to camera")
            }
        }
    }
    
    func didCancelled(){
        self.call?.resolve(["result":false]);
    }
    
    func didFoundCode(code: String) {
        self.call?.resolve(["result":true,"code": code]);
    }
}

//
//  BarcodeScannerDelegate.swift
//  CapacitorBarcodeScanner
//
//  Created by Jorge Videla on 22-08-22.
//

import Foundation

protocol BarcodeScannerDelegate: AnyObject {
    func didFoundCode(code: String)
    
    func didFoundCodes(codes:[String])
    
    func didCancelled()
}

import { WebPlugin } from '@capacitor/core';
import type { BarcodeScannerPlugin, MultiScanResult, ScanResult } from './definitions';
export declare class BarcodeScannerWeb extends WebPlugin implements BarcodeScannerPlugin {
    multiScan(): Promise<MultiScanResult>;
    scan(): Promise<ScanResult>;
}

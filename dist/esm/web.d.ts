import { WebPlugin } from '@capacitor/core';
import type { BarcodeScannerPlugin, ScanResult } from './definitions';
export declare class BarcodeScannerWeb extends WebPlugin implements BarcodeScannerPlugin {
    scan(): Promise<ScanResult>;
}

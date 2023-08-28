export interface BarcodeScannerPlugin {
    /**
     * Start scan screen
     * This promise will fail if permission for camera is denied
     */
    scan(): Promise<ScanResult>;
    /**
     * Start scan screen
     * the difference vs scan is this will not close automatically, and continues scannning multiple codes
     * ! Added in v1.1.1
     */
    multiScan(opts?: MultiScanOptions): Promise<MultiScanResult>;
}
/**
 * Represents a Scan Result
 */
export interface ScanResult {
    /**
     * sucess status, its true when scanner got code
     */
    result: boolean;
    /**
     * scanned code
     */
    code?: string;
}
/**
 * Represents a Multiple scan result
 */
export interface MultiScanResult {
    result: boolean;
    count: number;
    codes: string[];
}
export interface MultiScanOptions {
    /**
     * Max quantity of codes to scan, when reached the amount activity or viewcontroller will close and return the scanned codes, it defaults to 9999
     */
    maxScans?: number;
}

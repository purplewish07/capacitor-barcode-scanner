export interface BarcodeScannerPlugin {
    /**
     * Start scan screen
     * This promise will fail if permission for camera is denied
     */
    scan(): Promise<ScanResult>;
    /**
     * Start scan screen
     * the difference vs scan is this will not close automatically, and continues scannning multiple codes
     */
    multiScan(): Promise<MultiScanResult>;
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

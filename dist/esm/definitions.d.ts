export interface BarcodeScannerPlugin {
    /**
     * Start scan screen
     * This promise will fail if permission for camera is denied
     */
    scan(): Promise<ScanResult>;
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

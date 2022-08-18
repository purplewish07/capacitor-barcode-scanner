export interface BarcodeScannerPlugin {
  scan():Promise<ScanResult>;
}


export interface ScanResult{
  result:boolean;
  value?:string|undefined;
}
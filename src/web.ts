import { WebPlugin } from '@capacitor/core';

import type { BarcodeScannerPlugin, MultiScanResult, ScanResult } from './definitions';

export class BarcodeScannerWeb extends WebPlugin implements BarcodeScannerPlugin {


  async multiScan(): Promise<MultiScanResult> {
    let result = window.prompt('Leer', undefined);

    if(result){
      return {
        result:true,
        count:result.split(',').length,
        codes:result.split(',')
      };
    }else{
      let codes:string[] = [];
      return {result:false,count:0,codes:codes};
    }
  }


  async scan():Promise<ScanResult>{
    let result = window.prompt('Leer', undefined);
    if(result){
      return {result:true,code:result};
    }else{
      return {result:false};
    }
  }
}

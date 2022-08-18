import { WebPlugin } from '@capacitor/core';

import type { BarcodeScannerPlugin, ScanResult } from './definitions';

export class BarcodeScannerWeb extends WebPlugin implements BarcodeScannerPlugin {
  async scan():Promise<ScanResult>{
    let result = window.prompt('Leer', undefined);
    if(result){
      return {result:true,value:result};
    }else{
      return {result:false};
    }
  }
}

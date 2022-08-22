import { WebPlugin } from '@capacitor/core';
export class BarcodeScannerWeb extends WebPlugin {
    async scan() {
        let result = window.prompt('Leer', undefined);
        if (result) {
            return { result: true, code: result };
        }
        else {
            return { result: false };
        }
    }
}
//# sourceMappingURL=web.js.map
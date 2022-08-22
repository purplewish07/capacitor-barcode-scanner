var capacitorBarcodeScanner = (function (exports, core) {
    'use strict';

    const BarcodeScanner = core.registerPlugin('BarcodeScanner', {
        web: () => Promise.resolve().then(function () { return web; }).then(m => new m.BarcodeScannerWeb()),
    });

    class BarcodeScannerWeb extends core.WebPlugin {
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

    var web = /*#__PURE__*/Object.freeze({
        __proto__: null,
        BarcodeScannerWeb: BarcodeScannerWeb
    });

    exports.BarcodeScanner = BarcodeScanner;

    Object.defineProperty(exports, '__esModule', { value: true });

    return exports;

})({}, capacitorExports);
//# sourceMappingURL=plugin.js.map

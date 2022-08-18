package cl.kgames.capacitorbarcodescanner;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "BarcodeScanner")
public class BarcodeScannerPlugin extends Plugin {


    @PluginMethod
    public void scan(PluginCall call) {
        JSObject result = new JSObject();
        result.put("value","123123");
        result.put("result",true);
        call.resolve(result);
    }
}

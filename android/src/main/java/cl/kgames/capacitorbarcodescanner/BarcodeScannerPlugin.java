package cl.kgames.capacitorbarcodescanner;

import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResult;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "BarcodeScanner")
public class BarcodeScannerPlugin extends Plugin {


    @PluginMethod
    public void scan(PluginCall call) {
        Intent intent = new Intent(getContext(),ScannerActivity.class);
        startActivityForResult(call,intent,"onScan");
    }

    @ActivityCallback
    private void onScan(PluginCall call, ActivityResult result) {
        if (call == null) {
            return;
        }
        JSObject json = new JSObject();


        try{
            String code = result.getData().getStringExtra("code");
            if(code != null && code.length() > 0){
               json.put("result",true);
               json.put("value",code);
               call.resolve(json);
            }else{
                throw new Exception("nada leido");
            }
        }
        catch(Exception ex){
            json.put("result",false);
            call.resolve(json);
        }
    }
}

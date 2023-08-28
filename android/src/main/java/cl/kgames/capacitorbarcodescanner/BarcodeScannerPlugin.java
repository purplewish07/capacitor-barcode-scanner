package cl.kgames.capacitorbarcodescanner;

import android.Manifest;
import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResult;

import com.getcapacitor.JSObject;
import com.getcapacitor.Logger;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import org.json.JSONArray;

@CapacitorPlugin(name = "BarcodeScanner",
        permissions = {
                @Permission(strings = { Manifest.permission.CAMERA }, alias = BarcodeScannerPlugin.CAMERA)
        }
)
public class BarcodeScannerPlugin extends Plugin {


    static final String CAMERA = "camera";
    private static final String PERMISSION_DENIED_ERROR_CAMERA = "User denied access to camera";

    @PluginMethod
    public void scan(PluginCall call) {
        if(checkCameraPermissions(call,false)){
            showScanner(call,false);
        }
    }

    @PluginMethod
    public void multiScan(PluginCall call){
        if(checkCameraPermissions(call,true)){
            showScanner(call,true);
        }
    }

    private void showScanner(PluginCall call, boolean multi){
        Intent intent = new Intent(getContext(),ScannerActivity.class);
        intent.putExtra("multi",multi);
        if(multi){
            int maxReads = call.getInt("maxScans",9999);
            intent.putExtra("maxScans",maxReads);
        }
        startActivityForResult(call,intent,"onScan");
    }

    @ActivityCallback
    private void onScan(PluginCall call, ActivityResult result) {
        if (call == null) {
            return;
        }
        JSObject json = new JSObject();

        Boolean multiScan = result.getData().getBooleanExtra("multi",false);
        System.out.println("[BSP] multi: "+multiScan.toString());
        try{
            if(!multiScan){
                String code = result.getData().getStringExtra("code");
                if(code != null && code.length() > 0){
                    json.put("result",true);
                    json.put("code",code);
                    call.resolve(json);
                }else{
                    throw new Exception("nothing_readed");
                }
            }else{
                String[] codes = result.getData().getStringArrayExtra("codes");
                System.out.println("[BSP] codes: ");
                JSONArray arrayCodes = new JSONArray();
                for (int i = 0; i < codes.length; i++) {
                    System.out.println("[BSP] "+i+": "+codes[i]);
                    arrayCodes.put(codes[i]);
                }
                if(codes.length > 0){
                    json.put("result",true);
                }else{
                    json.put("result",false);
                }
                json.put("count",codes.length);
                json.put("codes",arrayCodes);
                call.resolve(json);
            }

        }
        catch(Exception ex){
            json.put("result",false);
            call.resolve(json);
        }
    }

    private boolean checkCameraPermissions(PluginCall call,boolean multi) {
        boolean needCameraPerms = isPermissionDeclared(CAMERA);
        boolean hasCameraPerms = !needCameraPerms || getPermissionState(CAMERA) == PermissionState.GRANTED;

        if (!hasCameraPerms) {
            String cbName = "cameraPermissionsCallback";
            if(multi){
                cbName = "cameraPermissionsForMultiCallback";
            }
            requestPermissionForAlias(CAMERA, call, cbName);
            return false;
        }
        return true;
    }

    /**
     * Completes the plugin call after a camera permission request
     *
     * @param call the plugin call
     */
    @PermissionCallback
    private void cameraPermissionsCallback(PluginCall call) {
        if (getPermissionState(CAMERA) != PermissionState.GRANTED) {
            Logger.debug(getLogTag(), "User denied camera permission: " + getPermissionState(CAMERA).toString());
            call.reject(PERMISSION_DENIED_ERROR_CAMERA);
            return;
        }
        showScanner(call,false);
    }

    /**
     * Completes the plugin call after a camera permission request
     *
     * @param call the plugin call
     */
    @PermissionCallback
    private void cameraPermissionsForMultiCallback(PluginCall call) {
        if (getPermissionState(CAMERA) != PermissionState.GRANTED) {
            Logger.debug(getLogTag(), "User denied camera permission: " + getPermissionState(CAMERA).toString());
            call.reject(PERMISSION_DENIED_ERROR_CAMERA);
            return;
        }
        showScanner(call,true);
    }
}

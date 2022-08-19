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
        if(checkCameraPermissions(call)){
            showScanner(call);
        }
    }

    private void showScanner(PluginCall call){
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
               json.put("code",code);
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

    private boolean checkCameraPermissions(PluginCall call) {
        boolean needCameraPerms = isPermissionDeclared(CAMERA);
        boolean hasCameraPerms = !needCameraPerms || getPermissionState(CAMERA) == PermissionState.GRANTED;

        if (!hasCameraPerms) {
            requestPermissionForAlias(CAMERA, call, "cameraPermissionsCallback");
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
        showScanner(call);
    }
}

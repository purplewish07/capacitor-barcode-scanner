package cl.kgames.capacitorbarcodescanner;

import android.Manifest;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.activity.result.ActivityResult;
import androidx.camera.view.PreviewView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.getcapacitor.Bridge;
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
public class BarcodeScannerPlugin extends Plugin implements BarcodeResultListener {

    static final String CAMERA = "camera";
    private static final String PERMISSION_DENIED_ERROR_CAMERA = "User denied access to camera";

    public static Bridge staticBridge;
    private CoordinatorLayout scannerLayout;
    private ScannerService scannerService;

    @Override
    public void load() {
        staticBridge = this.bridge;
        scannerService = new ScannerService(getContext(), getActivity(),this);
        // 预加载布局和相机资源
        preloadScannerLayout();
    }

    @Override
    public void onBarcodeResult(String code) {
        PluginCall savedCall = getSavedCall();
        if (savedCall != null && code != null) {
            JSObject result = new JSObject();
            result.put("result", true);
            result.put("code", code);
            savedCall.resolve(result);
            // After resolving, clear the saved call
            freeSavedCall();  // Ensure to clear reference to the call
            stopScanner();
        } else {
            Log.e(getLogTag(), "No active plugin call for barcode result.");
        }

    }

    @PluginMethod
    public void scan(PluginCall call) {
        saveCall(call);
        if(checkCameraPermissions(call)){
            showScanner(call);
        }
    }

    @PluginMethod
    public void stop(PluginCall call) {
        stopScanner();
        call.resolve();
    }

    private void stopScanner() {
        if (scannerService != null) {
            scannerService.stopCamera();
        }
        PluginCall savedCall = getSavedCall();
        if (savedCall != null) {
            savedCall.resolve(new JSObject().put("message", "Scanning stopped successfully."));
            freeSavedCall();  // 释放 PluginCall
        }
        getActivity().runOnUiThread(() -> {
            if (scannerService != null) {
                scannerService.stopCamera();  // 停止相机预览
            }
            if (scannerLayout.getParent() != null) {
                ViewGroup parentView = (ViewGroup) scannerLayout.getParent();
                parentView.removeView(scannerLayout);  // 从视图层次结构中移除 scannerLayout
            }
            staticBridge.getWebView().setBackgroundColor(Color.WHITE);
            staticBridge.getWebView().loadUrl("javascript:document.documentElement.style.backgroundColor = '';void(0);");
        });
    }

    private void preloadScannerLayout() {
        getActivity().runOnUiThread(() -> {
            // 动态加载 activity_scanner 布局，传递 null 作为父视图
            LayoutInflater inflater = LayoutInflater.from(getContext());
            scannerLayout = (CoordinatorLayout) inflater.inflate(R.layout.activity_scanner, null, false);
            PreviewView previewView = scannerLayout.findViewById(R.id.preview_view);
            scannerService.prepareCamera(previewView); // 准备相机，但不綁定
        });
    }

    private void showScanner(PluginCall call){

        getActivity().runOnUiThread(() -> {
            ViewGroup parentView = (ViewGroup) staticBridge.getWebView().getParent();

            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            );
            scannerLayout.setBackgroundColor(Color.TRANSPARENT);
            scannerLayout.setLayoutParams(layoutParams);
            parentView.addView(scannerLayout, layoutParams);

            PreviewView previewView = scannerLayout.findViewById(R.id.preview_view);
            scannerService.startCamera(previewView);

            Animation aniSlide = AnimationUtils.loadAnimation(getContext(), R.anim.scanner_animation);
            View barLine = scannerLayout.findViewById(R.id.barcode_line);
            barLine.startAnimation(aniSlide);
            // 将 WebView bring to front
            staticBridge.getWebView().bringToFront();
            staticBridge.getWebView().setBackgroundColor(Color.TRANSPARENT);
            staticBridge.getWebView().loadUrl("javascript:document.documentElement.style.backgroundColor = 'transparent';void(0);");

        });
        //Intent intent = new Intent(getContext(),ScannerActivity.class);
        //startActivityForResult(call,intent,"onScan");


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

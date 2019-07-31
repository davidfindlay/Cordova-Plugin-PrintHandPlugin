package com.printhand.printingsample;

import com.dynamixsoftware.intentapi.IDocument;
import com.dynamixsoftware.intentapi.IJob;
import com.dynamixsoftware.intentapi.IPrintCallback;
import com.dynamixsoftware.intentapi.IPrinterContext;
import com.dynamixsoftware.intentapi.IPrinterInfo;
import com.dynamixsoftware.intentapi.IServiceCallback;
import com.dynamixsoftware.intentapi.ISetLicenseCallback;
import com.dynamixsoftware.intentapi.IntentAPI;
import com.dynamixsoftware.intentapi.PrintHandOption;
import com.dynamixsoftware.intentapi.Result;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class IntentApiPrintHandPlugin extends CordovaPlugin {

    private String PACKAGE_NAME_FREE = "com.dynamixsoftware.printhand";
    private String PACKAGE_NAME_PREMIUM = "com.dynamixsoftware.printhand.premium";
    private static String TAG = "CordovaPluginShareIntent";
    private Context context;
    private CallbackContext handleCallbackContext;
    private String printHandPackage;

    private IntentAPI intentApi;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        context = this.cordova.getActivity();
        intentApi = new IntentAPI(this.cordova.getActivity() != null ? this.cordova.getActivity() : context); // some features not worked if initialized without activity

        try {
            intentApi.print("PrintingSample", "text/html", "Test 1234");
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

}
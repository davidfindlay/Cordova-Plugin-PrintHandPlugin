package io.github.cordovaPluginPrintHand;

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

import io.github.cordovaPluginPrintHand.FilesUtils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.support.v4.content.FileProvider;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.os.RemoteException;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.File;

import java.util.*;

public class IntentApiPrintHandPlugin extends CordovaPlugin {

    private String PACKAGE_NAME_FREE = "com.dynamixsoftware.printhand";
    private String PACKAGE_NAME_PREMIUM = "com.dynamixsoftware.printhand.premium";
    private static String TAG = "CordovaPluginShareIntent";
    private Context context;
    private CallbackContext handleCallbackContext;
    private String printHandPackage;

    private IntentAPI intentApi;

    private String messages = new String();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        messages = "";
        this.handleCallbackContext = callbackContext;
        context = this.cordova.getActivity();

        intentApi = new IntentAPI(this.cordova.getActivity() != null ? this.cordova.getActivity() : context); // some features not worked if initialized without activity

        if (action.equals("startService")) {
            startService(intentApi, callbackContext);
            return true;
        }

        if (action.equals("echo")) {
            String message = args.getString(0);
            this.echo(message, callbackContext);
            return true;
        }

        if (action.equals("printFile")) {

            final String filename = args.getString(0);
            final String fileDescription = args.getString(1);
            final String mimeType = args.getString(2);

            String filePath = getFilePath(context, filename);

            if (filePath == null) {
                final String errorPath = new File(getFilesDir(context), filename).getAbsolutePath();
                callbackContext.error("printFile: unable to find " + filename + " at " + errorPath);
                return false;
            }

            intentApi.print(Uri.parse("content://" + filePath), mimeType, fileDescription);
//
//            try {
//
//                intentApi.print("PrintingSample", "application/pdf", Uri.parse("file://" + filePath));
//            } catch (RemoteException e) {
//                callbackContext.error("printFile: " + e.toString());
//            }

            callbackContext.success(messages);

            return true;
        }

        if (action.equals("printFileWithoutUI")) {

            final String filename = args.getString(0);
            final String fileDescription = args.getString(1);
            final String mimeType = args.getString(2);

            String filePath = getFilePath(context, filename);

            if (filePath == null) {
                final String errorPath = new File(getFilesDir(context), filename).getAbsolutePath();
                callbackContext.error("printFile: unable to find " + filename + " at " + errorPath);
                return false;
            }

            try {
                List<PrintHandOption> fileOptions = intentApi.getFilesOptions();
                messages += fileOptions.toString();
                intentApi.print(fileDescription, mimeType, Uri.parse("content://" + filePath));
            } catch (RemoteException e) {
                callbackContext.error("printFile: " + e.toString());
            }

            callbackContext.success(messages);

            return true;
        }

        if (action.equals("setupPrinter")) {
            intentApi.setupCurrentPrinter();
            callbackContext.success("Setup printer");
            return true;
        }

        if (action.equals("configPrinter")) {
            intentApi.changePrinterOptions();
            callbackContext.success("Change printer options");
            return true;
        }

        return false;
    }

    static String getFilePath(Context context, String filename) {
        File file = new File(getFilesDir(context), filename);
        return file.exists() ? file.getAbsolutePath() : null;
    }

    private static File getFilesDir(Context context) {
        return context.getExternalCacheDir();
    }

    private void echo(String message, CallbackContext callbackContext) {
        if (message != null && message.length() > 0) {
            callbackContext.success(message);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    public void startService(IntentAPI intentApi, CallbackContext callbackContext) {
        try {
            intentApi.runService(new IServiceCallback.Stub() {
                @Override
                public void onServiceDisconnected() {
                    callbackContext.success("Service disconnected");
                }

                @Override
                public void onServiceConnected() {
                    callbackContext.success("Service connected");
                }

                @Override
                public void onFileOpen(int progress, int finished) {
                    messages += new String("onFileOpen progress " + progress + "; finished " + (finished == 1));
                }

                @Override
                public void onLibraryDownload(int progress) {
                    messages += new String("onLibraryDownload progress " + progress);
                }

                @Override
                public boolean onRenderLibraryCheck(boolean renderLibrary, boolean fontLibrary) {
                    messages += new String("onRenderLibraryCheck render library " + renderLibrary + "; fonts library " + fontLibrary);
                    return true;
                }

                @Override
                public String onPasswordRequired() {
                    messages += new String("onPasswordRequired");
                    return "password";
                }

                @Override
                public void onError(Result result) {
                    messages += new String("error, Result " + result + "; Result type " + result.getType());
                }
            });
        } catch (RemoteException e) {
            callbackContext.error(e.toString());
        }
        try {
            intentApi.setPrintCallback(new IPrintCallback.Stub() {
                @Override
                public void startingPrintJob() {
                    messages += new String("startingPrintJob");
                }

                @Override
                public void start() {
                    messages += new String("start");
                }

                @Override
                public void sendingPage(int pageNum, int progress) {
                    messages += new String("sendingPage number " + pageNum + ", progress " + progress);
                }

                @Override
                public void preparePage(int pageNum) {
                    messages += new String("preparePage number " + pageNum);
                }

                @Override
                public boolean needCancel() {
                    messages += new String("needCancel");
                    // If you need to cancel printing send true
                    return false;
                }

                @Override
                public void finishingPrintJob() {
                    messages += new String("finishingPrintJob");
                }

                @Override
                public void finish(Result result, int pagesPrinted) {
                    messages += new String("finish, Result " + result + "; Result type " + result.getType() + "; Result message " + result.getType().getMessage() + "; pages printed " + pagesPrinted);
                }
            });
        } catch (RemoteException e) {
            callbackContext.error(e.toString());
        }
    }

}
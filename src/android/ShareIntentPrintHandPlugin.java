package com.printhand.printingsample;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

public class ShareIntentPrintHandPlugin extends CordovaPlugin{

	private String PACKAGE_NAME_FREE = "com.dynamixsoftware.printhand";
	private String PACKAGE_NAME_PREMIUM = "com.dynamixsoftware.printhand.premium";
	private static String TAG = "CordovaPluginShareIntent";
    private Context context;
    private CallbackContext handleCallbackContext;

    private String printHandPackage;

    ShareIntentPrintHandPlugin() {
    	super();
        PackageManager pm = context.getPackageManager();
        if (isPrintHandPremiumInstalled(pm)) {
            printHandPackage = PACKAGE_NAME_PREMIUM;
        } else if (isPrintHandFreeInstalled(pm)) {
            printHandPackage = PACKAGE_NAME_FREE;
        }
    }

    private isPrintHandPremiumInstalled(PackageManager packageManager) {
        boolean found = true;
        try {
            packageManager.getPackageInfo(PACKAGE_NAME_PREMIUM, 0);
        } catch (Package.NameNotFoundException e) {
            found = false;
        }
        return found;
    }

    private isPrintHandFreeInstalled(PackageManager packageManager) {
        boolean found = true;
        try {
            packageManager.getPackageInfo(PACKAGE_NAME_FREE, 0);
        } catch (Package.NameNotFoundException e) {
            found = false;
        }
        return found;
    }

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		this.handleCallbackContext = callbackContext;
		this.context = this.cordova.getActivity();
		if (action.equals("printWithHttpURL")) {
			final String url = args.getString(0);
			if(url==null)
			{
				handleCallbackContext.error("Http URL Missing");
				return true;
			}
			this.cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					shareWebPage(url);
				}
			});
			return true;
		}
		else if (action.equals("printWebPageWithContain")) {
			final String url = args.getString(0);
			if(url==null)
			{
				handleCallbackContext.error("WebPage String Missing");
				return true;
			}
			this.cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					shareWebPageString(url);
				}
			});
				return true;
		}
		else if (action.equals("printImage")) {
			final String imagePath = args.getString(0);
			final String actionView = args.getString(1);
			if(imagePath==null)
			{
				handleCallbackContext.error("Image Path Missing");
				return true;
			}
			if(actionView==null)
			{
				handleCallbackContext.error("Action View Value Missing set true/false");
				return true;
			}
			this.cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					shareImage(imagePath,Boolean.valueOf(actionView));
				}
			});

			return true;
		}
		else if (action.equals("printFile")) {
			final String imagePath = args.getString(0);
			final String actionView = args.getString(1);
			final String mineTypeView = args.getString(2);
			if(imagePath==null)
			{
				handleCallbackContext.error("File Path Missing");
				return true;
			}
			if(actionView==null)
			{
				handleCallbackContext.error("Action View Value Missing set true/false");
				return true;
			}
			if(actionView==null)
			{
				handleCallbackContext.error("File Mine Type Missing");
				return true;
			}
			this.cordova.getThreadPool().execute(new Runnable() {
				public void run() {
					shareFile(Boolean.valueOf(actionView),imagePath,mineTypeView);
				}
			});

			return true;
		}
		return false;
	}


	/***
	 * Tries to share image using ACTION_VIEW or ACTION_SEND Intent.
	 * @param actionView true if ACTION_VIEW, false if ACTION_SEND
	 */
	public void shareImage(String imagePath,boolean actionView) {
	
		String action = actionView ? Intent.ACTION_VIEW : Intent.ACTION_SEND;
		Intent i = new Intent(action);

		Uri uri = Uri.parse(imagePath);
		if (actionView) {
			i.setDataAndType(uri, "image/png");
		} else {
			i.putExtra(Intent.EXTRA_STREAM, uri);
			i.setType("image/png");
		}

        if (printHandPackage != null) {
            i.setPackage(printHandPackage);
            try {
                context.startActivity(i);
                handleCallbackContext.success();
                return;
            } catch (ActivityNotFoundException e) {
                handleCallbackContext.error("Application with package name " + printHandPackage + " is not available.");
            }
        } else {
            handleCallbackContext.error("PrintHand not available");
        }
	}


	/***
	 * Tries to share file using ACTION_VIEW or ACTION_SEND Intent.
	 * @param actionView true if ACTION_VIEW, false if ACTION_SEND
	 */
	public void shareFile(boolean actionView,String filePath,String fileMIMEtypes ) {

		String action = actionView ? Intent.ACTION_VIEW : Intent.ACTION_SEND;
		Intent i = new Intent(action);

		// Scheme "content" also available
		String scheme = "file://";

		// MIME types available:
		// application/pdf
		// application/vnd.ms-word
		// application/ms-word
		// application/msword
		// application/vnd.openxmlformats-officedocument.wordprocessingml.document
		// application/vnd.ms-excel
		// application/ms-excel
		// application/msexcel
		// application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
		// application/vnd.ms-powerpoint
		// application/ms-powerpoint
		// application/mspowerpoint
		// application/vnd.openxmlformats-officedocument.presentationml.presentation
		// application/haansofthwp
		// text/plain
		// text/html

		Uri uri = Uri.parse(filePath);
		if (actionView) {
			i.setDataAndType(uri, fileMIMEtypes);
		} else {
			i.putExtra(Intent.EXTRA_STREAM, uri);
			i.setType("application/msword");
		}
		if (printHandPackage != null) {
            i.setPackage(printHandPackage);
            try {
                context.startActivity(i);
                handleCallbackContext.success();
                return;
            } catch (ActivityNotFoundException e) {
                handleCallbackContext.error("Application with package name " + printHandPackage + " is not available.");

            }
        } else {
            handleCallbackContext.error("PrintHand not available");
        }
	}

	/***
	 * Tries to share web page.
	 */
	public void shareWebPage(String url) {
		Intent i = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.parse(url);
		i.setDataAndType(uri, "text/html");

        if (printHandPackage != null) {
            i.setPackage(printHandPackage);
            try {
                context.startActivity(i);
                handleCallbackContext.success();
                return;
            } catch (ActivityNotFoundException e) {
                handleCallbackContext.error("Application with package name " + printHandPackage + " is not available.");
            }
        } else {
            handleCallbackContext.error("PrintHand not available");
        }
	}
	
	/***
	 * Tries to share web page as string.
	 */
	public void shareWebPageString(String printString) {
		Intent i = new Intent(Intent.ACTION_SEND);

		i.setType("text/html");
		i.putExtra(Intent.EXTRA_TEXT, printString);

        if (printHandPackage != null) {
            i.setPackage(printHandPackage);
            try {
                context.startActivity(i);
                handleCallbackContext.success();
                return;
            } catch (ActivityNotFoundException e) {

                handleCallbackContext.error("Application with package name " + printHandPackage + " is not available.");
            }
        } else {
            handleCallbackContext.error("PrintHand not available");
        }
	}

}

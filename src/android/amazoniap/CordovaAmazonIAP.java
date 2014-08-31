package com.mediamatrixdoo.amazoniap;

import java.util.HashSet;
import java.util.Set;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.RequestId;

import android.util.Log;

public class CordovaAmazonIAP extends CordovaPlugin {
    private static final String LOG_TAG = "CordovaAmazonIAP";
    private CordovaPurchasingListener purchasingListener;
    
	public static final String GET_USER_DATA = "getUserData";
	public static final String GET_PRODUCT_DATA = "getProductData";
	public static final String GET_PURCHASE_UPDATES = "getPurchaseUpdates";
	public static final String PURCHASE = "purchase";
	public static final String NOTIFY_FULFILLED = "notifyFulfilled";
	public static final String NOTIFY_UNAVAILABLE = "notifyUnavailable";
	
	private void deferCallbackResponse (String requestId, CallbackContext callbackContext) {
		purchasingListener.addRequestCallback(requestId, callbackContext.getCallbackId());
        final PluginResult result = new PluginResult(PluginResult.Status.NO_RESULT);
        result.setKeepCallback(true);
        callbackContext.sendPluginResult(result);
	}
	
	private void deferCallbackResponse (RequestId requestId, CallbackContext callbackContext) {
		deferCallbackResponse(requestId.toString(), callbackContext);
	}
    
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        purchasingListener = new CordovaPurchasingListener(webView);
        PurchasingService.registerListener(cordova.getActivity().getApplicationContext(), purchasingListener);
    }
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		try {
			if (GET_USER_DATA.equals(action)) {
				Log.i(LOG_TAG, "getUserData called");
				
				final RequestId requestId = PurchasingService.getUserData();
                this.deferCallbackResponse(requestId, callbackContext);
				return true;
			}
			else if (GET_PRODUCT_DATA.equals(action)) {
				Log.i(LOG_TAG, "getProductData called");	
				JSONArray skusArray = args.getJSONArray(0);
				
				final Set<String> skusSet = new HashSet<String>();
                for (int i = 0; i < skusArray.length(); i++) {
                    skusSet.add(skusArray.optString(i));
                }
				
				final RequestId requestId = PurchasingService.getProductData(skusSet);
                this.deferCallbackResponse(requestId, callbackContext);
				return true;
			}
			else if (GET_PURCHASE_UPDATES.equals(action)) {
				Log.i(LOG_TAG, "getPurchaseUpdates called");
				
				if (purchasingListener.containsRequestCallback("getPurchaseUpdatesId")) {
					callbackContext.error("getPurchaseUpdates aready called");
				} else {
					boolean reset = args.getBoolean(0);
	                
					PurchasingService.getPurchaseUpdates(reset);
	                // for some reason RequestId from PurchasingService.getPurchaseUpdates is not the same as in callback function
					// so we need to provide fixed string for id 
	                this.deferCallbackResponse("getPurchaseUpdatesId", callbackContext);
				}	
				return true;
			}
			else if (PURCHASE.equals(action)) {
				Log.i(LOG_TAG, "purchase called");
		    	String productSKU = args.getString(0);
                
                final RequestId requestId = PurchasingService.purchase(productSKU);
                this.deferCallbackResponse(requestId, callbackContext);
		    	return true;
		    }
			else if (NOTIFY_FULFILLED.equals(action)) {
				Log.i(LOG_TAG, "notifyFulfillment FULFILLED called");
				String receiptId = args.getString(0);
				
				PurchasingService.notifyFulfillment(receiptId, FulfillmentResult.FULFILLED);
				callbackContext.success("DONE");
				return true;
			}
			else if (NOTIFY_UNAVAILABLE.equals(action)) {
				Log.i(LOG_TAG, "notifyFulfillment UNAVAILABLE called");
				String receiptId = args.getString(0);
				
				PurchasingService.notifyFulfillment(receiptId, FulfillmentResult.UNAVAILABLE);
				callbackContext.success("DONE");
				return true;
			}
		    callbackContext.error("Invalid method");
		    return false;
		} catch(Exception e) {
			Log.w(LOG_TAG, "Exception: " + e.getMessage());
		    callbackContext.error(e.getMessage());
		    return false;
		}
	}
}
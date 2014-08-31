package com.mediamatrixdoo.amazoniap;

import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserDataResponse;

public class CordovaPurchasingListener implements PurchasingListener {

    private static final String LOG_TAG = "CordovaAmazonIAP";

    private CordovaWebView webView;
    
    private final ConcurrentHashMap<String, String> requestCallbacks;
    
    public CordovaPurchasingListener(final CordovaWebView webView) {
        this.webView = webView;
        requestCallbacks = new ConcurrentHashMap<String, String>();
    }
    
    public void addRequestCallback(final String requestId, final String callbackId) {
    	requestCallbacks.put(requestId, callbackId);
    }
    
    public boolean containsRequestCallback(final String requestId) {
    	return requestCallbacks.containsKey(requestId);
    }

    private void sendCallbackResponse(final String requestId, final JSONObject resultObj) {
    	final PluginResult pluginResult;
    	final String callbackId = requestCallbacks.remove(requestId);
    	
    	pluginResult = new PluginResult(PluginResult.Status.OK, resultObj);
        pluginResult.setKeepCallback(false);
        this.webView.sendPluginResult(pluginResult, callbackId);
    }
    
    private void sendErrorResponse(final String requestId, final String errMsg) {
    	final PluginResult pluginResult;
    	final String callbackId = requestCallbacks.remove(requestId);
    	
    	pluginResult = new PluginResult(PluginResult.Status.ERROR, errMsg);
        pluginResult.setKeepCallback(false);
        this.webView.sendPluginResult(pluginResult, callbackId);
    }
    
    @Override
    public void onUserDataResponse(final UserDataResponse response) {
    	JSONObject resultObj;

        final UserDataResponse.RequestStatus status = response.getRequestStatus();
        switch (status) {
        case SUCCESSFUL:
            
            try {
            	resultObj = new JSONObject();
                resultObj.put("userId", response.getUserData().getUserId());
                resultObj.put("marketplace", response.getUserData().getMarketplace());
                this.sendCallbackResponse(response.getRequestId().toString(), resultObj);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                this.sendErrorResponse(response.getRequestId().toString(), e.getMessage());
            }
            
            break;
        case FAILED:
        case NOT_SUPPORTED:
            Log.d(LOG_TAG, "onUserDataResponse failed, status code is " + status);
            this.sendErrorResponse(response.getRequestId().toString(), "onUserDataResponse failed, status code is " + status);
            break;
        }
    }

    @Override
    public void onProductDataResponse(final ProductDataResponse response) {
    	JSONObject resultObj;
    	JSONObject productsObj;
    	JSONObject productObj;
    	JSONObject price;
        final ProductDataResponse.RequestStatus status = response.getRequestStatus();
        Log.d(LOG_TAG, "onProductDataResponse: RequestStatus (" + status + ")");

        switch (status) {
        case SUCCESSFUL:
            final Set<String> unavailableSkus = response.getUnavailableSkus();

            try {
            	resultObj = new JSONObject();
            	productsObj = new JSONObject();
            	for (Map.Entry<String, Product> entry : response.getProductData().entrySet()) {
            	    String key = entry.getKey();
            	    productObj = entry.getValue().toJSON();
            	    price = entry.getValue().getPrice().toJSON();
            	    productObj.put("price", price);
            	    productsObj.put(key, productObj);
            	}
            	resultObj.put("productData", productsObj);
            	resultObj.put("unavailableSkus", new JSONArray(unavailableSkus));
                this.sendCallbackResponse(response.getRequestId().toString(), resultObj);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                this.sendErrorResponse(response.getRequestId().toString(), e.getMessage());
            }

            break;
        case FAILED:
        case NOT_SUPPORTED:
            Log.d(LOG_TAG, "onProductDataResponse: failed, should retry request");
            this.sendErrorResponse(response.getRequestId().toString(), "onUserDataResponse failed, status code is " + status);
            break;
        }
    }

    @Override
    public void onPurchaseUpdatesResponse(final PurchaseUpdatesResponse response) {
    	JSONObject resultObj;
    	JSONArray receiptsArray;

        final PurchaseUpdatesResponse.RequestStatus status = response.getRequestStatus();
        switch (status) {
        case SUCCESSFUL:
        	try {
        		resultObj = new JSONObject();
        		receiptsArray = new JSONArray();
            	resultObj.put("userData", response.getUserData().toJSON());
            	for (final Receipt receipt : response.getReceipts()) {
            		receiptsArray.put(receipt.toJSON());
                }
                resultObj.put("receipts", receiptsArray);
                resultObj.put("hasMore", response.hasMore());
                this.sendCallbackResponse("getPurchaseUpdatesId", resultObj);
                
        	} catch (JSONException e) {
        		Log.e(LOG_TAG, e.getMessage(), e);
                this.sendErrorResponse(response.getRequestId().toString(), e.getMessage());
        	}
        	
            break;
        case FAILED:
        case NOT_SUPPORTED:
            Log.d(LOG_TAG, "onPurchaseUpdatesResponse failed, status code is " + status);
            this.sendErrorResponse(response.getRequestId().toString(), "onPurchaseUpdatesResponse failed, status code is " + status);
            break;
        }

    }

    @Override
    public void onPurchaseResponse(final PurchaseResponse response) {
    	JSONObject resultObj;
        final PurchaseResponse.RequestStatus status = response.getRequestStatus();
        final Receipt receipt;

        switch (status) {
        case SUCCESSFUL:
            receipt = response.getReceipt();
            
            try {
            	resultObj = new JSONObject();
            	resultObj.put("status", status);
            	resultObj.put("userData", response.getUserData().toJSON());
                resultObj.put("receipt", receipt.toJSON());
                this.sendCallbackResponse(response.getRequestId().toString(), resultObj);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                this.sendErrorResponse(response.getRequestId().toString(), e.getMessage());
            }
            break;
        case ALREADY_PURCHASED:

        	try {
            	resultObj = new JSONObject();
            	resultObj.put("status", status);
            	resultObj.put("userData", response.getUserData().toJSON());
                this.sendCallbackResponse(response.getRequestId().toString(), resultObj);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                this.sendErrorResponse(response.getRequestId().toString(), e.getMessage());
            }    
            break;
        case INVALID_SKU:
            try {
            	resultObj = new JSONObject();
            	resultObj.put("status", status);
            	resultObj.put("userData", response.getUserData().toJSON());
                this.sendCallbackResponse(response.getRequestId().toString(), resultObj);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                this.sendErrorResponse(response.getRequestId().toString(), e.getMessage());
            }    
            break;
        case FAILED:
        case NOT_SUPPORTED:
            Log.d(LOG_TAG, "onPurchaseResponse failed, status code is " + status);
            this.sendErrorResponse(response.getRequestId().toString(), "onPurchaseResponse failed, status code is " + status);
            break;
        }

    }

}

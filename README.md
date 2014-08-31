# Cordova/ PhoneGap Plugin Amazon In-App Purchasing v2.0

By using this Cordova plugin within your app, you enable your customers to use 1-Click ordering to purchase items from Amazon. The plugin will work with Cordova / PhoneGap >= 3.0. It completely wraps Amazon IAP v2.0 API so you can offer consumables, entitlements, or subscriptions in your app.

## Installation

For Cordova:
	cordova plugin add https://github.com/zoxxx/cordova-plugin-amazon-in-app-purchasing.git

For PhoneGap:
	phonegap local plugin add https://github.com/zoxxx/cordova-plugin-amazon-in-app-purchasing.git

## Usage

### Initialization

Plugin is initialized when it's referenced by your JavaScript code for the first time.

### API

Plugin's JavaScript API methods are almost identical as in original Amazon IAP v2.0 API.

* **amazonIAP.getProductData(skusArray, successCallback, errorCallback)** - This API method will confirm (un)availability of product skus listed in skusArray. You'll also get properties like local pricing and other related properties to the product(s).
  * skusArray - Array of product skus (E.g. ['prod_sku_1', 'prod_sku_2', ...])
  * successCallback - If the API call was successful this callaback will receive data object with two properties:
    * productData - Object with skus and properties of each available sku that was initially listed in skusArray
    * unavailableSkus - Array with unavailable skus that were initially listed in skusArray
  * errorCallback - Will receive error message string in case of error

* **amazonIAP.getUserData(successCallback, errorCallback)**
Amazon recommended to call this method inside onResume event handler to retrieve the user ID of the customer that is currently logged into the Amazon Appstore. Your app should handle multiple users - more specifically, it should handle the case where the user logged into the Amazon Appstore changes.
  * successCallback - Data object with **userId** and **marketplace** properties will be provided to callback 
  * errorCallback - Will receive error message string in case of error

* **amazonIAP.getPurchaseUpdates(reset, successCallback, errorCallback)**
This method retrieves purchase history for the current user. To check if product is canceled, find the most recent receipt (by start date) for product SKU from the returned receipts and verify that the *endDate* property exists.
  * reset - Boolean, set to *false* to retrieve the current state of receipts for your pending orders since the last call to the method, set to *true* will get you the entire purchase history for the user
  * successCallback - Success callback will get passed data object with **userData**, **receipts** and **hasMore** properties will be provided to callback. If **hasMore** property is *true* you should call *getPurchaseUpdates* again to receive rest of the purchase history.
  * errorCallback - Will get error message string in case of error

* **amazonIAP.purchase(productSKU, successCallback, errorCallback)**
This method will initiate purchase for a specific SKU.
  * productSKU - String product sku
  * successCallback - Success callback will get passed data object with **status** and **userData** properties. If **status** property is SUCCESSFUL there will be third property object - **receipt**. Status property value could also be ALREADY_PURCHASED or INVALID_SKU.
  * errorCallback - Will get error message string in case of error.

* **amazonIAP.notifyFullfiled(receiptId, successCallback, errorCallback)**
Amazon recommends calling notifyFullfiled for each product that has been fulfilled in your app. Once receive Fulfilled status for the purchase, Amazon will not try to send the purchase receipt to application any more. It's especially important to call this method for CONSUMABLE purchases.
  * receiptId - Receipt id of the product successfully purchased and for which app handled the purchase fulfillment.
  * successCallback - Will get "DONE" string after the notification message get dispatched to Amazon.
  * errorCallback - Will get error message string in case of error.

* **amazonIAP.notifyUnavailable(receiptId, successCallback, errorCallback)**
If the product sku is not applicable anymore, call this method.
  * receiptId - Receipt id of the product that is not applicable anymore.
  * successCallback - Will get "DONE" string after the notification message get dispatched to Amazon.
  * errorCallback - Will get error message string in case of error.

## Examples

TO DO

## Testing

Amazon provides few options for testing your app before going live. Read more at: https://developer.amazon.com/public/apis/earn/in-app-purchasing/docs-v2/testing-iap-2.0



## Support

For bugs and issues please use repository issue tracker. For other inquiries contact me at zoran.b(at)mediamatrixdoo.com. However note that my availability on this project is limited by my spare time.



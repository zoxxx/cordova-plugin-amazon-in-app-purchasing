var amazonIAP = {
	getUserData: function (successCallback, errorCallback) {
		if (typeof successCallback === 'function' && typeof errorCallback === 'function') {
			cordova.exec(
					successCallback,
					errorCallback,
					'CordovaAmazonIAP',
					'getUserData',
					[]
			);
		} else {
			console.log('amazonIAP.getUserData params error');
		}
		
	},
	getProductData: function (skusArray, successCallback, errorCallback) {
		if (Array.isArray(skusArray) && skusArray.length > 0 && typeof successCallback === 'function' && typeof errorCallback === 'function') {
			cordova.exec(
					successCallback,
					errorCallback,
					'CordovaAmazonIAP',
					'getProductData',
					[skusArray]
			);
		} else {
			console.log('amazonIAP.getProductData params error');
		}
	},
	getPurchaseUpdates: function (reset, successCallback, errorCallback) {
		if (typeof reset === 'boolean' && typeof successCallback === 'function' && typeof errorCallback === 'function') {
			cordova.exec(
					successCallback,
					errorCallback,
					'CordovaAmazonIAP',
					'getPurchaseUpdates',
					[reset]
			);
		} else {
			console.log('amazonIAP.getPurchaseUpdates params error');
		}
	},
	purchase: function (productSKU, successCallback, errorCallback) {
		if (typeof productSKU === 'string' && productSKU !== '' && typeof successCallback === 'function' && typeof errorCallback === 'function') {
			cordova.exec(
					successCallback, // success callback function
		            errorCallback, // error callback function
		            'CordovaAmazonIAP',
		            'purchase',
		            [productSKU]
			);
		} else {
			console.log('amazonIAP.purchase params error');
		}
		
	},
	notifyFulfilled: function (receiptId, successCallback, errorCallback) {
		if (typeof receiptId === 'string' && receiptId !== '' && typeof successCallback === 'function' && typeof errorCallback === 'function') {
			cordova.exec(
					successCallback,
					errorCallback,
					'CordovaAmazonIAP',
					'notifyFulfilled',
					[receiptId]
			);
		}
	},
	notifyUnavailable: function (receiptId, successCallback, errorCallback) {
		if (typeof receiptId === 'string' && receiptId !== '' && typeof successCallback === 'function' && typeof errorCallback === 'function') {
			cordova.exec(
					successCallback,
					errorCallback,
					'CordovaAmazonIAP',
					'notifyUnavailable',
					[receiptId]
			);
		}
	}	
};

module.exports = amazonIAP;
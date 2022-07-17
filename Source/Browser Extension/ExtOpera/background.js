
// Přijme hostname z popup.js
chrome.runtime.onMessage.addListener(function(msg, sender, sendResponse) {
	chrome.runtime.sendNativeMessage('com.am.pswenc', msg,
  	function(message) {
		// Odešleme zpět zprávu s heslem
		sendResponse(message);
  });
	// Delay při přenosu
	return true;
});

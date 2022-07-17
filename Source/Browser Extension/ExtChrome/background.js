
// Přijme hostname z popup.js
chrome.runtime.onMessage.addListener(function(msg, sender, sendResponse) {
	console.log("Received %o from %o, frame", msg.text, sender.tab, sender.frameId);
	chrome.runtime.sendNativeMessage('com.am.pswenc', msg,
  	function(message) {
		console.log("Received " + message);
		sendResponse(message);
  });
	// Delay při přenosu
	return true;
});

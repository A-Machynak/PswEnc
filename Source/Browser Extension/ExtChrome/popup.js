// EventListener - DOMContentLoaded
// Čeká, až se načte stránka
var dom;
document.addEventListener("DOMContentLoaded", function() {
    chrome.tabs.query({'active': true, 'lastFocusedWindow': true}, function (tabs) {
        // Zjistíme hostname
        var url = new URL(tabs[0].url);
        dom = url.host;
        var btn = document.getElementById("btn");
        var setBtn = document.getElementById("setBtn");
        // Zkontrolujeme, jestli se btn vůbec načetl
        if(setBtn) {
            setBtn.addEventListener("click", set);
        }
        if(btn) {
            btn.addEventListener("click", show);
        }
        // Hostname zapíšeme do divu
        document.getElementById("content-web").innerHTML = dom;
    });
});
// idk
function show() {
    var elem = document.getElementById("btn");
    elem.style.transition = "1.25s";
    elem.style.marginLeft = "500px";
    setTimeout(function() { 
        elem.parentNode.removeChild(elem); 
        document.getElementById("pswfield").style.opacity = "1";
    }, 1000);
}
function set() {
    var elem = document.getElementById("setBtn");
    var mkey = document.getElementById("setMk").value;
    console.log({"Hostname": dom, "MasterKey": mkey});
    elem.parentNode.parentNode.removeChild(document.getElementById("formpsw"));
    document.getElementById("loading").style.visibility = "visible";
    // Pošleme hostname do background.js
    // jakmile přijme, zavolá se function(response)
    chrome.runtime.sendMessage({"Hostname": dom, "MasterKey": mkey}, function(response) {
        // Schovat loading
        document.getElementById("loading").style.opacity = "0";
        // Upravit text
        document.getElementById("cnt-txt").innerHTML = "Password "
        // Zobrazit tlačítko na zobrazení hesla
        btn.style.visibility = "visible";
        // Přepíšeme heslo
        console.log(JSON.stringify(response));
        document.getElementById("pswfield").innerHTML = response.Password;
    });
}
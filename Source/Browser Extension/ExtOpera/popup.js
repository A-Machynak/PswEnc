// EventListener - DOMContentLoaded
// Čeká, až se načte stránka
document.addEventListener("DOMContentLoaded", function() {
    chrome.tabs.query({ active : true, currentWindow : true }, function (tabs) {
        // Zjistíme hostname
        var url = new URL(tabs[0].url);
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
        document.getElementById("setHostname").value = url.host;
    });
});

function set() {
    var elem = document.getElementById("setBtn");
    var mkey = document.getElementById("setMk").value;
    // Pokud uživatel přepsal doménu
    var newDom = document.getElementById("setHostname").value;
    elem.parentNode.parentNode.removeChild(document.getElementById("formpsw"));
    document.getElementById("loading").style.visibility = "visible";

    // Pošleme hostname do background.js
    // jakmile přijme a odpoví, zavolá se function(response)
    chrome.runtime.sendMessage({"Hostname": newDom, "MasterKey": mkey}, function(response){
        // Schovat loading
        document.getElementById("loading").style.opacity = "0";
        // Upravit text
        document.getElementById("cnt-txt").innerHTML = "Password "
        // Zobrazit tlačítko na zobrazení hesla
        btn.style.visibility = "visible";
        // Přepíšeme heslo
        //console.log(JSON.stringify(response));
        document.getElementById("pswfield").innerHTML = response.Password;
    });
}

// Zobrazí heslo
function show() {
    var elem = document.getElementById("btn");
    elem.style.transition = "1.25s";
    elem.style.marginLeft = "500px";
    setTimeout(function() { 
        elem.parentNode.removeChild(elem); 
        document.getElementById("pswfield").style.opacity = "1";
    }, 1000);
}
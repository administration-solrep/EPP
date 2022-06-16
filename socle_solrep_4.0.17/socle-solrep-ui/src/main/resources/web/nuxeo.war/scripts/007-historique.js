function initJsHistorique() {
    refreshIfNecessary();
}

function refreshIfNecessary() {
    if ($(".historique-running").length > 0) {
        setTimeout(refreshHistorique, 3000);
    }
}

function refreshHistorique() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/historique/rafraichir";
    var myRequest = {
        contentId: "historique-list",
        dataToSend: null,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        caller: this,
    };
    callAjaxRequest(myRequest, replaceHtmlFunctionAndRefreshHistorique);
}

var replaceHtmlFunctionAndRefreshHistorique = function replaceHtmlFunctionAndRefreshHistorique(containerID, result) {
    replaceHtmlFunction(containerID, result);
    // On rafraichit tant qu'il y a une migration en cours
    refreshIfNecessary();
};

function exportHistorique(id) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/historique/exporter";
    var myRequest = {
        contentId: null,
        dataToSend: "id=" + id,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        caller: this,
    };
    callAjaxRequest(myRequest, displaySuccessOrMessage, displaySimpleErrorMessage);
}

window.displaySuccessOrMessage = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.successMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.successMessageQueue);
    } else if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    }
};

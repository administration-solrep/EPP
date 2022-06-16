/**************************************************************************************/
/*******                        Scripts outil edition                          ********/
/**************************************************************************************/

window.displayEditLinkError = function () {
    constructAlert(
        errorMessageType,
        ["une erreur est survenue lors de la création du lien d'édition. Vous pouvez essayer de recharger votre page."],
        null
    );
};

window.createEditLinkReconnectSuccess = function (link) {
    document.getElementById("nglink").href = link;
    //On intialise les composant javascript avant de cliquer sur le lien pour permettre la navigation dans le site suite au clic
    initJavascript();
    document.getElementById("nglink").click();
};

$("#nglink").ready(function () {
    if ($("#editToken").length) {
        updateLinkEdition($("#editToken").val(), "/ngedit", "/connexion", [], createEditLinkReconnectSuccess);
    }
});

function updateLinkEdition(token, pathToCut, action, otherargs, successCallBack, callBackSpecificArgs) {
    if (token) {
        let link = "epg2ng://" + window.location.hostname;
        if (window.location.port != null && window.location.port != "") {
            link += ":" + window.location.port;
        }
        const path = window.location.pathname;
        const pathIndex = path.indexOf(pathToCut);
        if (pathIndex >= 0) {
            link += path.substring(0, pathIndex).replace("/app-ui", "") + action + "?";
            link += "_h=X-Authentication-Token:" + encodeURIComponent(token);
            if (otherargs.length > 0) {
                otherargs.forEach(function (arg) {
                    link += "&" + encodeURIComponent(arg.name) + "=" + encodeURIComponent(arg.value);
                });
            }
            successCallBack(link, callBackSpecificArgs);
        } else {
            displayEditLinkError();
        }
    } else {
        displayEditLinkError();
    }
}

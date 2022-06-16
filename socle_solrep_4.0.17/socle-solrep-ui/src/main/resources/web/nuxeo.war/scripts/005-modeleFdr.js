function buildRequest(ajaxUrl, data) {
    return (myRequest = {
        contentId: null,
        dataToSend: {
            id: data,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    });
}

function doSupprimerModele() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/fdr/modeles/supprimer";
    var myRequest = buildRequest(ajaxUrl, $("#idFdr").val());

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doUnlockModele() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/fdr/modele/unlock";
    var myRequest = buildRequest(ajaxUrl, $("#idModele").val());

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doRetourList() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/fdr/modele/retourList";
    var myRequest = buildRequest(ajaxUrl, $("#idModele").val());

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

function doDemandeValidationModele() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/fdr/modele/demandeValidation";
    var myRequest = buildRequest(ajaxUrl, $("#idModele").val());

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doAnnulerDemandeValidationModele() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/fdr/modele/annulerDemandeValidation";
    var myRequest = buildRequest(ajaxUrl, $("#idModele").val());

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doRefusValidationModele() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/fdr/modele/refusValidation";
    var myRequest = buildRequest(ajaxUrl, $("#idModele").val());

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doAccepterValidationModele() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/fdr/modele/accepterDemandeValidation";
    var myRequest = buildRequest(ajaxUrl, $("#idModele").val());

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doValiderModele() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/fdr/modele/valider";

    if ($("#editModeleFDR").length) {
        var myRequest = {
            contentId: null,
            dataToSend: $("#editModeleFDR").serialize() + "&sauvegarder=true",
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
        };
    } else {
        var myRequest = buildRequest(ajaxUrl, $("#idModele").val());
    }

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doModifierModele() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/fdr/modele/modifier";
    var myRequest = buildRequest(ajaxUrl, $("#idModele").val());

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function openModalImportGouvernement(id) {
    // L'ajout de l'attribut "data-controls" permet de ne pas avoir l'erreur "tmpDialog is null" lors de la fermeture de la modal
    $(".ACTION_IMPORT_GOUVERNEMENT").attr("data-controls", id);
    cleanAlerts();
    $("#btn-confirm-add-doc").focus();
}

function exportOrganigramme() {
    window.open($("#ajaxCallPath").val() + "/organigramme/export");
}

function doImporterGouvernement() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/organigramme/importer";
    var myRequest = {
        contentId: null,
        dataToSend: null,
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };
    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

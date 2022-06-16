function applyProfilParameters() {
    var form = $("#profilUtilisateurForm");
    var ajaxUrl = $("#ajaxCallPath").val() + "/profile/parametres";
    if (!isValidForm(form)) return false;
    var myRequest = {
        contentId: null,
        dataToSend: form.serialize(),
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };
    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function loadMetadatas() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/profile/metadatas";

    var myRequest = {
        contentId: "modal-user-profile-content",
        dataToSend: null,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, replaceHtmlFunctionAndInitSelectAndInitValidation, displaySimpleErrorMessage);
}

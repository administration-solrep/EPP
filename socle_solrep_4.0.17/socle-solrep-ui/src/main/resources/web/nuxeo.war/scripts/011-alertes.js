function createAlert(input) {
    var idRequete = $(input).parents(".quick-access-dropdown").find("input[name=id]").val();
    window.location.href =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) +
        "/suivi/alertes/creer?idRequete=" +
        idRequete +
        "#main_content";
}

var checkErrorOrGoToAlertBloc = function (contentId, result) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        $("#reload-loader").css("display", "block");
        window.location.href =
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + "/suivi#suivi_menu_alerte";
    }
};
function saveAlerte() {
    var $form = $("#alerte-form");
    if (isValidForm($form)) {
        var ajaxUrl = $("#ajaxCallPath").val() + "/suivi/alertes/sauvegarder";
        var myRequest = {
            contentId: null,
            dataToSend: $form.serialize(),
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            caller: this,
            loadingButton: $("#alerte-save-button"),
        };
        callAjaxRequest(myRequest, checkErrorOrGoToAlertBloc, displaySimpleErrorMessage);
    }
}

function suspendAlert(input) {
    const ajaxUrl = $("#ajaxCallPath").val() + "/suivi/alertes/suspendre";
    doActivateOrSuspendAlert(input, ajaxUrl);
}

function activateAlert(input) {
    const ajaxUrl = $("#ajaxCallPath").val() + "/suivi/alertes/activer";
    doActivateOrSuspendAlert(input, ajaxUrl);
}

function doActivateOrSuspendAlert(input, ajaxUrl) {
    const id = $(input).parents(".quick-access-dropdown").find("input[name=id]").val();
    const myRequest = {
        contentId: null,
        dataToSend: { id: id },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
        caller: this,
    };
    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function deleteAlert(input) {
    const ajaxUrl = $("#ajaxCallPath").val() + "/suivi/alertes/supprimer";
    doActivateOrSuspendAlert(input, ajaxUrl);
}

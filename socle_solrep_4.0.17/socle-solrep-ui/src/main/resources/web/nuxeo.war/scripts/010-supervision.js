function chargeOngletSupervision(me) {
    var targetURL = "/admin/supervision/inactif";
    var myRequest = {
        contentId: $(me).attr("aria-controls"),
        dataToSend: null,
        method: "GET",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + targetURL,
        isChangeURL: false,
        overlay: null,
        caller: me,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction, tabLoadError);
}

function chargeOngletSupervisionActif(me) {
    var targetURL = "/admin/supervision/actif";
    var myRequest = {
        contentId: $(me).attr("aria-controls"),
        dataToSend: null,
        method: "GET",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + targetURL,
        isChangeURL: false,
        overlay: null,
        caller: me,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction, tabLoadError);
}

function getUsersNotConnected() {
    var dateForm = $("#dateForm");
    if (isValidForm(dateForm)) {
        var targetURL = "/admin/supervision/resultats";
        var myRequest = {
            contentId: "result-list",
            dataToSend: dateForm.serialize(),
            method: "GET",
            dataType: "html",
            ajaxUrl: $("#ajaxCallPath").val() + targetURL,
            isChangeURL: false,
            overlay: null,
            loadingButton: $("#searchButton"),
        };

        callAjaxRequest(myRequest, replaceHtmlFunction, tabLoadError);
    }
}

function exportConnectedUsersPdf() {
    doExportConnectedUsers("telecharger/pdf");
}

function exportConnectedUsersXls() {
    doExportConnectedUsers("telecharger/xls");
}

function doExportConnectedUsers(url) {
    let downloadUrl =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) +
        "/admin/supervision/" +
        url;
    window.open(downloadUrl, "_blank");
}

function exportNotConnectedUsersPdf() {
    const date = $(".tabulation").find("#dateConnexion").val();
    doExportConnectedUsers("telecharger/pdf?dateConnexion=" + date);
}

function exportNotConnectedUsersXls() {
    const date = $(".tabulation").find("#dateConnexion").val();
    doExportConnectedUsers("telecharger/xls?dateConnexion=" + date);
}

function reinitSearchSupervision() {
    var targetURL = "/admin/supervision/reinit";
    var myRequest = {
        contentId: null,
        dataToSend: null,
        method: "GET",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + targetURL,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, hardReloadPage, displaySimpleErrorMessage);
}

function loadMailContent() {
    let destinataires = [];
    $(".tabulation__content--active table.custom-table > tbody > tr").each(function () {
        destinataires.push($(this).attr("data-id"));
    });

    doLoadMailContent(destinataires);
}

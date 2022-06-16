//Put SWORD solrep specific scripts for appli here
$(document).ready(function () {
    initJsHistorique();
    initJsActualites();
    initModalesSauvegarde();
    initJsStats();
    initRapidSearch();
});

function initRapidSearch() {
    if ($("#rapidSearchForm").length > 0) {
        $("#rapidSearchForm").on("submit", launchRechercheRapide);
    }
}

function actualiserMailbox() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/corbeille/actualiser";
    var myRequest = {
        contentId: "mailboxListTree",
        dataToSend: "&masquerCorbeilles=" + $("#masquer_corbeilles").prop("checked"),
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#mailboxListTree").find(".overlay").first().attr("id"),
        caller: this,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction);
}

function doLaunchRechercheRapide(event, field) {
    event.preventDefault();
    if (isValidForm($("#rapidSearchForm"))) {
        const critere = $("#rapidSearchForm #" + field).val();
        url =
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) +
            "/recherche/rapide?" +
            field +
            "=" +
            critere +
            "#main_content";
        if (window.location.href.endsWith(url)) {
            window.location.reload(true);
        } else {
            window.location.href = url;
        }
    }
}

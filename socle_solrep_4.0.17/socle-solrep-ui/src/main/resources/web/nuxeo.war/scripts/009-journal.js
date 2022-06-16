function reinitJournalSearch() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/" + $("#dossierLinkId").val() + "/journal";
    var myRequest = {
        contentId: null,
        dataToSend: $("#dossierLinkId").serialize(),
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: true,
        url: $("#listeDossiers").data("url"),
        overlay: "reload-loader",
    };
    callAjaxRequest(myRequest, hardReloadPage);
}

function doJournalSearch() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/journal/journalDossier";
    var data = $("#dossierLinkId").serialize() + "&" + $("#dossierId").serialize() + "&" + $("#searchForm").serialize();
    var myRequest = {
        contentId: "resultList",
        dataToSend: data,
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: true,
        url: $("#listeDossiers").data("url"),
        overlay: null,
        loadingButton: $("#journalSearch"),
    };
    callAjaxRequest(myRequest, replaceHtmlFunction);
}

function doReinitFilterSearch() {
    window.location.href =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) +
        $("#dataUrl").val() +
        "#main_content";
}

function doSearchJournalTechnique() {
    if (isValidForm($("#searchForm"))) {
        var ajaxUrl = $("#ajaxCallPath").val() + $("#ajaxSearchEndpoint").val();
        var mydatas = $("#searchForm").serialize() + "&";
        $("#journalResults")
            .find("select[data-isForm='true']")
            .each(function () {
                if (this.value !== undefined && mydatas.indexOf(this.name + "=" + this.value) < 0) {
                    mydatas += this.name + "=" + this.value + "&";
                }
            });
        mydatas = mydatas.substring(0, mydatas.length - 1);
        var myRequest = {
            contentId: "journalResults",
            dataToSend: mydatas,
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            url: $("#journalResults").data("url"),
            isChangeURL: true,
            overlay: null,
            loadingButton: $("#journalTechniqueSearch"),
        };

        callAjaxRequest(myRequest, replaceHtmlFunction, displaySimpleErrorMessage);
    }
}

function exportJournalTechniqueSearchResult() {
    var ajaxUrl = $("#ajaxCallPath").val() + $("#dataAjaxExportUrl").val();
    var myRequest = {
        contentId: null,
        dataToSend: $("#searchForm").serialize(),
        method: "GET",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        xhrFields: {
            responseType: "blob", // to avoid binary data being mangled on charset conversion
        },
        loadingButton: $(".ACTION_RECHERCHE_ADMIN_ACTION_EXPORT"),
    };

    callAjaxRequest(myRequest, downloadFileWithoutMessage, displaySimpleErrorMessage);
}

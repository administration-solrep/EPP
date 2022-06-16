function exportStatsZip(input) {
    let ids = [];
    $(".custom-table tr input[type=checkbox]:checked").each(function () {
        ids.push($(this).data("id"));
    });

    const ajaxUrl = $("#ajaxCallPath").val() + "/stats/exporter/zip";
    const myRequest = {
        contentId: null,
        dataToSend: { ids: ids },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function removeZipStats() {
    let file = $("#fileName").val();
    const ajaxUrl = $("#ajaxCallPath").val() + "/stats/archive/supprimer";
    const myRequest = {
        contentId: null,
        dataToSend: { fileName: file },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function sendStatExcelsByMail(input) {
    sendStatByMail(input, "envoyer/excel");
}

function sendStatPdfsByMail(input) {
    sendStatByMail(input, "envoyer/pdf");
}

function sendStatByMail(input, url) {
    const stat = $(input).parents(".js-actions").data("stat");

    const ajaxUrl = $("#ajaxCallPath").val() + "/stats/" + url;
    const myRequest = {
        contentId: null,
        dataToSend: { stat: stat },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrToast, displaySimpleErrorMessage);
}

function downloadStatPDF(input) {
    doStatDownload(input, "telecharger/pdf");
}

function downloadStatExcel(input) {
    doStatDownload(input, "telecharger/excel");
}

function doStatDownload(input, url) {
    const $container = $(input).parents(".js-actions");
    const stat = $container.data("stat");
    const organigramme = $container.data("organigramme");

    let urlPdf = $("#basePath").val() + "stats/" + url + "?stat=" + stat;
    if (organigramme) {
        urlPdf += "&organigramme=" + organigramme;
    }
    window.open(urlPdf, "_blank");
}

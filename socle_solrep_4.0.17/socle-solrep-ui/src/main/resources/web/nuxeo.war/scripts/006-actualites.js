function initJsActualites() {
    var myTable = $("[id^=REMOVE_NEWS]").closest(".tableForm");
    var tableCheckboxes = myTable.find("input");
    tableCheckboxes.prop("checked", false); // on réinitialise la sélection du tableau des actualités car on peut supprimer des lignes de ce tableau
}

// Réinitialiser les filtres -> on recharge la page
function reinitFilterNews() {
    window.location.href =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) + "/admin/actualites#main_content";
}

// Afficher la liste des actualités (écran gestion des actualités)
function getResults() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/admin/actualites/resultats";

    var myRequest = {
        contentId: "listeActualites",
        dataToSend: $("#searchForm :input[value!='null']").serialize(),
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        url: $("#listeActualites").data("url"),
        isChangeURL: true,
        overlay: null,
        loadingButton: $("#btn-filtrer-actualites"),
    };

    callAjaxRequest(myRequest, replaceHtmlFunction, displaySimpleErrorMessage);
}

// Sauvegarder une actualité
function saveActualite(event) {
    if (isValidForm($("#creation-actualite-form")) && isNotEmptyTinyMce("Contenu de l'actualité", null)) {
        let hasPj = false;
        if ($("input[name='documentFileAdd']").length) {
            hasPj = true;
        }
        let saveActualiteRequest = {
            contentId: null,
            dataToSend: {
                dateEmission: $("#dateEmission").val(),
                dateValidite: $("#dateValidite").val(),
                objet: $("#objet").val(),
                statut: $("input[name='statut']:checked").val(),
                contenu: encodeHTMLSpecialChar(tinymce.get("mce").getContent()),
                hasPj: hasPj,
            },
            method: "POST",
            dataType: "html",
            ajaxUrl: $("#ajaxCallPath").val() + "/admin/actualites/sauvegarde",
            isChangeURL: false,
            overlay: null,
            loadingButton: event.target,
        };

        uploadFiles(
            getFilesFromFileInput("documentFileAdd"),
            event.target,
            (batchId) => {
                saveActualiteRequest.dataToSend.uploadBatchId = batchId;
                callAjaxRequest(saveActualiteRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
            },
            displaySimpleErrorMessage
        );
    }
}

function reinitFilterHistoNews() {
    window.location.href =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) + "/historiqueActualites";
}

function getHistoResults() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/historiqueActualites";
    var myRequest = {
        contentId: "listeHistoriqueActualites",
        dataToSend: $("#searchForm").serialize(),
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        url: $("#listeHistoriqueActualites").data("url"),
        isChangeURL: true,
        overlay: null,
        loadingButton: $("#btn-filtrer-actualites"),
    };

    callAjaxRequest(myRequest, replaceHtmlFunction, displaySimpleErrorMessage);
}

function readNews(news) {
    var id = news.substring("actualite_".length);
    var ajaxUrl = $("#ajaxCallPath").val() + "/actualites/read";
    var myRequest = {
        contentId: null,
        dataToSend: { actualiteId: id },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, function () {});
}

function doRemoveActualites(form) {
    var myTable = $("[id^=REMOVE_NEWS]").closest(".tableForm");
    var actualites = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            actualites.push($(this).closest("tr").attr("data-id"));
        });

    var ajaxUrl = $("#ajaxCallPath").val() + "/actualites/supprimer";
    var myRequest = {
        contentId: null,
        dataToSend: {
            idActualites: actualites,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
    };
    $("#reload-loader").css("display", "block");

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

var onLoadContentModal = function replaceHTMLinContainerAndInitTextArea(containerID, result) {
    $("#" + containerID).html(result);
    if (tinymce.get("mce_mail")) {
        tinymce.remove("#mce_mail");
    }
    initTextAreaMessage("mce_mail");
    initAsyncSelect();
};

function loadDossierMailContent() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/actionDossier/mail/contenu";
    var myRequest = {
        contentId: "modal-dossier-mail-content",
        dataToSend: null,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, onLoadContentModal, displaySimpleErrorMessage);
}

function sendDossierMail(event) {
    var form = $("#formDossierMail");
    if (isValidForm(form)) {
        var ajaxUrl = $("#ajaxCallPath").val() + "/actionDossier/mail/sendDossier";
        var options = $("#model-mail-destinataire option");
        var destinataires = [];
        $.map(options, function (option) {
            destinataires.push(option.text);
        });
        var destinataireIds = [];
        $.map(options, function (option) {
            destinataireIds.push(option.value);
        });

        var myRequest = {
            contentId: null,
            dataToSend: {
                destinataires: destinataires,
                copie: $('input[name="copie"]:checked').val(),
                destinataireIds: destinataireIds,
                autres: $("#autres").val(),
                objet: $("#objet").val(),
                message: encodeHTMLSpecialChar(tinymce.get("mce_mail").getContent()),
                dossierId: $("#dossierId").val(),
            },
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            loadingButton: $(event.target),
        };
        callAjaxRequest(myRequest, showMessage, displaySimpleErrorMessage);
    }
    closeModal($("#modal-dossier-send-mail").get(0));
}

function getSelectedContent() {
    var myTable = $("[id^=table_mass_action_toolbar]").closest(".tableForm");
    var dossiers = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            dossiers.push($(this).closest("tr").attr("data-id"));
        });
    return dossiers;
}

window.downloadFile = function (contentId, result, caller, extraDatas, xhr) {
    downloadFileWithoutMessage(contentId, result, caller, extraDatas, xhr);
    showMessage();
};

window.downloadFileWithoutMessage = function (contentId, result, caller, extraDatas, xhr) {
    var disposition = xhr.getResponseHeader("content-disposition");
    var matches = /"([^"]*)"/.exec(disposition);
    var filename = matches != null && matches[1] ? matches[1] : "";
    var link = document.createElement("a");
    link.href = window.URL.createObjectURL(result);
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    hideReloadLoader();
};

function doExporterTableauDossier() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/actionDossier/export/excel";
    var myRequest = {
        contentId: null,
        dataToSend: {
            idDossiers: getSelectedContent(),
        },
        method: "POST",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        xhrFields: {
            responseType: "blob", // to avoid binary data being mangled on charset conversion
        },
    };
    callAjaxRequest(myRequest, downloadFile, displaySimpleErrorMessage);
}

function chargeOngletDossier(onglet, me, async) {
    const modale = $("#modale-sauvegarde-dossier");
    modale.attr("data-confirmed", "yes");

    replaceHtmlFunction($(me).attr("aria-controls"), "Chargement");

    var targetURL = "/dossier/" + $("#dossierId").val() + "/" + onglet;
    var myRequest = {
        contentId: "consult_dossier_content",
        dataToSend: $("#dossierLinkId").serialize(),
        method: "GET",
        async: async,
        dataType: "html",
        url:
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + targetURL,
        ajaxUrl: $("#ajaxCallPath").val() + targetURL,
        isChangeURL: true,
        overlay: null,
        caller: me,
        extraDatas: {
            triggerBtn: $(me),
            dossierLocked: $("#LEVER_VERROU_DOSSIER").length > 0 ? true : false,
        },
    };

    callAjaxRequest(myRequest, loadOngletData, tabLoadErrorExtraDataTarget);
}

function doAddDocumentFondDeDossier() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/ajout/fichier/fdd";

    var filesCtl = getFilesFromFileInput("documentFileAdd");

    var formData = new FormData();

    formData.append("dossierId", encodeURIComponent($("#dossierId").val()));
    formData.append("groupId", encodeURIComponent($("#groupId").val()));

    filesCtl.forEach(function (inp) {
        formData.append("documentFile", inp);
    });

    var myRequest = {
        contentId: null,
        dataToSend: formData,
        processData: false,
        contentType: false,
        enctype: "multipart/form-data",
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: $("#btn-confirm-add-doc"),
    };
    closeModal($("#modal-add-document").get(0));
    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doDossierSauvegardeModale(callback, ...params) {
    doPopModaleSauvegarde("dossier", callback, ...params);
}

function updateOngletDataCallback(modale, encode) {
    pushURLFunction(encode);
    var url = window.location.pathname.split("/");
    $(modale).attr("data-onglet", url[url.length - 1]);
    $(modale).attr("data-onglet-parent", url[url.length - 2]);
}

function updateOngletData(evt, targetURL) {
    var encode = encodeURI(
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) + targetURL
    );

    doDossierSauvegardeModale(evt, updateOngletDataCallback, "#modale-sauvegarde-dossier", encode);
}

function verouilleDossier() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/verrouille";
    var myRequest = {
        contentId: null,
        dataToSend: $("#dossierId").serialize(),
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function deverouilleDossier() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/deverrouille";
    var myRequest = {
        contentId: null,
        dataToSend: $("#dossierId").serialize(),
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function deverouilleAdminDossier() {
    doInitModal("#init-modal-deverouillage");
    const idModal = $("#init-modal-deverouillage").data("controls");
    const modal = $("#" + idModal);
    modal.addClass("interstitial-overlay__content--visible");
    modal.find("button")[0].focus();
}

function unreadDossier() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/dossier/unread";
    var myRequest = {
        contentId: null,
        dataToSend: { dossierId: $("#dossierId").val(), dossierLinkId: $("#dossierLinkId").val() },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, goPreviousPage, displaySimpleErrorMessage);
}

function doSubstitution() {
    let idDossier = $("#idDossier").val();
    let idModele = $("#idModele").val();

    showReloadLoader();

    window.location = $("#basePath").val() + "dossier/" + idDossier + "/substitution/valider?idModele=" + idModele;
}

function updateStatutLockDossierAria(isDossierLock) {
    if (isDossierLock) {
        $("#statutLockDossierAria").text("Le dossier a été verrouillé");
    } else {
        $("#statutLockDossierAria").text("Le dossier a été déverrouillé");
    }
}

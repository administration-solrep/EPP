function sendMail() {
    var modal = $("#modal-send-mail");
    if (isValidForm(modal) && isNotEmptyTinyMce("Message", "modal-mail-content")) {
        var ajaxUrl = $("#ajaxCallPath").val() + "/email/envoyer";
        var myRequest = {
            contentId: null,
            dataToSend: {
                expediteur: $("#expediteur").val(),
                objet: $("#objet").val(),
                message: encodeHTMLSpecialChar(tinymce.get("mce").getContent()),
            },
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: "reload-loader",
        };

        uploadFiles(
            getFilesFromFileInput("documentFileAdd"),
            $("#btn-save-actualite"),
            (batchId) => {
                myRequest.dataToSend.uploadBatchId = batchId;
                closeModal(modal.get(0)); // fermer la modale
                callAjaxRequest(myRequest, reloadPage, displaySimpleErrorMessage);
            },
            displaySimpleErrorMessage
        );
    }
}

function loadMailContentUserSearch() {
    let destinataires = [];
    $("#listeUsers")
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            destinataires.push($(this).closest("tr").attr("data-id"));
        });

    doLoadMailContent(destinataires);
}

var onLoadMCEModal = function replaceHTMLinContainerAndInitTextArea(containerID, result) {
    $("#" + containerID).html(result);
    if (tinymce.get("mce")) {
        tinymce.remove("#mce");
    }
    initTextAreaMessage("mce");
    initAsyncSelect();
};

function doLoadMailContent(destinataires) {
    const ajaxUrl = $("#ajaxCallPath").val() + "/email/contenu";

    const myRequest = {
        contentId: "modal-mail-content",
        dataToSend: { destinataires: destinataires },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, onLoadMCEModal, displaySimpleErrorMessage);
}

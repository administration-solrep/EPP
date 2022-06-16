function doDeleteProfil() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/profile/ficheProfil/supprimer";

    var myRequest = {
        contentId: null,
        dataToSend: {
            id: $("#idProfil").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrGoPrevious, displaySimpleErrorMessage);
}

function createProfilFormSubmit() {
    profilForm = $("#profil_form");
    fonctions = [];
    $('input[id^="fonctionAttribuee-"]:checked').each(function () {
        var id = $(this).attr("id").substring(18, $(this).attr("id").length);
        fonctions.push(id);
    });
    if (isValidForm(profilForm)) {
        const ajaxUrl =
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + "/admin/profile/creer";
        var myRequest = {
            contentId: null,
            dataToSend: {
                label: $("#profil_label").val(),
                fonctions: fonctions,
            },
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
        };

        callAjaxRequest(myRequest, checkErrorOrGoPrevious, displaySimpleErrorMessage);
    }
}

function updateProfilFormSubmit() {
    fonctions = [];
    $('input[id^="fonctionAttribuee-"]:checked').each(function () {
        var id = $(this).attr("id").substring(18, $(this).attr("id").length);
        fonctions.push(id);
    });
    const ajaxUrl =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) + "/admin/profile/modifier";
    var myRequest = {
        contentId: null,
        dataToSend: {
            id: $("#profil_id").val(),
            label: $("#profil_label").val(),
            fonctions: fonctions,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrGoListProfil, displaySimpleErrorMessage);
}

window.checkErrorOrGoListProfil = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        window.location.href =
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + "/admin/profile/liste#main_content";
    }
};

function goToEditProfil() {
    const idProfil = $("#idProfil").val();
    window.location.href =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) +
        "/admin/profile/modification?id=" +
        idProfil +
        "#main_content";
}

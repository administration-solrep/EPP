function previousPageLetter(me) {
    $(".pagination__item--is-active").prev().children(".pagination__page-link.link")[0].click();
    showHideButtonPagination();
}

function nextPageLetter(me) {
    $(".pagination__item--is-active").next().children(".pagination__page-link.link")[0].click();
    showHideButtonPagination();
}

function goToLetter(index, from) {
    var targetURL = window.location.toString().includes("admin") ? "/admin/users/liste" : "/recherche/users/liste";
    var userData = "";
    var username = $("#search_user_field").val();

    if (index) {
        userData = "index=" + index;
    }

    var myRequest = {
        contentId: "listeUsers",
        dataToSend: userData + "&recherche=" + username,
        method: "GET",
        dataType: "html",
        url:
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + targetURL,
        ajaxUrl: $("#ajaxCallPath").val() + targetURL,
        isChangeURL: true,
        overlay: $("#listeUsers").find(".overlay").first().attr("id"),
        caller: this,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction, tabLoadError);
}

function showHideButtonPagination() {
    var previousButton = $(".pagination__item--previous-page");
    if (previousButton !== undefined) {
        if ($(previousButton).next().hasClass("pagination__item--is-active")) {
            $(previousButton).addClass("visibility-hidden");
        } else {
            $(previousButton).removeClass("visibility-hidden");
        }
    }

    var nextButton = $(".pagination__item--next-page");
    if (nextButton !== undefined) {
        if ($(nextButton).prev().hasClass("pagination__item--is-active")) {
            $(nextButton).addClass("visibility-hidden");
        } else {
            $(nextButton).removeClass("visibility-hidden");
        }
    }
}

function searchUser(username) {
    var targetAjaxURL = window.location.toString().includes("admin") ? "/admin/users/liste" : "/recherche/users/liste";
    var targetURL = $("#data-url").val() ? $("#data-url").val() : targetAjaxURL;

    var userData = "";
    if (username) {
        userData = "recherche=" + username;
    } else {
        if ($("#search_user_field").val()) {
            $("#search_user_field").val("");
        }
    }

    var myRequest = {
        contentId: "listeUsers",
        dataToSend: userData,
        method: "GET",
        dataType: "html",
        url:
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + targetURL,
        ajaxUrl: $("#ajaxCallPath").val() + targetAjaxURL,
        isChangeURL: true,
        overlay: $("#listeUsers").find(".overlay").first().attr("id"),
        caller: this,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction, tabLoadError);
}

function refreshDateInputActivation() {
    if ($("#temporaire-oui").is(":checked")) {
        $("#date-fin-input").prop("disabled", false);
        cleanPreviousErrorOrSuccess($("#date-fin-input"));
        $("#date-fin-input").siblings(".form-date-picker-input__open-calendar-button").prop("disabled", false);
    } else {
        $("#date-fin-input").prop("disabled", true);
        cleanPreviousErrorOrSuccess($("#date-fin-input"));
        $("#date-fin-input").siblings(".form-date-picker-input__open-calendar-button").prop("disabled", true);
    }
}

function doDeleteUser() {
    var id = $("#idUser").val();
    var ajaxUrl = $("#ajaxCallPath").val() + "/admin/user/" + id + "/delete";
    var myRequest = {
        contentId: null,
        dataToSend: null,
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: $(".ACTION_ADMIN_USER_EDIT_DELETE"),
    };

    callAjaxRequest(myRequest, checkErrorOrGoPrevious, displaySimpleErrorMessage);
}

function createUserFormSubmit() {
    userForm = $("#user_form");
    if (isValidForm(userForm)) {
        const ajaxUrl =
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + "/admin/user/create";
        var myRequest = {
            contentId: null,
            dataToSend: userForm.serialize(),
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: "reload-loader",
        };

        callAjaxRequest(myRequest, checkErrorOrGoUserFiche, displaySimpleErrorMessage);
    }
}

window.checkErrorOrGoUserFiche = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        showReloadLoader();
        window.location.replace($("#basePath").val() + "admin/user/" + jsonResponse.data + "#main_content");
    }
};

function doSearchDeleteUser() {
    var myTable = $("#listeUsers");
    var data = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            data.push($(this).closest("tr").attr("data-id"));
        });

    var ajaxUrl = $("#ajaxCallPath").val() + "/rechercher/supprimer";
    var myRequest = {
        contentId: null,
        dataToSend: {
            userIds: data,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrHardReload, displaySimpleErrorMessage);
}

function doAddUsersToFavori() {
    var myTable = $("#listeUsers");
    var data = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            data.push($(this).closest("tr").attr("data-id"));
        });

    var ajaxUrl = $("#ajaxCallPath").val() + "/recherche/favoris/utilisateurs/ajouter";
    var myRequest = {
        contentId: null,
        dataToSend: {
            userIds: data,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

// Sauvegarder le paramétrage de gestion de l'accès
function saveGestionAcces() {
    var messageBanniere;
    if ($("#format").val() == "html") {
        // tinymce
        messageBanniere = tinymce.get("mce") ? tinymce.get("mce").getContent() : "";
    } else {
        messageBanniere = $("#mce").val();
    }

    var myRequest = {
        contentId: null,
        dataToSend: {
            restrictionAcces: $("input[name='restrictionAcces']:checked").val(),
            descriptionRestriction: $("#descriptionRestriction").val(),
            affichageBanniere: $("input[name='affichageBanniere']:checked").val(),
            messageBanniere: isHTML(messageBanniere) ? encodeHTMLSpecialChar(messageBanniere) : messageBanniere,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl:
            $("#ajaxCallPath").val().substring(0, $("#ajaxCallPath").val().length) + "/admin/user/acces/sauvegarde",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

$(document).ready(function () {
    $("#search_user_field").keyup(function (event) {
        if (event.keyCode === 13) {
            searchUser($("#search_user_field").val());
        }
    });
});

function exportUserSearch() {
    const idUsers = [];
    var myTable = $("#table-users");
    $(myTable)
        .find(":input:checked.js-custom-table-line-check")
        .each(function () {
            idUsers.push($(this).closest("tr").attr("data-id"));
        });

    if (idUsers.length == 0) {
        // sélectionner tous les utilisateurs
        $(myTable)
            .find("tr.table-line")
            .each(function () {
                idUsers.push($(this).attr("data-id"));
            });
    }

    exportByMail(idUsers, "USER");
}

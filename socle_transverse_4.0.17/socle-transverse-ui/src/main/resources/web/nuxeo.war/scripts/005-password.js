/*********************************/
/*        JS for password        */
/*********************************/

$("#reset_password_dialog :button:last").keydown(function (event) {
    const isEventKeyTab = event.which == 9;
    if ($("this:focus") && isEventKeyTab) {
        event.preventDefault();
        // ce serait mieux de donner le focus au 1er élément focusable de la modale mais le selector :focusable n'est disponible qu'avec jquery-ui
        $("#reset_password_dialog :input:first").focus();
    }
});

$("#reset_password_form").submit(function (event) {
    const ajaxUrl =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) + "/user/password/update";
    var myRequest = {
        contentId: null,
        dataToSend: $(this).serialize(),
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: $(this).find("button[type='submit']"),
    };

    callAjaxRequest(myRequest, passwordResetSuccess, passwordResetError);

    event.preventDefault();
});

window.passwordResetSuccess = function (containerID, result, caller, extraDatas) {
    var jsonResponse = JSON.parse(result);
    var messages = jsonResponse.messages;
    cleanAlerts();
    if (messages.successMessageQueue.length > 0) {
        $("#reset_password_form").remove();
        $("#reset_passwword_success").show();
    } else if (messages.dangerMessageQueue.length > 0) {
        $.each(messages.dangerMessageQueue, function (index, item) {
            item.alertOrigin = "reset_password_dialog";
        });
        constructAlertContainer(messages.dangerMessageQueue);
    }
};

window.passwordResetError = function (containerID, result, caller, extraDatas) {
    const detailedErrorMessage = $("#reset_password_error > span");
    if (detailedErrorMessage.length) {
        detailedErrorMessage.remove();
    }

    if (result.status == "400") {
        $("#reset_password_error")
            .append("<span>" + result.responseText + "</span>")
            .show();
    } else {
        $("#reset_password_error").append("<span> Une erreur serveur est survenue </span>").show();
    }
    $("#reset_password_error").focus();
};

$("#update_password_form").submit(function (event) {
    if (!isValidForm($("#update_password_form"))) {
        return false;
    }
    const ajaxUrl =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) + "/user/password/updateForUser";
    var myRequest = {
        contentId: null,
        dataToSend: $(this).serialize(),
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: $(this).find("button[type='submit']"),
    };

    const needsLogout = $("#logout_on_success").val() === "true";

    if (needsLogout) {
        callAjaxRequest(myRequest, passwordUpdateSuccessLogout, passwordUpdateError);
    } else {
        callAjaxRequest(myRequest, passwordUpdateSuccess, passwordUpdateError);
    }

    event.preventDefault();
});

window.passwordUpdateSuccessLogout = function (containerID, result, caller, extraDatas) {
    checkMessages(result, goToLogoutURL);
};

window.goToLogoutURL = function () {
    const logoutUrl =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) + "/logout";
    window.location.replace(logoutUrl);
};

window.passwordUpdateSuccess = function (containerID, result, caller, extraDatas) {
    checkMessages(result, goPreviousPage);
};

checkMessages = function (result, callback) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length == 0 && messagesContaineur.warningMessageQueue.length == 0) {
        callback();
    } else {
        if (messagesContaineur.dangerMessageQueue.length > 0) {
            constructAlertContainer(messagesContaineur.dangerMessageQueue);
        } else {
            constructAlertContainer(messagesContaineur.warningMessageQueue);
        }
    }
};

window.passwordUpdateError = function (result) {
    showErrors(new Array(result.responseText), $("#password_input"));
};

$("#ask_reset_password_form").submit(function (event) {
    const ajaxUrl =
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) + "/user/password/reset";
    var myRequest = {
        contentId: null,
        dataToSend: {
            userId: $("#username").val(),
            email: $("#mail").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: $(this).find("button[type='submit']"),
    };

    callAjaxRequest(myRequest, passwordAskResetSuccess, displaySimpleErrorMessage);
    event.preventDefault();
});

function passwordAskResetSuccess(elementId, result, caller, extraDatas, myXhr) {
    var jsonResponse = JSON.parse(result);
    var messages = jsonResponse.messages;
    cleanAlerts();
    if (messages.dangerMessageQueue.length > 0) {
        constructAlertContainer(messages.dangerMessageQueue);
    } else if (messages.successMessageQueue.length > 0) {
        window.location.replace(
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + "/login?resetpwd=true"
        );
    }
}

var notificationDelay;

function initNotifications() {
    // Affichage des notifications de rafraichissement de corbeille ?
    if ($(".notif-corbeille").length > 0) {
        initLastUpdate(); // pour initialiser la date de dernier contrôle en session
        getNotificationDelay();
    }
    // Affichage des notifications de rafraichissement de corbeille ?
    if ($(".notif-evenement").length > 0) {
        initLastUpdate(); // pour initialiser la date de dernier contrôle en session
        getNotificationDelay();
    }
    // Rechargement du cache TDR ?
    if ($(".notif-reloadCache").length > 0) {
        initLastUpdate(); // pour initialiser la date de dernier contrôle en session
        getReloadCacheTDRDelay();
    }
}

function initLastUpdate() {
    var now = new Date();
    const myRequest = {
        contentId: null,
        dataToSend: { firstLoad: now },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/notification/init",
        isChangeURL: false,
        async: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, showMessage, displaySimpleErrorMessage);
}

function getNotificationDelay() {
    const myRequest = {
        contentId: null,
        dataToSend: {},
        method: "GET",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/notification/delai",
        isChangeURL: false,
        async: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, setNotificationTimeout, displaySimpleErrorMessage);
}

function getReloadCacheTDRDelay() {
    const myRequest = {
        contentId: null,
        dataToSend: {},
        method: "GET",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/notification/delai",
        isChangeURL: false,
        async: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, setReloadCacheTDRTimeout, displaySimpleErrorMessage);
}

function setReloadCacheTDRTimeout(containerID, result) {
    setNotifTimeout(result, reloadCacheTDRFunc);
}

function setNotificationTimeout(containerID, result) {
    setNotifTimeout(result, checkNotificationsFunc);
}

function setNotifTimeout(result, func) {
    notificationDelay = result;

    setTimeout(func, notificationDelay);
}

var reloadCacheTDRFunc = function reloadCacheTDR() {
    const myRequest = {
        contentId: null,
        dataToSend: {},
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/notification/rechargerCacheTDR",
        isChangeURL: false,
        async: false,
        overlay: null,
        ignoreChronology: true,
    };

    callAjaxRequest(myRequest, cacheReloaded, displaySimpleErrorMessage);
};

function cacheReloaded() {
    setTimeout(reloadCacheTDRFunc, notificationDelay);
}

var checkNotificationsFunc = function checkNotifications() {
    // Quelle corbeille ?
    var now = new Date();
    console.log(now);
    let idCorbeille = "";
    $(".notif-corbeille")
        .find("input[data-isForm='true']")
        .each(function () {
            if (this.name === "idCorbeille" && this.value !== undefined) {
                idCorbeille = this.value;
                console.log("idCorbeille : " + idCorbeille);
            }
        });

    // Quel événement ?

    let idEvenement = "";
    if ($("#basePath").val().includes("epp")) {
        $(".notif-evenement")
            .find("#idComm")
            .each(function () {
                idEvenement = this.value;
                console.log("idComm : " + idEvenement);
            });
    } else if ($("#basePath").val().includes("epg")) {
        $(".notif-evenement")
            .find("#idMessage")
            .each(function () {
                idEvenement = this.value;
                console.log("idMessage : " + idEvenement);
            });
    }

    if (idCorbeille != "" || idEvenement != "") {
        const myRequest = {
            contentId: null,
            dataToSend: {},
            method: "GET",
            dataType: "html",
            ajaxUrl:
                $("#ajaxCallPath").val() + "/notification?idCorbeille=" + idCorbeille + "&idEvenement=" + idEvenement,
            isChangeURL: false,
            async: false,
            overlay: null,
            ignoreChronology: true,
        };

        callAjaxRequest(myRequest, displayNotification, displaySimpleErrorMessage);
    }
};

function displayWarning(messageLabel, buttonTitle, type) {
    let message = $("<span></span>")
        .append(DOMPurify.sanitize(messageLabel))
        .addClass("warning_refresh_notification")
        .attr("id", "warning_refresh_notification");

    let button = $("<button></button>")
        .append("Rafraîchir")
        .attr("type", "button")
        .addClass("base-btn base-btn--button base-btn--default base-btn--colored-link")
        .attr("title", buttonTitle);

    button.focus();
    button.on("click", function () {
        window.location.reload();
    });

    message.append(button);
    window.scrollTo({ top: 0, behavior: "smooth" });
    displayWarningMessage(message, false);
}
function displayNotification(containerID, result) {
    let jsonResponse = JSON.parse(result);

    if (jsonResponse.data.corbeilleModified) {
        displayWarning("Le contenu de la corbeille a été modifié. ", "Rafraichir la corbeille", "corbeille");
    } else if (jsonResponse.data.evenementModified) {
        displayWarning(
            "L'événement a été modifié, sauvegardez avant de rafraîchir. ",
            "Rafraichir la page",
            "evenement"
        );
    } else {
        setTimeout(checkNotificationsFunc, notificationDelay);
    }
}

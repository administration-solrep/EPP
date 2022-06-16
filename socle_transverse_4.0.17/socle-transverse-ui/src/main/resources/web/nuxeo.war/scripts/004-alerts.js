function initAlert(ref) {
    if (ref == undefined) {
        ref = "body";
    }

    $(ref)
        .find(".alert")
        .each(function () {
            initAlertDiv($(this));
        });
}

/*
 * Supprime toutes les alertes du dom
 */
var cleanAlerts = function () {
    $("body")
        .find(".alert")
        .each(function () {
            $(this).remove();
        });
};

/*
 * S'occupe de placer les divs d'alerte en fonction de leur type
 * et de leur target.
 */
var initAlertDiv = function (alertDiv) {
    var targetId = alertDiv.find("input[name='targetId']").val();
    var targetRole = "";
    var targetDiv = null;
    var content = alertDiv.find(".alert__content").clone();

    if (alertDiv.hasClass("alert--info")) {
        targetRole = "log";
    } else if (alertDiv.hasClass("alert--success")) {
        targetRole = "status";
    } else if (alertDiv.hasClass("alert--danger")) {
        alertDiv.focus();
    } else {
        targetRole = "alert";
    }

    if (targetId) {
        targetDiv = $("#" + targetId);
    } else if (alertDiv.hasClass("toast")) {
        targetDiv = $("#toast-container");
    } else {
        targetDiv = $("main");
    }
    targetDiv.prepend(alertDiv);
    $("#" + targetRole + "Messages").prepend(content);

    alertDiv.attr("aria-hidden", "false");
    alertDiv.show();
};

function constructSuccessAlert(message) {
    return {
        alertMessage: [message],
        alertOrigin: null,
        alertType: {
            cssClass: "alert alert--success toast toast--shadow",
            hasCloseButton: true,
            level: {
                icon: "icon icon--check bubble-icon alert__icon alert__icon--success",
                isOrdered: false,
                srOnly: "Confirmation",
            },
            role: "status",
        },
    };
}

function constructInfoAlert(message) {
    return {
        alertMessage: [message],
        alertOrigin: null,
        alertType: {
            cssClass: "alert alert--info toast toast--shadow",
            hasCloseButton: true,
            level: {
                icon: "icon icon--check bubble-icon alert__icon alert__icon--info",
                isOrdered: false,
                srOnly: "Confirmation",
            },
            role: "status",
        },
    };
}

function constructDangerAlert(message) {
    return {
        alertMessage: [message],
        alertOrigin: null,
        alertType: {
            cssClass: "alert alert--danger toast toast--shadow",
            hasCloseButton: true,
            level: {
                icon: "icon icon--check bubble-icon alert__icon alert__icon--danger",
                isOrdered: false,
                srOnly: "Danger",
            },
            role: "status",
        },
    };
}

function constructAlertContainer(containers) {
    $.each(containers, function (index, item) {
        constructAlert(item.alertType, item.alertMessage, item.alertOrigin);
    });
}

function constructAlert(type, messages, ref, sr = true) {
    if (messages.length > 0) {
        var parent = $("#main_content");

        var container = $("<div></div>").addClass(type.cssClass).attr("tabindex", "-1");

        var statusDiv = $("<div></div>").attr("role", "status").addClass("alerts--flex");

        var icon = $("<span></span>").addClass(type.level.icon).attr("aria-hidden", "true");
        var srOnly = $("<span></span>").addClass("sr-only").text(type.level.srOnly);
        var content;

        if (messages.length > 1) {
            content = $("<ol></ol>").addClass("alert__content base-list base-list--ordered");
            $.each(messages, function (index, value) {
                var elem = $("<li></li>").addClass("list-item list-item--ordered").text(value);
                content.append(elem);
            });
        } else if (messages.length == 1) {
            content = $("<p></p>").addClass("alert__content").append(messages[0]);
        }

        statusDiv.append(icon, srOnly, content);

        container.append(statusDiv);

        if (type.hasCloseButton) {
            container.append(constructCloseButton());
        }

        if (ref) {
            var targetId = $("<input></input>").attr({ type: "hidden", name: "targetId", value: ref });
            container.prepend(targetId);
            // Div permettant de faire un focus sur l'alert
            var focusDiv = $("<div></div>")
                .attr("tabindex", "-1")
                .attr("id", "focusAlert-" + ref);
            container.append(focusDiv);
        }

        parent.prepend(container);
        if (sr) {
            initAlertDiv(container);
        }
    }
}

function constructCloseButton() {
    var button = $("<button></button>")
        .attr("type", "button")
        .addClass(
            "base-btn base-btn--button base-btn--default base-btn--icon-action-bg-primary base-btn--center base-btn--rounded js-alert-action alert__button"
        )
        .attr("title", "Fermer la notification");

    var crossSpan = $("<span></span>").addClass("icon icon--cross-bold").attr("aria-hidden", "true");
    var srOnly = $("<span></span>").addClass("sr-only").text("Fermer la notification");

    button.append(crossSpan, srOnly);
    button.on("click", function () {
        var toastContainer = $(button).closest(".alert").get(0);
        fadeOut(toastContainer);
        setTimeout(function () {
            toastContainer.remove();
        }, 500);
    });

    return button;
}

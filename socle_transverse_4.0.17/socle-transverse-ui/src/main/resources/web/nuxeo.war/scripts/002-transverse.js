// Put SWORD transverse scripts here
var firstLoad = true;
var isSafari =
    /constructor/i.test(window.HTMLElement) ||
    (function (p) {
        return p.toString() === "[object SafariRemoteNotification]";
    })(!window["safari"] || (typeof safari !== "undefined" && safari.pushNotification));

var requestWaiting = [];

var lastRequestLoad = null;

var selectAutocompleteList = [];

const contentLoadingButton = "contentLoadingButton";
const nbCallLoadingButton = "nbCallLoadingButton";
const pendingButtonClass = "base-btn--pending";
const appListName = ["reponses", "solon-epg", "solon-epp"];

function simpleSortColumn(elem) {
    var $tableForm = $(elem).closest(".tableForm");

    // Nettoyage des précédentes valeurs
    $tableForm.find("[data-field]").each(function () {
        $(this).removeData("order");
        $(this).removeAttr("data-order");

        if ($(this).data("value") !== undefined && $(this).data("field") !== $(elem).data("field")) {
            $(this).removeData("value");
            $(this).removeAttr("data-value");
            $(this).prop("data-value", null);
        }
    });

    if ($(elem).data("inverse")) {
        if ($(elem).data("value") === "desc") {
            $(elem).data("value", "asc");
        } else if ($(elem).data("value") === "asc") {
            $(elem).removeData("value");
            $(elem).removeAttr("data-value");
            $(elem).prop("data-value", null);
        } else {
            $(elem).data("value", "desc");
        }
    } else {
        if ($(elem).data("value") === "asc") {
            $(elem).data("value", "desc");
        } else if ($(elem).data("value") === "desc") {
            $(elem).removeData("value");
            $(elem).removeAttr("data-value");
            $(elem).prop("data-value", null);
        } else {
            $(elem).data("value", "asc");
        }
        $(elem)
            .children("span.sr-only")
            .text($(elem).data("sort-" + $(elem).data("value") + "-label"));
    }
    goToFirstPage($tableForm);
    tableChangeEvent($tableForm, elem);
}

function tableChangeEvent($table, focus) {
    if (isEppTable($table)) {
        tableChangeEventEpp($table);
    } else if (isTableWithRapidSearch($table)) {
        tableChangeEventMgpp($table, focus);
    } else {
        var focus_id = "#" + $(focus).attr("id");
        var isReponsesEpgRapidSearch = $table.data("ajaxurl").endsWith("/ajax/recherche/rapide");
        var isEpgResultList = $table.data("ajaxurl").endsWith("ajax/suivi/liste");
        var isDataSearch = $table.data("search");
        var isFdrSearch = $table.data("ajaxurl").endsWith("ajax/fdr/rechercher/resultats");
        var isEpgDossierSuppr =
            $table.data("ajaxurl").endsWith("ajax/admin/dossiers/suppression/consultation") ||
            $table.data("ajaxurl").endsWith("ajax/admin/dossiers/suppression/suivi");
        var isSearch =
            $table.data("ajaxurl").endsWith("ajax/recherche/resultats") ||
            $table.data("ajaxurl").endsWith("ajax/journal/journalDossier") ||
            $table.data("ajaxurl").endsWith("ajax/admin/journalTechnique/resultats") ||
            $table.data("ajaxurl").endsWith("ajax/suivi/resultats") ||
            $table.data("ajaxurl").endsWith("ajax/admin/batch/recherche") ||
            $table.data("ajaxurl").endsWith("ajax/admin/archivage") ||
            $table.data("ajaxurl").endsWith("ajax/recherche/experte/resultats") ||
            $table.data("ajaxurl").endsWith("ajax/suivi/pan/journalTechnique/resultats") ||
            $table.data("ajaxurl").endsWith("ajax/rechercher/resultats") ||
            $table.data("ajaxurl").endsWith("ajax/fdr/squelettes") ||
            $table.data("ajaxurl").endsWith("ajax/dossier/" + $("#dossierId").val() + "/substitution/liste") ||
            $table.data("ajaxurl").endsWith("ajax/recherche/favoris/utilisateurs") ||
            isReponsesEpgRapidSearch ||
            isEpgResultList ||
            isFdrSearch ||
            isEpgDossierSuppr ||
            isDataSearch;
        var isQuickSearch = $table.data("ajaxurl").endsWith("ajax/admin/dossiers/abandon");
        var isEppRapidSearch = $table.data("ajaxurl").endsWith("ajax/corbeille/search");
        var isPostSearch = $table.data("post");
        var isPost = isSearch || isQuickSearch || isEppRapidSearch || isPostSearch;
        var mydatas = "isTableChangeEvent=true&";
        $table.find("[data-field]").each(function () {
            if ($(this).data("value") !== undefined) {
                mydatas += $(this).data("field") + "=" + $(this).data("value") + "&";
            }
            if ($(this).data("order") !== undefined) {
                mydatas += $(this).data("field") + "Order" + "=" + $(this).data("order") + "&";
            }
        });

        $table.find("input[data-isForm='true'], select[data-isForm='true']").each(function () {
            if (this.value !== undefined && mydatas.indexOf(this.name + "=" + this.value) < 0) {
                mydatas += this.name + "=" + this.value + "&";
            }
        });

        if (isPost) {
            // admin - recherche utilisateurs / fonction TRI
            if ($table.data("ajaxurl").endsWith("ajax/rechercher/resultats")) {
                mydatas += $("#searchUserForm").serialize();
            } else if ($table.data("ajaxurl").endsWith("ajax/admin/actualites/resultats")) {
                mydatas += $("#searchForm :input[value!='null']").serialize();
            } else if (isFdrSearch) {
                mydatas += $("#searchModeleForm").serialize();
            } else {
                mydatas += $("#searchForm").serialize();
            }
        } else {
            mydatas = mydatas.substring(0, mydatas.length - 1);
        }

        var myContentId = "";

        if (isEpgDossierSuppr) {
            const lg = $table.data("ajaxurl").split("/");
            myContentId = "listResultlisteDossierSuppression" + lg[lg.length - 1];
        } else if ($table.data("ajaxurl").endsWith("ajax/dossier/" + $("#dossierId").val() + "/substitution/liste")) {
            myContentId = "listeModeles";
        } else if (
            (isSearch || $table.data("ajaxurl").endsWith("ajax/suivi/requete-generale")) &&
            !isReponsesEpgRapidSearch &&
            !isEpgResultList
        ) {
            myContentId = "resultList";
        } else {
            myContentId = $table[0].id;
        }

        var myRequest = {
            contentId: myContentId,
            dataToSend: mydatas,
            method: isPost ? "POST" : "GET",
            dataType: "html",
            url: $table.data("url"),
            ajaxUrl: $table.data("ajaxurl"),
            isChangeURL: !isPost,
            overlay: $("#" + $table[0].id)
                .find(".overlay")
                .first()
                .attr("id"),
            caller: $(event.target).is(":button") ? $(event.target) : $(event.target).parent(),
            extraDatas: {
                successCallback: function () {
                    $(focus_id).focus();
                },
            },
        };

        callAjaxRequest(myRequest, replaceHtmlFunctionAndInitSelectAndInitValidation);
    }
}

function isTableWithRapidSearch($table) {
    return (
        $table.closest("#listeCommunications").length > 0 ||
        $table.closest("#listeSuiviEcheances").length > 0 ||
        $table.data("ajaxurl").endsWith("/ajax/dossier/filtrer") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/lois/tableau-maitre") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/lois/tableau-lois") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/lois/historique-maj") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/ordonnances/historique-maj") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/directives/historique-maj") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/habilitations/historique-maj") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/ratification/historique-maj") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/ordonnances/tableau-maitre") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/ordonnances/tableau-ordonnances") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/directives/tableau-maitre") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/habilitations/tableau-maitre") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/ratification/tableau-maitre") ||
        $table.data("ajaxurl").endsWith("/ajax/suivi/pan/traites/tableau-maitre")
    );
}

function isEppTable($table) {
    return (
        $table.data("ajaxurl").startsWith("/solon-epp") &&
        !$table.data("ajaxurl").endsWith("ajax/admin/batch/recherche")
    );
}

function ariaLoader(overlay, isReloadPage) {
    if (overlay.is(":hidden")) {
        overlay.attr("role", "progressbar");
        overlay.attr("aria-valuemin", "0");
        overlay.attr("aria-valuemax", "100");
        overlay.attr("aria-label", "mise à jour");
        overlay.append(
            $("<span>", {
                class: "sr-only",
                text: "Chargement en cours...",
            })
        );
        overlay.fadeIn("slow");
        overlay.focus();
        var currentValue = 0;

        return setInterval(function () {
            overlay.attr("aria-valuenow", currentValue);
            overlay.attr("aria-valuetext", currentValue + " %");
            currentValue++;

            if (currentValue > 100) {
                currentValue = 0;
            }
        }, 300);
    }

    overlay.attr("data-reload", isReloadPage);
}

function csrfSafeMethod(method) {
    // these HTTP methods do not require CSRF protection
    return /^(GET|HEAD|OPTIONS)$/.test(method);
}

function callAjaxRequest(myRequest, successCallback, errorCallback) {
    var request = {
        url: myRequest.ajaxUrl,
        data: myRequest.dataToSend,
        ignoreChronology: new Boolean(myRequest.ignoreChronology).valueOf(),
        time: Date.now(),
    };
    var url = encodeURI(myRequest.url);
    var ajaxUrl = encodeURI(myRequest.ajaxUrl);
    if (!isRequestAlreadyLaunch(request)) {
        requestWaiting.push(request);

        if (myRequest.isChangeURL) {
            let urlToPush = url;
            if (myRequest.dataToSend != null && myRequest.dataToSend.length > 0) {
                urlToPush = urlToPush + "?" + myRequest.dataToSend;
            }

            if (myRequest.anchor != null) {
                urlToPush = urlToPush + "#" + myRequest.contentId;
            }
            pushURLFunction(urlToPush);
        }

        var interval = null;
        const isMultipart = myRequest.enctype == "multipart/form-data";

        $.ajax({
            beforeSend: function (xhr) {
                if (!csrfSafeMethod(myRequest.method)) {
                    let token = $('meta[name="csrf-token"]').attr("content");
                    //Si le token n'est pas trouvé dans les meta on prend celui qu'il y a en tant que champ dans le form.
                    //S'il n'y en a toujours pas c'est qu'il y a un pb sur le dev de la page layout et template
                    if (token == null) {
                        token = $('input[name="csrf-token"]').val();
                    }
                    xhr.setRequestHeader("CSRF-Token", token);
                }
                // activation de l'overlay
                if (myRequest.overlay != null) {
                    interval = ariaLoader($("#" + myRequest.overlay), false);
                }

                // activation du bouton de chargement
                if (myRequest.loadingButton != null) {
                    enableLoadingButton(myRequest.loadingButton);
                }
            },
            complete: function (event) {
                clearInterval(interval);
                // désactivation de l'overlay
                if (myRequest.overlay != null && !$("#" + myRequest.overlay).attr("data-reload")) {
                    $("#" + myRequest.overlay).fadeOut("slow");
                    $("#" + myRequest.overlay)
                        .find("span.sr-only")
                        .remove();
                }

                // désactivation du bouton de chargement
                if (myRequest.loadingButton != null) {
                    disableLoadingButton(myRequest.loadingButton);
                }
            },
            headers: myRequest.headers || {},
            url: ajaxUrl,
            async: myRequest.async != null ? myRequest.async : true,
            type: myRequest.method,
            data: myRequest.dataToSend,
            dataType: myRequest.dataType,
            enctype: myRequest.enctype,
            processData: isMultipart ? false : true,
            contentType: isMultipart ? false : "application/x-www-form-urlencoded; charset=UTF-8",
            xhrFields: myRequest.xhrFields || {},
            success: function (result, statut, xhr) {
                requestWaiting.splice(getIndexRequest(request), 1);
                if (lastRequestLoad == null || lastRequestLoad.time < request.time) {
                    if (!myRequest.ignoreChronology) {
                        // Système mis en place je pense pour garder une cohérence dans l'ordre des requêtes callback
                        // (si un requête se termine après une aute qui avait été lancé avant alors on ignore le callback)
                        // Cela pose problèmes lors de traitement long si une requête en arrière plan (e.g, l'appel
                        // au endpoint de notifications MGPP) est exécutée en même temps; ces requêtes doivent donc
                        // être flag comme étant "hors chronologie"
                        lastRequestLoad = request;
                    }
                    successCallback(myRequest.contentId, result, myRequest.caller, myRequest.extraDatas, xhr);
                }
                initJavascript();
            },
            error: function (result) {
                requestWaiting.splice(getIndexRequest(request), 1);
                if (errorCallback != null) {
                    errorCallback(myRequest.contentId, result, myRequest.caller, myRequest.extraDatas);
                }
            },
        });
    }
}

function isRequestAlreadyLaunch(request) {
    return getIndexRequest(request) >= 0;
}

function getIndexRequest(request) {
    var index = -1;
    $.each(requestWaiting, function (i, item) {
        if (item.url === request.ajaxUrl && item.data === request.data) {
            index = i;
            return;
        }
    });
    return index;
}

var pushURLFunction = function pushURLState(urlToPush) {
    history.pushState(null, "", urlToPush);
};

var prev_href = "";
$(window).on("popstate", function () {
    // On retire la partie ancre de l'URL à vérifier
    var href = location.href.split("#")[0];

    if (prev_href === "") {
        prev_href = href;
    }

    if (firstLoad === false || isSafari === false) {
        // On ne recharge que si l'URL a changé
        if (href !== prev_href) {
            location.reload(true);
        }
    }
    prev_href = href;

    firstLoad = false;
});

$(window).on("load", function () {
    // Si la modal de password est présente on remplace la fonction d'initialisation pour ne pas avoir les événements de fermeture au clic
    if (
        $("#reset_password_overlay") &&
        $("#reset_password_overlay").hasClass("interstitial-overlay__content--visible")
    ) {
        window.initInterstitial = function () {};
        $("#reset_password_dialog :input:first").focus();
    }
});

$(document).ready(function () {
    initAlert();
    // affichage du tooltip pour le champ de formulaire de type password
    const rules = $("#password_rules");
    if (rules.length) {
        tippy("#password_tooltip", {
            content: rules.html(),
            placement: "right",
            allowHTML: true,
        });
    }
    initDoubleModals();
});

function initDoubleModals() {
    // Permet de déplacer les modals lorsqu'elles sont l'une dans l'autre
    // Afin de pouvoir fermé la première modal l'autre qu'on ouvre la seconde modal.
    const modals = document.querySelectorAll(".interstitial-overlay__content");
    modals.forEach(function (modalParent) {
        const modalEnfants = modalParent.querySelectorAll(".interstitial-overlay__content");
        modalEnfants.forEach(function (modalEnfant) {
            $(modalEnfant).insertAfter(modalParent);
        });
    });
}

$(document).ready(function () {
    $(".overlay").hide();

    updateCurrentTab();
    $("#searchForm").on("submit", launchSearch);
    $("#searchUserForm").on("submit", launchUserSearch);
    showHideButtonPagination();
    makeLinkActive();
    initAsyncSelect();
    initSessionReminder();
    initNotifications();
});

$(".back-to-top").click(function () {
    $("#topmenubutton").focus();
});

function initJavascript() {
    initWindowHelpers();
    initHeaderMenu();
    initBackToTop();
    initComplexSelects();
    initToolTip();
    initSimpleFilePicker();
    initAutocomplete();
    initSelectAutocomplete();
    initRichText();
    initProgressBar();
    initLetterSlider();
    initInterstitial();
    initSeeMore();
    initEllipsedContent();
    initDropdown();
    initDatePicker();
    initCustomTable();
    initActionTable();
    initTabs();
    initDataGrid();
    initStickyColumnTable();
    initAlert();
    initSessionReminder();
    initDoubleModals();
}

// Transforme les paramètres (string) en une collection d'input hidden qui seront ajoutés à l'élément passé en paramètre
function parametersToHidden(parameters, elementId) {
    var paramArray = parameters.split(";");
    for (var i = 0; i < paramArray.length; i++) {
        var elts = paramArray[i].split("=");
        var inputHidden = document.createElement("input");
        inputHidden.type = "hidden";
        inputHidden.name = elts[0];
        inputHidden.id = elts[0];
        // On supprime l'élément existant au cas où on serait déjà passé par là
        $("#" + inputHidden.id).remove();

        inputHidden.value = elts[1];
        $("#" + elementId).append(inputHidden);
    }
}

function setDefaultValues(defaultValues, elementId) {
    if (defaultValues) {
        var defaultValuesArray = defaultValues.split(";");
        for (var i = 0; i < defaultValuesArray.length; i++) {
            var elts = defaultValuesArray[i].split("=");
            const inputName = elts[0];
            var inputValues = [];

            inputValues.push(elts[1]);

            if ($("#" + elementId + " [name='" + inputName + "']")) {
                $("#" + elementId + " [name='" + inputName + "']").val(inputValues);
            }
        }
    }
}

function enableLoadingButton(button) {
    var $button = $(button);

    if ($button.data(nbCallLoadingButton)) {
        $button.data(nbCallLoadingButton, $button.data(nbCallLoadingButton) + 1);
    } else {
        $button.data(nbCallLoadingButton, 1);
        //Création du pending button qui va remplacer le bouton actuel le temps de la requête
        var $pendingButton = $("<button></button>")
            .addClass($button.attr("class"))
            .addClass(pendingButtonClass)
            .attr("disabled", true)
            .append(
                $("<img></img>")
                    .attr("src", "/" + getAppliName() + "/img/loader.svg")
                    .attr("alt", "Chargement en cours")
                    .attr("width", "29")
                    .attr("height", "29")
            );
        //On cache le bouton actuel
        $button.hide();
        //On insert le bouton d'attente
        $pendingButton.insertAfter($button);
        $pendingButton.focus();
    }
}

function getAppliName() {
    const splitPath = document.location.pathname.split("/");
    if (appListName.includes(splitPath[1])) {
        return splitPath[1];
    }
    return "";
}

function disableLoadingButton(button) {
    var $button = $(button);
    if ($button.data(nbCallLoadingButton) == null || $button.data(nbCallLoadingButton) < 2) {
        if ($button.next().hasClass(pendingButtonClass)) {
            //On supprime le bouton d'attente
            $button.next().remove();
        }
        //On réaffiche le bouton initial
        $button.show();
        $button.data(nbCallLoadingButton, null);

        // On récupère si une alert d'erreur est présente pour placer le focus
        var divAlert = $(".alert--danger");
        if (divAlert != null) {
            divAlert.focus();
        } else {
            $button.focus();
        }
    } else {
        $button.data(nbCallLoadingButton, $button.data(nbCallLoadingButton) - 1);
    }
}

function doInitModal(link) {
    var $link = $(link);
    var id = $link.data("controls");
    var title = $("#dialogTitle-" + id);
    var message = $("#message-" + id);
    var btnconfirm = $("#btn-confirm-" + id);
    var defaultInput = $link.data("defaultValues");
    var paramInitJs = $link.data("param-init-js");

    title.text($link.data("title"));
    message.text("");
    message.append($link.data("message"));
    btnconfirm.attr("onclick", $link.data("function"));

    const btnConfirmLabel = $link.data("btn-confirm-label");

    if (btnConfirmLabel) {
        btnconfirm.text(btnConfirmLabel);
    }

    setDefaultValues(defaultInput, id);

    if ($link.data("init-js")) {
        window[$link.data("init-js")](paramInitJs);
    }
}

function unlockScroll() {
    var scrollY = document.body.style.top;
    document.body.style.position = "";
    document.body.style.top = "";
    document.body.style.left = "";
    document.body.style.right = "";
    window.scrollTo(0, parseInt(scrollY || "0") * -1);
}

window.checkErrorOrGoPrevious = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        goPreviousPage();
    }
};

window.checkErrorOrToast = function (contentId, result, caller, extraDatas) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;

    if (messagesContaineur.dangerMessageQueue.length > 0 || messagesContaineur.warningMessageQueue.length > 0) {
        if (messagesContaineur.dangerMessageQueue.length > 0) {
            constructAlertContainer(messagesContaineur.dangerMessageQueue);
        }
        if (messagesContaineur.warningMessageQueue.length > 0) {
            constructAlertContainer(messagesContaineur.warningMessageQueue);
        }
    } else {
        if (messagesContaineur.infoMessageQueue.length > 0) {
            constructAlertContainer(messagesContaineur.infoMessageQueue);
        }
        if (messagesContaineur.successMessageQueue.length > 0) {
            constructAlertContainer(messagesContaineur.successMessageQueue);
        }

        if (extraDatas !== undefined && extraDatas.successCallback !== undefined) {
            extraDatas.successCallback();
        }
    }
};

var replaceHtmlFunction = function replaceHTMLinContainer(containerID, result, caller, extraDatas) {
    $("#" + containerID).html(result);

    if (extraDatas !== undefined && extraDatas.successCallback !== undefined) {
        extraDatas.successCallback();
    }
};

var replaceHtmlFunctionFromJsonResponse = function replaceHTMLinContainer(containerID, result, caller, extraDatas) {
    let jsonResponse = JSON.parse(result);
    $("#" + containerID).html(jsonResponse.data);

    if (extraDatas !== undefined && extraDatas.successCallback !== undefined) {
        extraDatas.successCallback();
    }
};

var appendHtmlFunction = function appendHTMLInContainer(containerID, htmlToAppend) {
    $("#" + containerID).append(htmlToAppend);
};

var replaceWithHtmlFunction = function replaceWithHtmElement(elementID, html) {
    $("#" + elementID).replaceWith(html);
};

window.tabLoadError = function (containerID, result, caller, extraDatas) {
    $("#" + containerID).html($("<p>Une erreur s'est produite</p>"));
};

window.tabLoadErrorExtraDataTarget = function (containerID, result, caller, extraDatas) {
    var triggerBtn = extraDatas.triggerBtn;
    $("#" + triggerBtn.attr("aria-controls")).html($("<p>Une erreur s'est produite</p>"));
};

var goPreviousPage = function goPreviousPageFunction() {
    ariaLoader($("#reload-loader"), true);
    window.location.replace($("#urlPreviousPage").val());
};

var reloadPage = function reloadPageFunction() {
    ariaLoader($("#reload-loader"), true);
    window.location.reload();
};

function showReloadLoader() {
    ariaLoader($("#reload-loader"), true);
}

function hideReloadLoader() {
    $("#reload-loader").hide();
}

var scrollToElement = function scrollToElement(idElement, duree) {
    $([document.documentElement, document.body]).animate(
        {
            scrollTop: $("#" + idElement).offset().top,
        },
        duree
    );
    $("#" + idElement).focus();
};

function selectTableCheckbox(elem) {
    selectTableCheckboxWithoutAction(elem);
    var myTable = $(elem).closest(".tableForm");
    var someChecked = $(myTable).find(":checkbox:checked.js-custom-table-line-check").length > 0;
    enableTableSelectionActions(someChecked); // activer (ou non) les actions
}

function selectTableCheckboxWithoutAction(elem) {
    updateCheckboxHeader(elem);
    updateCheckboxLabel(elem, $(elem).is(":checked"));
}

function updateCheckboxLabel(elem, selected) {
    var label = $(elem).parent().children("label");
    label.attr("title", label.data(selected ? "deselect-label" : "select-label"));
    label.children("span").html(label.data(selected ? "deselect-label" : "select-label"));
}

function updateCheckboxHeader(elem) {
    var myTable = $(elem).closest(".tableForm");
    var someChecked = $(myTable).find(":checkbox:checked.js-custom-table-line-check").length > 0;
    var someUnchecked = $(myTable).find(":checkbox:not(:checked).js-custom-table-line-check").length > 0;
    var headerCheckbox = $(myTable).find(":checkbox.js-custom-table-header-check");
    $(headerCheckbox).prop("indeterminate", someChecked && someUnchecked);
    $(headerCheckbox).prop("checked", someChecked && !someUnchecked);
    if (someChecked && !someUnchecked) {
        updateCheckboxLabel(headerCheckbox, true);
    }
}

function selectAllCheckBox(elem) {
    selectAllCheckBoxWithoutAction(elem);
    enableTableSelectionActions(elem.checked);
}

function selectAllCheckBoxWithoutAction(elem) {
    var myTable = $(elem).closest(".tableForm");
    var isSelected = elem.checked;
    var myIndex = $(elem).index();
    myTable.find("tbody tr").each(function () {
        var cac = $(this).find("input");
        if (cac.length > 0) {
            cac[0].checked = isSelected;
            updateCheckboxLabel(cac, isSelected);
        }
    });
    updateCheckboxLabel(elem, isSelected);
}

function enableTableSelectionActions(someChecked) {
    enableSpecificTableSelectionActions(someChecked, $("#main_content"));
}

function enableSpecificTableSelectionActions(someChecked, parentElement) {
    parentElement.find(".action-table-selection").each(function () {
        $(this).attr("disabled", !someChecked);
    });
}

const replaceHtmlFunctionAndFocusResult = function replaceHtmlFunctionAndFocusResult(
    containerID,
    result,
    caller,
    extraDatas
) {
    replaceHtmlFunction(containerID, result);
    $(extraDatas.elementToFocus).focus();
};

const replaceHtmlFunctionAndInitSelect = function (elementID, html) {
    replaceWithHtmlFunction(elementID, html);
    initAsyncSelect();
    initFormValidation();
};

const replaceHtmlFunctionAndInitSelectAndInitValidation = function (containerID, result, extraDatas) {
    replaceHtmlFunction(containerID, result, extraDatas);
    initAsyncSelect();
    initFormValidation();
};

const replaceHtmlFunctionAndInitSelectAndFocusResult = function replaceHtmlFunctionAndInitSelectAndFocusResult(
    containerID,
    result,
    caller,
    extraDatas
) {
    replaceHtmlFunction(containerID, result);
    initAsyncSelect();
    const alertContainerID = extraDatas.alertContainerID;
    if (alertContainerID && $("#ALERT_DANGER-" + alertContainerID).length) {
        $("#ALERT_DANGER-" + alertContainerID).attr("tabindex", "-1");
        setTimeout(function () {
            $("#ALERT_DANGER-" + alertContainerID).focus();
        }, 5);
    } else {
        $(extraDatas.elementToFocus).focus();
    }
};

window.checkErrorOrGoToUrl = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        window.location.href = extraDatas.redirectUrl;
    }
};

function launchUserSearch(event) {
    event.preventDefault();
    cleanAlerts();
    if (isValidForm($("#searchUserForm"))) {
        const ajaxCallPath = DOMPurify.sanitize($("#ajaxCallPath").val());
        const ajaxSearchEndpoint = DOMPurify.sanitize($("#ajaxSearchEndpoint").val());
        const ajaxUrl = ajaxCallPath + ajaxSearchEndpoint;
        const myRequest = {
            contentId: "resultList",
            dataToSend: $("#searchUserForm").serialize(),
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            caller: this,
            loadingButton: $(this).find("button[type='submit']"),
            extraDatas: {
                elementToFocus: "#focusResultat",
                alertContainerID: "searchUserForm",
            },
        };
        callAjaxRequest(myRequest, replaceHtmlFunctionAndInitSelectAndFocusResult, tabLoadError);
    }
}

function launchSearch(event) {
    event.preventDefault();
    if (isValidForm($("#searchForm"))) {
        const ajaxCallPath = DOMPurify.sanitize($("#ajaxCallPath").val());
        const ajaxSearchEndpoint = DOMPurify.sanitize($("#ajaxSearchEndpoint").val());
        const ajaxUrl = ajaxCallPath + ajaxSearchEndpoint;
        var loader = $(event.target).is(":button") ? $(event.target) : $(this).find("button[type='submit']");
        var myRequest = {
            contentId: "resultList",
            dataToSend: $("#searchForm").serialize(),
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            caller: this,
            loadingButton: loader,
            extraDatas: {
                elementToFocus: "#focusResultat",
            },
        };
        callAjaxRequest(myRequest, replaceHtmlFunctionAndInitSelectAndFocusResult, tabLoadError);
    }
}

function reinitSearch(url, callback) {
    var ajaxUrl = $("#ajaxCallPath").val() + url;
    var myRequest = {
        contentId: null,
        dataToSend: null,
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
        caller: this,
    };
    callAjaxRequest(myRequest, callback);
}

function updateFormSubmit(id, path, callback = checkErrorOrGoPrevious) {
    form = $("#" + id);
    if (isValidForm(form)) {
        const ajaxUrl =
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + path;
        var myRequest = {
            contentId: null,
            dataToSend: form.serialize(),
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            loadingButton: form.find("button[type='submit']"),
        };

        callAjaxRequest(myRequest, callback, displaySimpleErrorMessage);
    }
}

function getFilesFromFileInput(id) {
    var files = [];
    var type = "single";

    if ($("#" + id).attr("data-multiple")) {
        type = "multiple";
    }

    _files = $("[id^=form_input_file_drag_and_drop_" + type + "_with_text_" + id + "_]");
    _names = $("[id^=form_input_file_drag_and_drop_" + type + "_with_text_newname_" + id + "_]");

    _extensions = _files.map(function () {
        return this.files[0].name.split(".").pop();
    });

    for (var i = 0; i < _files.length; i++) {
        files.push(
            new File([_files[i].files[0]], _names[i].value + "." + _extensions[i], { type: _files[i].files[0].type })
        );
    }

    return files;
}

window.displaySimpleErrorMessage = function (contentId, result) {
    if (result.status == "500") {
        constructAlert(errorMessageType, ["Une erreur est survenue sur le serveur"], null);
    } else if (result.status == "401") {
        constructAlert(
            errorMessageType,
            [
                'Votre session est expirée ! Veuillez vous reconnecter pour utiliser cette fonctionnalité. <a href="javascript:window.location.reload();">Se reconnecter</a>',
            ],
            null
        );
    } else if (result.status == "415") {
        constructAlert(errorMessageType, ["Ce type de fichier n'est pas accepté"], null);
    } else if (result.status == "403") {
        constructAlert(
            errorMessageType,
            [
                "Vous n'êtes pas autorisé à accéder à la fonctionnalité. Ce message peut s'afficher lorsque votre session a été réinitialisée. Veuillez recharger la page, si le problème persiste cela signifie que vous ne disposez pas des droits pour exécuter l'action demandée. <a href=\"javascript:window.location.reload();\">Recharger la page</a>",
            ],
            null
        );
    } else {
        constructAlert(errorMessageType, [result.responseText], null);
    }
};

window.displayWarningMessage = function (message, sr = true) {
    constructAlert(warningMessageType, [message], null, sr);
    $("#session_refresh").focus();
};

window.checkErrorOrReloadCallback = function (contentId, result, caller, extraDatas, xhr, callback) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        callback();
    }
};

window.checkErrorOrReload = function (contentId, result, caller, extraDatas, xhr) {
    checkErrorOrReloadCallback(contentId, result, caller, extraDatas, xhr, reloadPage);
};

window.checkErrorOrHardReload = function (contentId, result, caller, extraDatas, xhr) {
    checkErrorOrReloadCallback(contentId, result, caller, extraDatas, xhr, hardReloadPage);
};

window.showMessage = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else if (messagesContaineur.warningMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.warningMessageQueue);
    } else if (messagesContaineur.infoMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.infoMessageQueue);
    } else if (messagesContaineur.successMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.successMessageQueue);
    }
};

var errorMessageType = {
    cssClass: "alert alert--danger",
    role: "status",
    hasCloseButton: false,
    level: {
        icon: "icon icon--exclamation-point bubble-icon alert__icon alert__icon--danger",
        srOnly: "Erreur",
        isOrdered: true,
    },
};

var warningMessageType = {
    cssClass: "alert alert--warning",
    role: "status",
    hasCloseButton: false,
    level: {
        icon: "icon icon--information bubble-icon alert__icon alert__icon--warning",
        srOnly: "Alerte",
        isOrdered: true,
    },
};

window.goPreviousOrReloadIfError = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        reloadPage();
    } else {
        goPreviousPage();
    }
};

window.reloadOrGoPreviousIfInfoMessage = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.infoMessageQueue.length > 0) {
        goPreviousPage();
    } else {
        reloadPage();
    }
};

var hardReloadPage = function reloadPageFunction() {
    showReloadLoader();
    window.scrollTo(0, 0);
    window.location.reload("true");
};

window.initAsyncSelect = function () {
    var selects = document.querySelectorAll("select.js-aria-autocomplete-select-with-async");
    selects.forEach(function (select) {
        var multiple = select.hasAttribute("multiple");
        var sourceSelect = $(select).data("url");
        var queryParamName = $(select).data("query-param");
        var minLength = $(select).data("min-length") ? $(select).data("min-length") : 0;
        var keyMapping = $(select).data("key-mapping");
        var labelMapping = $(select).data("label-mapping");
        var acceptsCustomTags = $(select).data("accepts-custom-tags") == true;
        var disabled = $(select).attr("readonly");
        var dynamicInput = $(select).data("dynamic-input") ? $(select).data("dynamic-input") : null;

        var optionsSelect = {
            source: sourceSelect,
            asyncQueryParam: queryParamName,
            onReady: function (wrapper) {
                var autoSelect = this;

                if (disabled) {
                    $(wrapper).children("input").attr("disabled", true);
                }

                if (!acceptsCustomTags) {
                    //On récupère l'ID commun du wrapper
                    var commonId = wrapper.id.replace("-wrapper", "");

                    if (multiple) {
                        //Dans le cas d'un select multiple
                        //On initialise les valeurs déjà présentes dans le select au niveau du plugin
                        $(select)
                            .find("option:checked")
                            .each(function (index) {
                                var selectOption = $(this);
                                insertAutocompleteValue(
                                    autoSelect,
                                    commonId,
                                    select,
                                    wrapper,
                                    selectOption.val(),
                                    selectOption.text(),
                                    index,
                                    disabled
                                );
                            });
                    } else if ($(select).find("option:checked").length > 0) {
                        //Si on est pas sur un select multiple et qu'on a une valeur dans le select
                        //On défini la valeur en tant que valeur sélectionnée dans le plugin
                        var selectOption = $(select).find("option:checked");

                        //Mis a jour de l'input texte
                        $("#" + commonId + "-input").val(selectOption[0].text);

                        //Ajout de la valeur dans le tableau de sélection du plugin
                        var myOptionValue = {
                            key: selectOption.val(),
                            label: selectOption.text(),
                            value: selectOption.val(),
                        };
                        autoSelect.selected.push(myOptionValue);
                    }
                }

                if (!autoSelect.deletionsDisabled && !multiple) {
                    //Dans le cas d'un select non multiple
                    //on ajoute un événement sur l'input text pour vider le select si l'input est vidé
                    $("#" + commonId + "-input").on("keyup", function (evt) {
                        if (
                            $("#" + commonId + "-input")
                                .val()
                                .trim() === "" &&
                            $(select).find("option").length
                        ) {
                            $(select).empty();
                            //En cas de vidage de l'input via une touche de suppression
                            //on déclenche volontairement un blur pour mettre à jour la valeur au niveau du composant
                            if (evt.keyCode == 8 || evt.keyCode == 32 || evt.keyCode == 46) {
                                $(this).trigger("blur");
                            }
                            isValidInput($(select));
                        }
                    });
                }
            },
            onAsyncPrep: function (url) {
                let modifiedUrl = url;
                if (dynamicInput != null) {
                    const inputs = dynamicInput.split(",");
                    for (const value of inputs) {
                        if (value != "" && $("#" + value).length && $("#" + value).serialize() != "") {
                            modifiedUrl += "&" + $("#" + value).serialize();
                        }
                    }
                }
                return modifiedUrl;
            },
            onConfirm: function (selected) {
                //Lors d'un choix dans les suggestions
                //On crée l'option pour le select
                var newOption = $("<option>", {
                    value: selected.value,
                    text: selected.label,
                    selected: true,
                });

                //Si l'option n'est pas déjà présente on l'ajoute au select
                if ($(select).find('option[value="' + selected.value + '"]').length === 0) {
                    //Si on est pas en sélection multiple
                    //On vide le select avant d'ajouter notre option
                    if (!multiple) {
                        $(select).empty();
                    }

                    //On ajoute l'option au select
                    $(select).append(newOption);
                }

                var wrapper = $(select).parent().find(".aria-autocomplete__wrapper")[0];
                var commonId = wrapper.id.replace("-wrapper", "");

                //Suppression du bouton temporaire
                if ($("#" + commonId + "-delete-solon")) {
                    $("#" + commonId + "-delete-solon").remove();
                }

                isValidInput($(select));
            },
            onFocus: function (wrapper) {
                var autoSelect = this;
                if (!multiple && $(select).find("option:checked").length > 0) {
                    var selectOption = $(select).find("option:checked");

                    //Ajout de la valeur dans le tableau de sélection du plugin
                    var myOptionValue = {
                        key: selectOption.val(),
                        label: selectOption.text(),
                        value: selectOption.val(),
                    };

                    autoSelect.selected.push(myOptionValue);
                }
            },
            onAsyncSuccess: function (query, xhr, isFirstCall) {
                //On parse le retour du webservice
                return JSON.parse(xhr.response);
            },
            onDelete: function (deleted) {
                if (!disabled) {
                    //Lors d'une suppression, on supprime aussi l'option dans le select
                    $(select)
                        .find("option")
                        .each(function () {
                            var selectOption = $(this);
                            if (selectOption.val() == deleted.value) {
                                selectOption.remove();
                            }
                        });

                    var wrapper = $(select).parent().find(".aria-autocomplete__wrapper")[0];
                    var commonId = wrapper.id.replace("-wrapper", "");

                    //Suppression du bouton temporaire
                    if ($("#" + commonId + "-delete-solon")) {
                        $("#" + commonId + "-delete-solon").remove();
                    }

                    isValidInput($(select));
                }
            },
            autoGrow: multiple,
            multiple: multiple,
            minLength: minLength,
            deleteOnBackspace: true,
            deleteAllControl: true,
        };

        var optionsSelectMapping = {};

        if (keyMapping) {
            if (!labelMapping) labelMapping = keyMapping;
            optionsSelectMapping = {
                sourceMapping: { value: keyMapping, label: labelMapping },
            };
        }

        var optionsSelectCustom = {};

        if (acceptsCustomTags && !keyMapping && !labelMapping) {
            optionsSelectCustom = {
                onResponse: function (...backValues) {
                    const value = $(select).parent().find("input").val();
                    if (backValues.filter((v) => v === value).length > 0) {
                        return backValues;
                    } else {
                        return [value, ...backValues];
                    }
                },
            };
        }

        // Merge default settings and options
        if (window.ariaAutocompleteDefaults) {
            optionsSelect = $.extend(
                {},
                window.ariaAutocompleteDefaults,
                optionsSelect,
                optionsSelectMapping,
                optionsSelectCustom
            );
        }

        if (!select.classList.contains("mounted")) {
            select.classList.add("mounted");
            var autocomplete = AriaAutocomplete(select, optionsSelect);

            //  l'input sélectionnable par l'utilisateur n'est pas celui qui porte le process de validation
            //  il faut donc le trigger manuellement
            $(select)
                .parent()
                .find("input")
                .blur(function (evt) {
                    var focus = $(select).parent().find(".focused");

                    if (!focus.hasClass("aria-autocomplete__option")) {
                        isValidInput($(select));
                    }
                });

            $(select).parent().find("input").attr("aria-labelledby", $(select).attr("aria-labelledby"));

            selectAutocompleteList.push({
                id: select.id,
                component: autocomplete,
            });
        }
    });
};

function insertAutocompleteValue(autoSelect, commonId, select, wrapper, value, text, index, disabled) {
    //On crée le tag correspondant à la valeur
    var selectedSpan = $("<span>", {
        "aria-label": disabled ? text : "supprimer " + text,
        class: "aria-autocomplete__selected",
        role: "button",
        tabindex: 0,
        "aria-describedby": commonId + "-label",
        id: commonId + "-option-selected-" + index,
        text: text,
    });

    //On crée l'option équivalente pour le plugin
    var myOptionValue = {
        key: value,
        label: text,
        value: value,
    };

    if (!disabled) {
        // On ajoute la propriété spécifique au plugin sur le span avec
        // l'objet correspondant à l'option
        // Nécessaire pour que la suppression fonctionne
        selectedSpan[0]["_ariaAutocompleteSelectedOption"] = myOptionValue;
    } else {
        selectedSpan.addClass("aria-autocomplete__selected--disabled");
    }

    //On ajoute l'option dans la liste de sélection du plugin
    autoSelect.selected.push(myOptionValue);

    //On ajoute le tag dans l'IHM
    selectedSpan.insertBefore($(wrapper).find(".aria-autocomplete__sr-assistance"));

    //Si nous avons plusieurs valeurs par défaut
    //On ajoute un bouton de suppression global
    if ($(select).find("option:checked").length > 1) {
        //Création du bouton
        var deleteButton = $("<span>", {
            role: "button",
            class: "aria-autocomplete__delete-all",
            tabindex: 0,
            id: commonId + "-delete-solon",
            "aria-describedby": commonId + "-label",
            html: $("<span>", {
                class: "sr-only aria-autocomplete__sr-only",
                text: "Supprimer toutes les options sélectionnées",
            }),
        });

        //On insert le bouton
        deleteButton.insertAfter($("#" + commonId + "-list"));

        //On bind notre propre fonction pour supprimer les éléments et RAZ le plugin
        //Impossible de demander au plugin de le faire car on a pas accès à la propriété deleteAll
        deleteButton.on("click", function () {
            //On vide le tableau de sélection du plugin
            autoSelect.selected.splice(0, autoSelect.selected.length);

            //On vide le select
            $(select).empty();

            //On remove l'ensemble des tags
            $(wrapper).find(".aria-autocomplete__selected").remove();

            //On supprime le bouton
            deleteButton.remove();

            isValidInput($(select));
        });
    }
}

function updateCurrentTab() {
    const $this = $(".dossier-onglet.tabulation__item--active");
    if ($this) {
        var dataName =
            $this.attr("data-name") === "traitement" ? $this.attr("data-name") + "/papier" : $this.attr("data-name");
        var targetURL = "/dossier/" + $("#dossierId").val() + "/" + dataName + "?" + $("#dossierLinkId").serialize();

        $this.removeAttr("onclick");

        $this.on("click", function (evt) {
            return updateOngletData(evt, targetURL);
        });
    }
}

function updateOngletData(evt, targetURL) {
    var encode = encodeURI(
        $("#ajaxCallPath")
            .val()
            .substring(0, $("#ajaxCallPath").val().length - 5) + targetURL
    );
    pushURLFunction(encode);
}

function makeLinkActive() {
    const path = $(location).attr("pathname");
    let found = false;

    const directLinks = $(".quick-access__list").add("#sidebar .tree-navigation__list").children().find("a:first");

    directLinks.each(function () {
        const a = $(this);

        if (found) return true;
        const href = a.attr("href");
        const hash = href.indexOf("#");

        const link = hash !== -1 ? href.substr(0, hash) : href;

        if (link === path) {
            makeActive(a);
            found = true;
        }
    });

    if (found) return;

    // si l'url courante ne correspond à aucun item du menu, on remonte le breadcrumb jusqu'à trouver un parent correspondant
    $($(".breadcrumb-text__link").get().reverse()).each(function () {
        if (found) return false;

        const breadHref = $(this).attr("href");

        if (breadHref) {
            const breadHash = breadHref.indexOf("#");
            const breadQuery = breadHref.indexOf("?");
            let idx = -1;

            if (breadHash === -1 && breadQuery !== -1) {
                idx = breadQuery;
            } else if (breadHash !== -1 && breadQuery === -1) {
                idx = breadHash;
            } else if (breadHash !== -1 && breadQuery !== -1) {
                idx = Math.min(breadHash, breadQuery);
            }

            const breadLink = idx !== -1 ? breadHref.substr(0, idx) : breadHref;

            directLinks.each(function () {
                const a = $(this);
                const href = a.attr("href");
                const hash = href.indexOf("#");
                const link = hash !== -1 ? href.substr(0, hash) : href;

                if (breadLink === link) {
                    makeActive(a);
                    found = true;
                    return false;
                }
            });
        }
    });
}

function makeActive(a) {
    if (a.hasClass("quick-access__link")) {
        a.parent().addClass("quick-access__link--active");
    } else {
        a.parent().addClass("tree-navigation__item--active");
    }
    a.attr("title", a.text().trim() + " - actif");
    a.attr("aria-current", "true");
}

var toggleSidebar = function () {
    if ($("#sidebar__toggle").attr("aria-expanded") === "false") {
        $("#sidebar__content").css("display", "block");
    } else {
        $("#sidebar__content").css("display", "none");
    }
};

function exportByMail(ids, type) {
    var myRequest = {
        contentId: null,
        dataToSend: {
            ids: ids,
            type: type,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/exportMail",
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrToast, displaySimpleErrorMessage);
}

function addMultipleDate(inputId, inputName) {
    let $input = $("input[name=" + inputId + "-picker]");
    const date = $input.val();
    if (date && isValidInput($input)) {
        let $div = $input.parents(".items-start").find(".multiple-dates");
        $div.append(
            '<div class="flex justify-between items-center m-t-2 multiple-date">' +
                date +
                '<button type="button"' +
                '+ class="js-tooltip base-btn--autocomplete-delete base-btn base-btn--button base-btn--default base-btn--light base-btn--min base-btn--transparent"' +
                'data-tippy-content="Supprimer ' +
                date +
                '"' +
                'onclick="deleteMultipleDate(this)"> ' +
                '<span aria-hidden="true"' +
                '	class="icon icon--times-circle icon--big"></span><span' +
                '	class="sr-only">Supprimer ' +
                date +
                " </span>" +
                "</button>" +
                '<input type="hidden" name="' +
                inputName +
                '" value="' +
                date +
                '"/>' +
                "</div>"
        );
        $input.val("");
    }
}

function deleteMultipleDate(button) {
    const $div = $(button).parent(".multiple-date").remove();
}

var timeout;
var focused_item;

window.initSessionReminder = function () {
    $("#main_content #session_refresh__link").parent().parent().parent().remove();

    clearTimeout(timeout);
    var location = window.location.href;

    if (location.indexOf("#") !== -1) {
        location = location.split("#")[0];
    }

    if (location.indexOf("?") !== -1) {
        location = location.split("?")[0];
    }
    const delay = $("#timeoutDelay").val() ? $("#timeoutDelay").val() - 10 : 50;
    if (location.substring(location.length - "/login".length) !== "/login") {
        timeout = setTimeout(() => {
            displayWarningMessage(
                "Votre session va bientôt expirer. " +
                    '<button type="button" ' +
                    'class="base-btn base-btn--button base-btn--default base-btn--colored-link" ' +
                    'tabindex="0" ' +
                    'id="session_refresh__link" ' +
                    'title="Réactualiser ma session" ' +
                    'onclick="updateSession(this)">Actualiser</button>',
                false
            );
            focused_item = document.activeElement;
            $("#session_refresh__link").focus();
        }, delay * 60 * 1000); // minutes * secondes * millisecondes
    }
};

var updateSession = function (el) {
    var myRequest = {
        method: "GET",
        dataType: "text",
        ajaxUrl: $("#ajaxCallPath").val() + "/session/update",
        isChangeURL: false,
        caller: $(event.target).is(":button") ? $(event.target) : $(event.target).parent(),
        extraDatas: {},
    };

    callAjaxRequest(myRequest, function () {
        $(focused_item).focus();
        initSessionReminder();
    });
};

window.checkErrorOrRedirect = function (contentId, result) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        const urlAjax = $("#ajaxCallPath").val();
        window.location.href = urlAjax.substring(0, urlAjax.length - 5) + jsonResponse.data;
    }
};

window.encodeHTMLSpecialChar = function (string) {
    return string.replaceAll("\\", "&bsol;").replaceAll("%", "&percnt;");
};

window.isHTML = RegExp.prototype.test.bind(/(<([^>]+)>)/i);

window.removeAttributeInString = function (data, attributeKey) {
    const splitStr = data.split("&");
    let newData = "";
    for (const str of splitStr) {
        if (str.split("=")[0] != attributeKey) {
            newData += str + "&";
        }
    }
    return newData.substr(0, newData.length - 1);
};

function getDateToday() {
    var date = new Date();
    return ("0" + date.getDate()).slice(-2) + "/" + ("0" + (date.getMonth() + 1)).slice(-2) + "/" + date.getFullYear();
}

//surcharge de la méthode initTinyMCE pour mon cas(il existe déjà un composant
//tinymce sur la même page avec l'id mce
function initTextAreaMessage(id) {
    initTinyMCE({ selector: "textarea#" + id });
}

function buildSearchMap() {
    let typeForm = "";
    var map = new Map();

    if ($("#searchForm").length > 0) typeForm = "#searchForm";
    else if ($("#formDynamique").length > 0) typeForm = "#formDynamique";
    var form;
    if (event && $(event.srcElement).closest(typeForm).length) {
        form = $($(event.srcElement).closest(typeForm)[0]);
    } else {
        form = $(typeForm);
    }

    form.find("select[multiple]").each(function () {
        if ($(this).val().length !== 0) {
            map.set($(this).attr("name"), $(this).val());
        }
    });

    var dataForm = form.serializeArray();
    for (var i = 0; i < dataForm.length; i++) {
        if (!map.has(dataForm[i].name) && dataForm[i].value !== "" && dataForm[i].name !== "inclure") {
            // Pour éviter "fr.dila.st.core.exception.STValidationException: Contenu invalide"
            // par SolonSecurityFilter#cleanValue, on remplace les quotes par _.
            // Comme on est sur du filtrage, ce ne sera pas remplacé côté back et interprêté comme
            // le caractère joker (de taille 1) d''Oracle
            var valueField = dataForm[i].value.replaceAll("'", "_");

            map.set(dataForm[i].name, valueField);
        }
    }

    return map;
}

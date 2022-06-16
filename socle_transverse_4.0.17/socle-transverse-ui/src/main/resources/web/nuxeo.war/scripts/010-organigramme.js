function buildTypesSelectionQueryParam(type) {
    // On peut passer une liste de types par exemple "GVT,MIN,USR"
    var types = type.replace(/\s+/g, "").split(",");
    var tmp = [];
    $(types).each(function (index, value) {
        tmp.push("typeSelection=" + value);
    });
    var typesSelection = tmp.join("&");
    return typesSelection;
}

function loadSelectOrganigramme(id, type, activatePosteFilter, selectedNode, isMulti, filterCE) {
    var orgaID = "orga-" + id;

    if ($("#" + orgaID + " :only-child").hasClass("overlay")) {
        var ajaxUrl = $("#ajaxCallPath").val() + "/organigramme/selectarbre";
        var myRequest = {
            contentId: orgaID,
            dataToSend:
                buildTypesSelectionQueryParam(type) +
                "&selectID=" +
                id +
                "&activatePosteFilter=" +
                activatePosteFilter +
                "&selectedNode=" +
                selectedNode +
                "&dtoAttribute=" +
                selectedNode +
                "&isMulti=" +
                isMulti +
                "&filterCE=" +
                filterCE,
            method: "GET",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: "overlay-" + id,
            caller: this,
        };

        callAjaxRequest(myRequest, replaceHtmlFunction);
    }
}

function onOpenOrganigrammeSelectNode(id, type, activatePosteFilter, selectedNode, isMulti, dtoAttribute, filterCE) {
    var orgaID = "orga-" + id;
    var ajaxUrl = $("#ajaxCallPath").val() + "/organigramme/selectarbre";
    var myRequest = {
        contentId: orgaID,
        dataToSend:
            buildTypesSelectionQueryParam(type) +
            "&selectID=" +
            id +
            "&activatePosteFilter=" +
            activatePosteFilter +
            "&selectedNode=" +
            selectedNode +
            "&dtoAttribute=" +
            dtoAttribute +
            "&isMulti=" +
            isMulti +
            "&filterCE=" +
            filterCE,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "overlay-" + id,
        caller: $(event.target).is(":button") ? $(event.target) : $(event.target).parent(),
    };

    callAjaxRequest(myRequest, loadTreeData);
}

/**
 * Ajouter l'élément sélectionné sous l'input (avec possibilité de le supprimer)
 *
 * @param selectID
 * @param key
 * @param label
 * @returns
 */
function addFromOrganigramme(selectID, key, label) {
    var multiple = $("#" + selectID)[0].hasAttribute("multiple");
    var select = $("#" + selectID);
    var wrapper = $("#" + selectID)
        .parent()
        .find(".aria-autocomplete__wrapper")[0];
    var commonId = wrapper.id.replace("-wrapper", "");
    var autoSelectElem = selectAutocompleteList.find((x) => x.id === selectID);
    if (autoSelectElem) {
        var autoSelect = autoSelectElem.component;
        if (multiple) {
            if ($(select).find("option[value='" + key + "']").length === 0) {
                insertAutocompleteValue(
                    autoSelect,
                    commonId,
                    select,
                    wrapper,
                    key,
                    label,
                    $(select).find("option").length,
                    false
                );
            }
        } else {
            // Mis a jour de l'input texte
            $("#" + commonId + "-input").val(label);

            if (autoSelect.selected.length > 0) {
                autoSelect.selected.splice(0, 1);
            }

            // Ajout de la valeur dans le tableau de sélection du plugin
            var myOptionValue = {
                key: key,
                label: label,
                value: key,
            };
            autoSelect.selected.push(myOptionValue);
        }

        // Lors d'un choix dans les suggestions
        // On crée l'option pour le select
        var newOption = $("<option>", {
            value: key,
            text: label,
            selected: true,
        });

        // Si l'option n'est pas déjà présente on l'ajoute au select
        if ($(select).find("option[value='" + key + "']").length === 0) {
            // Si on est pas en sélection multiple
            // On vide le select avant d'ajouter notre option
            if (!multiple) {
                $(select).empty();
            }

            // On ajoute l'option au select
            $(select).append(newOption);
        }

        isValidInput($(select));
    }

    closeModal($("#modal-" + selectID).get(0));
    $("#" + selectID)
        .closest("div")
        .find("input")
        .focus();
}

function onChangeOrganigrammeDisplayMode(showDeactivated) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/organigramme";
    var myRequest = {
        contentId: "organigrammeTree",
        dataToSend: "showDeactivated=" + showDeactivated,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#organigrammeTree").find(".overlay").first().attr("id"),
        caller: $(event.target).parent(),
    };

    callAjaxRequest(myRequest, loadOrganigrammeTreeData);
}

function onOpenOrganigrammeNode(selectedNode) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/organigramme";
    var myRequest = {
        contentId: "organigrammeTree",
        dataToSend: "selectedNode=" + selectedNode,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#organigrammeTree").find(".overlay").first().attr("id"),
        caller: $(event.target).is(":button") ? $(event.target) : $(event.target).parent(),
    };

    callAjaxRequest(myRequest, loadOrganigrammeTreeData);
}

var loadOrganigrammeTreeData = function loadOrganigrammeTree(containerID, result, caller) {
    // On retire les messages d'alertes précédent.
    $("div[id^=ALERT_").remove();
    loadTreeData(containerID, result, caller);
    initInterstitial();
};

var onDeletedNodeFunc = function onDeletedNode(containerID, result, caller, extraDatas, xhr) {
    var resultObj = jQuery.parseJSON(result);
    if (resultObj.statut === "OK") {
        closeDeleteModal();
        // On recharge tout l'arbre
        checkErrorOrReload(containerID, result, caller, extraDatas, xhr);
    } else {
        var file = resultObj.data;
        // Affichage d'un lien vers le fichier à télécharger
        $("#remove-node-confirmation").hide();
        $("#download-excel-message").show();
        $("#download-excel-link").text(file);

        $("#download-excel-link").prop("data-parameters", file);

        $("#btn-confirm-deletion").hide();
    }
};

function closeDeleteModal() {
    var modal = $("#modal-node-suppression")[0];
    if (modal.classList.contains("interstitial-overlay__content--visible")) {
        modal.classList.remove("interstitial-overlay__content--visible");
    }
}

function resetDeleteModal() {
    $("#remove-node-confirmation").show();
    $("#download-excel-message").hide();
    $("#btn-confirm-deletion").show();
}

function deleteNode() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/organigramme/deleteNode";
    var myRequest = {
        contentId: "organigrammeTree",
        dataToSend:
            "nodeId=" + $("#nodeId").val() + "&nodeType=" + $("#nodeType").val() + "&curMin=" + $("#curMin").val(),
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
        caller: this,
    };

    callAjaxRequest(myRequest, onDeletedNodeFunc, displaySimpleErrorMessage);
}

$(document).ready(function () {
    $(".interstitial-overlay__content").on("click", function (e) {
        if (
            !e.target.classList.contains("interstitial-overlay__content--visible") &&
            e.target.id === "modal-node-suppression"
        ) {
            resetDeleteModal();
        }
    });

    $(".interstitial__close").on("click", function (e) {
        var modal = $(e.target).closest(".interstitial-overlay__content").get(0);
        if (
            !modal.classList.contains("interstitial-overlay__content--visible") &&
            modal.id === "modal-node-suppression"
        ) {
            resetDeleteModal();
        }
    });
});

function organigrammeCopyNode(caller) {
    var baseTooltip = $(caller).parents(".base-tooltip");
    var nodeId = $(baseTooltip).children('input[name="nodeId"]').val();
    var nodeType = $(baseTooltip).children('input[name="nodeType"]').val();
    var curMin = $(baseTooltip).children('input[name="curMin"]').val();

    var ajaxUrl = $("#ajaxCallPath").val() + "/organigramme/copy";
    var myRequest = {
        contentId: "organigrammeTree",
        dataToSend: "nodeId=" + nodeId + "&nodeType=" + nodeType + "&curMin=" + curMin,
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#organigrammeTree").find(".overlay").first().attr("id"),
        caller: $(caller),
    };

    callAjaxRequest(myRequest, loadOrganigrammeTreeData);
}

function organigrammePasteNode(caller, withUsers) {
    var baseTooltip = $(caller).parents(".base-tooltip");
    var nodeId = $(baseTooltip).children('input[name="nodeId"]').val();
    var nodeType = $(baseTooltip).children('input[name="nodeType"]').val();
    var curMin = $(baseTooltip).children('input[name="curMin"]').val();

    var ajaxUrl = $("#ajaxCallPath").val() + "/organigramme/paste";
    var myRequest = {
        contentId: "organigrammeTree",
        dataToSend: "nodeId=" + nodeId + "&nodeType=" + nodeType + "&curMin=" + curMin + "&withUsers=" + withUsers,
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#organigrammeTree").find(".overlay").first().attr("id"),
        caller: $(caller),
    };

    callAjaxRequest(myRequest, loadOrganigrammeTreeData);
}

function onActivation(caller) {
    var baseTooltip = $(caller).parents(".base-tooltip");
    var nodeId = $(baseTooltip).children('input[name="nodeId"]').val();
    var nodeType = $(baseTooltip).children('input[name="nodeType"]').val();
    var curMin = $(baseTooltip).children('input[name="curMin"]').val();
    var ajaxUrl = $("#ajaxCallPath").val() + "/organigramme/activation";
    var myRequest = {
        contentId: "organigrammeTree",
        dataToSend: "typeSelection=" + nodeType + "&selectID=" + nodeId + "&curMin=" + curMin + "&activate=true",
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#organigrammeTree").find(".overlay").first().attr("id"),
        caller: $(caller),
    };

    callAjaxRequest(myRequest, loadOrganigrammeTreeData, displaySimpleErrorMessage);
}

function onDeActivation(caller) {
    var baseTooltip = $(caller).parents(".base-tooltip");
    var nodeId = $(baseTooltip).children('input[name="nodeId"]').val();
    var nodeType = $(baseTooltip).children('input[name="nodeType"]').val();
    var curMin = $(baseTooltip).children('input[name="curMin"]').val();
    var ajaxUrl = $("#ajaxCallPath").val() + "/organigramme/activation";
    var myRequest = {
        contentId: "organigrammeTree",
        dataToSend: "typeSelection=" + nodeType + "&selectID=" + nodeId + "&curMin=" + curMin + "&activate=false",
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#organigrammeTree").find(".overlay").first().attr("id"),
        caller: $(caller),
    };

    callAjaxRequest(myRequest, loadOrganigrammeTreeData, displaySimpleErrorMessage);
}

var loadTreeData = function loadTree(containerID, result, caller) {
    replaceHtmlFunction(containerID, result);
    $("#" + caller.attr("id")).focus();
};
function releaseLock(id, type, curMin) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/organigramme/deverrouiller";
    var myRequest = {
        contentId: null,
        method: "POST",
        dataToSend: {
            idNode: id,
            type: type,
            curMin: curMin,
        },
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: $("#organigrammeTree").find(".overlay").first().attr("id"),
    };
    callAjaxRequest(myRequest, reloadOrganigrammeOrGoPrevious, displaySimpleErrorMessage);
}

function getNorNewListToParse() {
    const lstDisplayedNorDir = $.map($("#blockNor").find("label"), function (item) {
        return item.id.replace("nor-label-", "");
    });
    var lstSelectedMin = $("#ministere-rattachement").find("option");

    var lstMinToAdd = [];

    if (lstSelectedMin) {
        lstSelectedMin.each(function () {
            var curId = $(this).val();
            var itemIndex = $.inArray(curId, lstDisplayedNorDir);
            if (itemIndex <= -1) {
                lstMinToAdd.push(curId);
            }
        });
    }

    return lstMinToAdd;
}

var appendHtmlFunctionWithUpdateNorKeys = function appendHTMLinContainer(containerID, result, caller, extraDatas) {
    $("#" + containerID).append(result);
    updateNorKeys();

    if (extraDatas !== undefined && extraDatas.successCallback !== undefined) {
        extraDatas.successCallback();
    }
};

function updateNorKeys() {
    $("#blockNor").find('input[name ="keyNors"]').remove();
    $("#blockNor")
        .find("input[name='dir-nors']")
        .each(function () {
            const curInput = this;
            const value = curInput.id.replace("nor-", "") + ":" + curInput.value;
            $("#blockNor").append('<input type="hidden" name="keyNors" value="' + value + '" />');
        });
}

function onChangeTypeUnite() {
    if (isTypeDir()) {
        if ($("#blockNor")) {
            var lstSelectedMin = getNorNewListToParse();
            if (Array.isArray(lstSelectedMin)) {
                let norDIrections = [];
                lstSelectedMin.forEach(function (elem) {
                    const norValueInput = document.getElementById("hidden-nor-value-" + elem);
                    const value = norValueInput ? norValueInput.value : norValueInput;
                    let data = {
                        idParent: elem,
                        nor: value,
                    };
                    norDIrections.push(JSON.stringify(data));
                });
                var ajaxUrl =
                    $("#ajaxCallPath").val().replace("/ajax", "") +
                    "/organigramme/unitestructurelle/ajouterNorDirections";

                var myRequest = {
                    contentId: "blockNor",
                    dataToSend: {
                        norDirections: norDIrections,
                    },
                    method: "POST",
                    dataType: "html",
                    ajaxUrl: ajaxUrl,
                    isChangeURL: false,
                    overlay: null,
                };

                callAjaxRequest(myRequest, appendHtmlFunctionWithUpdateNorKeys, displaySimpleErrorMessage);
            }
        }
    } else {
        const container = $("[id^=container-nor-]").each(function () {
            this.remove();
        });
        updateNorKeys();
    }
}

function isTypeDir() {
    return "DIR" == $("input[name='type']:checked").first().val();
}

// Vérifie quand on rajoute un ministère de rattachement (l'event onChange du
// select est bloqué par le composant de l'autocomplétion)
$("#usEdition")
    .find("#ministere-rattachement")
    .bind("DOMNodeInserted", function () {
        if (isTypeDir()) {
            if ($("#blockNor")) {
                var lstSelectedMin = getNorNewListToParse();

                if (Array.isArray(lstSelectedMin)) {
                    lstSelectedMin.forEach(function (elem) {
                        var ajaxUrl =
                            $("#ajaxCallPath").val().replace("/ajax", "") +
                            "/organigramme/unitestructurelle/ajouterNorDirection";

                        const norValueInput = document.getElementById("hidden-nor-value-" + elem);

                        var myRequest = {
                            contentId: "blockNor",
                            dataToSend: { idParent: elem, value: norValueInput ? norValueInput.value : norValueInput },
                            method: "GET",
                            dataType: "html",
                            ajaxUrl: ajaxUrl,
                            isChangeURL: false,
                            overlay: null,
                        };

                        callAjaxRequest(myRequest, appendHtmlFunctionWithUpdateNorKeys, displaySimpleErrorMessage);
                    });
                }
            }
        }
    });

// Vérifie quand on supprime un ministère de rattachement (l'event onChange du
// select est bloqué par le composant de l'autocomplétion)
$("#usEdition")
    .find("#ministere-rattachement")
    .bind("DOMNodeRemoved", function (event) {
        if (isTypeDir() && $("#blockNor")) {
            const elementValue = event.target.value;
            const container = $("#container-nor-" + elementValue);
            if (container.length) {
                container.remove();
                updateNorKeys();
            }
        }
    });

function verifyDuplicateNameAndSavePosteWs() {
    verifyDuplicateNameAndSave($("#edit_poste_ws_form"), "posteWs");
}

function savePosteWsForm() {
    const editPosteWsForm = $("#edit_poste_ws_form");
    if (isValidForm(editPosteWsForm)) {
        editPosteWsForm.submit();
    }
}

function verifyDuplicateNameAndSavePoste() {
    verifyDuplicateNameAndSave($("#edit_poste_form"), "poste");
}

function savePosteForm() {
    const editPosteForm = $("#edit_poste_form");
    if (isValidForm(editPosteForm)) {
        editPosteForm.submit();
    }
}

function verifyDuplicateNameAndSave(editForm, type) {
    if (isValidForm(editForm)) {
        const ajaxUrl =
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) +
            "/admin/organigramme/" +
            type +
            "/verifier";
        var myRequest = {
            contentId: null,
            dataToSend: editForm.serialize(),
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            loadingButton: $("button[name='submit_button']"),
            extraDatas: {
                callType: type,
            },
        };

        callAjaxRequest(myRequest, showModalDuplicateName, displaySimpleErrorMessage);
    }
}

function showModalDuplicateName(containerID, result, caller, extraDatas) {
    const jsonResponse = JSON.parse(result);
    const isUniqueName = jsonResponse.data;
    if (!isUniqueName) {
        doInitModal("#init-modal-duplicate-name");
        const idModal = $("#init-modal-duplicate-name").data("controls");
        const modal = $("#" + idModal);
        modal.addClass("interstitial-overlay__content--visible");
        modal.find("button")[0].focus();
    } else if (extraDatas) {
        if (extraDatas.callType == "poste") {
            savePosteForm();
        } else if (extraDatas.callType == "posteWs") {
            savePosteWsForm();
        }
    }
}

function downloadExcel(obj) {
    var link = $(obj)[0].attributes["data-controls"].value;
    var param = $("#download-excel-link").prop("data-parameters");
    var fullLink = link.concat("?fileName=" + param);
    window.open(fullLink);
}

window.reloadOrganigrammeOrGoPrevious = function (contentId, result, caller, extraDatas, xhr) {
    var urlPrev = $("#urlPreviousPage").val();
    var path = window.location.pathname + "#main_content";
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else if (urlPrev === path) {
        reloadPage();
    } else {
        goPreviousPage();
    }
};

function isValidParentUS(hasMinistere) {
    const id = hasMinistere ? "#ministere-rattachement" : "#institutions-rattachement";
    if ($(id).val().length !== 0 || $("#unite-structurelle-rattachement").val().length !== 0) {
        return true;
    }
    const label = hasMinistere ? "un ministère" : "une institution";
    constructAlert(errorMessageType, [
        "Veuillez sélectionner au minimum " + label + " de rattachement ou une unité structurelle de rattachement.",
    ]);
    return false;
}

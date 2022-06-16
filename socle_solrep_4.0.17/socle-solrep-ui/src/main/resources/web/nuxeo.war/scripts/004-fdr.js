// Affiche/cache toute les notes
function toggleNotesVisibility(element, noToast) {
    const firstNoteLine = $("tr.table-line--notes").first();
    // Si pas de notes sur la FDR
    if (firstNoteLine.length === 0) return;

    // On détermine si les notes sont cachées
    const notesAreHidden = $(element).data("notes-hidden") !== undefined ? $(element).data("notes-hidden") : true;
    let message = "";

    toggleGlobalButtonNoteState(!notesAreHidden);
    if (notesAreHidden) {
        $("tr.table-line--notes").children().removeClass("hide-element");
        message = "Affichage des notes d'étape";
    } else {
        $("tr.table-line--notes").children().addClass("hide-element");
        message = "Masquage des notes d'étape";
    }

    if (!noToast) {
        cleanAlerts();
        const successAlert = [constructSuccessAlert(message)];
        constructAlertContainer(successAlert);
    }
    if (element) element.focus();
}

// Affiche/cache une seule note
function toggleNoteVisibility(stepId, button) {
    const $notes = $("tr#" + stepId).next();
    let message = "";

    // On détermine si la notes est cachée
    const firstNoteHidden = $notes.children().hasClass("hide-element");

    if (firstNoteHidden) {
        $notes.children().removeClass("hide-element");
        message = "Affichage de la note d'étape";
    } else {
        $notes.children().addClass("hide-element");
        message = "Masquage de la note d'étape";
    }

    toggleHideShowButtonWhenNecessary();

    const successAlert = [constructSuccessAlert(message)];
    constructAlertContainer(successAlert);
    button.focus();
}

function toggleHideShowButtonWhenNecessary() {
    // Il faut s'assurer que l'état du bouton toggle global des notes soit
    // cohérent avec l'affichage
    const firstNoteLine = $("tr.table-line--notes").first();
    // On définit un état de référence
    const notesAreHidden = firstNoteLine.children().first().hasClass("hide-element");
    let displayIsConsistent = true;

    // On vérifie si les notes sont toutes affichées ou toutes cachées
    $("tr.table-line--notes").each(function () {
        if (!$(this).children().first().hasClass("hide-element") === notesAreHidden) {
            displayIsConsistent = false;
        }
    });

    // Si toutes les notes sont dans le même état, on change l'état du bouton
    // global
    if (displayIsConsistent) {
        toggleGlobalButtonNoteState(notesAreHidden);
    }
}

function toggleGlobalButtonNoteState(notesAreHidden) {
    const tippyInstances = [];
    const $toggleButton = $(".ACTION_ROUTE_STEP_NOTE_HIDE_SHOW");
    $toggleButton.data("notes-hidden", notesAreHidden);
    $toggleButton.each(function () {
        tippyInstances.push(this._tippy);
    });
    const spanIcons = $(".ACTION_ROUTE_STEP_NOTE_HIDE_SHOW span:first-child");

    if (notesAreHidden) {
        $toggleButton.attr("title", "Afficher les notes d'étapes");
        tippyInstances.forEach((tippy) => {
            tippy.setContent("Afficher les notes d'étapes");
        });
        spanIcons.each(function () {
            $(this).removeClass("icon--note-bubble-eye-close");
            $(this).addClass("icon--note-bubble-eye");
        });
    } else {
        $toggleButton.attr("title", "Masquer les notes d'étapes");
        tippyInstances.forEach((tippy) => {
            tippy.setContent("Masquer les notes d'étapes");
        });
        spanIcons.each(function () {
            $(this).removeClass("icon--note-bubble-eye");
            $(this).addClass("icon--note-bubble-eye-close");
        });
    }
}

function removeEtape(button) {
    button.closest("fieldset").remove();
}

const addEtape = function addEtapeInContainer(containerID, result, extraDatas) {
    const container = $("#" + containerID);
    container.append(result);
    container
        .find("[id^=destinataire]")
        .closest(".form-input")
        .children(".form-input__footer")
        .addClass("modal-organigramme-footer");
    if (extraDatas.idManual) {
        $("#btnRemoveEtape-" + extraDatas.idManual).addClass("invisible");
    }
};

const loadStepAndInitSelect = function (elementID, html, result, caller, extraDatas) {
    replaceWithHtmlFunction(elementID, html);
    addEtape(elementID, result, extraDatas);
    initAsyncSelect();
    initFormValidation();
};

function doAddEtape(id = null) {
    var dataToSend;
    var ajaxUrl;
    let uniqueId = id;
    const fdrId = $("#d_fdr_content").length > 0 ? $("#d_fdr_content").attr("fdr-id") : $("#idModele").val();
    if (!uniqueId) {
        uniqueId = randomId();
    }

    if ($("#isSquelette").val() === "true") {
        ajaxUrl = $("#ajaxCallPath").val() + "/etape/ajouterEtapeSquelette";
        dataToSend = {
            uniqueId: uniqueId,
            fdrId: $("#id").val(),
        };
    } else {
        ajaxUrl = $("#ajaxCallPath").val() + "/etape/ajouterEtape";
        dataToSend = {
            uniqueId: uniqueId,
            fdrId: fdrId,
        };
    }

    const myRequest = {
        contentId: "etapeFdrContainer-" + $("#typeRef").val(),
        dataToSend: dataToSend,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        extraDatas: {
            idManual: id,
        },
    };
    callAjaxRequest(myRequest, addEtapAndInitSelect, displaySimpleErrorMessage);
}

function addEtapAndInitSelect(containerID, result, caller, extraDatas) {
    addEtape(containerID, result, extraDatas);
    initAsyncSelect();
}

function getLinesToAdd() {
    var lines = [];
    $("#etapeFdrContainer-" + $("#typeRef").val())
        .find("fieldset.m-t-6")
        .each(function () {
            var serializeObject = $(this).serializeArray();
            var objectToJson = "{";
            var idRow;
            for (i = 0; i < serializeObject.length; i++) {
                idRow = serializeObject[i]["name"].split("-")[1];
                var name = serializeObject[i]["name"].split("-")[0];
                if (name != "valAuto") {
                    var value = serializeObject[i]["value"];
                    objectToJson += '"';
                    objectToJson += name;
                    objectToJson += '" : "';
                    objectToJson += value;
                    objectToJson += '",';
                }
            }
            // add validation automatique (checkbox non serialisé)
            var valAuto = $("input[name=valAuto-" + idRow + "]").prop("checked");
            objectToJson += ' "valAuto" : "';
            objectToJson += valAuto;
            objectToJson += '"}';

            // ajout au tableau
            lines.push(objectToJson);
        });
    return lines;
}

function doSaveEtape() {
    let typeCreation;
    let modal;
    if ($("#typeRef").val() == "branch") {
        typeCreation = $("#typeCreation").val();
        modal = $("#ajoutBranchFdr");
    } else {
        typeCreation = $("input[name='type-creation']:checked").val();
        modal = $("#ajoutEtapeFdr");
    }
    if (!isValidForm(modal)) return false;
    const lines = getLinesToAdd();

    const ajaxUrl = $("#ajaxCallPath").val() + "/etape/saveEtape";
    const myRequest = {
        contentId: "ajoutEtapeFdr",
        dataToSend: {
            dossierLinkId: $("#dossierLinkId").val(),
            idBranch: $("#idBranch").val(),
            typeAjout: $("#actionLabel").val(),
            typeCreation: typeCreation,
            typeRef: $("#typeRef").val(), // correspond à étape ou branche
            typeActe: $("#typeActe").val(),
            lines: lines,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
        extraDatas: {
            idContentModal: "contentAjoutEtapeFdr",
        },
    };
    callAjaxRequest(myRequest, checkErrorOrFocusToFdrTable, displaySimpleErrorMessage);
    closeModal(modal.get(0));
}

window.modalErrorMessage = function (contentId, result, caller, extraDatas) {
    cleanAlerts();
    if (result.status == "400") {
        const contentId = extraDatas.idContentModal;
        constructAlert(errorMessageType, [result.responseText], contentId);
    } else {
        closeModal($("#" + contentId).get(0));
        displaySimpleErrorMessage(contentId, result);
    }
};

function checkErrorAndCloseModal(contentId, result, caller, extraDatas, xhr, callback) {
    closeModal($("#" + contentId).get(0));
    checkErrorOrReload(contentId, result, caller, extraDatas, xhr, callback);
}

function initEtape(isChangeType = false) {
    // On supprime les styles error/success
    cleanAllPreviousErrorOrSuccess();
    cleanAlerts();

    // On supprime les lignes d'étapes qui on été ajoutées
    if ($("#ajoutEtapeFdr").length) {
        // ajout de tous les Id du fragment "etapeFdr"
        var fieldsets = [];
        $("#etapeFdrContainer-" + $("#typeRef").val())
            .find("fieldset.m-t-6")
            .each(function () {
                fieldsets.push($(this).attr("id"));
            });

        // à l'initialisation, on a 1 élément
        var defaultFieldsets = ["fieldset-1"];
        fieldsets.forEach(function (fieldsetId) {
            if (defaultFieldsets.indexOf(fieldsetId) == -1) {
                $("#" + fieldsetId).remove();
            }
        });
    }

    // On va réinitialiser les champs
    const modal = $("#ajoutEtapeFdr");
    modal.find("select").prop("selectedIndex", 0);
    // Permet de vider le champ organigramme
    $("#destinataire-1").empty();
    // Permet d'ajouter une classe qui corrige le problème de remonté du champ
    // organigramme
    // Lorsque le champ est en erreur
    modal
        .find("[id^=destinataire]")
        .closest(".form-input")
        .children(".form-input__footer")
        .addClass("modal-organigramme-footer");
    modal.find("input[type=text],input[type=number], textarea").val("");

    if (isChangeType) {
        $("#form_select_stage_val_auto_serie_1").prop("checked", false);
        $("#obligatoire-1-0").prop("checked", true);
    } else {
        modal.find("input[type=hidden]").val("");
        modal.find("input[type=radio], input[type=checkbox]").prop("checked", false);
        $(".default_check").prop("checked", true);
    }

    $("#add-etape-button").prop("disabled", false);
    let firstRow = $("#fieldset-1");
    if (firstRow) {
        firstRow.find(".form-label--disabled").removeClass("form-label--disabled");
        firstRow.find(".form-select-input__field--disabled").removeClass("form-select-input__field--disabled");
        firstRow.find(".form-select__field-wrapper--disabled").removeClass("form-select__field-wrapper--disabled");
        firstRow.find(".form-input__footer--disabled").removeClass("form-input__footer--disabled");
        firstRow.find(".form-optin--disabled").removeClass("form-optin--disabled");
        firstRow.find(".form-optin__checkbox, .form-choice-input__input").prop("disabled", false);
    }
}

function changeTypeCreation(input) {
    initEtape(true);
    if (input == "parallele") {
        doAddEtape(2);
    }
}

function doSupprimerStep(idBranch) {
    const ajaxUrl = $("#ajaxCallPath").val() + "/etape/supprimerBrancheOuEtape";
    const myRequest = {
        contentId: null,
        dataToSend: {
            idBranch: idBranch,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrFocusToFdrTable, displaySimpleErrorMessage);
}

function moveStep(stepId, direction) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/etape/moveStep";
    var myRequest = {
        contentId: null,
        dataToSend: {
            stepId: stepId,
            direction: direction,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrFocusToFdrTable, displaySimpleErrorMessage);
}

function doCreateNoteEtape() {
    var modal = $("#add-edit-note-etape-modal");
    if (!isValidForm(modal)) return false;

    var commentContent = DOMPurify.sanitize($("#note-textarea").val());
    var stepId = $("#stepId").val();

    var ajaxUrl = $("#ajaxCallPath").val() + "/etape/note/creer";
    var myRequest = {
        contentId: null,
        dataToSend: {
            stepId: stepId,
            commentContent: commentContent,
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
    };
    $("#reload-loader").css("display", "block");
    closeModal(modal.get(0));
    resetTextarea();

    callAjaxRequest(myRequest, displayNoteSuccessToastAndLoadFdrTab, displaySimpleErrorMessage);
}

function doCreateNoteEtapeEnCours() {
    var modal = $("#add-edit-note-etape-modal");
    if (!isValidForm(modal)) return false;

    var ajaxUrl = $("#ajaxCallPath").val() + "/etape/note/creerEnCours";
    var myRequest = {
        contentId: null,
        dataToSend: {
            commentContent: DOMPurify.sanitize($("#note-textarea").val()),
            dossierLinkId: $("#dossierLinkId").val(),
            dossierId: $("#dossierId").val(),
        },
        async: false,
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
    };
    $("#reload-loader").css("display", "block");
    closeModal(modal.get(0));
    resetTextarea();

    callAjaxRequest(myRequest, displayNoteSuccessToastAndLoadFdrTab, displaySimpleErrorMessage);
}

function doEditNoteEtape() {
    var modal = $("#add-edit-note-etape-modal");
    if (!isValidForm(modal)) return false;

    var stepId = $("#stepId").val();
    var noteId = $("#noteId").val();
    var noteContent = DOMPurify.sanitize($("#note-textarea").val());

    var ajaxUrl = $("#ajaxCallPath").val() + "/etape/note/modifier";
    var myRequest = {
        contentId: null,
        dataToSend: {
            stepId: stepId,
            commentId: noteId,
            commentContent: noteContent,
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
    };
    $("#reload-loader").css("display", "block");
    closeModal(modal.get(0));
    resetTextarea();

    callAjaxRequest(myRequest, displayNoteSuccessToastAndLoadFdrTab, displaySimpleErrorMessage);
}

function doRemoveNoteEtape() {
    var tableFdrId = $("table.custom-table").attr("id");
    var stepId = $("#stepId").val();
    var commentId = $("#noteId").val();

    var ajaxUrl = $("#ajaxCallPath").val() + "/etape/note/supprimer";
    var myRequest = {
        contentId: null,
        dataToSend: {
            commentId: commentId,
            stepId: stepId,
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
    };
    $("#reload-loader").css("display", "block");

    callAjaxRequest(myRequest, displayNoteSuccessToastAndLoadFdrTab, displaySimpleErrorMessage);
}

function doAnswerNoteEtape() {
    var modal = $("#add-edit-note-etape-modal");
    if (!isValidForm(modal)) return false;

    var tableFdrId = "table-fdr";

    var ajaxUrl = $("#ajaxCallPath").val() + "/etape/note/creer";
    var myRequest = {
        contentId: null,
        dataToSend: {
            fdrId: document.getElementById("d_fdr_content").getAttribute("fdr-id"),
            stepId: $("#stepId").val(),
            commentContent: DOMPurify.sanitize($("#note-textarea").val()),
            commentParentId: $("#noteId").val(),
            dossierLinkId: $("#dossierLinkId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
    };
    $("#reload-loader").css("display", "block");
    closeModal(modal.get(0));
    resetTextarea();

    callAjaxRequest(myRequest, displayNoteSuccessToastAndLoadFdrTab, displaySimpleErrorMessage);
}

function displayNoteSuccessToastAndLoadFdrTab(elementId, result, caller, extraDatas, myXhr) {
    var jsonResponse = JSON.parse(result);
    var messages = jsonResponse.messages;

    var fdrTabButton = $(".tabulation__item[data-name='fdr']");
    chargeOngletDossier("fdr", fdrTabButton, false); // on recharge le
    // contenu de l'onglet
    // FDR
    // replaceWithHtmlFunction(elementId, jsonResponse.data);

    cleanAlerts();
    if (messages.successMessageQueue.length > 0) {
        constructAlertContainer(messages.successMessageQueue);
    }

    toggleNotesVisibility($("#" + elementId), true);
    $("#reload-loader").css("display", "none");
}

function resetTextarea() {
    $("#note-textarea").val("");
    cleanPreviousErrorOrSuccess($("#note-textarea"));
}

function initAddEditNoteEtapeModale(noteId) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/etape/note/charger";

    var myRequest = {
        contentId: "addEditNoteEtapeContent",
        dataToSend: {
            noteId: noteId,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };
    callAjaxRequest(myRequest, replaceHtmlFunctionAndInitOrganigramme, displaySimpleErrorMessage);

    document.getElementById("selectedStepsInfo").style.display = "none";
    document.getElementById("selectedStepsWarning").style.display = "none";
}

function initRemoveNoteEtapeModale() {
    $("#btn-confirm-validation-dialog-modal").html("Supprimer");
}

function checkSelectedSteps() {
    var myTable = $("[id^=ROUTE_STEP_SHARED_NOTE_ADD]").closest(".tableForm");
    var terminees = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            terminees.push($(this).closest("tr").attr("terminee"));
        });

    if (terminees.every((v) => v === "true")) {
        document.getElementById("selectedStepsInfo").style.display = "block";
        document.getElementById("selectedStepsWarning").style.display = "none";
    } else if (terminees.includes("true")) {
        document.getElementById("selectedStepsWarning").style.display = "block";
        document.getElementById("selectedStepsInfo").style.display = "none";
    } else {
        document.getElementById("selectedStepsInfo").style.display = "none";
        document.getElementById("selectedStepsWarning").style.display = "none";
    }
}

function doCreateSharedNoteEtape(form) {
    var modal = $("#add-edit-note-etape-modal");
    if (!isValidForm(modal)) return false;

    var tableFdrId = "table-fdr";
    var fdrId = document.getElementById("d_fdr_content").getAttribute("fdr-id");
    var noteContent = $("#note-textarea").val();
    var myTable = $("[id^=ROUTE_STEP_SHARED_NOTE_ADD]").closest(".tableForm");
    var etapes = [];
    var terminees = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            var terminee = $(this).closest("tr").attr("terminee");
            if (terminee != "true") {
                etapes.push($(this).closest("tr").attr("id"));
            }
            terminees.push(terminee);
        });
    if (terminees.every((v) => v === "true")) {
        return false;
    } else {
        var ajaxUrl = $("#ajaxCallPath").val() + "/etape/note/creerNotePartagee";
        var myRequest = {
            contentId: null,
            dataToSend: {
                fdrId: document.getElementById("d_fdr_content").getAttribute("fdr-id"),
                idEtapes: etapes,
                commentContent: noteContent,
                dossierLinkId: $("#dossierLinkId").val(),
            },
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
        };
        $("#reload-loader").css("display", "block");

        closeModal(modal.get(0));
        resetTextarea();

        callAjaxRequest(myRequest, displayNoteSuccessToastAndLoadFdrTab, displaySimpleErrorMessage);
    }
    document.getElementById("selectedStepsInfo").style.display = "none";
    document.getElementById("selectedStepsWarning").style.display = "none";
}

function openAddNotesEtapesMultiDossier() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/etape/note/chargerEtapesEnCours";
    var myTable = $(".ACTION_AJOUT_NOTE_ETAPE_MULTI").closest(".tableForm");
    var dossiers = [];
    var routingTaskTypes = [];
    $(myTable)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            dossiers.push($(this).closest("tr").attr("dossier-link-id"));
            routingTaskTypes.push($(this).closest("tr").attr("routing-task-type"));
        });

    var myRequest = {
        contentId: "modaleInfo",
        dataToSend: {
            idDossiers: dossiers,
            routingTaskTypes: routingTaskTypes,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, replaceWithHtmlFunction, displaySimpleErrorMessage);
}

function valideFormModaleNoteEtape() {
    var modal = $("#add-edit-note-etape-modal");
    return isValidForm(modal);
}

function doCreateNotesEtapesMultiDossiers() {
    var modal = $("#add-edit-note-etape-modal");
    if (valideFormModaleNoteEtape()) {
        var ajaxUrl = $("#ajaxCallPath").val() + "/etape/note/creerNotesMultiDossiers";
        var myTable = $(".ACTION_AJOUT_NOTE_ETAPE_MULTI").closest(".tableForm");
        var dossierLinkIds = [];
        $(myTable)
            .find(":checkbox:checked.js-custom-table-line-check")
            .each(function () {
                dossierLinkIds.push($(this).closest("tr").attr("dossier-link-id"));
            });

        var data = { dossierLinkIds: dossierLinkIds, commentContent: DOMPurify.sanitize($("#note-textarea").val()) };
        if (getOtherData() != null) {
            data = jQuery.extend(data, getOtherData());
        }

        var myRequest = {
            contentId: null,
            dataToSend: data,
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: "reload-loader",
        };
        closeModal(modal.get(0));
        resetTextarea();

        callAjaxRequest(myRequest, displaySuccessOrMessage, displaySimpleErrorMessage);
    }
}

/* surchargé côté EPG */

function getOtherData() {
    return null;
}

function loadStep(stepId) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/etape/charger";
    var myRequest = {
        contentId: "fieldset-editStep",
        dataToSend: {
            stepId: stepId,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, loadStepAndInitSelect, displaySimpleErrorMessage);
}

function doSaveEditEtape() {
    var modal = $("#editEtape");
    if (!isValidForm(modal)) return false;

    var ajaxUrl = $("#ajaxCallPath").val() + "/etape/editer";
    var myRequest = {
        contentId: $("#stepId").val(),
        dataToSend: {
            dossierLinkId: $("#dossierLinkId").val(),
            typeEtape: $("#typeEtape-editStep").val(),
            destinataire: $("#destinataire-editStep option[selected]").val(),
            echeance: $("#echeance-editStep").val(),
            valAuto: $("#form_select_stage_val_auto_serie_editStep").is(":checked"),
            obligatoire: $("input:radio[name ='obligatoire-editStep']:checked").val(),
            totalNbLevel: $("#totalNbLevel").val(),
            isModele: $("#isModele").val(),
            stepId: $("#stepId").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        extraDatas: {
            idContentModal: "contentEditEtapeFdr",
            idModal: "editEtape",
        },
    };

    callAjaxRequest(myRequest, resultEditStep, modalErrorMessage);
}

function resultEditStep(elementId, result, caller, extraDatas, myXhr) {
    var jsonResponse = JSON.parse(result);
    const modal = $("#" + extraDatas.idModal);
    closeModal(modal.get(0));
    var messages = jsonResponse.messages;

    $("#" + elementId)
        .find(".edit-step-replace")
        .each(function () {
            $(this).remove();
        });
    $("#" + elementId).append(jsonResponse.data);

    cleanAlerts();
    if (messages.successMessageQueue.length > 0) {
        constructAlertContainer(messages.successMessageQueue);
    }
}

function copySteps() {
    var $selectedIds = $(".roadmap-table .custom-table__body")
        .find("input[type=checkbox]:checked")
        .parents("tr")
        .map(function () {
            return this.id;
        });
    if ($selectedIds) {
        var ajaxUrl = $("#ajaxCallPath").val() + "/etape/copier";
        var myRequest = {
            contentId: null,
            dataToSend: {
                stepIds: $selectedIds.toArray(),
            },
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
        };

        callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
    }
}

function pasteStep(lineId, before) {
    var ajaxUrl = $("#ajaxCallPath").val() + "/etape/coller";
    var myRequest = {
        contentId: null,
        dataToSend: {
            lineId: lineId,
            before: before,
            rootDocId: $("#dossierId") && $("#dossierId").val() ? $("#dossierId").val() : $("#idModele").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doRechercheModeleFdr() {
    event.preventDefault();
    if (isValidForm($("#searchModeleForm"))) {
        var mydatas = $("#searchModeleForm").serialize();
        $("#listeModeles")
            .find("input[data-isForm='true'], select[data-isForm='true']")
            .each(function () {
                if (this.value !== undefined && this.name == "size") {
                    mydatas += "&" + this.name + "=" + this.value;
                }
            });
        var ajaxUrl = $("#ajaxCallPath").val() + "/fdr/rechercher/resultats";
        var myRequest = {
            contentId: "resultList",
            dataToSend: mydatas,
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            loadingButton: $("#btn-searchMfdr"),
            extraDatas: {
                elementToFocus: "#focusResultat",
            },
        };
        callAjaxRequest(myRequest, replaceHtmlFunctionAndFocusResult, displaySimpleErrorMessage);
    }
}

function checkErrorOrFocusToFdrTable(contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        window.location.hash = "#table-fdr";
        reloadPage();
    }
}

function toggleOrganigrammeSelectAutocomplete(organigramme, enable) {
    if (enable) {
        organigramme.find("button.interstitial-overlay__trigger").prop("disabled", false);
        organigramme.find("label.form-label").removeClass("form-label--disabled");
        organigramme.find("label.form-input__description").removeClass("form-label--disabled");
        organigramme.find("select").removeClass("form-select-input__field--disabled");
        organigramme.find("select").removeAttr("readonly");
        organigramme.find("select").attr("data-validation", "required");
        organigramme.find("input").removeAttr("disabled");
    } else {
        organigramme.find("button.interstitial-overlay__trigger").prop("disabled", true);
        organigramme.find("label.form-label").addClass("form-label--disabled");
        organigramme.find("label.form-input__description").addClass("form-label--disabled");
        organigramme.find("select").addClass("form-select-input__field--disabled");
        organigramme.find("select").prop("readonly", true);
        organigramme.find("select").removeAttr("data-validation");
        organigramme.find("input").attr("disabled", "disabled");
        organigramme.find("input").val("");
        organigramme.find("option").remove();
    }

    cleanPreviousErrorOrSuccess(organigramme.find("input"));
}

var widgetNumber = 0;

var evtType;
const typeEvenementId = "#typeEvenement";
const natureLoiId = "#natureLoi";
const natureLoiText = "#natureLoi-text";
const typeLoiId = "#typeLoi";
const objetId = "#form_textarea-objet";
const intituleId = "#form_textarea-intitule";
const dateCongresId = "#dateCongres";
const emetteurId = "#emetteur";
const destinataireId = "#destinataire";
const destinataireCopieId = "#destinataireCopie";
const destinataireCopieText = "#destinataireCopie-text";
const niveauLectureId = "#niveauLecture";
const rubriqueSelectedId = "#rubrique option";
const rubriqueInputId = "#rubriquearia-autocomplete-1-input";
var previousDateValue;

$(document).ready(function () {
    evtType = $(typeEvenementId).val();

    switch (evtType) {
        case "EVT01":
        case "EVT02":
            $(natureLoiId).change(function () {
                updateFields();
            });
            $(typeLoiId).change(function () {
                updateFields();
            });
        case "EVT39":
        case "EVT43BIS":
            $(objetId).change(function () {
                updateFields();
            });
            break;
        case "EVT35":
            setInterval(function () {
                if (previousDateValue != $(dateCongresId).val()) {
                    updateFields();
                    previousDateValue = $(dateCongresId).val();
                }
            }, 1000);
            break;
        case "EVT45":
            $(emetteur).change(function () {
                clearRubrique();
            });
            break;
    }

    $(destinataireId).unbind("change");
    $(destinataireId).change(function () {
        updateDestinataires();

        if (
            evtType == "EVT01" ||
            evtType == "EVT09" ||
            evtType == "EVT10" ||
            evtType == "EVT23BIS" ||
            evtType == "EVT25"
        ) {
            updateNiveauLecture();
            showHideNiveauLecture($("#niveauLecture").val());
        }
    });

    showHideNiveauLecture($("#niveauLecture").val());
});

function updateFields() {
    switch (evtType) {
        case "EVT01":
        case "EVT02":
            updateIntitule();
            break;
        case "EVT35":
            updateObjet();
            break;
        case "EVT39":
            updateIntituleWithObjet(
                "Proposition de résolution, déposée en application de l'article 34-1 de la constitution, "
            );
            break;
        case "EVT43BIS":
            updateIntituleWithObjet("Résolution européenne ");
            break;
    }
}

function updateIntitule() {
    var natureLoi = $(natureLoiId + " option:selected").text();
    if (natureLoi === "") {
        natureLoi = $(natureLoiId).text() === "" ? $(natureLoiId).val() : $(natureLoiId).text();
    }
    natureLoi = jQuery.trim(natureLoi);
    var typeLoi = $(typeLoiId + " option:selected").text();
    var typeLoiValue = $(typeLoiId + " option:selected").val();
    var objet = $(objetId).val();

    if ("" == typeLoiValue) {
        $(intituleId).val(natureLoi + " " + objet);
    } else {
        $(intituleId).val(natureLoi + " de " + typeLoi.toLowerCase() + " " + objet);
    }
}

function updateObjet() {
    var date = $(dateCongresId).val();
    $(objetId).val("Convocation du congrès " + date);
}

function updateIntituleWithObjet(value) {
    var objet = $(objetId).val();
    $(intituleId).val(value + objet);
}

function updateDestinataires() {
    const emetteurIdCreation = "#emetteur-id";
    let currentEmetteur = $(emetteurIdCreation).length ? emetteurIdCreation : emetteurId;
    if ($(currentEmetteur).val() == $(destinataireId).val()) {
        $(destinataireId).val("");
    } else if ($(destinataireCopieText).length || $(destinataireCopieId).length) {
        // Si le champ copie est affiché
        var myRequest = {
            contentId: null,
            dataToSend: {
                typeEvenement: evtType,
                emetteur: $(currentEmetteur).val(),
                destinataire: $(destinataireId).val(),
            },
            method: "POST",
            dataType: "html",
            ajaxUrl: $("#ajaxCallPath").val() + "/communication/changerDestinataireCopie",
            isChangeURL: false,
            overlay: "reload-loader",
            loadingButton: null,
        };

        callAjaxRequest(myRequest, setNewDestinataireCopie, displaySimpleErrorMessage);
    }
}

window.setNewDestinataireCopie = function (containerID, result, caller, extraDatas) {
    if (result != null) {
        var res = JSON.parse(result);
        var data = JSON.parse(res.data);
        if (data.destinataireCopieId != "" && data.destinataireCopieLabel != "") {
            if ($(destinataireCopieId).length && $(destinataireCopieText).length) {
                $(destinataireCopieId).val(data.destinataireCopieId);
            } else {
                $(destinataireCopieId).val(data.destinataireCopieLabel);
                var content =
                    '<input id="destinataireCopie-hidden" name="destinataireCopie" type="hidden" value="' +
                    data.destinataireCopieId +
                    '"/>';
                const destinataireCopieContainer = $(destinataireCopieText).length
                    ? destinataireCopieText
                    : destinataireCopieId;
                let $containerParent = $(destinataireCopieContainer).parent();
                let $destinataireCopieIdContent = $containerParent.find(destinataireCopieId);
                if ($destinataireCopieIdContent.length) {
                    $destinataireCopieIdContent.html(content);
                } else {
                    $containerParent.append(content);
                }
            }
            $(destinataireCopieText).text(data.destinataireCopieLabel);
        }
    }
};

function updateNiveauLecture() {
    var destinataire = $(destinataireId).val();
    if (destinataire == "ASSEMBLEE_NATIONALE") {
        if (evtType == "EVT23BIS") {
            $(niveauLectureId).val("NOUVELLE_LECTURE_AN");
        } else {
            $(niveauLectureId).val("AN");
        }
    } else if (destinataire == "SENAT") {
        if (evtType == "EVT23BIS") {
            $(niveauLectureId).val("NOUVELLE_LECTURE_SENAT");
        } else {
            $(niveauLectureId).val("SENAT");
        }
    }
}

function clearRubrique() {
    $(rubriqueSelectedId).remove();
    $(rubriqueInputId).val("");
}

function doSearch() {
    var map = buildSearchMap();

    var myRequest = {
        contentId: "listeCommunications",
        dataToSend: { search: JSON.stringify([...map]) },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/corbeille/search",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, replaceWithHtmlFunction, tabLoadError);
}

function reinitialiserSearch() {
    let typeForm = "";
    if ($("#searchForm").length > 0) typeForm = "#searchForm";
    else if ($("#formDynamique").length > 0) typeForm = "#formDynamique";
    var form = $($(event.srcElement).closest(typeForm)[0]);

    form.trigger("reset");
    form.find(".aria-autocomplete__selected").remove();

    var myRequest = {
        contentId: null,
        dataToSend: { idCorbeille: $("#idCorbeille").val() },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/corbeille/reinitialiser",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrHardReload, displaySimpleErrorMessage);
}

function addBLocAddPieceJointe(el, pjMultiValue, widgetName, defaultValue) {
    // cacher le bouton si le widget n'est pas multi piece jointe
    if (!pjMultiValue) {
        $(el).hide();
    } else {
        widgetNumber++;
    }

    var widgetLabel = $(el).data("label");
    var widget = pjMultiValue ? widgetName + widgetNumber : widgetName;

    var myRequest = {
        contentId: widgetName + "PjContainer",
        dataToSend: {
            widget: widget,
            widgetName: widgetName,
            widgetLabel: widgetLabel,
            multiValue: pjMultiValue,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl:
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + "/communication/buildBlocPJ",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, appendBlocPJ, tabLoadError);
}

var appendBlocPJ = function rappendBlocPJinContainer(containerID, result) {
    $("#" + containerID).append(result);
    initInterstitial();
};

function doEnCoursDeTraitement() {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idMessage: $("#idMessage").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/enCoursDeTraitement",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doTraiter() {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idMessage: $("#idMessage").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/traiter",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doTransmettreParMail() {
    var myRequest = {
        contentId: null,
        dataToSend: null,
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/transmettre",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrToast, displaySimpleErrorMessage);
}

function doDeleteCommunication() {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idMessage: $("#idMessage").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/supprimer",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrGoPrevious, displaySimpleErrorMessage);
}

function doAnnulerCommunication() {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idMessage: $("#idMessage").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/annuler",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrGoPrevious, displaySimpleErrorMessage);
}

function doAccepter() {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idMessage: $("#idMessage").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/accepter",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doRejeter() {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idMessage: $("#idMessage").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/rejeter",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doAccuserReception() {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idMessage: $("#idMessage").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/accuserReception",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doAbandonner() {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idMessage: $("#idMessage").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/abandonner",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function doPublier() {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idMessage: $("#idMessage").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/publier",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
}

function addValeurToList(el, widgetName) {
    var inputNode = $(el).closest("div").find("input:visible");
    var listNode = $(el).closest(".rightBlocWidget").find("ul");
    var date_regex = /^(0[1-9]|1\d|2\d|3[01])\/(0[1-9]|1[0-2])\/(19|20)\d{2}$/;
    if ($(inputNode).val().length > 0) {
        if (!widgetName.includes("date") || (widgetName.includes("date") && date_regex.test($(inputNode).val()))) {
            $(listNode).append(
                $("<li/>")
                    .text($(inputNode).val())
                    .append(
                        $("<input/>")
                            .attr("type", "hidden")
                            .attr("multiple", "")
                            .attr("name", widgetName)
                            .attr("value", DOMPurify.sanitize($(inputNode).val()))
                    )
                    .append(
                        $("<span/>")
                            .addClass("icon icon--cross-bold link__icon link__icon--append")
                            .on("click", function () {
                                $(this).closest("li").remove();
                            })
                            .attr("aria-hidden", "true")
                    )
            );
            $(inputNode).val("");
        }
    }
}

function goToCreationCommunication() {
    showReloadLoader();
    var path = $("#ajaxCallPath")
        .val()
        .substring(0, $("#ajaxCallPath").val().length - 5);
    var type = $("#select-evenement").val();
    window.location.href = path + "/communication/creation?typeEvenement=" + type + "#main_content";
}

function goToCreationCommunicationSuccessive() {
    var type = $("#communication-select").val();
    if (type != null) {
        doCheckLock("/communication/creation?typeEvenement=" + type + "&idMessagePrecedent=");
    }
}

function changeVersion() {
    const id = $("#idMessage").val();
    const path = $("#ajaxCallPath").val() + "/dossier/" + id + "/detailCommunication";

    if ($("#version-selector")) {
        var myRequest = {
            contentId: "d_tab_content-0",
            dataToSend: {
                version: $("#version-selector").val(),
            },
            method: "GET",
            dataType: "html",
            ajaxUrl: path,
            isChangeURL: true,
            overlay: "reload-loader",
        };

        callAjaxRequest(myRequest, replaceHtmlFunction, displaySimpleErrorMessage);
    }
}

function doCheckLock(urlSuccess, isFunction, skipUnlock, skipRelock) {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idMessage: $("#idMessage").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/verifier/verrou",
        extraDatas: {
            urlSuccess: urlSuccess,
            isFunction: isFunction,
            skipRelock: skipRelock,
            skipUnlock: skipUnlock,
        },
        isChangeURL: false,
        loadingButton: event.target,
    };

    callAjaxRequest(myRequest, openLockModalOrContinue, displaySimpleErrorMessage);
}

window.openLockModalOrContinue = function (contentId, result, caller, extraDatas) {
    const message = JSON.parse(result).data;
    const path = $("#ajaxCallPath")
        .val()
        .substring(0, $("#ajaxCallPath").val().length - 5);
    const isFunction = extraDatas.isFunction;
    let urlSuccess = isFunction
        ? extraDatas.urlSuccess
        : extraDatas.urlSuccess + DOMPurify.sanitize($("#idMessage").val()) + "#main_content";
    if (message) {
		if(extraDatas.skipUnlock) {
            constructAlert(errorMessageType, ["Vous n'avez plus le verrou sur cette communication."], null);
            return;
		}
        const modale = $("#modal-communication-verrou")[0];
        $(modale).find(".interstitial__content").html(message);
        $("#communication-verrou-url").data("url-success", urlSuccess);
        $("#communication-verrou-url").data("is-function", isFunction);
        $("#communication-verrou-url").data("skip-relock", extraDatas.skipRelock);
        openModal(modale);
    } else if (isFunction) {
        eval(urlSuccess);
    } else {
        window.location.href = path + urlSuccess;
    }
};

function doForceUnlockCommunication() {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idMessage: $("#idMessage").val(),
            skipLock: $("#communication-verrou-url").data("skip-relock")
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/deverrouiller/force",
        isChangeURL: false,
        overlay: "reload-loader",
    };
    callAjaxRequest(myRequest, goToSuccessAfterForceUnlock, displaySimpleErrorMessage);
}

window.goToSuccessAfterForceUnlock = function (contentId, result, caller, extraDatas) {
    const isFunction = $("#communication-verrou-url").data("is-function");
    const urlSuccess = $("#communication-verrou-url").data("url-success");
    if (isFunction) {
        eval(urlSuccess);
    } else {
	    const path = $("#ajaxCallPath")
	        .val()
	        .substring(0, $("#ajaxCallPath").val().length - 5);
	    window.location.href = path + urlSuccess;
	}
};

function unlockThenGoPreviousPage() {
    var myRequest = {
        contentId: null,
        dataToSend: {
            idMessage: $("#idMessage").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/communication/deverrouiller",
        isChangeURL: false,
        overlay: "reload-loader",
    };

    callAjaxRequest(myRequest, goPreviousOrReloadIfError, displaySimpleErrorMessage);
}

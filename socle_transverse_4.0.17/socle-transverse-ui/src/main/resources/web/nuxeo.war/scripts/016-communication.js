/**************************************************************************************/
/*******                        Scripts Gestion communication EPP et MGPP      ********/
/**************************************************************************************/
function showHideNiveauLecture(el) {
    if (el === "AN" || el === "SENAT") {
        $("#niveauLectureNumeroInput").show();
    } else {
        $("#niveauLectureNumeroInput").hide();
        $("#niveauLectureNumero").val("");
    }
}

function saveCommunication(publier, urlTarget) {
    var communicationForm = $("#formEditCommunication");
    if (!publier || isValidForm(communicationForm)) {
        $("#publier").val(publier);
        var pieceJointes = [];

        /*
         * Avant enregistrement : on parcourt tous les files du formulaire et on
         * upload un par un
         */
        $("form#formEditCommunication input[type=file]").each(function () {
            if ($(this)[0].files.length) {
                var pId = $(this).attr("name").split("-file")[0];
                var existingPJ = pieceJointes.find((pj) => pj.pieceId === pId);
                if (existingPJ) {
                    existingPJ.files = Array.from(existingPJ.files).concat(Array.from($(this)[0].files));
                } else {
                    pieceJointes.push({
                        pieceId: pId,
                        files: $(this)[0].files,
                    });
                }
            }
        });

        uploadPJsThenSaveCommunication(pieceJointes, [], urlTarget);
    }
}

function uploadPJsThenSaveCommunication(pieceJointes, result, urlTarget) {
    var curPieceJointe = pieceJointes.shift();
    if (curPieceJointe != null) {
        var files = curPieceJointe.files;
        if (files.length) {
            uploadFiles(
                files,
                null,
                (batchId) => {
                    result.push({
                        pieceId: curPieceJointe.pieceId,
                        uploadBatchId: batchId,
                    });
                    uploadPJsThenSaveCommunication(pieceJointes, result, urlTarget);
                },
                displaySimpleErrorMessage
            );
        } else {
            uploadPJsThenSaveCommunication(pieceJointes, result, urlTarget);
        }
    } else {
        saveCommunicationFinale(result, urlTarget);
    }
}

function saveCommunicationFinale(result, urlTarget) {
    var map = new Map();
    var form = $("#formEditCommunication");
    var dataForm = form.serializeArray();
    // Ajouter les selects sans options
    form.find("select")
        .filter(function () {
            return this.value == "" && !dataForm.some((el) => el.name === this.name);
        })
        .each(function () {
            dataForm.push({ ["name"]: this.name, ["value"]: "null" });
        });
    var doneItem = [];
    var pjMap = new Map();
    for (var i = 0; i < dataForm.length; i++) {
        var curName = dataForm[i].name;
        if (curName.includes("fromDossier")) {
            var arrayPj = pjMap.get(curName);
            if (!arrayPj) {
                arrayPj = [];
            }
            arrayPj.push(dataForm[i].value);
            pjMap.set(curName, arrayPj);
            map.set(curName, arrayPj);
            // Cas générique on gère des strings mais on fait attention qu'on n'est pas dans le cas d'un select multiple avec qu'un enfant
        } else if (!map.has(curName) && $("[name='" + curName + "'][multiple]").length <= 0) {
            map.set(curName, dataForm[i].value);
        } else {
            // Sinon c'est un tableau
            // On vérifie qu'on n'a pas déjà traité cet élément
            if (!doneItem.includes(curName)) {
                var values = [];
                // On rajoute l'élément qui avait déjà été stocké dans la map +
                // l'élément courant
                if (map.get(curName)) {
                    values.push(map.get(curName));
                }
                values.push(dataForm[i].value);
                // On nettoie notre map
                map.delete(curName);

                // On va parcourir le reste pour finir de traiter cet item
                if (i + 1 < dataForm.length) {
                    for (var j = i + 1; j < dataForm.length; j++) {
                        if (dataForm[j].name === curName) {
                            values.push(dataForm[j].value);
                        }
                    }
                }
                doneItem.push(curName);
                map.set(curName, values);
            }
        }
    }

    /*
     * Result st le json retourné après chaque upload de fichier il contient la
     * liste des fichiers uploadés avec l'id de la piece jointe
     */
    if (result != null) {
        result.forEach((pj) => map.set(pj.pieceId + "-uploadBatchId", pj.uploadBatchId));
    }

    var myRequest = {
        contentId: null,
        dataToSend: {
            communication: JSON.stringify([...map]),
            idMessage: $("#idMessage").val(),
            publier: $("#publier").val(),
            typeAction: $("#typeAction").val(),
            typeEvenement: $("#typeEvenement").val(),
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#basePath").val() + urlTarget,
        isChangeURL: false,
        overlay: "reload-loader",
        loadingButton: null,
    };

    callAjaxRequest(myRequest, checkErrorOrGoToCom, displaySimpleErrorMessage);
}

window.checkErrorOrGoToCom = function (contentId, result, caller, extraDatas, xhr) {
    var jsonResponse = JSON.parse(result);
    var dataJson = JSON.parse(jsonResponse.data);
    var pathRedirect =
        dataJson.app == "epp"
            ? "dossier/" + dataJson.idMessage + "/detailCommunication"
            : "mgpp/dossier/" + dataJson.idMessage + "/fiche";
    var messagesContaineur = jsonResponse.messages;
    if (messagesContaineur.dangerMessageQueue.length > 0) {
        constructAlertContainer(messagesContaineur.dangerMessageQueue);
    } else {
        showReloadLoader();
        window.location.href = $("#basePath").val() + pathRedirect;
    }
};

function envoyerMel(target) {
    var transmettreParMelForm = $("#transmettre_par_mel_form");
    if (isValidForm(transmettreParMelForm)) {
        const ajaxUrl = $("#ajaxCallPath").val() + target;
        var myRequest = {
            contentId: null,
            dataToSend: transmettreParMelForm.serialize(),
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: "reload-loader",
        };

        callAjaxRequest(myRequest, checkErrorOrGoPrevious, displaySimpleErrorMessage);
    }
}

function addFichiersToForm(pieceId) {
    $("#" + pieceId + "-fromDossier-error").empty();
    $("input[id^=form_input_file_drag_and_drop_multiple_with_text_" + pieceId + "_").each(function () {
        var fichiers = $(this)[0].files;
        for (let i = 0; i < fichiers.length; i++) {
            var oldInput = $(this);
            var newInput = oldInput.clone();
            oldInput.attr("id", "oldInput");
            var content =
                '<li class="uploadedFile">' +
                '<a href="' +
                URL.createObjectURL(fichiers[i]) +
                '" download="' +
                fichiers[i].name +
                '">' +
                fichiers[i].name +
                "</a>" +
                '<button type="button" onclick=annulerFichiersPJ(this) ' +
                'class="base-btn base-btn--button base-btn--default base-btn--light base-btn--transparent btnAddPieceJointe" ' +
                "aria-label=\"Annuler l'ajout de " +
                fichiers[i].name +
                '" ' +
                "data-tippy-content=\"Annuler l'ajout de " +
                fichiers[i].name +
                '">' +
                '<span aria-hidden="true" class="icon icon--bin link__icon link__icon--append"></span></button></li>';

            $(".list-item-file-" + pieceId).append(content);
            $(".list-item-file-" + pieceId)
                .children("li")
                .last()
                .append(newInput);
        }
    });

    $("input[id=oldInput").closest("li").remove();

    initToolTip();
}

function annulerFichiersPJ(button) {
    $(button).closest("li").remove();
}

function removePJ(el, pjMultiValue, widgetName) {
    $(el).closest("div").remove();
    if (!pjMultiValue) {
        $("#" + widgetName + "BtnAdd").show();
    }
}

function loadListeDocs(pieceId) {
    $("#" + pieceId + "-fromDossier-error").empty();
    var id = $("#idDossier").val().length > 0 ? $("#idDossier").val() : $("form#formEditCommunication #nor").val();

    if (id) {
        var myRequest = {
            contentId: "d_document_content-" + pieceId,
            dataToSend: {
                pieceId: pieceId,
                id: id,
            },
            method: "POST",
            dataType: "html",
            ajaxUrl: $("#ajaxCallPath").val() + "/mgpp/communication/ajoutDocuments",
            isChangeURL: false,
            overlay: "reload-loader",
            loadingButton: null,
        };
        callAjaxRequest(myRequest, replaceWithHtmlFunction, tabLoadError);
    } else {
        var errorDiv = initializeErrorDiv();
        errorDiv.text("Aucune référence à un dossier n'a été trouvée");
        $("#" + pieceId + "-fromDossier-error").append(errorDiv);
    }
}

function addDocsFromDossierToPJ(pieceId) {
    var docsSelected = new Map();
    $("#d_document_content-" + pieceId)
        .find(":checkbox:checked.js-custom-table-line-check")
        .each(function () {
            docsSelected.set($(this).closest("tr").attr("data-id"), $(this).closest("tr").find("a"));
        });

    var listDocsToUpdate = $(".list-item-file-" + pieceId);

    for (const [fichierId, fichierLink] of docsSelected) {
        var fichierNom = fichierLink.attr("title");
        var blocLI = document.createElement("li");

        var blocA = document.createElement("a");
        $(blocA).text(fichierNom);
        $(blocA).attr("href", fichierLink.attr("href"));
        $(blocA).attr("download", fichierNom);

        var blocButton = document.createElement("button");
        $(blocButton).attr("type", "button");
        $(blocButton).attr("onclick", "$(this).closest('li').remove()");
        $(blocButton).attr(
            "class",
            "base-btn base-btn--button base-btn--default base-btn--light base-btn--transparent"
        );

        var blocIcon = document.createElement("span");
        $(blocIcon).attr("aria-hidden", "true");
        $(blocIcon).attr("class", "icon icon--bin link__icon link__icon--append");

        $(blocButton).append(blocIcon);

        var blocInput = document.createElement("input");
        $(blocInput).attr("type", "hidden");
        $(blocInput).attr("name", pieceId + "-fromDossier");
        $(blocInput).val(fichierId);

        $(blocLI).append(blocA);
        $(blocLI).append(blocButton);
        $(blocLI).append(blocInput);
        $(listDocsToUpdate).append(blocLI);
    }
}

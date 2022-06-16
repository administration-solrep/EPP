function doPopModaleSauvegarde(id, event, callback, ...params) {
    var modale = $("#modale-sauvegarde-" + id);

    if (modale.length) {
        if (!window[modale.data("check-function")]()) {
            if (modale.attr("data-confirmed") == "no") {
                openModal(modale[0]);
                window["modale-sauvegarde-" + id + "-callback-target"] = event.target;
                modale.find("button")[0].focus();
                event.stopImmediatePropagation();
                return;
            }
        }
    }
    callback(...params);
}

function poursuivreNavigationModaleSauvegarde(button, save) {
    var modale = $(button).closest(".interstitial-overlay__content");
    var modaleName = modale.attr("id").substring(18);
    modale.attr("data-confirmed", "yes");
    if (save) {
        window[modale.attr("data-save-function")]();
    } else {
        callbackModaleSauvegarde(modaleName);
    }
    closeModal(modale[0]);
}

function callbackModaleSauvegarde(id) {
    $(window["modale-sauvegarde-" + id + "-callback-target"]).trigger("click");
    initModaleSauvegarde(id);
}

function annulerNavigationModaleSauvegarde(button) {
    var modale = $(button).closest(".interstitial-overlay__content");
    closeModal(modale.get(0));
}

function initModaleSauvegarde(id) {
    var modale = $("#modale-sauvegarde-" + id);
    if (modale.length == 1) {
        window[modale.attr("data-init-function")]();
        modale.attr("data-confirmed", "no");
    }
}

function initModalesSauvegarde() {
    $("div[id^=modale-sauvegarde-]").each(function () {
        initModaleSauvegarde($(this).attr("name"));
    });
}

function updateInputModaleSauvegarde(name, modale, val) {
    const inputName = modale.attr("id") + "-" + name;
    const input = $("#" + inputName);
    var sanitizeVal = val instanceof jQuery || val === "" ? val : DOMPurify.sanitize(val);

    if (input.length) {
        if (isHTML(val)) {
            input.html(sanitizeVal);
        } else {
            input.text(sanitizeVal);
        }
    } else {
        modale.append(
            $("<span>", {
                id: modale.attr("id") + "-" + name,
                hidden: true,
                html: sanitizeVal,
            })
        );
    }
}

function insertInputModaleSauvegarde(ids, modale, pattern) {
    ids.forEach(function (name) {
        const data = $(pattern.replace("{}", name));
        if (data.length) {
            updateInputModaleSauvegarde(name, modale, data.val());
        }
    });
}

function insertTableModaleSauvegarde(tableId, ids, modale) {
    const nbLignes = $("#" + tableId + " tbody tr").length;
    let array = $("#" + modale.attr("id") + "-" + tableId);

    if (!array.length) {
        array = $("<span>", {
            hidden: true,
            id: modale.attr("id") + "-" + tableId,
        });
        modale.append(array);
    } else {
        array.empty();
    }

    for (let i = 0; i < nbLignes; ++i) {
        let row = $("<span>");
        ids.forEach(function (name) {
            const data = $("#" + name + "-" + (i + 1));
            row.attr("data-" + name, data.text());
        });
        array.append(row);
    }
}

function insertCheckboxModaleSauvegarde(ids, modale) {
    ids.forEach(function (name) {
        const data = $("#" + name);
        if (data.length) {
            updateInputModaleSauvegarde(name, modale, data.is(":checked").toString());
        }
    });
}

function insertTextInputModaleSauvegarde(ids, modale) {
    insertInputModaleSauvegarde(ids, modale, "#{}");
}

function insertOptinInputModaleSauvegarde(ids, modale) {
    insertInputModaleSauvegarde(ids, modale, "[id^='{}-']:checked");
}

function insertAutocompleteInputModaleSauvegarde(ids, modale) {
    ids.forEach(function (name) {
        if ($("#" + name)) {
            const selectNode = $("#" + name + " option:selected");
            const saveNode = $("#" + modale.attr("id") + "-" + name);

            if (saveNode.length) {
                saveNode.html(selectNode.clone());
            } else {
                updateInputModaleSauvegarde(name, modale, selectNode.clone());
            }
        }
    });
}

function checkInputModaleSauvegarde(ids, modale, pattern) {
    let ok = true;
    ids.some(function (name) {
        const formData = $(pattern.replace("{}", name));
        const modaleData = $("#" + modale.attr("id") + "-" + name);

        if (!formData.length && !modaleData.length) {
            return;
        }
        const formDataVal = formData.val() || "";
        if (
            formData.length ^ modaleData.length ||
            (!isHTML(formDataVal) && modaleData.text() !== formDataVal) ||
            (isHTML(formDataVal) && modaleData.html() !== formDataVal)
        ) {
            ok = false;
            return true;
        }
    });
    return ok;
}

function checkTableModaleSauvegarde(tableId, ids, modale) {
    const formTableLines = $("#" + tableId + " tbody tr");
    const modaleTableLines = $("#" + modale.attr("id") + "-" + tableId + " span");

    let tableOk = true;

    if (formTableLines.length !== modaleTableLines.length) {
        return false;
    }

    for (let i = 0; i < modaleTableLines.length; i++) {
        ids.some(function (tdId) {
            if ($("#" + tdId + "-" + (i + 1)).text() !== $(modaleTableLines[i]).attr("data-" + tdId)) {
                tableOk = false;
                return true;
            }
        });
    }

    return tableOk;
}

function checkCheckboxInputModaleSauvegarde(ids, modale) {
    let ok = true;
    ids.some(function (name) {
        const formData = $("#" + name);
        const modaleData = $("#" + modale.attr("id") + "-" + name);

        if (!formData.length && !modaleData.length) {
            return;
        }
        if (formData.length ^ modaleData.length || modaleData.text() !== formData.is(":checked").toString()) {
            ok = false;
            return true;
        }
    });
    return ok;
}

function checkTextInputModaleSauvegarde(ids, modale) {
    return checkInputModaleSauvegarde(ids, modale, "#{}");
}

function checkOptinInputModaleSauvegarde(ids, modale) {
    return checkInputModaleSauvegarde(ids, modale, "[id^='{}-']:checked");
}

function checkAutocompleteInputModaleSauvegarde(ids, modale) {
    let ok = true;
    ids.some(function (name) {
        const saved = $("#" + modale.attr("id") + "-" + name + " option");
        const actual = $("#" + name + " option:selected");
        if (saved.length !== actual.length) {
            ok = false;
            return true;
        }
        if (saved.length && ok) {
            for (let i = 0; i < actual.length; ++i) {
                if (saved[i].value !== actual[i].value) {
                    ok = false;
                    return true;
                }
            }
        }
    });
    return ok;
}

function selectChampRequete(value) {
    if (value) {
        var ajaxUrl = $("#requete-url-ajax").val() + "/select-champ";
        var myRequest = {
            contentId: "requeteField",
            dataToSend: "id=" + value,
            method: "GET",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            caller: this,
        };
        callAjaxRequest(myRequest, onSelectChampRequete, displaySimpleErrorMessage);
    } else {
        $("#requeteField").empty();
    }
}

function onSelectOperator() {
    var onChange = $("#selectOperator option:selected").data("display");
    var $fields = $(".value-fields");
    var $second = $(".second-date");
    if (onChange) {
        if (onChange == "displayOne") {
            $second.hide();
            $second.find("input").prop("disabled", true);
        } else if (onChange == "displayTwo") {
            $second.show();
            $second.find("input").prop("disabled", false);
        } else if (onChange == "displayNone") {
            $fields.hide();
            $fields.find("input").prop("disabled", true);
        }
    }
}

var onSelectChampRequete = function displayChampRequete(containerID, result, caller) {
    replaceHtmlFunction(containerID, result);
    initAsyncSelect();
    initFormValidation();
};

function addElementToRequete() {
    if (isValidForm($("#requeteExperteChamp"))) {
        var ajaxUrl = $("#requete-url-ajax").val() + "/add";
        var dataToSend = $("#requeteExperteChamp").serialize();
        var labelSelect = $("select#value option:selected");
        if (labelSelect) {
            labelSelect.each(function (index, element) {
                dataToSend += "&displayValue=" + element.text;
            });
        }
        var myRequest = {
            contentId: "requeteTable",
            dataToSend: dataToSend,
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            caller: this,
            loadingButton: $("button#add-champ-button"),
        };
        callAjaxRequest(myRequest, reloadPage, displaySimpleErrorMessage);
    }
}

function removeLineToRequete(order) {
    var ajaxUrl = $("#requete-url-ajax").val() + "/remove";
    var myRequest = {
        contentId: "requeteTable",
        dataToSend: "order=" + order,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "table_requete_overlay",
        caller: this,
    };
    callAjaxRequest(myRequest, reloadPage, displaySimpleErrorMessage);
}

function moveRequeteLine(order, direction) {
    var ajaxUrl = $("#requete-url-ajax").val() + "/move";
    var myRequest = {
        contentId: "requeteTable",
        dataToSend: "order=" + order + "&direction=" + direction,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "table_requete_overlay",
        caller: this,
    };
    callAjaxRequest(myRequest, replaceHtmlFunction, displaySimpleErrorMessage);
}

function reinitRequete() {
    var ajaxUrl = $("#requete-url-ajax").val() + "/reinit";
    var myRequest = {
        contentId: "requeteTable",
        dataToSend: null,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "reload-loader",
        caller: this,
        loadingButton: null,
    };
    callAjaxRequest(myRequest, hardReloadPage, displaySimpleErrorMessage);
}

function launchRequete() {
    var ajaxUrl = $("#requete-url-ajax").val() + "/resultats";
    var mydatas = "";
    $("#resultList")
        .find("select[data-isForm='true']")
        .each(function () {
            if (this.value !== undefined && this.name == "size") {
                mydatas = this.name + "=" + this.value;
            }
        });
    var myRequest = {
        contentId: "resultList",
        dataToSend: mydatas,
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        caller: this,
        loadingButton: $("button#launch-requete-button"),
    };
    callAjaxRequest(myRequest, replaceHtmlFunction, displaySimpleErrorMessage);
}

function selectCategorieRequete(categorie) {
    let $options = $("#select-champ").children("option");
    $options.each(function () {
        let option = $(this);
        option.toggle(!categorie || option.data("categorie") == categorie);
    });
}

function switchType(input) {
    // On enlève le style des erreurs
    const formOptinParent = $(input).parents(".form-optin__optins");
    formOptinParent.siblings(".form-input__footer").children(".form-input__description--error").remove();
    formOptinParent.removeClass("form-optin--error");

    var $type = $(input).val();
    var $divToReplace = $(input).parents(".form-optin").siblings(".migration-structure");
    var $idToReplace = $divToReplace.attr("id");
    var $index = $divToReplace.data("index");
    var ajaxUrl = $("#ajaxCallPath").val() + "/migrations/switchType";
    var myRequest = {
        contentId: $idToReplace,
        dataToSend: "migrationType=" + $type + "&index=" + $index,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: "migration-overlay-" + $index,
        caller: this,
    };
    callAjaxRequest(myRequest, replaceHtmlFunctionAndInitOrganigramme, displaySimpleErrorMessage);
}

var replaceHtmlFunctionAndInitOrganigramme = function replaceHtmlFunctionAndInitOrganigramme(containerID, result) {
    replaceHtmlFunction(containerID, result);
    initSelectAutocomplete();
    initAsyncSelect();
};

function addMigration() {
    var $migrations = $(".page-migration__element");
    var $index =
        Math.max.apply(
            null,
            $.map($migrations, function (el) {
                return parseInt($(el).data("index"));
            })
        ) + 1;
    var $idToReplace = $migrations.last().attr("id");
    var ajaxUrl = $("#ajaxCallPath").val() + "/migrations/add";
    var myRequest = {
        contentId: $idToReplace,
        dataToSend: "index=" + $index,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        loadingButton: $("#migration-add-button"),
        caller: this,
    };
    callAjaxRequest(myRequest, appendHtmlFunction, displaySimpleErrorMessage);
}

var appendHtmlFunction = function appendHtmlFunction(containerID, result) {
    $("#" + containerID).after(result);
};

function deleteMigration(index) {
    $("#migration-" + index).remove();
}

function launchMigration() {
    event.preventDefault();
    if (isValidForm($("#migration-form")) && validateOptinMigration()) {
        var details = [];
        $(".migration-fieldset").each(function () {
            var serializeObject = $(this).serializeArray();
            var objectToJson = "{";
            for (i = 0; i < serializeObject.length; i++) {
                var name = serializeObject[i]["name"];
                var value = serializeObject[i]["value"];
                if (
                    !name.startsWith("deleteOld") &&
                    !name.startsWith("migrationType") &&
                    !name.startsWith("migrerModelesFdr") &&
                    !name.startsWith("migrationWithDossierClos")
                ) {
                    objectToJson += '"';
                    objectToJson += name;
                    objectToJson += '" : "';
                    objectToJson += value;
                    objectToJson += '",';
                }
            }

            objectToJson += serializeMigrationOptins($(this), "deleteOld", false);
            objectToJson += serializeMigrationOptins($(this), "migrerModelesFdr", false);
            objectToJson += serializeMigrationOptins($(this), "migrationWithDossierClos", false);
            objectToJson += serializeMigrationOptins($(this), "migrationType", true);

            // ajout au tableau
            details.push(objectToJson);
        });
        var ajaxUrl = $("#ajaxCallPath").val() + "/migrations/launch";
        var myRequest = {
            contentId: null,
            dataToSend: { details: details },
            method: "POST",
            dataType: "html",
            ajaxUrl: ajaxUrl,
            isChangeURL: false,
            overlay: null,
            loadingButton: $("#migration-launch-button"),
            caller: this,
        };
        callAjaxRequest(myRequest, checkErrorOrReload, displaySimpleErrorMessage);
    }
}

function validateOptinMigration() {
    // Permet de valider les optins
    let isValid = true;
    $(".migration-fieldset").each(function () {
        const formOptinParent = $(this).find(".form-optin__optins");
        $(this).find(".form-input__description--error").remove();
        formOptinParent.removeClass("form-optin--error");
        if (!$(this).find("input[name^=migrationType]:checked").val()) {
            const errorDiv = initializeErrorDiv();
            formOptinParent.addClass("form-optin--error");
            errorDiv.text("Veuillez sélectionner une structure à migrer");
            formOptinParent.next(".form-input__footer").prepend(errorDiv);
            isValid = false;
        }
    });
    return isValid;
}

function refreshProgress() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/migrations/refresh";
    var myRequest = {
        contentId: "migration-list",
        dataToSend: null,
        method: "GET",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        caller: this,
    };
    callAjaxRequest(myRequest, replaceHtmlFunctionAndRefreshMigration, displaySimpleErrorMessage);
}

var replaceHtmlFunctionAndRefreshMigration = function replaceHtmlFunctionAndRefresh(containerID, result) {
    replaceHtmlFunction(containerID, result);
    initAsyncSelect();
    if ($(".migration-running").length > 0) {
        //On reprogramme le rafraichissement si la migration est en cours
        setTimeout(refreshProgress, 3000);
    } else {
        $("#migration-cancel-button").prop("disabled", false);
        $("#migration-reinit-button").prop("disabled", false);
    }
};

function reinitMigrations() {
    var ajaxUrl = $("#ajaxCallPath").val() + "/migrations/reinit";
    var myRequest = {
        contentId: null,
        dataToSend: null,
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
        caller: this,
        loadingButton: $("#migration-reinit-button"),
    };
    callAjaxRequest(myRequest, reloadPage, displaySimpleErrorMessage);
}

$(document).ready(function () {
    if ($(".migration-running").length > 0) {
        setTimeout(refreshProgress, 3000);
    }
});

function serializeMigrationOptins(migration, inputName, isLast) {
    let result = "";
    const optin = migration.find("input[name^=" + inputName + "]:checked");
    if (optin.length > 0) {
        result += '"' + inputName + '" : "';
        result += optin.val() + '"';
        result += isLast ? "}" : ", ";
    }
    return result;
}

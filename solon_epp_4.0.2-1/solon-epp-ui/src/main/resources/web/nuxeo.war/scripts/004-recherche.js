function doGlobalSearch() {
    event.preventDefault();
    var map = buildSearchMap();

    var myRequest = {
        contentId: "listeCommunications",
        dataToSend: { search: JSON.stringify([...map]) },
        method: "POST",
        dataType: "html",
        ajaxUrl: $("#ajaxCallPath").val() + "/recherche/resultats",
        isChangeURL: false,
        overlay: "reload-loader",
        caller: this,
    };

    callAjaxRequest(myRequest, replaceWithHtmlFunction, tabLoadError);
}

function tableChangeEventEpp($table) {
    var map = buildSearchMap();

    $table.find("input[data-isForm='true'], select[data-isForm='true']").each(function () {
        if (this.value !== undefined) {
            map.set(this.name, this.value);
        }
    });

    // ajout des colonnes de tri à la map
    $table.find("[data-field]").each(function () {
        if ($(this).data("value") !== undefined) {
            map.set($(this).data("field"), $(this).data("value"));
        }
    });

    var myRequest = {
        contentId: "listeCommunications",
        dataToSend: { search: JSON.stringify([...map]) },
        method: "POST",
        dataType: "html",
        url: $table.data("url"),
        ajaxUrl: $table.data("ajaxurl"),
        isChangeURL: false,
        overlay: $("#" + $table[0].id)
            .find(".overlay")
            .first()
            .attr("id"),
        caller: null,
    };

    callAjaxRequest(myRequest, replaceHtmlFunction);
}

function onEnterPageNumber(event, element) {
    if (event.key == "Enter") {
        goToPage(element, element.val());
    }
}

function previousPage(me) {
    var $myTable = $(me).closest(".tableForm");
    var $myPageInput = $myTable.find('[name="page"]');
    $myPageInput.val(parseInt($myPageInput.val()) - 1);
    tableChangeEvent($myTable, me);
}

function nextPage(me) {
    var $myTable = $(me).closest(".tableForm");
    var $myPageInput = $myTable.find('[name="page"]');
    $myPageInput.val(parseInt($myPageInput.val()) + 1);
    tableChangeEvent($myTable, me);
}

function goToPage(me, page) {
    var $myTable = $(me).closest(".tableForm");
    var $myPageInput = $myTable.find('[name="page"]');
    $myPageInput.val(page);
    tableChangeEvent($myTable, me);
}

function goToFirstPage($table) {
    $table.find('[name="page"]').each(function () {
        $(this).val("1");
    });
}
function onDocPerPageChange(me) {
    var $table = $(me).closest(".tableForm");
    var maxPage = $("#pagination-nav").attr("data-totalpage");
    var pageNumber = $table.find('input[name="page"]').val();
    var prevDocNumber = $("select#document-page").attr("data-value");
    var newDocNumber = $("select#document-page").val();

    var currentPagePercentage = pageNumber / maxPage;
    var estimatedTotalNumber = prevDocNumber * maxPage;
    var newMaxPage = Math.ceil(estimatedTotalNumber / newDocNumber);
    var newPageNumber = Math.ceil(newMaxPage * currentPagePercentage);

    $table.find('[name="page"]').each(function () {
        $(this).val(newPageNumber);
    });

    tableChangeEvent($table, me);
}

function doAddSortedColumn(btn) {
    const index = $("#modal-multiple-sort-columns-container > *").length;
    const ajaxUrl = $("#ajaxCallPath").val() + "/tri-multiple/colonne/ajouter";
    const anchorID = $(btn).attr("id");
    let sortableColumnsId = [];
    let sortableColumnsLabel = [];

    $("th.table-header__cell--with-filter-action > button").each(function () {
        sortableColumnsId.push($(this).data("field"));
        sortableColumnsLabel.push($(this).data("label"));
    });

    const myRequest = {
        contentId: anchorID,
        dataToSend: {
            sortableColumnsId: sortableColumnsId,
            sortableColumnsLabel: sortableColumnsLabel,
            index: index,
        },
        method: "POST",
        dataType: "html",
        ajaxUrl: ajaxUrl,
        isChangeURL: false,
        overlay: null,
    };
    callAjaxRequest(myRequest, insertColumn, displaySimpleErrorMessage);
}

function insertColumn(anchorID, htmlToAppend) {
    $("#" + anchorID).before(htmlToAppend);
}

function removeColumn(button) {
    button.closest("div[id^=column-]").remove();
    reindexColumns();
}

function doMultipleSort(id) {
    // On vérifie d'abord qu'il n'y a pas de doublons dans les valeurs sélectionnées
    const selectedValue = [];

    $("#" + id)
        .find("select[id^=column_select-]")
        .each(function (i, element) {
            selectedValue.push($(element).val());
        });

    if (new Set(selectedValue).size !== selectedValue.length) {
        const dangerAlert = [constructDangerAlert("Veuillez sélectionner des colonnes différentes")];
        constructAlertContainer(dangerAlert);
        return false;
    }

    let $tableForm = $("#" + id).closest(".tableForm");

    $tableForm.find("[data-field]").each(function () {
        $(this).removeData("order");
        $(this).removeAttr("data-order");
        $(this).removeData("value");
        $(this).removeAttr("data-value");
    });

    goToFirstPage($tableForm);
    $("div[id^=column-]").each(function (index) {
        const $column = $(this);
        const $field = $column.find("option:selected").val();
        const $sort = $column.find("input:radio:checked").val();
        let $tableData = $("#" + id)
            .closest(".tableForm")
            .find("[data-field=" + $field + "]");
        $tableData.data("value", $sort);
        $tableData.data("order", index + 1);
    });
    closeModal($("#" + id).get(0));

    tableChangeEvent($tableForm);
}

function reindexColumns() {
    // On parcourt chaque colonne et on remplace chaque occurrence de l'index
    $("div[id^=column-]").each(function (i) {
        const $column = $(this);
        const columnId = $column.attr("id");
        const indexToReplace = columnId.charAt(columnId.length - 1);
        const columnHTML = $column.prop("outerHTML");

        // Les valeurs du select et des radio-button ne sont pas préservées dans le HTML
        // On les sauvegarde pour les réappliquer après
        const selectedValue = $column.find("option:selected").val();
        const checkedSort = $column.find("input:radio:checked").val();

        const regex = new RegExp(indexToReplace, "g");
        const newIndex = i + 1;
        const updatedColumn = columnHTML.replace(regex, newIndex);

        replaceWithHtmlFunction(columnId, updatedColumn);

        // On réapplique les valeurs du select et des radio-button
        const $newColumn = $("#column-" + newIndex);
        $newColumn.find("select").val(selectedValue).change();
        $newColumn.find("[name^=optin-name-]").val([checkedSort]);
    });
}

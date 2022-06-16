//Put SWORD specific scripts for appli here

var requestWaiting = [];

var lastRequestLoad = null;

$(document).ready(function () {
    $(".overlay").hide();
    updateCurrentTab();
    $(".tree-navigation__list").closest("li").children("ul").addClass("tree-navigation__list--subtree");
    $("#modal-user-profile-dialog").removeClass("interstitial--large");
    $(".page-login__inner").find("#sidebar__content").remove();
    hideBackToCorbeilles();
});

function getAppliName() {
    return "solon-epp";
}

function loadOngletDossier(onglet, me) {
    var targetURL = "/dossier/" + $("#dossierId").val() + "/" + onglet;
    var myRequest = {
        contentId: $(me).attr("aria-controls"),
        dataToSend: "",
        method: "GET",
        dataType: "html",
        url:
            $("#ajaxCallPath")
                .val()
                .substring(0, $("#ajaxCallPath").val().length - 5) + targetURL,
        ajaxUrl: $("#ajaxCallPath").val() + targetURL,
        isChangeURL: true,
        overlay: null,
        caller: me,
    };

    callAjaxRequest(myRequest, loadOngletData, tabLoadError);
}

function updateCurrentTab() {
    const $this = $(".dossier-onglet.tabulation__item--active");
    if ($this) {
        var targetURL = "/dossier/" + $("#dossierId").val() + "/" + $this.attr("data-name");

        $this.removeAttr("onclick");

        $this.on("click", function () {
            pushURLFunction(
                encodeURI(
                    $("#ajaxCallPath")
                        .val()
                        .substring(0, $("#ajaxCallPath").val().length - 5) + targetURL
                )
            );
        });
    }
}

var goPreviousPage = function goPreviousPageFunction() {
    showReloadLoader();
    window.location.replace($("#urlPreviousPage").val());
};

var loadOngletData = function loadOnglet(containerID, result, caller, extraDatas) {
    replaceHtmlFunction(containerID, result);
};

var replaceHtmlFunction = function replaceHTMLinContainer(containerID, result) {
    $("#" + containerID).html(result);
};

window.tabLoadError = function (containerID, result, caller, extraDatas) {
    $("#" + containerID).html($("<p>Une erreur s'est produite</p>"));
};

function getIndexRequest(request) {
    var index = -1;
    $.each(requestWaiting, function (i, item) {
        if (item.id === request.id && item.data === request.data) {
            index = i;
            return;
        }
    });
    return index;
}

function hideBackToCorbeilles() {
    /*
     * Cacher le button retour espace corbeilles si on est dans l'espace corbeilles
     * */
    if (window.location.href.includes("corbeille")) {
        $(".breadcrumbs__shortcut").hide();
    }
}

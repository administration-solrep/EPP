function reinitGestionAcces() {
    $("#banniere_non").prop("checked", true);
    $("#restriction_non").prop("checked", true);
    $("#descriptionRestriction").val("");
    $('#format option[value="html"]').prop("selected", true).change();
    tinymce.get("mce").setContent("");
}

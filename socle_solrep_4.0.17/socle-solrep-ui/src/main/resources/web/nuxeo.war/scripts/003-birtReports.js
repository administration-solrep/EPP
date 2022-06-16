function initJsStats() {}

function generateBirtReport() {
    if (!isValidForm($("#birtReportForm"))) return;
    window.location.hash = "#statTitle";
    ariaLoader($("#overlay_statistiques"), true);
    $("#birtReportForm").submit();
}

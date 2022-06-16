jQuery(document).ready(
    function() {
      var viewId = document.URL.substring(document.URL.indexOf("@") + 1,
          document.URL.indexOf("?"));
      var sequenceId = jQuery(
          "form>input[type='hidden'][name='javax.faces.ViewState']").val();
      jQuery("div.menu").first().after(
          "<a href='site/sessionInspector/jsfStateManager/viewState/" + viewId
              + "/" + sequenceId
              + "' style='background-color:#00FF00;' target='_blank'>"
              + "Inspect '" + sequenceId + "'</a>");
    });
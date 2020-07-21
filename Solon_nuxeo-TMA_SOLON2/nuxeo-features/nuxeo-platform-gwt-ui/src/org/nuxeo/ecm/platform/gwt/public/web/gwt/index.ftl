
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Standards Mode", which gives better cross    -->
<!-- browser compatibility to the application.     -->
<!-- Omitting this line leaves most browsers in    -->
<!-- "Quirks mode", emulating the bugs in older    -->
<!-- browser versions.                             -->

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>Main</title>

    <!-- configure nuxeo app -->
    <script laguage="javascript">
      var isomorphicDir = "${skinPath}/resources/js/sc/";

      <#assign username = Context.principal.name>
      var nx = {
        version : "1.0.0",
        skinPath : "${skinPath}",
        modulePath : "${This.path}",
        userName : "${username}",
        anonymousUserName : "Guest",
        repositoryRoots : { "/" : "Repository", "/default-domain/workspaces" : "Workspaces" },

        settings : { animations: "false" },

        fire : function(eventId, data) {
          alert("No JS event handler registered.");
          return null;
        }
      };
    </script>

    <!--CSS for loading message at application Startup-->
    <style type="text/css">
        body { overflow:hidden }
        #loading {
            background: rgba(255, 255, 255, 0.5);
            position: absolute;
            z-index: 2147483647;
            width: 100%;
            height: 100%;
            left: 0;
            top: 0;

        }

        #loading a {
            color: #225588;
        }

        #loading .loadingIndicator {
            position: relative;
            left: 40%;
            top: 40%;
            width: 20%;
            height: auto;
            border: 1px solid #ccc;
            background: white;
            font: bold 13px tahoma, arial, helvetica;
            padding: 10px;
            margin: 0;
            color: #444;
        }

        #loadingMsg {
            font: normal 10px arial, tahoma, sans-serif;
            white-space: wrap;
        }
    </style>

  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body>
    <!--add loading indicator while the app is being loaded-->
    <div id="loadingWrapper">
      <div id="loading">
        <div class="loadingIndicator">
           <table cellpadding="2", cellspacing="0" border="0">
              <tr valign="middle"><td align="right">
           <img src="${skinPath}/resources/images/loading_app.gif" width="32" height="32" hspace="2"/>
              </td><td align="left">
           <span id="loadingMsg">Loading Application...</span>
              </td><tr>
        </div>
      </div>
    </div>


    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>

    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="${skinPath}/resources/org.nuxeo.ecm.platform.gwt.UI.nocache.js"></script>

    <!--script type="text/javascript">document.getElementById('loadingMsg').innerHTML = 'Loading Core API...';</script-->

  </body>
</html>

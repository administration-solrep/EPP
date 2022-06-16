<html>
<head>
  <title>
     <@block name="title">
     WebEngine
     </@block>
  </title>
  <meta http-equiv="Content-Type" content="text/html;charset=UTF-8"/>
 </head>

<body>
  <div id="wrap">
    <div id="header">
      <div class="webEngineRoot"><a href="${appPath}"><img src="${skinPath}/image/dots.png" width="16" height="16" alt=""/></a></div>
       <@block name="header">
       </@block>
    </div>

    <div id="main-wrapper">

      <div id="main">
        <div class="main-content">
          <div id="content"><@block name="content" /></div>
        </div>
      </div>
    </div>
    <div id="footer">
       <@block name="footer">
       <p>&copy; 2000-2008 <a href="http://www.nuxeo.com/en/">Nuxeo</a>.</p>
       </@block>
    </div>

  </div>

</body>
</html>

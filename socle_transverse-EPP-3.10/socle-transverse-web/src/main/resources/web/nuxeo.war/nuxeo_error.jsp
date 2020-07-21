<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java"%>
<%@ page
	import="org.nuxeo.ecm.platform.ui.web.auth.plugins.AnonymousAuthenticator"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%
String context = request.getContextPath();
%>
<html>
<fmt:setBundle basename="messages" var="messages" />
<head>
<title>Erreur</title>
<style type="text/css">
<!--
body {
	font: normal 11px "Lucida Grande", sans-serif;
	color: #343434;
}

.topBar {
	background: url('<%=context%>/img/header-reponses.png') no-repeat top
		center #bd4044;
	width: 100%;
	height: 110px;
	border: 0;
}

.topBar .title {
	color: #FFFFFF;
	font: normal 30px Calibri, Arial, "Lucida Grande", sans-serif;
}

.topBar .content {
	color: #FFFFFF;
	font-size: 12px;
	vertical-align: top;
}

H1 {
	color: #0080ff;
	font: bold 20px Verdana, sans-serif;
}

H2 {
	color: #505050;
	font: bold 12px Verdana, sans-serif;
	margin: 15px 0 15px 0;
	padding: 5px;
	background: #EBF5FF;
	border: 1px solid #3299ff;
}

a {
	font-family: Verdana, sans-serif;
	color: #595959;
	font-style: sans-serif;
	font-size: 10pt;
}

a:hover {
	color: #404040;
}

.links {
	font-family: Verdana, sans-serif;
	color: #7B7B7B;
	font-style: sans-serif;
	font-size: 10pt;
	margin: 40px 0 0 0;
}

.logo {
	margin: 0 70px 0 0;
}

.stacktrace {
	padding: 0 5px 0 20px;
	background: url(<%=context%>/icons/page_text.gif ) no-repeat scroll 0%;
	margin: 10px 0 0 0;
}

.back {
	padding: 0 5px 0 20px;
	background: url(<%=context%>/icons/back.png ) no-repeat scroll 0%;
	margin: 10px 0;
}

.change {
	padding: 0 5px 0 20px;
	background: url(<%=context%>/icons/user_go.png ) no-repeat scroll 0%;
}

#stackTrace {
	border: 1px solid #999999;
	padding: 3px;
	margin: 15px 0;
	width: 100%;
	height: 700px;
	overflow: auto;
}

#requestDump {
	border: 1px solid #999999;
	padding: 3px;
	margin: 15px 0;
	width: 100%;
	height: 700px;
	overflow: auto;
}
-->
</style>
<script language="javascript" type="text/javascript">
    function toggleError(id) {
      var style = document.getElementById(id).style;
      if ("block" == style.display) {
        style.display = "none";
      } else {
        style.display = "block";
      }
    }
  </script>
</head>
<body>
	<%
  String user_message = (String) request.getAttribute("user_message");
  String exception_message = (String) request.getAttribute("exception_message");
  String stackTrace = (String) request.getAttribute("stackTrace");
  Boolean securityError = (Boolean) request.getAttribute("securityError");
  String request_dump = (String) request.getAttribute("request_dump");

  String pageTitle="Erreur";
  if ((securityError!=null) && (securityError.booleanValue()==true))
  {
    pageTitle = "Permission refusée";
  }
  boolean isAnonymous = AnonymousAuthenticator.isAnonymousRequest(request);

%>

<body style="margin: 0;">

	<table cellspacing="0" cellpadding="0" border="0" width="100%">
		<tbody>
			<tr class="topBar">
				<td colspan="2">
					<table width="300px">
						<tr>
							<td rowspan="2" width="75px"><img
								src="<%=context%>/img/logopm.png" /></td>
						</tr>
					</table>

				</td>
			</tr>
			<tr>
				<td style="padding: 30px; vertical-align: top;">

					<h1><%=pageTitle%></h1> <% if (!isAnonymous) { %>
					<h2><%=user_message%></h2>


					<div class="links">
						<div class="back">
							<a href="<%=context %>/">retour</a>
						</div>
						<div class="stacktrace">
							<a href="#"
								onclick="javascript:toggleError('stackTrace'); return false;">
								erreur </a>
						</div>
						<div id="stackTrace" style="display: none;">
							<h2><%=exception_message %>
							</h2>
							<inputTextarea rows="20" style="width: 100%;" readonly="true">
							<pre>
            <%=stackTrace%>
            </pre> </inputTextarea>
						</div>
						<div class="stacktrace">
							<a href="#"
								onclick="javascript:toggleError('requestDump'); return false;">
								contexte </a>
						</div>
						<div id="requestDump" style="display: none;">
							<h2>Contexte</h2>
							<inputTextarea rows="20" cols="100" readonly="true">
							<pre>
            <%=request_dump%>
            </pre> </inputTextarea>
						</div>
					</div>
					</div> <%} else { %>
					<h2>You must be authenticated to perform this operation</h2>
					<div class="change">
						<a href="<%=context%>/logout">Login</a>
					</div> <%} %>

				</td>
			</tr>
		</tbody>
	</table>
</body>
</html>

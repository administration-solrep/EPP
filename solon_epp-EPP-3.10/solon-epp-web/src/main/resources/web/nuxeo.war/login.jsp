<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- Nuxeo Enterprise Platform, svn $Revision: 22925 $ -->
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page language="java"%>
<%@ page import="org.nuxeo.runtime.api.Framework"%>
<%@ page import="java.util.Locale"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ page import="fr.dila.st.api.service.EtatApplicationService"%>
<%@ page import="fr.dila.st.api.administration.EtatApplication"%>
<%@ page import="fr.dila.st.core.service.STServiceLocator"%>
<%@ page import="java.util.Map"%>
<%@ page import="fr.dila.st.api.service.ConfigService"%>
<%@ page import="fr.dila.st.api.constant.STConfigConstants"%>
<%@ page import="fr.dila.st.api.service.STParametreService"%>
<%
String productName = Framework.getProperty("org.nuxeo.ecm.product.name");
String productVersion = Framework.getProperty("org.nuxeo.ecm.product.version");
String productTag = Framework.getProperty("reponses.product.tag");
String context = request.getContextPath();
Locale locale = request.getLocale();
String language = locale == null ? "en" : locale.getLanguage();
String country = locale == null ? "US" : locale.getCountry();

String pageRenseignement;
try {
  STParametreService paramService = STServiceLocator.getSTParametreService();
  pageRenseignement = paramService.getUrlRenseignement();
} catch (Exception e) {
    pageRenseignement = "#";
}

Boolean affichageBanniere = false;
String messageBanniere = "";
String restrictionDescription = "";
String identPlateformeCouleur = "000000";
String identPlateformeLibelle = "";
String identPlateformeCouleurBg = "ffffff";
try {
  EtatApplicationService etatApp = STServiceLocator.getEtatApplicationService();
  Map<String, Object> result = etatApp.getRestrictionAccesUnrestricted();
  affichageBanniere = (Boolean)result.get(EtatApplicationService.AFFICHAGE_RADIO);
  messageBanniere = (String)result.get(EtatApplicationService.MESSAGE);
} catch (Exception e) {
  restrictionDescription="Erreur d'accès à l'état de l'application";
}
try {
  ConfigService configService = STServiceLocator.getConfigService();
  identPlateformeCouleur = configService.getValue(STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_COULEUR);
  identPlateformeLibelle = configService.getValue(STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_LIBELLE);
  identPlateformeCouleurBg = configService.getValue(STConfigConstants.SOLON_IDENTIFICATION_PLATEFORME_COULEURBG);
} catch (Exception e) {
  restrictionDescription="Erreur d'accès à la configuration de l'application";
} 
%>
<html>
<fmt:setLocale value="fr" scope="session" />
<fmt:setBundle basename="messages" var="messages" />

<head>
<title>SOLON EPP</title>
<link rel="icon" type="image/png" href="<%=context%>/icons/favicon.png" />
<style type="text/css">
<!--
body {
	font: normal 11px "Lucida Grande", sans-serif;
	color: #343434;
	background-color: #<%=identPlateformeCouleurBg%>
}

.topBar {
	background: url('<%=context%>/img/header-reponses.png') no-repeat scroll
		center center #bd4044;
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

table.loginForm {
	border-spacing: 3px;
	padding: 3px;
}

.tdLogin {
	background-repeat: no-repeat;
	background-attachment: scroll;
	background-position: center center;
	width: 100%;
}

.login {
	background: #fff
		url("<%=context%>/img/theme_solonepp/marianne_fond.gif") bottom right
		no-repeat;
	opacity: 0.95;
	filter: alpha(opacity = 95);
	border: 1px solid black;
	padding: 20px 0px 0px 0px;
	width: 350px;
}

.login_label {
	font: bold 12px "Lucida Grande", sans-serif;
	text-align: right;
	white-space: nowrap;
	color: #005595;
	margin: 0 4px 0 0;
}

.login_input {
	border: 1px inset #454545;
	background: white;
	padding: 3px;
	color: #454545;
	margin: 0 10px 5px 0px;
	font: normal 10px "Lucida Grande", sans-serif;
}

/* this class is a repeat because defined in nxthemes-setup.xml but
nxthemes css is not used in login.jsp */
.login_button {
	cursor: pointer;
	color: #454545;
	font-size: 10px;
	background: #CECFD1 url(<%=context%>/img/theme_galaxy/buttons.png)
		repeat-x scroll left top;
	border: 1px solid #BFC5CB;
	padding: 2px 5px 2px 5px;
	margin: 5px 10px 10px 0;
}

.login_button:hover {
	border: 1px solid #92999E;
	color: #000000;
}

.news_container {
	position: absolute;
	top: 36px;
	right: 360px;
	text-align: left;
}

.block_container {
	border: 1px solid #aaaaaa;
	overflow: auto;
	height: 650px;
	width: 250px;
	opacity: 0.90;
	filter: alpha(opacity = 90);
	padding: 0;
	overflow: auto;
	overflow-x: hidden;
}

.display_feuilles_styles {
	font: bold 11px "Lucida Grande", sans-serif;
}

.errorMessage {
	color: #000;
	font: bold 10px "Lucida Grande", sans-serif;
	border: 1px solid #666;
	background: url(<%=context%>/icons/warning.gif) 2px 3px no-repeat
		#FFCC33;
	margin-bottom: 12px;
	display: block;
	padding: 5px 5px 5px 23px;
	text-align: center;
}

.footer {
	width: 100%;
	text-align: center;
	font-size: 12px;
}

.version {
	color: #fff;
	background: #fff;
}

#solon_identification_plateforme_div{color:#<%=identPlateformeCouleur%>;font-size:20px;font-weight:bold;margin-top:30px;}
-->
</style>

</head>

<body style="margin: 0; text-align: center;">

	<table cellspacing="0" cellpadding="0" border="0" width="100%"
		height="100%">
		<tbody>
			<tr class="topBar">
				<td colspan="2">
					<table width="300px">
						<tr>
							<td rowspan="2" width="75px"><img
								src="<%=context%>/img/logopm.png" /></td>
							<td class="title">SOLON EPP</td>
						</tr>
						<tr>
							<td class="content">Bienvenue dans SOLON EPP</td>
						</tr>


					</table>

				</td>
			</tr>
			<tr>
				<td class="tdLogin" align="center">
					<form method="post" action="nxstartup.faces">
						<!-- To prevent caching -->
						<%
              response.setHeader("Cache-Control", "no-cache"); // HTTP 1.1
              response.setHeader("Pragma", "no-cache"); // HTTP 1.0
              response.setDateHeader("Expires", -1); // Prevents caching at the proxy server
          %>
						<!-- ;jsessionid=<%=request.getSession().getId()%> -->
						<!-- ImageReady Slices (login_cutted.psd) -->
						<div class="login">
							<table>
								<tr>
									<td class="login_label"><label for="username"> <fmt:message
												bundle="${messages}" key="label.login.username" />
									</label></td>
									<td><input class="login_input" type="text"
										name="user_name" id="username" size="22"></td>
								</tr>
								<tr>
									<td class="login_label"><label for="password"> <fmt:message
												bundle="${messages}" key="label.login.password" />
									</label></td>
									<td><input class="login_input" type="password"
										name="user_password" id="password" size="22"></td>
								</tr>
								<tr>
									<td></td>
									<td>
										<% // label.login.logIn %> <input type="hidden" name="language"
										id="language" value="fr"> <input id="requestedUrl"
										type="hidden" value="" name="requestedUrl"> <input
										type="hidden" name="form_submitted_marker"
										id="form_submitted_marker"> <input
										class="login_button" type="submit" name="Submit"
										value="<fmt:message bundle="${messages}" key="label.login.logIn" />">
									</td>
								</tr>
								<tr>
									<td></td>
									<td><c:if test="${param.loginFailed}">
											<div class="errorMessage">
												<fmt:message bundle="${messages}"
													key="label.login.invalidUsernameOrPassword" />
											</div>
										</c:if>
										<!--  FEV 550 Message tentative de bruteforce -->
										<c:if test="${param.loginWait}">
											<div class="errorMessage">
												<fmt:message bundle="${messages}" key="message.login.bloque" /> ${param.loginMessage}
											</div>
										</c:if>

										 <c:if test="${param.loginMissing}">
											<div class="errorMessage">
												<fmt:message bundle="${messages}"
													key="label.login.missingUsername" />
											</div>
										</c:if> <c:if test="${param.securityError}">
											<div class="errorMessage">
												<fmt:message bundle="${messages}"
													key="label.login.securityError" />
											</div>
										</c:if></td>
								</tr>

							</table>

						</div>
						
						<!--  debut #solon_identification_plateforme_div -->
			            <div id="solon_identification_plateforme_div">
			            	<%=identPlateformeLibelle%>
			            </div>
			            <!-- fin #solon_identification_plateforme_div -->

				<% if (affichageBanniere) { %>
<div align="justify"
					style="width: 350px; border: solid 3px #635a4d; padding-top: 5px; padding-left: 5px; padding-right: 5px; background-color: white; margin: 10 auto">
					<span> <%=messageBanniere%>
					</span>
				</div><% } %>
					</form>


				</td>

				

			</tr>
<tr>
<span style="margin: auto"/>
</tr>

			<tr>
				<td class="footer" colspan="2"><a href="<%=pageRenseignement%>">Pour tout
						renseignement sur les conditions d'accès à l'outil</a><br /> <span>
						<%=productTag%>
				</span></td>
			</tr>

		</tbody>
	</table>

	<!--   Current User = <%=request.getRemoteUser()%> -->

</body>
</html>

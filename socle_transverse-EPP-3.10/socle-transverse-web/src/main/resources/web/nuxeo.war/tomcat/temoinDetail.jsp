<%@ include file="import.jsp"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Etat du serveur</title>
</head>
<style>
body {
	font: 12px verdana, sans-serif;
	width: 400px;
}

h1 {
	font-size: 18px;
	border-bottom: 1px solid #ccc;
	margin-top: 30px;
	padding-bottom: 8px;
}

h2 {
	font-size: 14px;
	border-bottom: 1px solid #ccc;
	margin-top: 40px;
	padding-bottom: 6px;
}

table {
	width: 300px;
}

table,td {
	border: 1px solid #888;
}

td {
	padding: 8px;
	text-align: right;
}

.status_OK {
	background: #adff71;
}

.status_KO {
	background: #ff7358;
}
</style>
<body>
	<%
  // On utilise la classe ServerStatus qui permet d'avoir un aperçu de l'état du serveur
  ServerStatus serverStatus = null;
  serverStatus = new ServerStatus();
  //Récupération de la date
  SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yy à H:mm:ss");
  Calendar cal = Calendar.getInstance();
  %>

	<h1>Etat du serveur</h1>

	<table>
		<%
    for (ServerTest test : serverStatus.getTests()) {
        boolean testPass = test.runTest(serverStatus.getSession());
        %>
		<tr class="status_<%=testPass ? "OK" : "KO"%>">
			<td><%=test.getName()%></td>
			<td><%=testPass ? "OK" : "KO"%></td>
			<td><%=test.getElapsedTime() + " ms"%></td>
		</tr>
		<%
    }
    %>
	</table>

	<h1>Informations cluster</h1>

	<h2>Identifiant de l'instance courante</h2>

	<p>
		<b>NX_NODEID :</b>
		<%=serverStatus.getNxNodeId()%></p>

	<h2>Invalidations par instance</h2>

	<table>
		<tr>
			<th>NX_NODEID</th>
			<th>Nombre</th>
			<th>Création</th>
		</tr>
		<%
    List<String[]> clusters = serverStatus.getClusterInvals();
    for (String[] cluster : clusters) {
      %>
		<tr>
			<td><%=cluster[0]%></td>
			<td><%=cluster[1]%></td>
			<td><%=cluster[2]%></td>
		</tr>
		<%
    }
    %>
	</table>

	<h2>
		Page générée le
		<%=formater.format(cal.getTime()) %></h2>

</body>
</html>
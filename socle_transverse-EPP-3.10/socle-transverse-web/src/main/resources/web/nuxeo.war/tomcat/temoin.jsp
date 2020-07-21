<%@ include file="import.jsp"%>
<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>État du serveur</title>
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

	<h1>État du serveur</h1>

	<table>
		<%
          boolean testPass = serverStatus.getServerStatus();
        %>
		<tr class="status_<%=testPass ? "OK" : "KO"%>">
			<td>État général</td>
			<td><%=testPass ? "OK" : "KO"%></td>
		</tr>
	</table>

	<h2>
		Page générée le
		<%=formater.format(cal.getTime()) %></h2>

</body>
</html>

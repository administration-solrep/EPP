package fr.dila.st.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public abstract class AbstractGenerateDocTests {

	private static final int					NOMBRE_COLONNES	= 3;

	protected static Map<String, List<String>>	categoriesAndTestsMap;

	protected static void updateMapWithTestClass(Class testClass) {

		for (Method method : testClass.getDeclaredMethods()) {
			if (method.isAnnotationPresent(TestDocumentation.class)) {
				Annotation annotation = method.getAnnotation(TestDocumentation.class);
				TestDocumentation testDocumentation = (TestDocumentation) annotation;

				for (String category : testDocumentation.categories()) {
					List<String> testsList = categoriesAndTestsMap.get(category);
					if (testsList == null) {
						testsList = new ArrayList<String>();
					}
					// En récupérant juste le nom de méthode, on s'expose à avoir des noms identiques provenant de
					// classes différentes.
					// La méthode la plus esthétique pour palier ce problème est d'afficher le chemin complet
					// (package+className+methodName)
					// mais provoque un affichage potentiellement long pour tout alors que ce n'est pas toujours utile.
					// Autre solution : afficher simplement le className+methodName
					testsList.add(testClass.getSimpleName() + "." + method.getName());
					categoriesAndTestsMap.put(category, testsList);
				}
			}
		}
	}

	protected static StringBuilder generateHtml(String application) {
		StringBuilder html = initHtml();
		appendIntroToHtml(html, application);
		appendCategoriesToHtml(html);
		appendFooter(html);
		closeHtml(html);

		return html;
	}

	protected static StringBuilder initHtml() {
		StringBuilder html = new StringBuilder();

		html.append("<html><meta charset=\"UTF-8\">").append("<head>");
		appendCSS(html);
		html.append("<title>Références des tests et catégories</title></head>").append("<body>");

		return html;
	}

	protected static void appendCSS(StringBuilder html) {
		html.append("<style>").append("th {background-color: #4CAF50;color: white;}")
				.append("tr:nth-child(even){background-color: #f2f2f2}").append("h1 {text-align: center;padding: 15}")
				.append("table {width: 100%}").append("td {word-wrap: break-word;}")
				.append("footer{text-align: right;font-style: italic;font-size: 0.7em;}").append("</style>");
	}

	protected static void appendIntroToHtml(StringBuilder html, String application) {
		html.append("<h1>Application ").append(application).append("</h1>");
	}

	protected static void appendCategoriesToHtml(StringBuilder html) {
		html.append("<table>").append("<th>Categories</th><th>Tests</th>").append("<th>Categories</th><th>Tests</th>")
				.append("<th>Categories</th><th>Tests</th>");
		int cpt = 0;
		for (Entry<String, List<String>> entry : categoriesAndTestsMap.entrySet()) {
			if (cpt % NOMBRE_COLONNES == 0) {
				html.append("<tr>");
			}
			html.append("<td>").append(entry.getKey()).append("</td><td><ul>");
			for (String testName : entry.getValue()) {
				html.append("<li>").append(testName).append("</li>");
			}
			html.append("</ul></td>");
			cpt++;
			if (cpt % NOMBRE_COLONNES == 0 || cpt == categoriesAndTestsMap.entrySet().size()) {
				html.append("</tr>");
			}
		}
		html.append("</table>");
	}

	protected static void appendFooter(StringBuilder html) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH);
		String dateGeneration = format.format(Calendar.getInstance().getTime());
		html.append("<footer>Date de génération : ").append(dateGeneration).append("</footer>");
	}

	protected static void closeHtml(StringBuilder html) {
		html.append("</body></html>");
	}

}

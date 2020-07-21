package fr.dila.st.core.requeteur;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import fr.dila.st.api.requeteur.RequeteurContributionBuilder;
import fr.dila.st.api.requeteur.RequeteurWidgetDescription;
import fr.dila.st.core.util.StreamUtil;

/**
 * Une implémentation par défaut d'un constructeur de contribution. Le nom d'un widget utilisé dans la contribution est
 * le nom de la catégorie concaténé au nom de widget. Des widgets avec le même nom et des catégories différentes sont
 * considérés comme des widgets différents
 * 
 * @author jgomez
 * 
 */
public class RequeteurContributionBuilderImpl implements RequeteurContributionBuilder {

	private static final Log	LOGGER			= LogFactory.getLog(RequeteurContributionBuilderImpl.class);

	private String				contribName;
	private String				layoutName;
	private String				componentName;
	private String				fileName;
	private Boolean				showCategories	= true;

	public RequeteurContributionBuilderImpl() {
		super();
	}

	public RequeteurContributionBuilderImpl(String contribName, String componentName, String layoutName,
			Boolean showCategories) {
		this(contribName, componentName, layoutName);
		this.showCategories = showCategories;
	}

	public RequeteurContributionBuilderImpl(String contribName, String componentName, String layoutName) {
		this.contribName = contribName;
		this.layoutName = layoutName;
		this.componentName = componentName;
		this.fileName = "incremental_smart_query_selection_layout_template";
	}

	public RequeteurContributionBuilderImpl(String contribName, String componentName, String layoutName, String fileName) {
		this.contribName = contribName;
		this.layoutName = layoutName;
		this.fileName = fileName;
	}

	/**
	 * Génère la contribution du requêteur, comprenant le layout et les widgets
	 * 
	 * @param widgetDescriptions
	 * @throws Exception
	 */
	@Override
	public URL createRequeteurContribution(Collection<RequeteurWidgetDescription> widgetDescriptions) throws Exception {

		File file = File.createTempFile(this.contribName, "xml");
		file.deleteOnExit();
		FileWriter fstream = new FileWriter(file);
		BufferedWriter out = new BufferedWriter(fstream);

		startComponent(out);
		createLayoutContribution(widgetDescriptions, out);
		createWidgetContribution(widgetDescriptions, out);
		endComponent(out);
		out.close();
		return file.toURI().toURL();

	}

	protected void endComponent(BufferedWriter out) throws IOException {
		out.write("</component>\n");
	}

	protected void startComponent(BufferedWriter out) throws IOException {
		out.write("<?xml version=\"1.0\"?>\n");
		out.write("<!-- Généré automatiquement : NE PAS MODIFIER -->\n");
		out.write("<component name=\"" + this.componentName + "\">\n");
		out.write("<require>org.nuxeo.ecm.platform.smart.folder.layouts</require>\n");
		out.write("<require>org.nuxeo.ecm.platform.smart.query.layouts</require>\n");
		out.write("<require>fr.dila.solonepg.core.vocabulaire</require>\n");
	}

	/**
	 * Crée la contribution de widget
	 * 
	 * @param widgetDescriptions
	 *            Les descriptions de widgets
	 * @param out
	 *            Le flux de sortie
	 * @throws Exception
	 */
	protected void createWidgetContribution(Collection<RequeteurWidgetDescription> widgetDescriptions,
			BufferedWriter out) throws Exception {
		String contribution = "<extension target=\"org.nuxeo.ecm.platform.forms.layout.WebLayoutManager\" point=\"widgets\">";
		out.write(contribution);
		generateWidgets(widgetDescriptions, out);
		String endContribution = "</extension>";
		out.write(endContribution);
	}

	/**
	 * Crée la contribution layout.
	 * 
	 * @param widgetDescriptions
	 *            Les descriptions de widget
	 * @param out
	 *            Le flux sur lequel sera écrit la contribution layout
	 * @throws IOException
	 */
	private void createLayoutContribution(Collection<RequeteurWidgetDescription> widgetDescriptions, BufferedWriter out)
			throws IOException {
		out.write("<extension target=\"org.nuxeo.ecm.platform.forms.layout.WebLayoutManager\" point=\"layouts\">\n");
		out.write("<layout name=\"" + this.layoutName + "\">\n");
		out.write("<templates>\n");
		out.write("<template mode=\"any\">\n");
		out.write("/layouts/" + this.fileName + ".xhtml\n");
		out.write("</template>\n");
		out.write("</templates>");
		out.write("<properties mode=\"any\">\n");
		out.write("<property name=\"hideNotOperator\">true</property>\n");
		out.write("<property name=\"showCategories\">" + this.showCategories + "</property>\n");
		out.write("<property name=\"requeteurContributionId\">" + this.componentName + "</property>\n");
		out.write("</properties>\n");
		out.write("<rows>\n");
		for (RequeteurWidgetDescription description : widgetDescriptions) {
			out.write("\t<row name=\"" + getUsedWidgetName(description) + "\">\n");
			out.write("\t\t<widget>" + getUsedWidgetName(description) + "</widget>\n");
			out.write("\t</row>\n");
		}
		out.write("</rows>\n");
		out.write("</layout>\n");
		out.write("</extension>");
	}

	/**
	 * Génère les widgets par rapport à un template défini par le type du widget.
	 * 
	 * @param widgetsDescriptions
	 *            Les widgets que l'on désire afficher sous forme xml
	 * @param out
	 *            Le flux sur lequel sont écrits les widgets
	 * @throws IOException
	 */
	protected void generateWidgets(Collection<RequeteurWidgetDescription> widgetsDescriptions, BufferedWriter out)
			throws IOException {
		for (RequeteurWidgetDescription widgetDescription : widgetsDescriptions) {
			URL url = Thread.currentThread().getContextClassLoader()
					.getResource("requeteur/requeteur_template_" + widgetDescription.getType() + ".xml");
			if (url == null) {
				LOGGER.warn(String.format("Widget %s de type %s n'a pas de template associé, enlevé !",
						widgetDescription.getWidgetName(), widgetDescription.getType()));
				LOGGER.warn(String.format("<excludedField name=\"%s\"/>", widgetDescription.getName()));
				continue;
			}
			InputStream content = (InputStream) url.getContent();
			String xmlOutput = StreamUtil.inputStreamAsString(content);

			xmlOutput = xmlOutput.replaceAll("NAME", getUsedWidgetName(widgetDescription));
			xmlOutput = xmlOutput.replaceAll("SEARCH_FIELD", widgetDescription.getName());
			xmlOutput = xmlOutput.replaceAll("LABEL", widgetDescription.getLabel());
			xmlOutput = xmlOutput.replaceAll("CATEGORY", widgetDescription.getCategory());
			xmlOutput = xmlOutput.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", "");
			if (widgetDescription.getExtraProperties() != null) {
				for (String propertyName : widgetDescription.getExtraProperties().keySet()) {
					xmlOutput = xmlOutput.replace(propertyName, widgetDescription.getExtraProperties()
							.get(propertyName));
				}
			}
			xmlOutput = xmlOutput.replaceAll("<root>", "");
			xmlOutput = xmlOutput.replaceAll("</root>", "");
			xmlOutput = xmlOutput.replaceAll("(?m)^[ \t]*\r?\n", "");
			out.write(xmlOutput + "\n");
		}
	}

	/**
	 * Le nom de widget utilisé dans la contribution
	 * 
	 * @param description
	 * @return
	 */
	public String getUsedWidgetName(RequeteurWidgetDescription description) {
		return description.getWidgetNameWithCategory();
	}

	protected void debug(File file, PrintStream out) throws FileNotFoundException, IOException {
		// debug
		FileReader debugStream = null;
		BufferedReader out2 = null;
		try {
			debugStream = new FileReader(file);
			out2 = new BufferedReader(debugStream);
			String currentLine = "";
			while ((currentLine = out2.readLine()) != null) {
				out.println(currentLine);
			}
			out.close();
		} finally {
			if (out2 != null) {
				out2.close();
			}
			if (debugStream != null) {
				debugStream.close();
			}
		}
	}

	public String getContribName() {
		return contribName;
	}

	public void setContribName(String contribName) {
		this.contribName = contribName;
	}

	public String getLayoutName() {
		return layoutName;
	}

	public void setLayoutName(String layoutName) {
		this.layoutName = layoutName;
	}

	public String getComponentName() {
		return componentName;
	}

	public void setComponentName(String componentName) {
		this.componentName = componentName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Boolean getShowCategories() {
		return showCategories;
	}

	public void setShowCategories(Boolean showCategories) {
		this.showCategories = showCategories;
	}

}

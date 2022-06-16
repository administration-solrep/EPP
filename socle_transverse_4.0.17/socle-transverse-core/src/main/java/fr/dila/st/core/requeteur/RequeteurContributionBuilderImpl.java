package fr.dila.st.core.requeteur;

import fr.dila.st.api.requeteur.RequeteurContributionBuilder;
import fr.dila.st.api.requeteur.RequeteurWidgetDescription;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Une implémentation par défaut d'un constructeur de contribution. Le nom d'un widget utilisé dans la contribution est
 * le nom de la catégorie concaténé au nom de widget. Des widgets avec le même nom et des catégories différentes sont
 * considérés comme des widgets différents
 *
 * @author jgomez
 *
 */
public class RequeteurContributionBuilderImpl implements RequeteurContributionBuilder {
    private static final Log LOGGER = LogFactory.getLog(RequeteurContributionBuilderImpl.class);

    private String contribName;
    private String layoutName;
    private String componentName;
    private String fileName;
    private Boolean showCategories = true;

    public RequeteurContributionBuilderImpl() {
        super();
    }

    public RequeteurContributionBuilderImpl(
        String contribName,
        String componentName,
        String layoutName,
        Boolean showCategories
    ) {
        this(contribName, componentName, layoutName);
        this.showCategories = showCategories;
    }

    public RequeteurContributionBuilderImpl(String contribName, String componentName, String layoutName) {
        this.contribName = contribName;
        this.layoutName = layoutName;
        this.componentName = componentName;
        this.fileName = "incremental_smart_query_selection_layout_template";
    }

    public RequeteurContributionBuilderImpl(
        String contribName,
        String componentName,
        String layoutName,
        String fileName
    ) {
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
    protected void createWidgetContribution(
        Collection<RequeteurWidgetDescription> widgetDescriptions,
        BufferedWriter out
    )
        throws Exception {
        String contribution =
            "<extension target=\"org.nuxeo.ecm.platform.forms.layout.WebLayoutManager\" point=\"widgets\">";
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
    private void createLayoutContribution(
        Collection<RequeteurWidgetDescription> widgetDescriptions,
        BufferedWriter out
    )
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
        throws IOException {}

    /**
     * Le nom de widget utilisé dans la contribution
     *
     * @param description
     * @return
     */
    public String getUsedWidgetName(RequeteurWidgetDescription description) {
        return description.getWidgetNameWithCategory();
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

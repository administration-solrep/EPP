package fr.dila.ss.api.birt;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Un rapport Birt.
 *
 * @author tlombard
 */
@XmlRootElement(name = "report")
@XmlAccessorType(XmlAccessType.FIELD)
public class BirtReport {
    /** Id du rapport */
    @XmlAttribute(name = "id")
    private String id;

    /** Titre affiché dans l'IHM. */
    @XmlAttribute(name = "title")
    private String title;

    /** Chemin du fichier dans solon2ng-birt-models-{epg,rep} */
    @XmlAttribute(name = "file")
    private String file;

    /** Propriétés associées au rapport */
    @XmlElement(name = "property", type = ReportProperty.class)
    @XmlElementWrapper(name = "properties")
    private List<ReportProperty> properties;

    @XmlTransient
    private Map<String, ReportProperty> propertiesMap;

    private void initProperties() {
        if (properties == null) {
            properties = new ArrayList<>();
        }
        propertiesMap = new LinkedHashMap<>();
        for (ReportProperty p : properties) {
            propertiesMap.put(p.getName(), p);
        }
    }

    private BirtReport() {
        // Default constructor
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Map<String, ReportProperty> getProperties() {
        if (propertiesMap == null) {
            initProperties();
        }
        return propertiesMap;
    }

    public void setProperties(Map<String, ReportProperty> properties) {
        if (propertiesMap == null) {
            initProperties();
        }
        this.propertiesMap = properties;
    }
}

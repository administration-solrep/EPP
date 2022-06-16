package fr.dila.ss.api.birt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Liste des rapports Birt connus dans l'application via le fichier de
 * configuration associé.
 *
 * @author tlombard
 *
 */
@XmlRootElement(name = "reports")
@XmlAccessorType(XmlAccessType.FIELD)
public class BirtReports {
    @XmlTransient
    private Map<String, BirtReport> birtReportMap;

    @XmlElement(name = "report")
    private List<BirtReport> reports;

    private BirtReports() {
        // Default constructor
    }

    public Map<String, BirtReport> getBirtReportMap() {
        if (birtReportMap == null) {
            init();
        }
        return birtReportMap;
    }

    public void setBirtReportMap(Map<String, BirtReport> birtReportMap) {
        if (birtReportMap == null) {
            init();
        }
        this.birtReportMap = birtReportMap;
    }

    private void init() {
        if (reports == null) {
            reports = new ArrayList<>();
        }
        birtReportMap = new HashMap<>();
        for (BirtReport p : reports) {
            birtReportMap.put(p.getId(), p);
        }
    }
}

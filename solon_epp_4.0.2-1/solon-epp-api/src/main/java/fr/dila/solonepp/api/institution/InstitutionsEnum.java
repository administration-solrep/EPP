package fr.dila.solonepp.api.institution;

import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PropertyException;

public enum InstitutionsEnum {
    ASSEMBLEE_NATIONALE("Assemblée Nationale", "forAssNat"),
    SENAT("Sénat", "forSenat"),
    GOUVERNEMENT("Gouvernement", ""),
    DILA("DILA", ""),
    CMP("Commissions mixtes paritaires", "forCMP"),
    OFFICES_DELEGATIONS("Offices et délégations", "forOffDel"),
    GRP_AN_SENAT("Groupes de travail Assemblée nationale - Sénat", "forGroupeTravail"),
    CONGRES_PARLEMENT("Congrès", "forCongres");

    private String label;
    private String columnRubrique;

    private InstitutionsEnum(String label, String columnRubrique) {
        this.label = label;
        this.columnRubrique = columnRubrique;
    }

    public String getLabel() {
        return label;
    }

    public String getColumnRubrique() {
        return columnRubrique;
    }

    public static boolean isInstitutionAlwaysAccessible(String name) {
        if (StringUtils.isBlank(name)) {
            return false;
        }
        InstitutionsEnum institution = valueOf(name);
        switch (institution) {
            case CMP:
            case OFFICES_DELEGATIONS:
            case GRP_AN_SENAT:
            case CONGRES_PARLEMENT:
                return true;
            case ASSEMBLEE_NATIONALE:
            case SENAT:
            case GOUVERNEMENT:
            case DILA:
            default:
                return false;
        }
    }

    public static String getNorDirectionFromInstitution(String institution, DocumentModel parameterDoc)
        throws PropertyException {
        String norDirection;
        switch (InstitutionsEnum.valueOf(institution)) {
            case ASSEMBLEE_NATIONALE:
                norDirection = (String) parameterDoc.getPropertyValue("parws:norDirectionAN");
                break;
            case SENAT:
                norDirection = (String) parameterDoc.getPropertyValue("parws:norDirectionSenat");
                break;
            case CMP:
            case CONGRES_PARLEMENT:
            case GRP_AN_SENAT:
            case OFFICES_DELEGATIONS:
                norDirection = (String) parameterDoc.getPropertyValue("parws:norDirectionAutres");
                break;
            case DILA:
            case GOUVERNEMENT:
            default:
                // Nothing to do in that case ; shouldn't happen
                return "";
        }
        return norDirection;
    }

    /**
     * Returns institutionKey if no institution found
     *
     * @param institutionKey
     * @return
     */
    public static String getLabelFromInstitutionKey(String institutionKey) {
        return Stream
            .of(values())
            .filter(inst -> inst.name().equals(institutionKey))
            .findFirst()
            .map(InstitutionsEnum::getLabel)
            .orElse(institutionKey);
    }
}

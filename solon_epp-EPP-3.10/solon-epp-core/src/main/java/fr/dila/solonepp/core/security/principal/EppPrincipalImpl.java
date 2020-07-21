package fr.dila.solonepp.core.security.principal;

import java.util.Set;

import org.nuxeo.ecm.core.api.ClientException;

import fr.dila.solonepp.api.institution.InstitutionsEnum;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.st.core.user.STPrincipalImpl;

/**
 * Implémentation du principal du socle SOLON EPP.
 * 
 * @author jtremeaux
 */
public class EppPrincipalImpl extends STPrincipalImpl implements EppPrincipal {
    /**
     * Serial UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Ensemble des identifiants techniques des institutions.
     */
    private Set<String> institutionIdSet;

    /**
     * Constructeur de STPrincipalImpl.
     * 
     * @param name name
     * @param isAnonymous isAnonymous
     * @param isAdministrator isAdministrator
     * @param updateAllGroups updateAllGroups
     * @throws ClientException
     */
    public EppPrincipalImpl(String name, boolean isAnonymous, boolean isAdministrator, boolean updateAllGroups) throws ClientException {
        super(name, isAnonymous, isAdministrator, updateAllGroups);
    }

    @Override
    public Set<String> getInstitutionIdSet() {
        return institutionIdSet;
    }

    @Override
    public void setInstitutionIdSet(Set<String> institutionSet) {
        this.institutionIdSet = institutionSet;
    }

    @Override
    public String getInstitutionId() {
        if (institutionIdSet == null || institutionIdSet.isEmpty()) {
            return null;
        }
        return institutionIdSet.iterator().next();
    }

    @Override
    public boolean isInstitutionGouvernement() {
        return institutionIdSet != null && institutionIdSet.contains(InstitutionsEnum.GOUVERNEMENT.name());
    }

    @Override
    public boolean isInstitutionAn() {
        return institutionIdSet != null && institutionIdSet.contains(InstitutionsEnum.ASSEMBLEE_NATIONALE.name());
    }

    @Override
    public boolean isInstitutionSenat() {
        return institutionIdSet != null && institutionIdSet.contains(InstitutionsEnum.SENAT.name());
    }

    @Override
    public boolean isInstitutionDila() {
        return institutionIdSet != null && institutionIdSet.contains(InstitutionsEnum.DILA.name());
    }

    @Override
    public void setAdministrator(boolean administrator) {
        this.isAdministrator = administrator;
    }
}

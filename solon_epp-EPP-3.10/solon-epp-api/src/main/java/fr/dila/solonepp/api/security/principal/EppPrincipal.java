package fr.dila.solonepp.api.security.principal;

import java.util.Set;

import fr.dila.st.api.security.principal.STPrincipal;

/**
 * Principal de SOLON EPP.
 * 
 * @author jtremeaux
 */
public interface EppPrincipal extends STPrincipal {
    /**
     * Getter de institutionIdSet.
     * 
     * @return institutionIdSet
     */
    Set<String> getInstitutionIdSet();

    /**
     * Setter de institutionIdSet.
     * 
     * @param institutionIdSet institutionIdSet
     */
    void setInstitutionIdSet(Set<String> institutionSet);

    /**
     * Retourne une des institutions de l'utilisateur. Si l'utilisateur appartient à plusieurs institutions (via ses postes), comportement indéfini.
     * 
     * @return Institution
     */
    String getInstitutionId();

    /**
     * Détermine si l'utilisateur fait partir de l'institution gouvernement.
     * 
     * @return Vrai si l'utilisateur fait partir de l'institution gouvernement
     */
    boolean isInstitutionGouvernement();

    /**
     * Détermine si l'utilisateur fait partir de l'institution AN.
     * 
     * @return Vrai si l'utilisateur fait partir de l'institution AN
     */
    boolean isInstitutionAn();

    /**
     * Détermine si l'utilisateur fait partir de l'institution sénat.
     * 
     * @return Vrai si l'utilisateur fait partir de l'institution sénat
     */
    boolean isInstitutionSenat();

    /**
     * Détermine si l'utilisateur fait partir de l'institution dila.
     * 
     * @return Vrai si l'utilisateur fait partir de l'institution dila
     */
    boolean isInstitutionDila();

    /**
     * force le user courant en admin (evite les unrestricted)
     * 
     * 
     */
    void setAdministrator(boolean administrator);
}

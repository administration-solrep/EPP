package fr.dila.solonepp.core.assembler.ws;

import com.google.common.collect.ImmutableBiMap;
import fr.dila.solonepp.api.constant.SolonEppLifecycleConstant;
import fr.sword.xsd.solon.epp.EtatVersion;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Assembleur des états de version objet métier <-> Web service.
 *
 * @author jtremeaux
 */
public class VersionEtatAssembler {
    private static final ImmutableBiMap<EtatVersion, String> versionEtatMap = new ImmutableBiMap.Builder<EtatVersion, String>()
        .put(EtatVersion.ABANDONNE, SolonEppLifecycleConstant.VERSION_ABANDONNE_STATE)
        .put(EtatVersion.BROUILLON, SolonEppLifecycleConstant.VERSION_BROUILLON_STATE)
        .put(EtatVersion.EN_ATTENTE_VALIDATION, SolonEppLifecycleConstant.VERSION_ATTENTE_VALIDATION_STATE)
        .put(EtatVersion.OBSOLETE, SolonEppLifecycleConstant.VERSION_OBSOLETE_STATE)
        .put(EtatVersion.PUBLIE, SolonEppLifecycleConstant.VERSION_PUBLIE_STATE)
        .put(EtatVersion.REJETE, SolonEppLifecycleConstant.VERSION_REJETE_STATE)
        .build();

    /**
     * Assemble l'objet web service -> métier.
     *
     * @param xsd Objet web service
     * @return Objet métier
     */
    public static String assembleXsdToEtatVersion(EtatVersion xsd) {
        String versionEtat = versionEtatMap.get(xsd);
        if (versionEtat == null) {
            throw new NuxeoException("Etat de version inconnu : " + xsd);
        }
        return versionEtat;
    }

    /**
     * Assemble l'objet métier -> web service.
     *
     * @param versionEtat Objet métier
     * @return Objet web service
     */
    public static EtatVersion assembleEtatVersionToXsd(String versionEtat) {
        EtatVersion xsd = versionEtatMap.inverse().get(versionEtat);
        if (xsd == null) {
            throw new NuxeoException("Etat de version inconnu : " + versionEtat);
        }
        return xsd;
    }
}

package fr.dila.solonepp.core.descriptor.metadonnees;

import fr.dila.solonepp.api.descriptor.metadonnees.EvenementMetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.descriptor.metadonnees.VersionMetaDonneesDescriptor;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur des metadonnées (evenement + version).
 *
 * @author asatre
 */
@XObject("metadonnees")
public class MetaDonneesDescriptorImpl implements MetaDonneesDescriptor {
    /**
     * Nom du type d'événement (ex. "EVT01").
     */
    @XNode("@name")
    private String name;

    /**
     * Propriétés qui peuvent / doivent être fournies pour ce type de version.
     */
    @XNode(value = "evenementMetadonnees")
    private EvenementMetaDonneesDescriptorImpl evenement;

    @XNode(value = "versionMetadonnees")
    private VersionMetaDonneesDescriptorImpl version;

    /**
     * Constructeur par défaut de VersionSchemaDescriptor.
     */
    public MetaDonneesDescriptorImpl() {}

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setEvenement(EvenementMetaDonneesDescriptor evenement) {
        this.evenement = (EvenementMetaDonneesDescriptorImpl) evenement;
    }

    @Override
    public EvenementMetaDonneesDescriptor getEvenement() {
        return evenement;
    }

    @Override
    public void setVersion(VersionMetaDonneesDescriptor version) {
        this.version = (VersionMetaDonneesDescriptorImpl) version;
    }

    @Override
    public VersionMetaDonneesDescriptor getVersion() {
        return version;
    }
}

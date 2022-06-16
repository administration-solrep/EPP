package fr.dila.solonepp.api.descriptor.evenementtype;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur de la distribution d'un type d'événement.
 *
 * @author jtremeaux
 */
@XObject("distribution")
public class DistributionDescriptor {
    /**
     * Emetteurs possibles de ce type d'événement.
     */
    @XNode(value = "emetteur")
    private DistributionElementDescriptor emetteur;

    /**
     * Destinataires possibles de ce type d'événement.
     */
    @XNode(value = "destinataire")
    private DistributionElementDescriptor destinataire;

    /**
     * Copies possibles de ce type d'événement.
     */
    @XNode(value = "copie")
    private DistributionElementDescriptor copie;

    /**
     * Getter de emetteur.
     *
     * @return emetteur
     */
    public DistributionElementDescriptor getEmetteur() {
        return emetteur;
    }

    /**
     * Setter de emetteur.
     *
     * @param emetteur emetteur
     */
    public void setEmetteur(DistributionElementDescriptor emetteur) {
        this.emetteur = emetteur;
    }

    /**
     * Getter de destinataire.
     *
     * @return destinataire
     */
    public DistributionElementDescriptor getDestinataire() {
        return destinataire;
    }

    /**
     * Setter de destinataire.
     *
     * @param destinataire destinataire
     */
    public void setDestinataire(DistributionElementDescriptor destinataire) {
        this.destinataire = destinataire;
    }

    /**
     * Getter de copie.
     *
     * @return copie
     */
    public DistributionElementDescriptor getCopie() {
        return copie;
    }

    /**
     * Setter de copie.
     *
     * @param copie copie
     */
    public void setCopie(DistributionElementDescriptor copie) {
        this.copie = copie;
    }
}

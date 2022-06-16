package fr.dila.solonepp.core.assembler;

import fr.dila.solonepp.api.domain.evenement.Evenement;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Assembleur des données des objets Evenement.
 *
 * @author jtremeaux
 */
public class EvenementAssembler {

    /**
     * Constructeur de EvenementAssembler.
     *
     */
    public EvenementAssembler() {}

    /**
     * Assemble les propriétés d'une événement pour modifier une evenement existante.
     *
     * @param evenementFromDoc Nouveau document événement
     * @param evenementToDoc Document événement à modifier
     */
    public void assembleEvenementForUpdate(DocumentModel evenementFromDoc, DocumentModel evenementToDoc) {
        Evenement evenementFrom = evenementFromDoc.getAdapter(Evenement.class);
        Evenement evenementTo = evenementToDoc.getAdapter(Evenement.class);

        evenementTo.setDestinataire(evenementFrom.getDestinataire());
        evenementTo.setDestinataireCopie(evenementFrom.getDestinataireCopie());
    }
}

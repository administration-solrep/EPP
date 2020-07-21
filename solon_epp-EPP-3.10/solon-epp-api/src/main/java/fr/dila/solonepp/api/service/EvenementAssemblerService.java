package fr.dila.solonepp.api.service;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;

/**
 * Service permettant de gérer l'assemblage les événements.
 * 
 * @author asatre
 */
public interface EvenementAssemblerService extends Serializable {

    Assembler getAssemblerInstanceFor(EvenementType evenementType, EppEvtContainer eppEvtContainer, CoreSession session, EppPrincipal eppPrincipal)
            throws ClientException;

}

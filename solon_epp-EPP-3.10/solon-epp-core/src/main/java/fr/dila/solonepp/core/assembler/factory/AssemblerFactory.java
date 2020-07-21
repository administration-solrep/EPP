package fr.dila.solonepp.core.assembler.factory;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.EvenementAssemblerService;
import fr.dila.st.core.util.ServiceUtil;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;

public class AssemblerFactory {

    public static Assembler getXsdToObjectAssembler(final EppEvtContainer eppEvtContainer, final CoreSession session, final String typeEvenement,
            final EppPrincipal eppPrincipal) throws ClientException {

        // Vérifie le type d'événement
        final EvenementType evenementType = EvenementType.fromValue(typeEvenement);
        if (evenementType == null) {
            throw new ClientException("Type de communication inconnu");
        }

        final EvenementAssemblerService assemblerService = ServiceUtil.getService(EvenementAssemblerService.class);

        return assemblerService.getAssemblerInstanceFor(evenementType, eppEvtContainer, session, eppPrincipal);
    }
}

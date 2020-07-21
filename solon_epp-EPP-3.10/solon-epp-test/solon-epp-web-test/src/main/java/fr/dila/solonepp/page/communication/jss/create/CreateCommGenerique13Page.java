package fr.dila.solonepp.page.communication.jss.create;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.jss.detail.DetailCommGenerique13Page;


public class CreateCommGenerique13Page extends AbstractCreateComm {

    public static final String TYPE_COMM = "Generique - Demande de jours supplémentaires de séance";
    
    
    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailCommGenerique13Page.class;

    }
    
    public DetailCommGenerique13Page createCommGenerique13(final String destinataire ) {
        checkValue(COMMUNICATION, TYPE_COMM);
        selectInOrganigramme(destinataire, DESTINATAIRE);
        sleep(3);
        return publier();
    }
    
}
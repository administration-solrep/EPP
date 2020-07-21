package fr.dila.solonepp.page.communication.aud.create.generique;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.aud.detail.generique.DetailCommGenerique14Page;

public class CreateCommGenerique14Page extends AbstractCreateComm {

    public static final String TYPE_COMM = "Generique - Autres documents transmis aux assemblées";
    public static final String COMMUNICATION = "nxw_metadonnees_evenement_libelle";

    
    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailCommGenerique14Page.class;
    }
    
    public DetailCommGenerique14Page createCommGenerique14(final String destinataire ) {
        checkValue(COMMUNICATION, TYPE_COMM);
        selectInOrganigramme(destinataire, DESTINATAIRE);
        return publier();
    }
    
}

package fr.dila.solonepp.page.communication.oep.create;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.oep.detail.DetailComm490Page;


public class CreateComm490Page extends AbstractCreateComm {

    public static final String TYPE_COMM = "OEP-01 : Notification de la vacance d'un mandat au sein d'un organisme extraparlementaire";
    
    public DetailComm490Page createComm490(String idDossier,String objet, String baseLegale) {
        checkValue(COMMUNICATION, TYPE_COMM);
        setIdentifiantDossier(idDossier);
        setObjet(objet);
        setbaseLegale(baseLegale);
        return publier();
    }
    
    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm490Page.class;
    }


}

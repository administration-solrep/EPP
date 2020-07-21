package fr.dila.solonepp.page.communication.cens.create;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.cens.detail.DetailComm17Page;

public class CreateComm17Page extends AbstractCreateComm {

    public static final String TYPE_COMM = "CENS-01 - Motion de censure article 49 alinéa 2";

    public void createComm17(String idDossier, String objet) {
        checkValue(COMMUNICATION, TYPE_COMM);
        setIdentifiantDossier(idDossier);
        setObjet(objet);
        publier();
    }

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm17Page.class;
    }

}

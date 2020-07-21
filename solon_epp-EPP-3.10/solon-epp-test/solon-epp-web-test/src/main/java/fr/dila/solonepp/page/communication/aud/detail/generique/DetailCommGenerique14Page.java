package fr.dila.solonepp.page.communication.aud.detail.generique;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.aud.create.generique.CreateCommGenerique14Page;

public class DetailCommGenerique14Page extends AbstractDetailComm {

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateCommGenerique14Page.class;
    }

}

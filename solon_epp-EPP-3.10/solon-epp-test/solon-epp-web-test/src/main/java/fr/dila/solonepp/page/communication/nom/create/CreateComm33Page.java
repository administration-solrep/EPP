package fr.dila.solonepp.page.communication.nom.create;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.nom.detail.DetailComm33Page;


public class CreateComm33Page extends AbstractCreateComm {

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm33Page.class;

    }
}
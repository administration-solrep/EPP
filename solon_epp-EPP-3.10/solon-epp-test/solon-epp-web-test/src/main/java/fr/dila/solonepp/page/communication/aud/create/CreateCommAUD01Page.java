package fr.dila.solonepp.page.communication.aud.create;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.aud.detail.DetailCommAUD01Page;

public class CreateCommAUD01Page extends AbstractCreateComm {

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailCommAUD01Page.class;
    }

}

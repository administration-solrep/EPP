package fr.dila.solonepp.page.communication.aud.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.aud.create.CreateCommAUD01Page;

public class DetailCommAUD01Page extends AbstractDetailComm {

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateCommAUD01Page.class;
    }

}

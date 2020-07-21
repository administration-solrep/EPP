package fr.dila.solonepp.page.communication.aud.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.aud.create.CreateCommAUD02Page;

public class DetailCommAUD02Page extends AbstractDetailComm {

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateCommAUD02Page.class;
    }

}

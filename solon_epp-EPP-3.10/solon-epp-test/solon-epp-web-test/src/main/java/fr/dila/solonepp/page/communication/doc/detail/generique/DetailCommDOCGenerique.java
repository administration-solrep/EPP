package fr.dila.solonepp.page.communication.doc.detail.generique;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.doc.create.generique.CreateDOCGenerique;

public class DetailCommDOCGenerique extends AbstractDetailComm {

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateDOCGenerique.class;
    }
}

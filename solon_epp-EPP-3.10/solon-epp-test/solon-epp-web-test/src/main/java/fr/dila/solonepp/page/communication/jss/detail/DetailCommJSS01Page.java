package fr.dila.solonepp.page.communication.jss.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.jss.create.CreateCommJSS01Page;

public class DetailCommJSS01Page extends AbstractDetailComm {

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateCommJSS01Page.class;
    }
}

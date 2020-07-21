package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm01Page;

public class DetailComm01Page extends AbstractDetailComm {

    public static final String NATURE_LOI = "nxw_metadonnees_version_natureLoi_row";
    public static final String TYPE_LOI = "nxw_metadonnees_version_typeLoi_row";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm01Page.class;
    }

}

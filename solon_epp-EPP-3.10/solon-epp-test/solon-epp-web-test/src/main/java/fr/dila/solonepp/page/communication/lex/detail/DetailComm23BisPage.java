package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm23BisPage;

public class DetailComm23BisPage extends AbstractDetailComm {
    public static final String TYPE_COMM = "LEX-27 : Demande de nouvelle lecture";
    public static final String DATE_DEMANDE = "nxw_metadonnees_version_date_row";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm23BisPage.class;
    }
}

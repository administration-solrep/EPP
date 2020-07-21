package fr.dila.solonepp.page.communication.lex.detail;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.create.CreateComm23QuaterPage;

public class DetailComm23QuaterPage extends AbstractDetailComm {
    public static final String TYPE_COMM = "LEX-29 : Demande de lecture définitive à l'Assemblée nationale";
    public static final String DATE_DEMANDE = "nxw_metadonnees_version_date_row";

    @Override
    protected Class<? extends AbstractCreateComm> getModifierResultPageClass() {
        return CreateComm23QuaterPage.class;
    }
}

package fr.dila.solonepp.page.communication.jss.create;

import java.util.Calendar;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.jss.detail.DetailCommJSS01Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

public class CreateCommJSS01Page extends AbstractCreateComm {

    public static final String TYPE_COMM = "JSS-01 : Demande de jours supplémentaires de séance";
    public static final String DATE_VOTE_INPUT_DATE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateVoteInputDate";
    public static final String OBJET_ID = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_objet";
    public static final String HORODATAGE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_horodatage";

    public static final String URL = "http://PJPMVANPourAVIAN.solon-epg.fr";
    
    public static final String PJ = "src/main/attachments/piece-jointe.doc";
    

    public DetailCommJSS01Page createCommJSS01(final String idDossier, final String objet) {
        checkValue(COMMUNICATION, TYPE_COMM);
        checkValueContain(HORODATAGE, DateUtils.format(Calendar.getInstance()));
        setObjet(objet);
        setIdentifiantDossier(idDossier);
        addPieceJointe(PieceJointeType.LETTRE, URL, PJ);
        return publier();
    }

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailCommJSS01Page.class;
    }

}

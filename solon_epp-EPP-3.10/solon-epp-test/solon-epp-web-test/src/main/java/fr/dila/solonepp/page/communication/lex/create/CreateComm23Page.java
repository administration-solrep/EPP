package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm23Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 26 (EVT23) : CMP - Demande de lecture des conclusions
 * 
 * @author mbd
 * 
 */
public class CreateComm23Page extends AbstractCreateComm {

    public static final String TYPE_COMM = "LEX-26 : CMP - Demande de lecture des conclusions";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm23Page.class;
    }

    public DetailComm23Page createComm23(String destinataire, String commentaire) {
        checkValue(COMMUNICATION, TYPE_COMM);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);
        checkValue(EMETTEUR, "Gouvernement");
        
        setDestinataire(destinataire);
        checkValue("nxw_metadonnees_evenement_destinataire", destinataire);

        if (destinataire.equals("Sénat")) {
            checkValue(COPIE, "Assemblée nationale");
        } else {
            checkValue(COPIE, "Sénat");
        }
        
        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);

        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        addPieceJointe(PieceJointeType.LETTRE_PM, URL, PJ);

        return publier();
    }

}

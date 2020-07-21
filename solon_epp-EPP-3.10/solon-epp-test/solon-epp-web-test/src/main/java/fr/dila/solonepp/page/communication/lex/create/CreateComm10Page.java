package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm10Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 14 (EVT10) : Engagement de la procédure accélérée
 * @author mbd
 *
 */
public class CreateComm10Page extends AbstractCreateComm {
  
    public static final String TYPE_COMM = "LEX-14 : Engagement de la procédure accélérée";
    
    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";
    
    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm10Page.class;
    }
    
    public DetailComm10Page createComm10(String commentaire, String destinataire) {
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValue(COMMUNICATION, TYPE_COMM);
        checkValueStartWith(HORODATAGE, horodatage);
        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);
        setDestinataire(destinataire);
        //Plante 1fois sur 2 sur Jenkins car un élément n'est aps attaché au DOM or cette valeur est vérifiée plus tard 
//        checkValue(DESTINATAIRE, destinataire);
//        if (destinataire.equals("Assemblée nationale")) {
//            checkValue(COPIE, "Sénat");
//        } else {
//            checkValue(COPIE, "Assemblée nationale");
//        }

        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        addPieceJointe(PieceJointeType.LETTRE_PM, URL, PJ);
        
        return publier();
    }

}

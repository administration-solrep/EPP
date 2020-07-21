package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm22Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 25 (EVT22) : CMP - Notification du résultat et dépôt du rapport
 * 
 * @author mbd
 * 
 */
public class CreateComm22Page extends AbstractCreateComm {

    public static final String TYPE_COMM = "LEX-25 : CMP - Notification du résultat et dépôt du rapport";
    public static final String DATE_DEPOT_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateDepotTexteInputDate";
    public static final String NUMERO_DEPOT_RAPPORT_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroDepotRapport";
    public static final String NUMERO_DEPOT_TEXTE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroDepotTexte";
    public static final String RESULTAT_CMP_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_resultatCMP";
    private static final String ID_DOSSIER_COMM_PRECEDENTE = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_identifiant_precedent";

    
    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";
    
    public void setDateDepot(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_DEPOT_INPUT));
        fillField("Date de dépôt", elem, date);
    }

    public void setNumeroDepotRapport(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUMERO_DEPOT_RAPPORT_INPUT));
        fillField("Numéro de dépôt du rapport", elem, numero);
    }

    public void setNumeroDepotTexte(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUMERO_DEPOT_TEXTE_INPUT));
        fillField("Numéro de dépôt du texte", elem, numero);
    }

    public void setResultatCMP(String libelle) {
        final WebElement elem = getDriver().findElement(By.id(RESULTAT_CMP_SELECT));
        Select select = new Select(elem);
        select.selectByVisibleText(libelle);
    }

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm22Page.class;
    }

    public DetailComm22Page createComm22(String emetteur, String numeroDepotRapport, String dateDepot, String numeroDepotTexte, String libelleResultatCmp, String commentaire) {
        checkValue(COMMUNICATION, TYPE_COMM);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);
        checkValue(EMETTEUR, emetteur);
        checkValue("nxw_metadonnees_evenement_destinataire", "Gouvernement");

        if (emetteur.equals("Sénat")) {
            checkValue(COPIE, "Assemblée nationale");
        } else {
            checkValue(COPIE, "Sénat");
        }

        setNumeroDepotRapport(numeroDepotRapport);
        checkValue(NUMERO_DEPOT_RAPPORT_INPUT, numeroDepotRapport);

        setDateDepot(dateDepot);
        checkValue(DATE_DEPOT_INPUT, dateDepot);

        setNumeroDepotTexte(numeroDepotTexte);
        checkValue(NUMERO_DEPOT_TEXTE_INPUT, numeroDepotTexte);

        setResultatCMP(libelleResultatCmp);

        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);

        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        addPieceJointe(PieceJointeType.RAPPORT, URL, PJ);
        addPieceJointe(PieceJointeType.TEXTE, URL, PJ);

        return publier();
    }
    
    public DetailComm22Page createComm22(final String dossier_id,final String dateDepotTexteInputDate,final String resultat_cmp_label){
        
        checkValue(ID_DOSSIER_COMM_PRECEDENTE, dossier_id);
        
        setDateDepot(dateDepotTexteInputDate);
        setResultatCMP(resultat_cmp_label);
        
        return publier();
      }

}

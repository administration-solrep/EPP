package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm23QuinquiesPage;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 30 (EVT23Quinquies) : Enregistrement de la demande de lecture définitive
 * 
 * @author mbd
 * 
 */
public class CreateComm23QuinquiesPage extends AbstractCreateComm {

    public static final String TYPE_COMM = "LEX-30 : Enregistrement de la demande de lecture définitive";
    public static final String DATE_DEPOT_TEXTE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateDepotTexteInputDate";
    public static final String NUMERO_DEPOT_TEXTE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroDepotTexte";
    public static final String COMM_SAISIE_FOND_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisieAuFond_suggest";
    public static final String COMM_SAISIE_FOND_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisieAuFond_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String COMM_SAISIE_FOND_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisieAuFond_delete";
    public static final String COMM_SAISIE_AVIS_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisiePourAvis_suggest";
    public static final String COMM_SAISIE_AVIS_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisiePourAvis_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String COMM_SAISIE_AVIS_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisiePourAvis_list:0:nxw_metadonnees_version_commissionSaisiePourAvis_delete";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";

    public void setNumeroDepotTexte(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUMERO_DEPOT_TEXTE_INPUT));
        fillField("Numero dépôt texte", elem, numero);
    }
    
    public void setDateDepotTexte(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_DEPOT_TEXTE_INPUT));
        fillField("Date dépôt texte", elem, date);
    }
    
    public void addCommissionSaisieAuFond(final String commission) {
        final WebElement elem = getDriver().findElement(By.id(COMM_SAISIE_FOND_INPUT));
        fillField("Commission saisie au fond", elem, commission);
        waitForPageSourcePart(By.xpath(COMM_SAISIE_FOND_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(COMM_SAISIE_FOND_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(COMM_SAISIE_FOND_DELETE), TIMEOUT_IN_SECONDS);
    }
    
    public void addCommissionSaisiePourAvis(final String commission) {
        final WebElement elem = getDriver().findElement(By.id(COMM_SAISIE_AVIS_INPUT));
        fillField("Commission saisie pour avis", elem, commission);
        waitForPageSourcePart(By.xpath(COMM_SAISIE_AVIS_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(COMM_SAISIE_AVIS_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(COMM_SAISIE_AVIS_DELETE), TIMEOUT_IN_SECONDS);
    }
    
    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm23QuinquiesPage.class;
    }

    public DetailComm23QuinquiesPage createComm23Quinquies(String dateDepot, String numeroDepot, String commFond, String commAvis, 
            String commentaire) {
        checkValue(COMMUNICATION, TYPE_COMM);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);
        checkValue(EMETTEUR, "Assemblée nationale");
        
        checkValue("nxw_metadonnees_evenement_destinataire", "Gouvernement");
        checkValue(COPIE, "Sénat");
        
        setDateDepotTexte(dateDepot);
        checkValue(DATE_DEPOT_TEXTE_INPUT, dateDepot);
        
        setNumeroDepotTexte(numeroDepot);
        checkValue(NUMERO_DEPOT_TEXTE_INPUT, numeroDepot);
        addCommissionSaisieAuFond(commFond);
        addCommissionSaisiePourAvis(commAvis);
        
        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);

        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        addPieceJointe(PieceJointeType.TEXTE, URL, PJ);

        return publier();
    }

}

package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm03Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 03 (EVT03) : Pjl - Enregistrement du dépôt 
 * @author mbd
 *
 */
public class CreateComm03Page extends AbstractCreateComm {

    public static final String TYPE_COMM = "LEX-03 : Pjl - Enregistrement du dépôt";
    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";
    
    public static final String URL_DOSSIER_AN_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_urlDossierAn_url";
    public static final String URL_DOSSIER_SENAT = "nxw_metadonnees_version_urlDossierSenat_row";
    public static final String NUM_DEPOT_TXT_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroDepotTexte";
    public static final String DATE_DEPOT_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateDepotTexteInputDate";
    public static final String COMM_SAISIE_FOND_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisieAuFond_suggest";
    public static final String COMM_SAISIE_FOND_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisieAuFond_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String COMM_SAISIE_FOND_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisieAuFond_delete";
    public static final String COMM_SAISIE_AVIS_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisiePourAvis_suggest";
    public static final String COMM_SAISIE_AVIS_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisiePourAvis_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String COMM_SAISIE_AVIS_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisiePourAvis_list:0:nxw_metadonnees_version_commissionSaisiePourAvis_delete";

    
    public void setUrlAN(String url) {
        final WebElement elem = getDriver().findElement(By.id(URL_DOSSIER_AN_INPUT));
        fillField("UrlAN", elem, url);
    }
    
    public void setNumeroDepotTexte(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUM_DEPOT_TXT_INPUT));
        fillField("Numero dépôt texte", elem, numero);
    }
    
    public void setDateDepotTexte(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_DEPOT_INPUT));
        fillField("Date dépôt texte", elem, date);
    }
    
    public void addCommissionSaisieAuFond(final String commission) {
        final WebElement elem = getDriver().findElement(By.id(COMM_SAISIE_FOND_INPUT));
        fillField("Commission saisie au fond", elem, commission);
        waitForPageSourcePartDisplayed(By.xpath(COMM_SAISIE_FOND_SUGGEST), TIMEOUT_IN_SECONDS);
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
        return DetailComm03Page.class;
    }
    
    public DetailComm03Page createComm03(String urlDossierAn, String numeroDepotTexte, String commentaire, String dateDepot, String commFond, String commAvis) {
        checkValue(COMMUNICATION, TYPE_COMM);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);

        setUrlAN(urlDossierAn);
        checkValue(URL_DOSSIER_AN_INPUT, urlDossierAn);

        setNumeroDepotTexte(numeroDepotTexte);
        checkValue(NUM_DEPOT_TXT_INPUT, numeroDepotTexte);

        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);

        setDateDepotTexte(dateDepot);
        checkValue(DATE_DEPOT_INPUT, dateDepot);

        addCommissionSaisieAuFond(commFond);
        addCommissionSaisiePourAvis(commAvis);

        addPieceJointe(PieceJointeType.TEXTE, URL, PJ);
        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        
        return publier();
    }
    
    public DetailComm03Page createComm03(String dateDepot, String numeroDepotTexte) {
        checkValue(COMMUNICATION, TYPE_COMM);

        setDateDepotTexte(dateDepot);
        checkValue(DATE_DEPOT_INPUT, dateDepot);
        
        setNumeroDepotTexte(numeroDepotTexte);
        checkValue(NUM_DEPOT_TXT_INPUT, numeroDepotTexte);

        return publier();
    }

}

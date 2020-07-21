package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm13BisPage;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.NiveauLectureCode;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 18 (EVT13 Bis) : Pjl - Enregistrement du dépôt en navette
 * 
 * @author mbd
 * 
 */
public class CreateComm13BisPage extends AbstractCreateComm {

    public static final String TYPE_COMM = "LEX-18 : Pjl - Enregistrement du dépôt en navette";
    public static final String NUMERO_NIVEAU_LECTURE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_niveauLecture:nxw_metadonnees_version_niveauLecture_input";
    public static final String NIVEAU_LECTURE_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_niveauLecture:nxw_metadonnees_version_niveauLecture_select_one_menu";
    public static final String URL_DOSSIER_AN_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_urlDossierAn_url";
    public static final String DATE_DEPOT_TEXTE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateDepotTexteInputDate";
    public static final String NUMERO_DEPOT_TEXTE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroDepotTexte";
    public static final String NUMERO_TEXTE_ADOPTE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroTexteAdopte";
    public static final String DATE_ADOPTION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateAdoptionInputDate";
    public static final String SORT_ADOPTION_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_sortAdoption";
    public static final String COMM_SAISIE_FOND_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisieAuFond_suggest";
    public static final String COMM_SAISIE_FOND_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisieAuFond_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String COMM_SAISIE_FOND_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisieAuFond_delete";
    public static final String COMM_SAISIE_AVIS_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisiePourAvis_suggest";
    public static final String COMM_SAISIE_AVIS_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisiePourAvis_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String COMM_SAISIE_AVIS_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisiePourAvis_list:0:nxw_metadonnees_version_commissionSaisiePourAvis_delete";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";

    public void setNumeroNiveauLecture(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUMERO_NIVEAU_LECTURE_INPUT));
        fillField("Niveau de lecture", elem, numero);
    }

    public void setNiveauLectureOrganisme(String libelle) {
        final WebElement elem = getDriver().findElement(By.id(NIVEAU_LECTURE_SELECT));
        Select select = new Select(elem);
        select.selectByVisibleText(libelle);
    }

    public void setNumeroDepotTexte(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUMERO_DEPOT_TEXTE_INPUT));
        fillField("Numero dépôt texte", elem, numero);
    }

    public void setDateDepotTexte(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_DEPOT_TEXTE_INPUT));
        fillField("Date dépôt texte", elem, date);
    }

    public void addCommissionSaisieAvis(final String commission) {
        final WebElement elem = getDriver().findElement(By.id(COMM_SAISIE_AVIS_INPUT));
        fillField("Commission saisie pour avis", elem, commission);
        waitForPageSourcePart(By.xpath(COMM_SAISIE_AVIS_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(COMM_SAISIE_AVIS_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(COMM_SAISIE_AVIS_DELETE), TIMEOUT_IN_SECONDS);
    }

    public void addCommissionSaisieFond(final String commission) {
        final WebElement elem = getDriver().findElement(By.id(COMM_SAISIE_FOND_INPUT));
        fillField("Commission saisie au fond", elem, commission);
        waitForPageSourcePart(By.xpath(COMM_SAISIE_FOND_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(COMM_SAISIE_FOND_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(COMM_SAISIE_FOND_DELETE), TIMEOUT_IN_SECONDS);
    }

    public void setNumeroTexteAdopte(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUMERO_TEXTE_ADOPTE_INPUT));
        fillField("Numéro texte adopté", elem, numero);
    }

    public void setDateAdoption(String dateAdoption) {
        final WebElement elem = getDriver().findElement(By.id(DATE_ADOPTION_INPUT));
        fillField("Date adoption", elem, dateAdoption);
    }

    public void setSortAdoption(String libelle) {
        final WebElement elem = getDriver().findElement(By.id(SORT_ADOPTION_SELECT));
        Select select = new Select(elem);
        select.selectByVisibleText(libelle);
    }

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm13BisPage.class;
    }

    public DetailComm13BisPage createComm13Bis(String emetteur, String commentaire, String numeroTexteAdopte, String dateAdoption, 
            String sortAdoption, String dateDepotTexte, String numeroDepot, String commFond, String commAvis, Integer niveauLecture, NiveauLectureCode organisme) {
        checkValue(COMMUNICATION, TYPE_COMM);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);
        checkValue(EMETTEUR, emetteur);
        //checkValue(DESTINATAIRE, "Gouvernement");

        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);
        
        setNiveauLecture(niveauLecture, organisme);
        
        
        if (niveauLecture != null) {
            checkValue(NUMERO_NIVEAU_LECTURE_INPUT, Integer.toString(niveauLecture));
        }

        setNumeroTexteAdopte(numeroTexteAdopte);
        checkValue(NUMERO_TEXTE_ADOPTE_INPUT, numeroTexteAdopte);

        setDateAdoption(dateAdoption);
        checkValue(DATE_ADOPTION_INPUT, dateAdoption);

        setSortAdoption(sortAdoption);
        setDateDepotTexte(dateDepotTexte);
        checkValue(DATE_DEPOT_TEXTE_INPUT, dateDepotTexte);

        setNumeroDepotTexte(numeroDepot);
        checkValue(NUMERO_DEPOT_TEXTE_INPUT, numeroDepot);

        addCommissionSaisieFond(commFond);
        addCommissionSaisieAvis(commAvis);

        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        addPieceJointe(PieceJointeType.TEXTE, URL, PJ);

        return publier();
    }
    
    public DetailComm13BisPage createComm13Bis(final String numeroDepotTexte, final String dateDepotTextInputDate){
        
        setNumeroDepotTexte(numeroDepotTexte);
        setDateDepotTexte(dateDepotTextInputDate);
        return publier();
      }

}

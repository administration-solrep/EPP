package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm04BisPage;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 04 (EVT04BIS) : Dépôt de rapport, avis et texte de commission
 * @author mbd
 *
 */
public class CreateComm04BisPage extends AbstractCreateComm {

    public static final String TYPE_COMM = "LEX-04 : Dépôt de rapport, avis et texte de commission";    
    public static final String NIV_LECTURE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_niveauLecture:nxw_metadonnees_version_niveauLecture_input";
    public static final String NIV_LECTURE_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_niveauLecture:nxw_metadonnees_version_niveauLecture_select_one_menu";
    public static final String DATE_DISTRIBUTION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateDistributionElectroniqueInputDate";
    public static final String NATURE_RAPPORT_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_natureRapport";
    public static final String RAPPORTEURS_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_rapporteurList_suggest";
    public static final String RAPPORTEURS_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_rapporteurList_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String RAPPORTEURS_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_rapporteurList_list:0:nxw_metadonnees_version_rapporteurList_delete";
    public static final String DATE_DEPOT_RAPPORT_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateDepotRapportInputDate";
    public static final String NUM_DEPOT_RAPPORT_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroDepotRapport";
    public static final String DATE_DEPOT_TXT_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateDepotTexteInputDate";
    public static final String NUM_DEPOT_TXT_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroDepotTexte";
    public static final String COMMISSION_SAISIE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisie_suggest";
    public static final String COMMISSION_SAISIE_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisie_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String COMMISSION_SAISIE_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissionSaisie_delete";
    public static final String VERSION_TITRE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_titre"; 
    
    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";
    
    public void setNumeroNiveauLecture(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NIV_LECTURE_INPUT));
        fillField("Niveau de lecture", elem, numero);
    }
    
    public void setNiveauLectureOrganisme(String libelle) {
        final WebElement elem = getDriver().findElement(By.id(NIV_LECTURE_SELECT));
        Select select = new Select(elem);
        select.selectByVisibleText(libelle);
    }
    
    public void setDateDistribution(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_DISTRIBUTION_INPUT));
        fillField("Date de distribution", elem, date);
    }
    
    public void setNatureRapport(String libelle) {
        final WebElement elem = getDriver().findElement(By.id(NATURE_RAPPORT_SELECT));
        Select select = new Select(elem);
        select.selectByVisibleText(libelle);
    }
    
    public void addRapporteur(final String rapporteur) {
        final WebElement elem = getDriver().findElement(By.id(RAPPORTEURS_INPUT));
        fillField("Rapporteur", elem, rapporteur);
        waitForPageSourcePartDisplayed(By.xpath(RAPPORTEURS_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(RAPPORTEURS_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(RAPPORTEURS_DELETE), TIMEOUT_IN_SECONDS);
    }
    
    public void setNumeroDepotTexte(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUM_DEPOT_TXT_INPUT));
        fillField("Numero dépôt texte", elem, numero);
    }
    
        
    public void setDateDepotTexte(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_DEPOT_TXT_INPUT));
        fillField("Date dépôt texte", elem, date);
    }
    
    public void setNumeroDepotRapport(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUM_DEPOT_RAPPORT_INPUT));
        fillField("Numero dépôt rapport", elem, numero);
    }
    
    public void setDateDepotRapport(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_DEPOT_RAPPORT_INPUT));
        fillField("Date dépôt rapport", elem, date);
    }
    
    public void addCommissionSaisie(final String commission) {
        final WebElement elem = getDriver().findElement(By.id(COMMISSION_SAISIE_INPUT));
        fillField("Commission saisie au fond", elem, commission);
        waitForPageSourcePart(By.xpath(COMMISSION_SAISIE_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(COMMISSION_SAISIE_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(COMMISSION_SAISIE_DELETE), TIMEOUT_IN_SECONDS);
    }
    
    public void setVersionTitre(String titre) {
        final WebElement elem = getDriver().findElement(By.id(VERSION_TITRE_INPUT));
        fillField("Titre (intitulé)", elem, titre);
    }
    
    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm04BisPage.class;
    }
    
    public DetailComm04BisPage createComm04Bis(String dateDistribution, String natureRapport, String versionTitre, String numeroRapport, String dateDepotTexte, String numeroTexte,
            String rapporteur, String commission, String commentaire) {
        checkValue(COMMUNICATION, TYPE_COMM);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);

        setDateDistribution(dateDistribution);
        checkValue(DATE_DISTRIBUTION_INPUT, dateDistribution);
        
        setNatureRapport(natureRapport);
        setVersionTitre(versionTitre);
        checkValue(VERSION_TITRE_INPUT, versionTitre);
        setNumeroDepotRapport(numeroRapport);
        checkValue(NUM_DEPOT_RAPPORT_INPUT, numeroRapport);
        
        setDateDepotTexte(dateDepotTexte);
        checkValue(DATE_DEPOT_TXT_INPUT, dateDepotTexte);
        
        setNumeroDepotTexte(numeroTexte);
        checkValue(NUM_DEPOT_TXT_INPUT, numeroTexte);
        
        addRapporteur(rapporteur);
        addCommissionSaisie(commission);
        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);
        
        addPieceJointe(PieceJointeType.ANNEXE, URL, PJ);
        addPieceJointe(PieceJointeType.TEXTE, URL, PJ);
        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        addPieceJointe(PieceJointeType.RAPPORT, URL, PJ);
        addPieceJointe(PieceJointeType.AVIS, URL, PJ);
        
        return publier();
    }
    

}

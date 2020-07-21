package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm24Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 31 (EVT24) : Adoption définitive / Adoption ou rejet en Congrès
 * 
 * @author mbd
 * 
 */
public class CreateComm24Page extends AbstractCreateComm {
    public static final String TYPE_COMM = "LEX-31 : Adoption définitive / Adoption ou rejet en Congrès";
    public static final String DATE_ADOPTION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateAdoptionInputDate";
    public static final String NUMERO_TEXTE_ADOPTE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroTexteAdopte";
    public static final String SORT_ADOPTION_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_sortAdoption";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";
    
    public void setDateAdoption(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_ADOPTION_INPUT));
        fillField("Date adoption", elem, date);
    }

    public void setNumeroTexteAdopte(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUMERO_TEXTE_ADOPTE_INPUT));
        fillField("Numéro texte adopté", elem, numero);
    }

    public void setSortAdoption(String libelle) {
        final WebElement elem = getDriver().findElement(By.id(SORT_ADOPTION_SELECT));
        Select select = new Select(elem);
        select.selectByVisibleText(libelle);
    }

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm24Page.class;
    }

    public DetailComm24Page createComm24(String emetteur, String numeroTexte, String dateAdoption, String sortAdoption, String commentaire) {
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

        setNumeroTexteAdopte(numeroTexte);
        checkValue(NUMERO_TEXTE_ADOPTE_INPUT, numeroTexte);

        setDateAdoption(dateAdoption);
        checkValue(DATE_ADOPTION_INPUT, dateAdoption);

        setSortAdoption(sortAdoption);

        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);

        addPieceJointe(PieceJointeType.TEXTE_ADOPTE, URL, PJ);
        addPieceJointe(PieceJointeType.TRAVAUX_PREPARATOIRES, URL, PJ);
        addPieceJointe(PieceJointeType.COHERENT, URL, PJ);
        addPieceJointe(PieceJointeType.COPIES_3_LETTRES_TRANSMISSION, URL, PJ);
        addPieceJointe(PieceJointeType.PETITE_LOI, URL, PJ);
        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        
        return publier();
    }

    public DetailComm24Page createComm24(final String numeroTextAdopte, final String dateAdoptionInputDate, final String sortAdoption_label){
        setNumeroTexteAdopte(numeroTextAdopte);
        setDateAdoption(dateAdoptionInputDate);
        setSortAdoption(sortAdoption_label);
        return publier();
        
      }
}

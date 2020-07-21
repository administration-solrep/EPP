package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm13Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.NiveauLectureCode;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 17 (EVT13) : Pjl - Navettes diverses
 * 
 * @author mbd
 * 
 */
public class CreateComm13Page extends AbstractCreateComm {

    public static final String TYPE_COMM = "LEX-17 : Pjl - Navettes diverses";
    public static final String NUMERO_TEXTE_ADOPTE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroTexteAdopte";
    public static final String DATE_ADOPTION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateAdoptionInputDate";
    public static final String SORT_ADOPTION_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_sortAdoption";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";

    public void setNumeroTexte(String numero) {
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
        return DetailComm13Page.class;
    }

    public DetailComm13Page createComm13(String destinataire, String commentaire, String numeroTexte, String dateAdoption, 
            String sortAdoption, Integer niveauLecture, NiveauLectureCode organisme) {
        checkValue(COMMUNICATION, TYPE_COMM);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);
        checkValue(EMETTEUR, "Gouvernement");
        checkValue(DESTINATAIRE, null);
        checkValue(COPIE, null);

        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);
        setDestinataire(destinataire);
//        checkValue(DESTINATAIRE, destinataire);
//
//        if (destinataire.equals("Sénat")) {
//            checkValue(COPIE, "Assemblée nationale");
//        } else {
//            checkValue(COPIE, "Sénat");
//        }
        setNiveauLecture(niveauLecture, organisme);

        if (niveauLecture != null) {
            checkValue(NIVEAU_LECTURE_INPUT, niveauLecture.toString());
        }

        setNumeroTexte(numeroTexte);
        checkValue(NUMERO_TEXTE_ADOPTE_INPUT, numeroTexte);

        setDateAdoption(dateAdoption);
        checkValue(DATE_ADOPTION_INPUT, dateAdoption);
        setSortAdoption(sortAdoption);

        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        addPieceJointe(PieceJointeType.LETTRE, URL, PJ);
        addPieceJointe(PieceJointeType.TRAVAUX_PREPARATOIRES, URL, PJ);
        addPieceJointe(PieceJointeType.TEXTE_TRANSMIS, URL, PJ);

        return publier();
    }

}

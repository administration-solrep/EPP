package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm01Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.NiveauLectureCode;
import fr.sword.xsd.solon.epp.PieceJointeType;
import fr.sword.xsd.solon.epp.TypeLoi;

public class CreateComm01Page extends AbstractCreateComm {

    public static final String TYPE_COMM = "LEX-01 : Pjl - Dépôt";
    public static final String TYPE_LOI_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_typeLoi";
    public static final String NATURE_LOI = "nxw_metadonnees_version_natureLoi_row";
    public static final String AUTEUR_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_auteur_suggest";
    public static final String AUTEUR_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_auteur_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String AUTEUR_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_auteur_delete";
    public static final String COAUTEUR_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_coauteur_suggest";
    public static final String COAUTEUR_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_coauteur_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String COAUTEUR_RESET = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_coauteur_list:0:nxw_metadonnees_version_coauteur_delete";
    public static final String INTITULE = "nxw_metadonnees_version_intitule_row";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";

    public void setTypeLoi(final TypeLoi loi) {
        final WebElement elem = getDriver().findElement(By.id(TYPE_LOI_SELECT));
        getFlog().action("Selectionne \"" + loi + "\" dans le select \"Type loi\"");
        final Select select = new Select(elem);
        select.selectByValue(loi.name());
    }

    public void addAuteur(final String auteur) {
        final WebElement elem = getDriver().findElement(By.id(AUTEUR_INPUT));
        fillField("Auteur", elem, auteur);
        waitForPageSourcePart(By.xpath(AUTEUR_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(AUTEUR_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(AUTEUR_DELETE), TIMEOUT_IN_SECONDS);
    }

    public void addCoAuteur(final String coAuteur) {
        final WebElement elem = getDriver().findElement(By.id(COAUTEUR_INPUT));
        fillField("Auteur", elem, coAuteur);
        waitForPageSourcePart(By.xpath(COAUTEUR_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(COAUTEUR_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(COAUTEUR_RESET), TIMEOUT_IN_SECONDS);
    }

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm01Page.class;
    }

    public DetailComm01Page createComm01(String emetteur, String destinataire, String objet, String nor, String auteur, 
            String coauteur, Integer niveauLecture, NiveauLectureCode organisme, Boolean inclureMandatInactif) {
    	if (inclureMandatInactif){
    		tickTousMandats();
    	}
        checkValue(COMMUNICATION, TYPE_COMM);
        checkValue(IDENTIFIANT_COMMUNICATION, null);
        checkValue(IDENTIFIANT_DOSSIER, null);
        checkValue(EMETTEUR, emetteur);
        checkValue(DESTINATAIRE, null);
        checkValue(COPIE, null);

        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);
        setDestinataire(destinataire);
        if (destinataire.equals("Assemblée nationale")) {            
            checkValue(COPIE, "Sénat");
        } else {
            checkValue(COPIE, "Assemblée nationale");
        }
        
        setNiveauLecture(niveauLecture, organisme);        
        if (niveauLecture != null) {
            checkValue(NIVEAU_LECTURE_INPUT, niveauLecture.toString());
        }

        checkValue(INTITULE, null);

        setObjet(objet);
        setNor(nor);
        checkValue(NATURE_LOI, "Projet");

        setTypeLoi(TypeLoi.LOI);
        addAuteur(auteur);
        addCoAuteur(coauteur);

        checkValue(COMMENTAIRE, null);

        addPieceJointe(PieceJointeType.TEXTE, URL, PJ);
        addPieceJointe(PieceJointeType.DECRET_PRESENTATION, URL, PJ);
        addPieceJointe(PieceJointeType.EXPOSE_DES_MOTIFS, URL, PJ);
        addPieceJointe(PieceJointeType.LETTRE_PM, URL, PJ);

        // FIXME textarea problem
        // checkValue(INTITULE, "Projet de loi Objet");

        return publier();
    }

}

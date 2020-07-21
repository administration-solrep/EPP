package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm28Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 35 (EVT28) : Publication
 * 
 * @author mbd
 * 
 */
public class CreateComm28Page extends AbstractCreateComm {
    public static final String TYPE_COMM = "LEX-35 : Publication";
    public static final String NOR_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_nor";
    public static final String TITRE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_titre";
    public static final String DATE_PROMULGATION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_datePromulgationInputDate";
    public static final String DATE_PUBLICATION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_datePublicationInputDate";
    public static final String NUMERO_LOI_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroLoi";
    public static final String NUMERO_JO_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroJo";
    public static final String PAGE_JO_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_pageJo";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";
    
    public void setNor(String nor) {
        final WebElement elem = getDriver().findElement(By.id(NOR_INPUT));
        fillField("NOR", elem, nor);
    }
    public void setTitre(String titre) {
        final WebElement elem = getDriver().findElement(By.id(TITRE_INPUT));
        fillField("Titre", elem, titre);
    }
    
    public void setDatePromulgation(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_PROMULGATION_INPUT));
        fillField("Date promulgation", elem, date);
    }
    
    public void setDatePublication(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_PUBLICATION_INPUT));
        fillField("Date publication", elem, date);
    }

    public void setNumeroLoi(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUMERO_LOI_INPUT));
        fillField("Numéro de la loi", elem, numero);
    }
    
    public void setNumeroJO(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUMERO_JO_INPUT));
        fillField("Numéro du JO", elem, numero);
    }
    
    public void setPageJO(String numero) {
        final WebElement elem = getDriver().findElement(By.id(PAGE_JO_INPUT));
        fillField("Page du JO", elem, numero);
    }

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm28Page.class;
    }

    public DetailComm28Page createComm28(String destinataire, String nor, String titre, String datePromulgation, String datePublication, 
            String numeroLoi, String numeroJO, String pageJO, String commentaire) {
        checkValue(COMMUNICATION, TYPE_COMM);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);
        checkValue(EMETTEUR, "Gouvernement");
        setDestinataire(destinataire);
        //checkValue(DESTINATAIRE, destinataire);

        setNor(nor);
        checkValue(NOR_INPUT, nor);

        setTitre(titre);
        checkValue(TITRE_INPUT, titre);
        
        setDatePromulgation(datePromulgation);
        checkValue(DATE_PROMULGATION_INPUT, datePromulgation);
        
        setDatePublication(datePublication);
        checkValue(DATE_PUBLICATION_INPUT, datePublication);
        
        setNumeroLoi(numeroLoi);
        checkValue(NUMERO_LOI_INPUT, numeroLoi);
        
        setNumeroJO(numeroJO);
        checkValue(NUMERO_JO_INPUT, numeroJO);
        
        setPageJO(pageJO);
        checkValue(PAGE_JO_INPUT, pageJO);

        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);

        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        
        return publier();
    }

}

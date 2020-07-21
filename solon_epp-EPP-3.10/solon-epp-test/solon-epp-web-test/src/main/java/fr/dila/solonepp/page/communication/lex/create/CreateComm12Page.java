package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm12Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 16 (EVT12) : Pjl - Transmission ou notification du rejet
 * @author mbd
 *
 */
public class CreateComm12Page extends AbstractCreateComm {
    public static final String TYPE_COMM = "LEX-16 : Pjl - Transmission ou notification du rejet";
    public static final String NUMERO_TEXTE_ADOPTE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroTexteAdopte";
    public static final String DATE_ADOPTION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateAdoptionInputDate";
    public static final String SORT_ADOPTION_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_sortAdoption";
    private static final String IDENTIFIANT_PRECEDENT = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_identifiant_precedent";

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
        return DetailComm12Page.class;
    }
    
    public DetailComm12Page createComm12(String numeroTexte, String dateAdoption, String sortAdoption, String commentaire) {
        checkValue(COMMUNICATION, TYPE_COMM);
        final String horodatage = DateUtils.format(Calendar.getInstance());
        checkValueStartWith(HORODATAGE, horodatage);

        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);
        
        setNumeroTexte(numeroTexte);
        checkValue(NUMERO_TEXTE_ADOPTE_INPUT, numeroTexte);
        
        setDateAdoption(dateAdoption);
        checkValue(DATE_ADOPTION_INPUT, dateAdoption);
        setSortAdoption(sortAdoption);
        
        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);        
        addPieceJointe(PieceJointeType.PETITE_LOI, URL, PJ);
        addPieceJointe(PieceJointeType.TRAVAUX_PREPARATOIRES, URL, PJ);        
        addPieceJointe(PieceJointeType.TEXTE_ADOPTE, URL, PJ);
        addPieceJointe(PieceJointeType.COHERENT, URL, PJ);
        
        return publier();
    }
    
    //ABI -refacto à vérifier
    public DetailComm12Page createComm12(final String numeroTextAdopte, final String dateAdoptionInputDate, final String sortAdoption_label){
        setNumeroTexte(numeroTextAdopte);
        setDateAdoption(dateAdoptionInputDate);
        setSortAdoption(sortAdoption_label);

        return publier();
    }

    public void checkIdentifiantPrecedent(final String dossier_precedent_id){
        checkValue(IDENTIFIANT_PRECEDENT, dossier_precedent_id);
    }
}

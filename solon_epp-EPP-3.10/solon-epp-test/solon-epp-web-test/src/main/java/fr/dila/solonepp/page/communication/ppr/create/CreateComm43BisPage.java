package fr.dila.solonepp.page.communication.ppr.create;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.ppr.detail.DetailComm43BisPage;

public class CreateComm43BisPage extends AbstractCreateComm {

    public static final String TYPE_COMM = "PPRE : Transmission d'une résolution européenne";
    public static final String DATE_ADOPTION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateAdoptionInputDate";
    private static final String SORT_ADOPTION_SELECT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_sortAdoption"; 
    
    public DetailComm43BisPage createComm43Bis(String idDossier,String objet, String intitule, String dateAdoption) {
        checkValue(COMMUNICATION, TYPE_COMM);
        setIdentifiantDossier(idDossier);
        setObjet(objet);
        setSortAdoption_label("Adopté");
        setDateAdoption(dateAdoption);
        DetailComm43BisPage.sleep(1);
        checkValue(INTITULE, intitule);
        return publier();
    }
    
    public void setDateAdoption(String dateAdoption) {
        final WebElement elem = getDriver().findElement(By.id(DATE_ADOPTION_INPUT));
        fillField("Date adoption", elem, dateAdoption);
      }
    

    
    public void setSortAdoption_label(String sortAdoption_label){
        final WebElement sortAdoptionElem = getDriver().findElement(By.id(SORT_ADOPTION_SELECT));
        getFlog().action("Selectionne \"" + sortAdoption_label + "\" dans le select \"sort adoption\"");
        final Select select = new Select(sortAdoptionElem);
        select.selectByVisibleText(sortAdoption_label);
    }
    
    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm43BisPage.class;
    }


}

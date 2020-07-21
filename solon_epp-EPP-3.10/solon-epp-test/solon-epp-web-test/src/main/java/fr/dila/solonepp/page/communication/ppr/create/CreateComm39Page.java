package fr.dila.solonepp.page.communication.ppr.create;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.ppr.detail.DetailComm39Page;

public class CreateComm39Page extends AbstractCreateComm {

    public static final String TYPE_COMM = "PPR-01 : PPR 34-1C - Information du Gouvernement du dépôt";
    public static final String DATE_DEPOT_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateDepotTexteInputDate";
    public static final String NUM_DEPOT_TXT_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_numeroDepotTexte";
    
    public DetailComm39Page createComm39(String idDossier,String objet, String auteur, String intitule, String dateDepot, String numDepot) {
        checkValue(COMMUNICATION, TYPE_COMM);
        setIdentifiantDossier(idDossier);
        setObjet(objet);
        addAuteur(auteur);
        checkValueContain(INTITULE, intitule);
        setDateDepotTexte(dateDepot);
        setNumeroDepotTexte(numDepot);
        return publier();
    }
    
    public void setDateDepotTexte(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_DEPOT_INPUT));
        fillField("Date dépôt texte", elem, date);
      }
    
    public void setNumeroDepotTexte(String numero) {
        final WebElement elem = getDriver().findElement(By.id(NUM_DEPOT_TXT_INPUT));
        fillField("Numero dépôt texte", elem, numero);
      }
    
    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm39Page.class;
    }


}

package fr.dila.solonepp.page.communication.oep.create;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.oep.detail.DetailComm51Page;


public class CreateComm51Page extends AbstractCreateComm {

    public static final String DATE_JO_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateJoInputDate";
    public static final String DATE_DESIGNATION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateDesignationInputDate";

    public static final String TYPE_COMM = "OEP-03 : Désignation au sein d'un organisme extraparlementaire";

    public DetailComm51Page createComm51(String idDossier, String objet, String baseLegale, String dateJO, String dateDesignation) {
        checkValue(COMMUNICATION, TYPE_COMM);
        setIdentifiantDossier(idDossier);
        setObjet(objet);
        setbaseLegale(baseLegale);
        setDateJO(dateJO);
        setDateDesignation(dateDesignation);
        return publier();
    }

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm51Page.class;
    }
    
    public void setDateJO(final String dateJO) {
        final WebElement elem = getDriver().findElement(By.id(DATE_JO_INPUT));
        fillField("Date JO", elem, dateJO);
    }
    
    public void setDateDesignation(final String dateDesignation) {
        final WebElement elem = getDriver().findElement(By.id(DATE_DESIGNATION_INPUT));
        fillField("Date désignation", elem, dateDesignation);
    }


}

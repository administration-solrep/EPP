package fr.dila.solonepp.page.communication.sd.create;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.sd.detail.DetailCommSD01Page;

public class CreateCommSD01Page extends AbstractCreateComm {

    public static final String DATE_DEAMNDE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateDemandeInputDate";
    public static final String GROUPE_PARLEMETAIRE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_groupeParlementaire_suggest";
    public static final String GROUPE_PARLEMETAIRE_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_groupeParlementaire_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String GROUPE_PARLEMETAIRE_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_groupeParlementaire_list:0:nxw_metadonnees_version_groupeParlementaire_delete";

    public DetailCommSD01Page createCommSD01(String idDossier, String object, String dateDemande, String groupe) {
//        setIdentifiantDossier("ID1_SD-01");
//        setObjet("objet SD-01");
//        addGroupeParlementaire("groupeAN");
        setIdentifiantDossier(idDossier);
        setObjet(object);
        addGroupeParlementaire(groupe);
        setDateDemande(dateDemande);
        return publier();
    }

    // groupeAN
    public void addGroupeParlementaire(final String groupe) {
        final WebElement elem = getDriver().findElement(By.id(GROUPE_PARLEMETAIRE_INPUT));
        fillField("Auteur", elem, groupe);
        waitForPageSourcePart(By.xpath(GROUPE_PARLEMETAIRE_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(GROUPE_PARLEMETAIRE_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(GROUPE_PARLEMETAIRE_DELETE), TIMEOUT_IN_SECONDS);
    }

    public void setDateDemande(final String dateDemande) {
        final WebElement elem = getDriver().findElement(By.id(DATE_DEAMNDE));
        fillField("Date de la demande", elem, dateDemande);
    }

    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailCommSD01Page.class;
    }

}

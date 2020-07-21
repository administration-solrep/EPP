package fr.dila.solonepp.page.communication.aud.create;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.aud.detail.DetailCommAUD02Page;

public class CreateCommAUD02Page extends AbstractCreateComm {

    public static final String COMMISSIOM_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissions_suggest";
    public static final String COMMISSIOM_SUGGEST = "//table[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissions_suggestionBox:suggest']/tbody/tr/td[2]";
    public static final String COMMISSIOM_DELETE = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_commissions_list:0:nxw_metadonnees_version_commissions_delete";
	
    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailCommAUD02Page.class;
    }
    
    public DetailCommAUD02Page createCommAUD02(String commission) {
        addCommission(commission);
        return publier();
    }
    
    public void addCommission(final String commission) {
        final WebElement elem = getDriver().findElement(By.id(COMMISSIOM_INPUT));
        fillField("Commission(s)", elem, commission);
        waitForPageSourcePart(By.xpath(COMMISSIOM_SUGGEST), TIMEOUT_IN_SECONDS);
        final WebElement suggest = getDriver().findElement(By.xpath(COMMISSIOM_SUGGEST));
        suggest.click();
        waitForPageSourcePart(By.id(COMMISSIOM_DELETE), TIMEOUT_IN_SECONDS);
    }
}
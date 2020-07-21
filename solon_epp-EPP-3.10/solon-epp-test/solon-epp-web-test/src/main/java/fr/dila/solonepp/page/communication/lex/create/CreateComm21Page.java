package fr.dila.solonepp.page.communication.lex.create;

import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.lex.detail.DetailComm21Page;
import fr.dila.solonepp.utils.DateUtils;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * classe de description de la page de création d'une communication LEX 24 (EVT21) : CMP - Convocation
 * @author mbd
 *
 */
public class CreateComm21Page extends AbstractCreateComm {
	
	  private static final String INPUT_DATE_ID = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateList_inputInputDate";
	  private static final String PLUS_ICONE_ID = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateList_addText";
	  private static final String DELETE_ICONE_PATH = "//*[@id='evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateList_delete%s']";
	  private int inputDateCounter = 0;
	  
	  private static final String EMETTEUR_GLOBAL_REGION_ID = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_emetteur_global_region";
	  private static final String DESTINATAIRE_GLOBAL_REGION_ID = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_destinataire_global_region";
	  private static final String COPIE_GLOBAL_REGION_PATH = "//*[@id='evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_copie_global_region']/table/tbody/tr/td";
	  
  
    public static final String TYPE_COMM = "LEX-24 : CMP - Convocation";
    public static final String DATE_CONVOCATION_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateList_inputInputDate";
    public static final String DATE_CONVOCATION_ADD = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateList_addText";
    public static final String DATE_CONVOCATION_ADDED = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateList_listItem";
        
    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";
    
    public void addDate(String date) {
        final WebElement elem = getDriver().findElement(By.id(DATE_CONVOCATION_INPUT));
        fillField("Date", elem, date);
        getFlog().actionClickButton("Ajout date");
        getDriver().findElement(By.id(DATE_CONVOCATION_ADD)).click();
    }
    
    @Override
    protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
        return DetailComm21Page.class;
    }
    
    public DetailComm21Page createComm21(String emetteur, String dateConvocation, String commentaire) {
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
        
        setCommentaire(commentaire);
        checkValue(COMMENTAIRE, commentaire);
        
        addDate(dateConvocation);
        
        addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
        
        return publier();
    }
    
    public DetailComm21Page createComm21(final String inputInputDate1, final String inputInputDate2, final String emetteur_global_region_value, final String destinataire_global_region_value, final String copie_global_region_value){
        inputDateCounter = 0;
        
        setInputDate(inputInputDate1);
        
        if(StringUtils.isNotBlank(inputInputDate2))
          setInputDate(inputInputDate2);
        
        if(StringUtils.isNotBlank(emetteur_global_region_value))
          checkValue(EMETTEUR_GLOBAL_REGION_ID, emetteur_global_region_value);
        
        if(StringUtils.isNotBlank(destinataire_global_region_value))
          checkValue(DESTINATAIRE_GLOBAL_REGION_ID, destinataire_global_region_value);
        
        if(StringUtils.isNotBlank(copie_global_region_value))
          checkValue(By_enum.XPATH, COPIE_GLOBAL_REGION_PATH, copie_global_region_value, Boolean.FALSE);
        
        
        return publier();
      }
    
    private void setInputDate(final String inputInputDateValue){
        getDriver().findElement(By.id(INPUT_DATE_ID)).sendKeys(inputInputDateValue);
        
        getDriver().findElement(By.id(PLUS_ICONE_ID)).click();
        
        new WebDriverWait(getDriver(), TIMEOUT_IN_SECONDS).until(ExpectedConditions.presenceOfElementLocated(By.xpath( String.format(DELETE_ICONE_PATH, inputDateCounter) )));
        inputDateCounter++;
        
      }

}

package fr.dila.solonepp.page.communication.pg.complete;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.pg.detail.DetailCommPG_04;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * 
 * @author abianchi
 * @description page d'édition pour PG_04
 *
 */
public class CompletionPG04Page extends AbstractCreateComm {
	
	public static final String AUTRES = "Autres(s)";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";
	
	public DetailCommPG_04 completePG04(String Commentaire, String suffrages, String pour, String contre, String abst)
	{
		getFlog().startAction("Complétion données PG04");
		
		//setIdentifiantCommunication
		setCommentaire(Commentaire);
		setSuffrages(suffrages);
		setPour(pour);
		SetContre(contre);
		setAbstentions(abst);
		addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
		
		
		DetailCommPG_04 temp = publier("La communication a été complétée");
        getFlog().endAction();
        return temp;
	}

	private void setAbstentions(String nb) {
		final WebElement elem = getDriver().findElement(By.id("evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_abstention"));
		fillField("Abstention(s)", elem, nb);	}

	private void SetContre(String nb) {
		final WebElement elem = getDriver().findElement(By.id("evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_voteContre"));
		fillField("Vote(s) contre", elem, nb);		
	}

	private void setPour(String nb) {
		final WebElement elem = getDriver().findElement(By.id("evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_votePour"));
		fillField("Vote(s) pour", elem, nb);		
	}

	private void setSuffrages(String nb) {
		final WebElement elem = getDriver().findElement(By.id("evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_suffrageExprime"));
		fillField("Nombre de suffrages exprimés", elem, nb);		
	}

	@Override
	protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
		return DetailCommPG_04.class;
	}
	
	
}

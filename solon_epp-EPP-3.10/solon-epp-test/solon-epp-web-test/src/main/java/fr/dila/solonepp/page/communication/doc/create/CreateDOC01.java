package fr.dila.solonepp.page.communication.doc.create;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.doc.detail.DetailCommDOC01;
import fr.sword.xsd.solon.epp.PieceJointeType;

public class CreateDOC01 extends AbstractCreateComm {

	private static final String DATE_INPUT = "evenement_metadonnees:nxl_metadonnees_version:nxw_metadonnees_version_dateLettrePmInputDate";
	private static final String IDENTIFIANT_DOSSIER = "evenement_metadonnees:nxl_metadonnees_evenement:nxw_metadonnees_evenement_identifiant_dossier";

	private static final String URL = "http://www.url.com";
	private static final String PJ = "/attachments/piece-jointe.doc";

	@Override
	protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
		return DetailCommDOC01.class;
	}

	public DetailCommDOC01 createCommeDOC01(String idDossier, String destinataire, String objet, String date) {

		getFlog().startAction("create communication DOC01");

		setIdDossier(idDossier);
		setObjet(objet);
		setDate(date);
		setDestinataire(destinataire);

		addPieceJointe(PieceJointeType.LETTRE_PM, URL, PJ);

		getFlog().endAction();

		return this.publier();

	}

	private void setDate(String date) {
		final WebElement elem = getDriver().findElement(By.id(DATE_INPUT));
		fillField("Date de la présentation", elem, date);
	}

	private void setIdDossier(String idDossier) {
		final WebElement elem = getDriver().findElement(By.id(IDENTIFIANT_DOSSIER));
		fillField("Identifiant dossier", elem, idDossier);
	}

}

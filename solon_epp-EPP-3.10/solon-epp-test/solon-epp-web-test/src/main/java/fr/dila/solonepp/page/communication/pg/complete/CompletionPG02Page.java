package fr.dila.solonepp.page.communication.pg.complete;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.pg.detail.DetailCommPG_02;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * 
 * @author abianchi
 * @description page d'édition pour PG_02
 *
 */
public class CompletionPG02Page extends AbstractCreateComm {
	
	public static final String AUTRES = "Autres(s)";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";
	
	public DetailCommPG_02 completePG02(String Commentaire)
	{
		getFlog().startAction("Complétion données PG02");
		
		//setIdentifiantCommunication
		setCommentaire(Commentaire);
		addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
		
		
		DetailCommPG_02 temp = publier("La communication a été complétée");
        getFlog().endAction();
        return temp;
	}

	@Override
	protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
		return DetailCommPG_02.class;
	}
	
	
}

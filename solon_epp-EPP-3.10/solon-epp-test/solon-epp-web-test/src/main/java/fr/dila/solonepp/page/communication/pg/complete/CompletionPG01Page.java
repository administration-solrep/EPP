package fr.dila.solonepp.page.communication.pg.complete;

import fr.dila.solonepp.page.communication.abstracts.AbstractCreateComm;
import fr.dila.solonepp.page.communication.abstracts.AbstractDetailComm;
import fr.dila.solonepp.page.communication.pg.detail.DetailCommPG_01;
import fr.sword.xsd.solon.epp.PieceJointeType;

/**
 * 
 * @author abianchi
 * @description page d'édition pour PG_01
 *
 */
public class CompletionPG01Page extends AbstractCreateComm {
	
	public static final String AUTRES = "Autres(s)";

    private static final String URL = "http://www.url.com";
    private static final String PJ = "/attachments/piece-jointe.doc";
	
	public DetailCommPG_01 completePG01(String Commentaire)
	{
		getFlog().startAction("Complétion données PG01");
		
		setCommentaire(Commentaire);
		addPieceJointe(PieceJointeType.AUTRE, URL, PJ);
		
		
		DetailCommPG_01 temp = publier("La communication a été complétée");
        getFlog().endAction();
        return temp;
	}

	@Override
	protected Class<? extends AbstractDetailComm> getCreateResultPageClass() {
		return DetailCommPG_01.class;
	}
	
	
}

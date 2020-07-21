package fr.dila.st.core.operation.organigramme;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;

import fr.dila.cm.cases.CaseConstants;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.api.organigramme.OrganigrammeType;
import fr.dila.st.api.organigramme.UniteStructurelleNode;
import fr.dila.st.api.service.organigramme.OrganigrammeService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;

@Operation(id = CheckDirectionsOperation.ID, category = CaseConstants.CASE_MANAGEMENT_OPERATION_CATEGORY, label = "CheckDirections", description = "Vérifie les liens ministères-directions")
public class CheckDirectionsOperation {
	/**
	 * Identifiant technique de l'opération.
	 */
	public static final String ID = "ST.Organigramme.CheckDirections";

	private static final STLogger LOGGER = STLogFactory.getLog(CheckDirectionsOperation.class);

	private static final String INFO_MODE = "info";
	private static final String FIX_MODE = "fix";

	private final OrganigrammeService organigrammeService = STServiceLocator.getOrganigrammeService();
	private final List<String> warningValues = new ArrayList<String>();
	private final List<OrganigrammeNode> nodesUpdated = new ArrayList<OrganigrammeNode>();

	@Context
	protected CoreSession session;

	/**
	 * Mode demandé : info ou fix
	 */
	@Param(name = "mode", required = false)
	protected String mode = INFO_MODE;

	/**
	 * Default constructor
	 */
	public CheckDirectionsOperation() {
		// do nothing
	}

	@OperationMethod
	public void run() throws Exception {
		LOGGER.info(session, STLogEnumImpl.LOG_INFO_TEC, "Lancement de la procédure de vérification des nors directions en mode " + mode + ".");
		List<? extends OrganigrammeNode> roots = organigrammeService.getRootNodes();
		for (OrganigrammeNode node : roots) {
			checkNode(session, node, null, null, null);
		}

		if (INFO_MODE.equalsIgnoreCase(mode)) {
			LOGGER.info(session, STLogEnumImpl.LOG_INFO_TEC, "Résumé : ");
			if (warningValues.isEmpty()) {
				LOGGER.info(session, STLogEnumImpl.LOG_INFO_TEC, "Aucun problème détecté.");
			} else {
				for (String value : warningValues) {
					LOGGER.info(session, STLogEnumImpl.LOG_INFO_TEC, value);
				}
			}
		} else if (FIX_MODE.equalsIgnoreCase(mode)) {
			if (nodesUpdated.isEmpty()) {
				LOGGER.info(session, STLogEnumImpl.LOG_INFO_TEC, "Aucun problème à corriger.");
			} else {
				organigrammeService.updateNodes(nodesUpdated, false);
			}
		}
	}

	private void checkNode(CoreSession session, OrganigrammeNode node, OrganigrammeType parentType,
			String lettreDirection, String idMinistere) throws ClientException {
		if (OrganigrammeType.DIRECTION.equals(node.getType())) {
			UniteStructurelleNode usNode = (UniteStructurelleNode) node;
			if (OrganigrammeType.MINISTERE.equals(parentType)) {
				lettreDirection = usNode.getNorDirectionForMinistereId(idMinistere);
				if (INFO_MODE.equalsIgnoreCase(mode)) {
					StringBuilder message = new StringBuilder("Visite de la direction ");
					message.append(node.getLabel()).append(" (id : ").append(node.getId()).append(")");
					message.append(" lettre de direction : ").append(lettreDirection);
					LOGGER.debug(session, STLogEnumImpl.LOG_INFO_TEC, message.toString());
				}
			} else {
				String actuelleLettreDirection = usNode.getNorDirectionForMinistereId(idMinistere);
				if (lettreDirection != null && !lettreDirection.equals(actuelleLettreDirection)) {
					if (INFO_MODE.equalsIgnoreCase(mode)) {
						StringBuilder message = new StringBuilder("La lettre direction ");
						message.append(actuelleLettreDirection).append(" n'est pas celle attendue (");
						message.append(lettreDirection).append(") pour la direction ");
						message.append(usNode.getLabel()).append("(id : ").append(usNode.getId());
						message.append(") pour le ministère d'id : ").append(idMinistere);
						LOGGER.debug(session, STLogEnumImpl.LOG_INFO_TEC, message.toString());
						warningValues.add(message.toString());
					} else if (FIX_MODE.equalsIgnoreCase(mode)) {
						StringBuilder message = new StringBuilder("Mise à jour direction ");
						message.append(usNode.getLabel()).append("(id : ").append(usNode.getId());
						message.append(") pour le ministère d'id : ").append(idMinistere);
						message.append(". Lettre direction ").append(StringUtils.isNotBlank(actuelleLettreDirection) ? actuelleLettreDirection:"(vide)");
						message.append(" devient ").append(lettreDirection);
						LOGGER.info(session, STLogEnumImpl.LOG_INFO_TEC, message.toString());
						usNode.setNorDirectionForMinistereId(idMinistere, lettreDirection);
						nodesUpdated.add(usNode);
					}
				}
			}
		} else if (OrganigrammeType.MINISTERE.equals(node.getType())) {
			if (INFO_MODE.equalsIgnoreCase(mode)) {
				StringBuilder message = new StringBuilder("Visite du ministère ");
				message.append(node.getLabel()).append(" (id : ").append(node.getId()).append(")");
				LOGGER.debug(session, STLogEnumImpl.LOG_INFO_TEC, message.toString());
			}
			idMinistere = node.getId();
		}

		List<OrganigrammeNode> children = organigrammeService.getChildrenList(session, node, true);
		for (OrganigrammeNode child : children) {
			checkNode(session, child, node.getType(), lettreDirection, idMinistere);
		}
	}
}

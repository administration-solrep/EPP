package fr.dila.solonepp.core.service;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppParametreConstant;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.CorbeilleTypeService;
import fr.dila.solonepp.api.service.MailboxInstitutionService;
import fr.dila.solonepp.api.service.corbeilletype.CorbeilleNode;
import fr.dila.solonepp.core.descriptor.corbeilletype.CorbeilleInstitutionDescriptor;
import fr.dila.solonepp.core.descriptor.corbeilletype.CorbeilleNodeDescriptor;
import fr.dila.solonepp.core.descriptor.corbeilletype.MessageGroupDescriptor;
import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STParametreService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.service.STServiceLocator;
import fr.sword.naiad.nuxeo.ufnxql.core.query.QueryUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Implémentation du service permettant de gérer les types de corbeilles.
 *
 * @author jtremeaux
 */
public class CorbeilleTypeServiceImpl extends DefaultComponent implements CorbeilleTypeService {
    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -288558198138336943L;

    /**
     * Logger surcouche socle de log4j
     */
    private static final STLogger LOGGER = STLogFactory.getLog(CorbeilleTypeServiceImpl.class);

    private static final String UNKNOWN_CORBEILLE = "Aucune corbeille n'est déclarée pour l'institution ";

    /**
     * Point d'extention des types de corbeilles.
     */
    public static final String CORBEILLE_TYPE_EXTENSION_POINT = "corbeilleType";

    /**
     * Tableau associatif des <institutions, types de corbeilles> contribués.
     */
    private Map<String, CorbeilleInstitutionDescriptor> corbeilleInstitutionMap;

    @Override
    public void activate(ComponentContext context) {
        corbeilleInstitutionMap = new HashMap<String, CorbeilleInstitutionDescriptor>();
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        if (extensionPoint.equals(CORBEILLE_TYPE_EXTENSION_POINT)) {
            CorbeilleInstitutionDescriptor descriptor = (CorbeilleInstitutionDescriptor) contribution;
            corbeilleInstitutionMap.put(descriptor.getInstitution(), descriptor);
        } else {
            throw new IllegalArgumentException("Unknown extension point: " + extensionPoint);
        }
    }

    @Override
    public List<CorbeilleNode> getCorbeilleInstitutionTree(String institution) {
        CorbeilleInstitutionDescriptor corbeilleInstitutionDescriptor = corbeilleInstitutionMap.get(institution);
        if (corbeilleInstitutionDescriptor == null) {
            throw new NuxeoException(UNKNOWN_CORBEILLE + institution);
        }

        List<CorbeilleNode> corbeilleNodeList = new ArrayList<CorbeilleNode>();
        addCorbeilleList(corbeilleNodeList, corbeilleInstitutionDescriptor.getCorbeilleNodeList());
        return corbeilleNodeList;
    }

    @Override
    public List<CorbeilleNode> getCorbeilleInstitutionTreeWithCount(String institution, CoreSession session) {
        CorbeilleInstitutionDescriptor corbeilleInstitutionDescriptor = corbeilleInstitutionMap.get(institution);
        if (corbeilleInstitutionDescriptor == null) {
            throw new NuxeoException(UNKNOWN_CORBEILLE + institution);
        }
        Calendar cal = null;

        final STParametreService paramService = STServiceLocator.getSTParametreService();
        String nbJour = null;
        try {
            nbJour = paramService.getParametreValue(session, SolonEppParametreConstant.NB_JOUR_MESSAGE_AFFICHABLE);
        } catch (NuxeoException e) {
            LOGGER.error(
                session,
                STLogEnumImpl.FAIL_GET_PARAM_TEC,
                SolonEppParametreConstant.NB_JOUR_MESSAGE_AFFICHABLE
            );
        }
        Integer nb = null;
        if (StringUtils.isNotBlank(nbJour)) {
            nb = Integer.parseInt(nbJour);
        } else {
            nb = 10;
        }
        if (nb != null) {
            cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, -nb);
        }

        /*
         * SELECT m.ecm:uuid as id FROM Message AS m , Evenement AS e , Version AS v WHERE m.cslk:caseDocumentId =
         * e.ecm:uuid AND m.cslk:activeVersionId = v.ecm:uuid AND m.cslk:corbeilleList = ? AND ((m.cslk:dateTraitement
         * >= ? AND m.cslk:etatMessage IN ('AR_RECU', 'EMIS', 'TRAITE')) OR m.cslk:etatMessage IN ('EN_ATTENTE_AR',
         * 'EN_COURS_REDACTION', 'EN_COURS_TRAITEMENT', 'NON_TRAITE')) AND m.ecm:parentId = ?
         */

        List<CorbeilleNode> corbeilleNodeList = new ArrayList<CorbeilleNode>();
        addCorbeilleList(corbeilleNodeList, corbeilleInstitutionDescriptor.getCorbeilleNodeList(), session, cal);
        return corbeilleNodeList;
    }

    /**
     * Construit l'arborescence des corbeilles.
     *
     * @param corbeilleNodeList
     *            Liste des sous-noeuds (construite par effet de bord)
     * @param corbeilleNodeDescriptorList
     *            Liste des descripteurs à ajouter
     */
    private void addCorbeilleList(
        List<CorbeilleNode> corbeilleNodeList,
        List<CorbeilleNodeDescriptor> corbeilleNodeDescriptorList
    ) {
        if (corbeilleNodeDescriptorList == null) {
            return;
        }

        for (CorbeilleNodeDescriptor corbeilleNodeDescriptor : corbeilleNodeDescriptorList) {
            CorbeilleNode corbeilleNode = new CorbeilleNode();
            corbeilleNodeList.add(corbeilleNode);
            corbeilleNode.setType(corbeilleNodeDescriptor.getType());
            corbeilleNode.setName(corbeilleNodeDescriptor.getName());
            corbeilleNode.setLabel(corbeilleNodeDescriptor.getLabel());
            corbeilleNode.setDescription(corbeilleNodeDescriptor.getDescription());
            if (corbeilleNode.isTypeCorbeille()) {
                corbeilleNode.setHiddenColumnList(corbeilleNodeDescriptor.getHiddenColumnList());
            }
            if (corbeilleNode.isTypeSection()) {
                List<CorbeilleNode> corbeilleSubNodeList = new ArrayList<CorbeilleNode>();
                addCorbeilleList(corbeilleSubNodeList, corbeilleNodeDescriptor.getCorbeilleNodeList());
                corbeilleNode.setCorbeilleNodeList(corbeilleSubNodeList);
            }
        }
    }

    /**
     * Construit l'arborescence des corbeilles.
     *
     * @param corbeilleNodeList
     *            Liste des sous-noeuds (construite par effet de bord)
     * @param corbeilleNodeDescriptorList
     *            Liste des descripteurs à ajouter
     */
    private void addCorbeilleList(
        List<CorbeilleNode> corbeilleNodeList,
        List<CorbeilleNodeDescriptor> corbeilleNodeDescriptorList,
        CoreSession session,
        Calendar dateDeTraitementMin
    ) {
        if (corbeilleNodeDescriptorList == null) {
            return;
        }

        for (CorbeilleNodeDescriptor corbeilleNodeDescriptor : corbeilleNodeDescriptorList) {
            CorbeilleNode corbeilleNode = new CorbeilleNode();
            corbeilleNodeList.add(corbeilleNode);
            corbeilleNode.setType(corbeilleNodeDescriptor.getType());
            corbeilleNode.setName(corbeilleNodeDescriptor.getName());
            corbeilleNode.setLabel(corbeilleNodeDescriptor.getLabel());
            corbeilleNode.setDescription(corbeilleNodeDescriptor.getDescription());

            String query =
                "SELECT m.ecm:uuid as id FROM Message AS m , Evenement AS e , Version AS v  WHERE  m.cslk:caseDocumentId = e.ecm:uuid " +
                "AND m.cslk:activeVersionId = v.ecm:uuid AND  m.cslk:corbeilleList = ? " +
                "AND ((m.cslk:dateTraitement >= ? AND m.cslk:etatMessage IN ('AR_RECU', 'EMIS', 'TRAITE')) " +
                "OR m.cslk:etatMessage IN ('EN_ATTENTE_AR', 'EN_COURS_REDACTION', 'EN_COURS_TRAITEMENT', 'NON_TRAITE'))" +
                "AND m.ecm:parentId = ?";

            try {
                EppPrincipal principal = (EppPrincipal) session.getPrincipal();
                String institutionId = principal.getInstitutionId();

                final MailboxInstitutionService mailboxInstitutionService = SolonEppServiceLocator.getMailboxInstitutionService();
                DocumentModel mailboxDoc = mailboxInstitutionService.getMailboxInstitution(session, institutionId);

                String count = "0";
                if (mailboxDoc != null) {
                    List<Object> paramList = new ArrayList<Object>();
                    paramList.add(corbeilleNode.getName());
                    paramList.add(dateDeTraitementMin);
                    paramList.add(mailboxDoc.getId());
                    count =
                        QueryUtils
                            .doCountQuery(session, QueryUtils.ufnxqlToFnxqlQuery(query), paramList.toArray())
                            .toString();
                }
                corbeilleNode.setCount(count);
            } catch (NuxeoException e) {
                LOGGER.error(session, STLogEnumImpl.FAIL_BUILD_CORBEILLE_TREE_TEC, e);
            }

            if (corbeilleNode.isTypeCorbeille()) {
                corbeilleNode.setHiddenColumnList(corbeilleNodeDescriptor.getHiddenColumnList());
            }
            if (corbeilleNode.isTypeSection()) {
                List<CorbeilleNode> corbeilleSubNodeList = new ArrayList<CorbeilleNode>();
                addCorbeilleList(
                    corbeilleSubNodeList,
                    corbeilleNodeDescriptor.getCorbeilleNodeList(),
                    session,
                    dateDeTraitementMin
                );
                corbeilleNode.setCorbeilleNodeList(corbeilleSubNodeList);
            }
        }
    }

    @Override
    public List<String> findCorbeilleDistribution(String institution, String messageType, String evenementType) {
        CorbeilleInstitutionDescriptor corbeilleInstitutionDescriptor = corbeilleInstitutionMap.get(institution);
        if (corbeilleInstitutionDescriptor == null) {
            throw new NuxeoException(UNKNOWN_CORBEILLE + institution);
        }

        List<String> corbeilleList = new ArrayList<String>();
        findCorbeilleDistribution(
            corbeilleList,
            corbeilleInstitutionDescriptor.getCorbeilleNodeList(),
            messageType,
            evenementType
        );

        return corbeilleList;
    }

    /**
     * Parcours récursivement l'arbre des corbeilles afin de recherche la liste des corbeilles dans lesquelles le
     * message est distribué.
     *
     * @param corbeilleList
     *            Liste des corbeilles (construite par effet de bord).
     * @param corbeilleNodeDescriptorList
     *            List des types de corbeilles à parcourir
     * @param institution
     *            Institution
     * @param messageType
     *            Type de message
     * @param evenementType
     *            Type d'événement
     */
    protected void findCorbeilleDistribution(
        List<String> corbeilleList,
        List<CorbeilleNodeDescriptor> corbeilleNodeDescriptorList,
        String messageType,
        String evenementType
    ) {
        for (CorbeilleNodeDescriptor corbeilleNodeDescriptor : corbeilleNodeDescriptorList) {
            if (SolonEppConstant.CORBEILLE_NODE_TYPE_SECTION.equals(corbeilleNodeDescriptor.getType())) {
                findCorbeilleDistribution(
                    corbeilleList,
                    corbeilleNodeDescriptor.getCorbeilleNodeList(),
                    messageType,
                    evenementType
                );
            } else if (SolonEppConstant.CORBEILLE_NODE_TYPE_CORBEILLE.equals(corbeilleNodeDescriptor.getType())) {
                if (isMessageDistributedInCorbeilleType(corbeilleNodeDescriptor, messageType, evenementType)) {
                    corbeilleList.add(corbeilleNodeDescriptor.getName());
                }
            } else {
                throw new NuxeoException("Type de corbeille inconnu : " + corbeilleNodeDescriptor.getType());
            }
        }
    }

    /**
     * Détermine si un message est inclus dans un type de corbeille.
     *
     * @param corbeilleNodeDescriptor
     *            Description du type de corbeille
     * @param messageType
     *            Type de message
     * @param evenementType
     *            Type d'événement
     * @return Condition
     */
    private boolean isMessageDistributedInCorbeilleType(
        CorbeilleNodeDescriptor corbeilleNodeDescriptor,
        String messageType,
        String evenementType
    ) {
        for (MessageGroupDescriptor messageGroupDescriptor : corbeilleNodeDescriptor.getMessageGroupList()) {
            Set<String> messageTypeSet = messageGroupDescriptor.getMessageTypeMap().keySet();
            Set<String> evenementTypeSet = messageGroupDescriptor.getEvenementTypeMap().keySet();

            if (messageTypeSet.contains(messageType) && evenementTypeSet.contains(evenementType)) {
                return true;
            }
            if (
                "EMETTEUR".equals(messageType) &&
                "EVT45".equals(evenementType) &&
                evenementTypeSet.contains(evenementType)
            ) {
                return true;
            }
        }
        return false;
    }
}

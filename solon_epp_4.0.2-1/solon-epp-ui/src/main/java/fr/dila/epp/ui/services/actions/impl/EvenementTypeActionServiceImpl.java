package fr.dila.epp.ui.services.actions.impl;

import static fr.dila.st.ui.enums.STContextDataKey.ID;

import fr.dila.epp.ui.services.actions.EvenementTypeActionService;
import fr.dila.solonepp.api.descriptor.evenementtype.EvenementTypeDescriptor;
import fr.dila.solonepp.api.domain.evenement.Evenement;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.EvenementTypeService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.ResourceHelper;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.bean.SelectValueGroupDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.NuxeoException;

public class EvenementTypeActionServiceImpl implements EvenementTypeActionService {
    private static final String AUTRES_COMMUNICATIONS = "Autres communications";
    private static final String COMMUNICATIONS_CONSEILLEES = "Communications conseillées";
    private static final String EVENEMENT_SUCCESSIF = "create.evenement.evenement.successif.recupertation.error";
    private static final String TYPE_EVENEMENT_VIDE = "create.evenement.type.evenement.vide";

    protected final List<String> ignoredFromSortItems = Arrays.asList("GENERIQUE", "FUSION", "ALERTE", "EVT53");

    @Override
    public List<SelectValueDTO> getEvenementTypeList() {
        ArrayList<SelectValueDTO> listEvenement = new ArrayList<>();
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        List<EvenementTypeDescriptor> eventTypeList = evenementTypeService.findEvenementType();

        for (EvenementTypeDescriptor eventType : eventTypeList) {
            SelectValueDTO item = new SelectValueDTO(eventType.getName(), eventType.getLabel());
            listEvenement.add(item);
        }
        sortEvent(listEvenement);
        return listEvenement;
    }

    @Override
    public List<SelectValueDTO> getEvenementTypeListFromCategory(SpecificContext context) {
        String categorieEvenementId = context.getFromContextData(ID);
        if (StringUtils.isBlank(categorieEvenementId)) {
            return getEvenementTypeList();
        }

        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        List<EvenementTypeDescriptor> eventTypeList = evenementTypeService.findEvenementByCategory(
            categorieEvenementId
        );
        if (eventTypeList == null) {
            throw new NuxeoException("Procédure non trouvée: " + categorieEvenementId);
        }

        List<SelectValueDTO> evenementTypeList = new ArrayList<>();
        for (EvenementTypeDescriptor eventType : eventTypeList) {
            SelectValueDTO item = new SelectValueDTO(eventType.getName(), eventType.getLabel());
            evenementTypeList.add(item);
        }

        return evenementTypeList;
    }

    @Override
    public List<SelectValueDTO> getEvenementCreateurList(SpecificContext context) {
        List<SelectValueDTO> list = new ArrayList<>();
        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        List<EvenementTypeDescriptor> eventTypeList = evenementTypeService.findEvenementTypeCreateur();

        buildListEvenement(list, eventTypeList, (EppPrincipal) context.getSession().getPrincipal());
        sortEvent(list);

        return list;
    }

    private void buildListEvenement(
        List<SelectValueDTO> list,
        List<EvenementTypeDescriptor> eventTypeList,
        EppPrincipal eppPrincipal
    ) {
        if (eventTypeList == null) {
            return;
        }
        for (EvenementTypeDescriptor eventType : eventTypeList) {
            // Ajout des evenements que si on est dans la liste des emetteurs
            if (
                eventType
                    .getDistribution()
                    .getEmetteur()
                    .getInstitution()
                    .keySet()
                    .contains(eppPrincipal.getInstitutionId())
            ) {
                SelectValueDTO item = new SelectValueDTO(eventType.getName(), eventType.getLabel());
                list.add(item);
            }
        }
    }

    private void sortEvent(List<SelectValueDTO> listEvenement) {
        // tri par id
        Collections.sort(
            listEvenement,
            new Comparator<SelectValueDTO>() {

                @Override
                public int compare(SelectValueDTO o1, SelectValueDTO o2) {
                    // les événements génériques toujours à la fin de la liste
                    String value1 = o1.getId();
                    String value2 = o2.getId();

                    if (inIgnoredList(value1) && !inIgnoredList(value2)) {
                        return 1;
                    }
                    if (inIgnoredList(value2) && !inIgnoredList(value1)) {
                        return -1;
                    }
                    return (value1).compareTo(value2);
                }
            }
        );
    }

    private boolean inIgnoredList(String value) {
        for (String str : ignoredFromSortItems) {
            if (value != null && value.trim().contains(str)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<SelectValueDTO> getEvenementSuccessifList(SpecificContext context) {
        String evenementType = null;
        if (context.getCurrentDocument() != null) {
            evenementType = context.getCurrentDocument().getAdapter(Evenement.class).getTypeEvenement();
        } else {
            String message = ResourceHelper.getString(TYPE_EVENEMENT_VIDE);
            context.getMessageQueue().addWarnToQueue(message);
            return Collections.emptyList();
        }

        final EvenementTypeService evenementTypeService = SolonEppServiceLocator.getEvenementTypeService();
        List<EvenementTypeDescriptor> eventTypeList;
        List<EvenementTypeDescriptor> eventConseilleTypeList = null;
        try {
            eventTypeList = evenementTypeService.findEvenementTypeSuccessifWithSameProcedure(evenementType);
            if (StringUtils.isNotBlank(evenementType)) {
                eventConseilleTypeList =
                    evenementTypeService.findEvenementTypeSuccessif(context.getSession(), evenementType);
                eventTypeList.removeAll(eventConseilleTypeList);
            }
        } catch (NuxeoException e) {
            String message = ResourceHelper.getString(EVENEMENT_SUCCESSIF);
            context.getMessageQueue().addWarnToQueue(message);
            return Collections.emptyList();
        }

        // tri par label
        Collections.sort(
            eventTypeList,
            new Comparator<EvenementTypeDescriptor>() {

                @Override
                public int compare(EvenementTypeDescriptor o1, EvenementTypeDescriptor o2) {
                    return o1.getName().compareTo(o2.getName());
                }
            }
        );

        EppPrincipal principal = (EppPrincipal) context.getSession().getPrincipal();

        List<SelectValueDTO> listConseille = new ArrayList<>();
        buildListEvenement(listConseille, eventConseilleTypeList, principal);

        List<SelectValueDTO> listAutre = new ArrayList<>();
        buildListEvenement(listAutre, eventTypeList, principal);

        SelectValueGroupDTO selectValueGroupSuivant = new SelectValueGroupDTO(
            COMMUNICATIONS_CONSEILLEES,
            listConseille
        );

        SelectValueGroupDTO selectValueGroupOthers = new SelectValueGroupDTO(AUTRES_COMMUNICATIONS, listAutre);

        List<SelectValueDTO> options = new ArrayList<>();
        options.add(selectValueGroupSuivant);
        options.add(selectValueGroupOthers);

        return options;
    }
}

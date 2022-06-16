package fr.dila.st.ui.th.bean.mapper;

import static fr.dila.st.ui.enums.WidgetTypeEnum.TEXT;
import static fr.dila.st.ui.enums.WidgetTypeEnum.URL;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.DESTINATAIRE;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.DESTINATAIRE_COPIE;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.DOSSIER;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.EMETTEUR;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.ID_DOSSIER;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.URL_BASE_LEGALE;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.URL_DOSSIER_AN;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.URL_DOSSIER_SENAT;
import static fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum.URL_PUBLICATION;
import static fr.dila.st.ui.enums.parlement.WidgetModeEnum.EDIT;
import static fr.dila.st.ui.enums.parlement.WidgetModeEnum.HIDDEN;
import static fr.dila.st.ui.enums.parlement.WidgetModeEnum.VIEW;
import static java.util.Optional.ofNullable;

import com.google.common.collect.ImmutableSet;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import fr.dila.st.core.requete.recherchechamp.Parametre;
import fr.dila.st.ui.bean.WidgetDTO;
import fr.dila.st.ui.enums.STContextDataKey;
import fr.dila.st.ui.enums.parlement.CommunicationMetadonneeEnum;
import fr.dila.st.ui.enums.parlement.WidgetModeEnum;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapPropertyDescToWidget {
    public static final String IS_EDIT_MODE = "isEditMode";
    public static final String LAYOUT_MODE_CREER = "edit";
    public static final String LAYOUT_MODE_COMPLETER = "completer";
    public static final String LAYOUT_MODE_RECTIFIER = "rectifier";
    private static final ImmutableSet<String> METADONNEES_NON_MODIFIABLES_RECT_COMP = ImmutableSet.of(
        EMETTEUR.getName(),
        DESTINATAIRE.getName(),
        DESTINATAIRE_COPIE.getName(),
        DOSSIER.getName(),
        ID_DOSSIER.getName()
    );
    private static final ImmutableSet<CommunicationMetadonneeEnum> METADONNEES_URL = ImmutableSet.of(
        URL_DOSSIER_AN,
        URL_DOSSIER_SENAT,
        URL_BASE_LEGALE,
        URL_PUBLICATION
    );

    private MapPropertyDescToWidget() {
        // ràf
    }

    public static WidgetDTO initWidget(
        PropertyDescriptor property,
        SpecificContext context,
        boolean canOnlyConsult,
        String typeChamp,
        String labelPrefix
    ) {
        WidgetDTO widget = new WidgetDTO();
        boolean isEditMode = isEditMode(context);
        WidgetModeEnum widgetMode = isEditMode ? getWidgetMode(property, context, canOnlyConsult) : VIEW;

        widget.setName(property.getName());
        widget.setLabel(labelPrefix + property.getName());
        widget.setTypeChamp(typeChamp);
        widget.setRequired(isEditMode && property.isObligatoire());

        List<Parametre> parametres = new ArrayList<>(
            Arrays.asList(
                new Parametre("nameWS", property.getNameWS()),
                new Parametre("modifiable", property.isModifiable()),
                new Parametre("multiValue", property.isMultiValue()),
                new Parametre("ficheDossier", property.isFicheDossier()),
                new Parametre("renseignerEpp", property.isRenseignerEpp()),
                new Parametre("institutions", property.getListInstitutions()),
                new Parametre("visibility", property.isVisibility()),
                new Parametre("defaultValue", property.getDefaultValue()),
                new Parametre("minDate", property.getMinDate()),
                new Parametre("hidden", property.isHidden()),
                new Parametre("widgetMode", widgetMode)
            )
        );

        widget.setParametres(parametres);
        return widget;
    }

    public static boolean isEditMode(SpecificContext context) {
        return (boolean) ofNullable(context.getFromContextData(IS_EDIT_MODE)).orElse(false);
    }

    private static boolean checkIfEditMode(boolean onlyConsult, String currentLayoutMode, PropertyDescriptor property) {
        //Si utilisateur du sénat et champ url an (ou l'inverse) on force à lecture
        if (onlyConsult) {
            return false;
        }

        //Si on est en rectification ou complétion et qu'il s'agit d'une propriété non modifiable (les champs d'envois) on est en lecture
        if (
            (LAYOUT_MODE_COMPLETER.equals(currentLayoutMode) || LAYOUT_MODE_RECTIFIER.equals(currentLayoutMode)) &&
            METADONNEES_NON_MODIFIABLES_RECT_COMP.contains(property.getName())
        ) {
            return false;
        }

        boolean edit = true;
        //Si on est en complétion, les champs obligatoire simples ne sont pas modifiables (car déjà renseignés avant)
        if (LAYOUT_MODE_COMPLETER.equals(currentLayoutMode) && property.isObligatoire() && !property.isMultiValue()) {
            edit = false;
        }

        return edit;
    }

    public static WidgetModeEnum getWidgetMode(
        PropertyDescriptor property,
        SpecificContext context,
        boolean onlyConsult
    ) {
        if (property.isHidden()) {
            return HIDDEN;
        }

        WidgetModeEnum widgetMode = VIEW;
        String currentLayoutMode = context.getFromContextData(STContextDataKey.MODE_CREATION);
        if (property.isModifiable() && !property.isRenseignerEpp()) {
            if (checkIfEditMode(onlyConsult, currentLayoutMode, property)) {
                widgetMode = EDIT;
            }
        } else if (LAYOUT_MODE_RECTIFIER.equals(currentLayoutMode) && property.isRenseignerEpp()) {
            widgetMode = HIDDEN;
        }

        return widgetMode;
    }

    public static String getTypeChamp(CommunicationMetadonneeEnum metaEnum, WidgetModeEnum widgetMode) {
        String typeChamp;
        if (EDIT.equals(widgetMode) && metaEnum != null) {
            typeChamp = metaEnum.getEditWidgetType().toString();
        } else {
            typeChamp = METADONNEES_URL.contains(metaEnum) ? URL.toString() : TEXT.toString();
        }
        return typeChamp;
    }
}

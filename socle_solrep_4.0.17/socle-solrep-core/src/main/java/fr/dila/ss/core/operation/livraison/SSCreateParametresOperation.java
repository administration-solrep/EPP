package fr.dila.ss.core.operation.livraison;

import static fr.dila.ss.api.constant.SSParametreConstant.ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME;
import static fr.dila.ss.api.constant.SSParametreConstant.ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME_DESCRIPTION;
import static fr.dila.ss.api.constant.SSParametreConstant.ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME_TITRE;
import static fr.dila.ss.api.constant.SSParametreConstant.ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME_UNIT;
import static fr.dila.ss.api.constant.SSParametreConstant.ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME_VALUE;
import static fr.dila.ss.api.constant.SSParametreConstant.ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME;
import static fr.dila.ss.api.constant.SSParametreConstant.ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME_DESCRIPTION;
import static fr.dila.ss.api.constant.SSParametreConstant.ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME_TITRE;
import static fr.dila.ss.api.constant.SSParametreConstant.ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME_UNIT;
import static fr.dila.ss.api.constant.SSParametreConstant.ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME_VALUE;
import static fr.dila.ss.api.constant.SSParametreConstant.CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME;
import static fr.dila.ss.api.constant.SSParametreConstant.CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME_DESCRIPTION;
import static fr.dila.ss.api.constant.SSParametreConstant.CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME_TITRE;
import static fr.dila.ss.api.constant.SSParametreConstant.CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME_UNIT;
import static fr.dila.ss.api.constant.SSParametreConstant.CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME_VALUE;
import static fr.dila.ss.api.constant.SSParametreConstant.CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME;
import static fr.dila.ss.api.constant.SSParametreConstant.CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME_DESCRIPTION;
import static fr.dila.ss.api.constant.SSParametreConstant.CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME_TITRE;
import static fr.dila.ss.api.constant.SSParametreConstant.CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME_UNIT;
import static fr.dila.ss.api.constant.SSParametreConstant.CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME_VALUE;
import static fr.dila.ss.api.constant.SSParametreConstant.ERROR_MESSAGE_MAIL_EXPORT_NAME;
import static fr.dila.ss.api.constant.SSParametreConstant.ERROR_MESSAGE_MAIL_EXPORT_NAME_DESCRIPTION;
import static fr.dila.ss.api.constant.SSParametreConstant.ERROR_MESSAGE_MAIL_EXPORT_NAME_TITRE;
import static fr.dila.ss.api.constant.SSParametreConstant.ERROR_MESSAGE_MAIL_EXPORT_NAME_UNIT;
import static fr.dila.ss.api.constant.SSParametreConstant.ERROR_MESSAGE_MAIL_EXPORT_NAME_VALUE;
import static fr.dila.ss.api.constant.SSParametreConstant.EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME;
import static fr.dila.ss.api.constant.SSParametreConstant.EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME_DESCRIPTION;
import static fr.dila.ss.api.constant.SSParametreConstant.EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME_TITRE;
import static fr.dila.ss.api.constant.SSParametreConstant.EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME_UNIT;
import static fr.dila.ss.api.constant.SSParametreConstant.EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME_VALUE;
import static fr.dila.ss.api.constant.SSParametreConstant.OBJET_MAIL_EXPORT_NAME;
import static fr.dila.ss.api.constant.SSParametreConstant.OBJET_MAIL_EXPORT_NAME_DESCRIPTION;
import static fr.dila.ss.api.constant.SSParametreConstant.OBJET_MAIL_EXPORT_NAME_TITRE;
import static fr.dila.ss.api.constant.SSParametreConstant.OBJET_MAIL_EXPORT_NAME_UNIT;
import static fr.dila.ss.api.constant.SSParametreConstant.OBJET_MAIL_EXPORT_NAME_VALUE;

import com.google.common.collect.ImmutableList;
import fr.dila.st.api.constant.STConstant;
import fr.dila.st.core.operation.STApplication;
import fr.dila.st.core.operation.STVersion;
import fr.dila.st.core.operation.utils.AbstractCreateParametersOperation;
import fr.dila.st.core.operation.utils.ParametreBean;
import java.util.List;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;

/**
 * Operation pour ajouter des nouveaux paramètres dans les applications qui dépendent de Socle Solrep
 *
 */
@STVersion(version = "4.0.0", application = STApplication.EPG)
@STVersion(version = "4.0.3", application = STApplication.REPONSES)
@Operation(id = SSCreateParametresOperation.ID, category = STConstant.PARAMETRE_DOCUMENT_TYPE)
public class SSCreateParametresOperation extends AbstractCreateParametersOperation {
    public static final String ID = "SS.Parametre.Creation";

    @Context
    private OperationContext context;

    public SSCreateParametresOperation() {}

    @Override
    protected List<ParametreBean> getParametreBeans() {
        return ImmutableList.of(
            new ParametreBean(
                EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME,
                EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME_TITRE,
                EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME_DESCRIPTION,
                EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME_UNIT,
                EXPORTS_NOMBRE_MAX_DOSSIERS_PARAMETER_NAME_VALUE
            ),
            new ParametreBean(
                OBJET_MAIL_EXPORT_NAME,
                OBJET_MAIL_EXPORT_NAME_TITRE,
                OBJET_MAIL_EXPORT_NAME_DESCRIPTION,
                OBJET_MAIL_EXPORT_NAME_UNIT,
                OBJET_MAIL_EXPORT_NAME_VALUE
            ),
            new ParametreBean(
                CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME,
                CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME_TITRE,
                CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME_DESCRIPTION,
                CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME_UNIT,
                CORPS_ERROR_TEMPLATE_MAIL_EXPORT_NAME_VALUE
            ),
            new ParametreBean(
                CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME,
                CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME_TITRE,
                CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME_DESCRIPTION,
                CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME_UNIT,
                CORPS_SUCCESS_TEMPLATE_MAIL_EXPORT_NAME_VALUE
            ),
            new ParametreBean(
                ERROR_MESSAGE_MAIL_EXPORT_NAME,
                ERROR_MESSAGE_MAIL_EXPORT_NAME_TITRE,
                ERROR_MESSAGE_MAIL_EXPORT_NAME_DESCRIPTION,
                ERROR_MESSAGE_MAIL_EXPORT_NAME_UNIT,
                ERROR_MESSAGE_MAIL_EXPORT_NAME_VALUE
            ),
            new ParametreBean(
                ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME,
                ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME_TITRE,
                ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME_DESCRIPTION,
                ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME_UNIT,
                ALERTES_OBJET_MAIL_LIMITE_PARAMETER_NAME_VALUE
            ),
            new ParametreBean(
                ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME,
                ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME_TITRE,
                ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME_DESCRIPTION,
                ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME_UNIT,
                ALERTES_TEXTE_MAIL_LIMITE_PARAMETER_NAME_VALUE
            )
        );
    }

    @Override
    protected OperationContext getContext() {
        return context;
    }
}

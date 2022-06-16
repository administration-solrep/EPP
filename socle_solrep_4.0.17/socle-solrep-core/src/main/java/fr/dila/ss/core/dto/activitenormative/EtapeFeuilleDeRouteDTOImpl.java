package fr.dila.ss.core.dto.activitenormative;

import fr.dila.ss.api.constant.SSFeuilleRouteConstant;
import fr.dila.ss.api.dto.EtapeFeuilleDeRouteDTO;
import fr.dila.ss.api.feuilleroute.SSRouteStep;
import fr.dila.st.core.client.AbstractMapDTO;
import java.util.UUID;

public class EtapeFeuilleDeRouteDTOImpl extends AbstractMapDTO implements EtapeFeuilleDeRouteDTO {
    private static final long serialVersionUID = 1L;

    public EtapeFeuilleDeRouteDTOImpl() {
        this.setId(UUID.randomUUID().toString());
    }

    public EtapeFeuilleDeRouteDTOImpl(SSRouteStep routeStep) {
        if (routeStep != null) {
            if (routeStep.getDocument().getId() == null) {
                setId(UUID.randomUUID().toString());
            } else {
                setId(routeStep.getDocument().getId());
            }

            if (routeStep.getDeadLine() != null) {
                this.setDeadLine(routeStep.getDeadLine());
            }
        }
    }

    @Override
    public String getDocIdForSelection() {
        return getId();
    }

    @Override
    public String getType() {
        return "RouteStep";
    }

    @Override
    public String getTypeEtape() {
        return getString(SSFeuilleRouteConstant.ROUTING_TASK_TYPE_PROPERTY);
    }

    @Override
    public void setTypeEtape(String typeEtape) {
        put(SSFeuilleRouteConstant.ROUTING_TASK_TYPE_PROPERTY, typeEtape);
    }

    @Override
    public String getDeadLine() {
        return (String) get(SSFeuilleRouteConstant.ROUTING_TASK_DEADLINE_PROPERTY);
    }

    @Override
    public void setDeadLine(Long echeanceIndicative) {
        put(SSFeuilleRouteConstant.ROUTING_TASK_DEADLINE_PROPERTY, echeanceIndicative);
    }

    @Override
    public Boolean getObligatoireSGG() {
        return getBoolean(SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY);
    }

    @Override
    public void setObligatoireSGG(Boolean validationSGG) {
        put(SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_SGG_PROPERTY, validationSGG);
    }

    @Override
    public Boolean getObligatoireMinistere() {
        return getBoolean(SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY);
    }

    @Override
    public void setObligatoireMinistere(Boolean obligatoireMinistere) {
        put(SSFeuilleRouteConstant.ROUTING_TASK_OBLIGATOIRE_MINISTERE_PROPERTY, obligatoireMinistere);
    }

    @Override
    public String getId() {
        return getString(SSFeuilleRouteConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY);
    }

    @Override
    public void setId(String id) {
        put(SSFeuilleRouteConstant.ROUTING_TASK_DOCUMENT_ROUTE_ID_PROPERTY, id);
    }

    @Override
    public String toString() {
        return this.getId();
    }

    @Override
    public String getDistributionMailboxId() {
        return getString(SSFeuilleRouteConstant.ROUTING_TASK_MAILBOX_ID_PROPERTY);
    }

    @Override
    public void setDistributionMailboxId(String distributionMailboxId) {
        put(SSFeuilleRouteConstant.ROUTING_TASK_MAILBOX_ID_PROPERTY, distributionMailboxId);
    }

    @Override
    public Boolean getAutomaticValidation() {
        return getBoolean(SSFeuilleRouteConstant.ROUTING_TASK_AUTOMATIC_VALIDATION_PROPERTY);
    }

    @Override
    public void setAutomaticValidation(Boolean validationAutomatique) {
        put(SSFeuilleRouteConstant.ROUTING_TASK_AUTOMATIC_VALIDATION_PROPERTY, validationAutomatique);
    }
}

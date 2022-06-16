package fr.dila.ss.api.dto;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;

public interface EtapeFeuilleDeRouteDTO extends Map<String, Serializable> {
    String getId();
    void setId(String id);

    String getTypeEtape();
    void setTypeEtape(String typeEtape);

    String getDistributionMailboxId();
    void setDistributionMailboxId(String distributionMailboxId);

    Boolean getAutomaticValidation();
    void setAutomaticValidation(Boolean validationAutomatique);

    Boolean getObligatoireSGG();
    void setObligatoireSGG(Boolean validationSGG);

    Boolean getObligatoireMinistere();
    void setObligatoireMinistere(Boolean obligatoireMinistere);

    String getDeadLine();
    void setDeadLine(Long deadLine);

    public static Comparator<EtapeFeuilleDeRouteDTO> COMPARE = new Comparator<EtapeFeuilleDeRouteDTO>() {

        @Override
        public int compare(EtapeFeuilleDeRouteDTO one, EtapeFeuilleDeRouteDTO two) {
            return one.getId().compareTo(two.getId());
        }
    };
}

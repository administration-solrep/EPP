package fr.dila.st.ui.services.mail;

import fr.dila.st.api.constant.STConfigConstants;
import fr.dila.st.api.constant.STParametreConstant;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.ui.bean.SelectValueDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class MailUIServiceImpl implements MailUIService {

    @Override
    public List<SelectValueDTO> retrieveAdresseEmissionValues(SpecificContext context) {
        List<SelectValueDTO> adresseEmissionValues = new ArrayList<>();
        adresseEmissionValues.add(new SelectValueDTO("", "Sélectionner une valeur"));

        String paramValue = STServiceLocator
            .getSTParametreService()
            .getParametreValue(context.getSession(), STParametreConstant.ADRESSE_MEL_EMISSION_PARAMETER_NAME);

        if (StringUtils.isNotEmpty(paramValue)) {
            adresseEmissionValues.addAll(
                Arrays.asList(paramValue.split(";")).stream().map(SelectValueDTO::new).collect(Collectors.toList())
            );
        } else {
            // On ajoute seulement mail.from
            adresseEmissionValues.add(
                new SelectValueDTO(STServiceLocator.getConfigService().getValue(STConfigConstants.MAIL_FROM))
            );
        }
        return adresseEmissionValues;
    }
}

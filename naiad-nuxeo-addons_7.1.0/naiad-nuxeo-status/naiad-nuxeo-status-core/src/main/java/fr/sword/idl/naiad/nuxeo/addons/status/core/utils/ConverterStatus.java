package fr.sword.idl.naiad.nuxeo.addons.status.core.utils;

import org.nuxeo.ecm.core.convert.api.ConversionService;
import org.nuxeo.ecm.core.convert.api.ConverterCheckResult;

import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo;
import fr.sword.idl.naiad.nuxeo.addons.status.api.model.ResultInfo.ResultEnum;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

public class ConverterStatus extends AbstractParamStatusInfo {

    @Override
    public Object getStatusInfo() {
        final ResultInfo resultInfo = new ResultInfo();
        try {
            final ConversionService conversionService = ServiceUtil.getRequiredService(ConversionService.class);

            final ConverterCheckResult converterCheckResult = conversionService.isConverterAvailable(params);
            if (!converterCheckResult.isAvailable()) {
                resultInfo.setStatut(ResultEnum.KO);
                resultInfo.setDescription(params + " non disponible");
            }

        } catch (final Exception e) {
            resultInfo.setStatut(ResultEnum.KO);
        }

        return resultInfo;
    }

}

package fr.dila.st.ui.mapper;

import fr.dila.st.api.service.FonctionService;
import fr.dila.st.api.user.BaseFunction;
import fr.dila.st.core.service.STServiceLocator;
import fr.dila.st.core.util.ObjectHelper;
import fr.dila.st.ui.bean.SelectValueDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.nuxeo.ecm.core.api.DocumentModel;

public class MapDoc2BeanListFunctionProcess implements MapDoc2BeanProcess {

    public MapDoc2BeanListFunctionProcess() {
        // Nothing to do
    }

    @Override
    @SuppressWarnings("unchecked")
    public BiFunction<MapField, Object, Serializable> bean2DocMapper() {
        return (mfield, value) ->
            ObjectHelper
                .requireNonNullElseGet((List<SelectValueDTO>) value, Collections::<SelectValueDTO>emptyList)
                .stream()
                .map(SelectValueDTO::getId)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @SuppressWarnings("unchecked")
    public BiFunction<MapField, DocumentModel, Serializable> doc2BeanMapper() {
        return (mfield, doc) ->
            convertBaseFunctionListToDTOList((List<String>) doc.getPropertyValue(mfield.getNxprop().xpath()));
    }

    private ArrayList<SelectValueDTO> convertBaseFunctionListToDTOList(List<String> baseFunctionList) {
        FonctionService service = STServiceLocator.getFonctionService();
        return baseFunctionList
            .stream()
            .map(
                fonctionName -> {
                    BaseFunction fonction = service.getFonction(fonctionName);
                    return new SelectValueDTO(
                        fonctionName,
                        Optional.ofNullable(fonction).map(BaseFunction::getDescription).orElse(fonctionName)
                    );
                }
            )
            .sorted(Comparator.comparing(SelectValueDTO::getLabel))
            .collect(Collectors.toCollection(ArrayList::new));
    }
}

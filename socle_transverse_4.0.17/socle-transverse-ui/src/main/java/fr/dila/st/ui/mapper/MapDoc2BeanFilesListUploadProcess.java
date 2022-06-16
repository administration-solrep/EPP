package fr.dila.st.ui.mapper;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.nuxeo.ecm.automation.server.jaxrs.batch.Batch;
import org.nuxeo.ecm.automation.server.jaxrs.batch.BatchManager;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Map a batch id of nuxeo upload api to a List<Map<String, Serializable>> to be
 * included in files:files.
 *
 * @author olejacques
 *
 */
public class MapDoc2BeanFilesListUploadProcess implements MapDoc2BeanProcess {

    public MapDoc2BeanFilesListUploadProcess() {
        // Nothing to do
    }

    @Override
    public BiFunction<MapField, Object, Serializable> bean2DocMapper() {
        return (mfield, value) -> {
            if (CollectionUtils.isNotEmpty((List) value)) {
                BatchManager batchManager = ServiceUtil.getRequiredService(BatchManager.class);
                for (Object val : (List) value) {
                    Batch batch = batchManager.getBatch((String) val);
                    if (batch != null) {
                        List<Blob> files = batch.getBlobs();
                        return files
                            .stream()
                            .map(v -> Collections.singletonMap(STSchemaConstant.FILE_SCHEMA, (Serializable) v))
                            .collect(Collectors.toCollection(ArrayList::new));
                    }
                }
            }
            return new ArrayList<>();
        };
    }

    @Override
    public BiFunction<MapField, DocumentModel, Serializable> doc2BeanMapper() {
        return (mfield, doc) -> "";
    }
}

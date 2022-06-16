package fr.dila.st.ui.mapper;

import static fr.dila.st.core.service.STServiceLocator.getDownloadService;

import fr.dila.st.api.constant.STSchemaConstant;
import fr.dila.st.core.util.FileUtils;
import fr.dila.st.ui.bean.DocumentDTO;
import fr.sword.naiad.nuxeo.commons.core.util.FileUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Convert a files:files property to a list of {@link DocumentDTO}.
 *
 * @author olejacques
 *
 */
public class MapDoc2BeanFilesFilesProcess implements MapDoc2BeanProcess {

    public MapDoc2BeanFilesFilesProcess() {
        // Nothing to do
    }

    @Override
    public BiFunction<MapField, Object, Serializable> bean2DocMapper() {
        return (mfield, value) -> new ArrayList<>();
    }

    @Override
    @SuppressWarnings("unchecked")
    public BiFunction<MapField, DocumentModel, Serializable> doc2BeanMapper() {
        return (mfield, doc) -> {
            String xpath = mfield.getNxprop().xpath();
            List<Map<String, Blob>> files = (List<Map<String, Blob>>) doc.getPropertyValue(mfield.getNxprop().xpath());
            return IntStream
                .range(0, files.size())
                .boxed()
                .map(i -> toDocumentDTO(doc, xpath, files.get(i).get(STSchemaConstant.FILE_SCHEMA), i))
                .collect(Collectors.toCollection(ArrayList::new));
        };
    }

    private DocumentDTO toDocumentDTO(DocumentModel doc, String xpath, Blob blob, int index) {
        DocumentDTO documentDTO = new DocumentDTO();

        String filename = blob.getFilename();
        documentDTO.setLink(
            getDownloadService()
                .getDownloadUrl(
                    doc,
                    String.join("/", xpath, String.valueOf(index), STSchemaConstant.FILE_SCHEMA),
                    filename
                )
        );
        documentDTO.setNom(FileUtils.trimExtension(filename));
        documentDTO.setExtension("." + FileUtil.getExtension(filename));

        return documentDTO;
    }
}

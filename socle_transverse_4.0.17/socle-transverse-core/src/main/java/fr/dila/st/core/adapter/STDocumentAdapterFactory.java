package fr.dila.st.core.adapter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.api.adapter.DocumentAdapterFactory;

public interface STDocumentAdapterFactory extends DocumentAdapterFactory {
    default void checkDocumentSchemas(DocumentModel doc, String... schemas) {
        Stream
            .of(schemas)
            .forEach(
                schema -> {
                    if (!doc.hasSchema(schema)) {
                        throw new NuxeoException(
                            "Le document avec l'id " +
                            doc.getId() +
                            " doit contenir le schéma : " +
                            schema +
                            ". Liste des schémas du document : " +
                            Arrays.toString(doc.getSchemas())
                        );
                    }
                }
            );
    }

    default void checkDocumentFacet(DocumentModel doc, String facet) {
        if (!doc.hasFacet(facet)) {
            throw new NuxeoException(
                "Le document avec l'id " +
                doc.getId() +
                " doit contenir la facette : " +
                facet +
                ". Liste des facettes du document : " +
                doc.getFacets()
            );
        }
    }

    default void checkDocumentType(DocumentModel doc, String docType) {
        if (StringUtils.isBlank(docType)) {
            throw new IllegalArgumentException("Le type du document " + doc.getId() + " ne peut pas être vide");
        }

        if (!docType.equalsIgnoreCase(doc.getType())) {
            throw new NuxeoException(
                "Le document avec l'id " +
                doc.getId() +
                " doit être du type : " +
                docType +
                ". Type du document : " +
                doc.getType()
            );
        }
    }

    default void checkDocumentTypes(DocumentModel doc, List<String> docTypes) {
        if (CollectionUtils.isEmpty(docTypes)) {
            throw new IllegalArgumentException(
                "La liste des types du document " + doc.getId() + " ne peut pas être vide"
            );
        }

        if (!docTypes.contains(doc.getType())) {
            throw new NuxeoException(
                "Le document avec l'id " +
                doc.getId() +
                " doit avoir un de ces types : " +
                docTypes +
                ". Type du document : " +
                doc.getType()
            );
        }
    }
}

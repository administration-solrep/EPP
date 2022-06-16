package fr.dila.st.core.jointure;

import fr.dila.st.core.query.ufnxql.UFNXQLQueryAssembler.Emplacement;
import java.util.List;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Le descripteur pour une correspondance, utilisé dans le service jointure pour représenter une équivalence entre un
 * document et le moyen de l'obtenir par une jointure UFNXQL.
 *
 * @author jgomez
 *
 */
@XObject("correspondence")
public class CorrespondenceDescriptor {
    @XNode("@document")
    private String document;

    @XNode("@doc_prefix")
    private String docPrefix;

    @XNode(value = "@query_part", trim = false)
    private String queryPart;

    @XNode("@emplacement")
    private String emplacement;

    private List<CorrespondenceDescriptor> dependencies;

    /**
     * Default constructor
     */
    public CorrespondenceDescriptor() {
        // do nothing
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDocPrefix() {
        return docPrefix;
    }

    public void setDocPrefix(String docPrefix) {
        this.docPrefix = docPrefix;
    }

    public String getQueryPart() {
        return queryPart;
    }

    public void setQueryPart(String queryPart) {
        this.queryPart = queryPart;
    }

    public Emplacement getEmplacement() {
        Emplacement enum_emplacement = Emplacement.valueOf(emplacement);
        return enum_emplacement;
    }

    public void setEmplacement(String emplacement) {
        this.emplacement = emplacement;
    }

    public void setDependencies(List<CorrespondenceDescriptor> dependencies) {
        this.dependencies = dependencies;
    }

    public void addDependency(CorrespondenceDescriptor c) {
        this.dependencies.add(c);
    }

    public List<CorrespondenceDescriptor> getDependencies() {
        return dependencies;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof CorrespondenceDescriptor) {
            CorrespondenceDescriptor other_correspondence = (CorrespondenceDescriptor) obj;
            return (
                this.getDocPrefix().equals(other_correspondence.getDocPrefix()) &&
                this.getEmplacement().equals(other_correspondence.getEmplacement())
            );
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 13;
        final int multiplier = 31;
        // Pour chaque attribut, on calcule le hashcode
        // que l'on ajoute au résultat après l'avoir multiplié
        // par le nombre "multiplieur" :
        result = multiplier * result + (docPrefix == null ? 0 : docPrefix.hashCode());
        result = multiplier * result + (emplacement == null ? 0 : emplacement.hashCode());

        // On retourne le résultat :
        return result;
    }
}

package fr.dila.st.core.service;

import fr.dila.st.api.recherche.QueryAssembler;
import fr.dila.st.api.service.JointureService;
import fr.dila.st.core.jointure.CorrespondenceDescriptor;
import fr.dila.st.core.jointure.DependencyDescriptor;
import fr.dila.st.core.jointure.QueryAssemblerDescriptor;
import fr.dila.st.core.query.ufnxql.UFNXQLQueryAssembler;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.DefaultComponent;

public class STJointureServiceImpl extends DefaultComponent implements JointureService {
    private static final Log LOGGER = LogFactory.getLog(STJointureServiceImpl.class);

    private static final String CORRESPONDENCES_EP = "correspondences";

    private static final String QUERYASSEMBLER_EP = "queryassembler";

    private static final String DEPENDENCIES_EP = "dependencies";

    /**
     * Une map de queryAssembler, associés au nom de contribution qui les déclarent
     */
    protected Map<String, UFNXQLQueryAssembler> queryAssemblerMap;

    /**
     * Default constructor
     */
    public STJointureServiceImpl() {
        super();
    }

    @Override
    public void activate(ComponentContext context) {
        queryAssemblerMap = new HashMap<String, UFNXQLQueryAssembler>();
    }

    @Override
    public void deactivate(ComponentContext context) {
        queryAssemblerMap = null;
    }

    @Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) {
        String contributorName = contributor.getName().getName();
        if (QUERYASSEMBLER_EP.equals(extensionPoint)) {
            QueryAssemblerDescriptor desc = (QueryAssemblerDescriptor) contribution;
            UFNXQLQueryAssembler assembler = null;
            try {
                assembler = desc.getKlass().newInstance();
                assembler.setIsDefault(desc.getIsDefault());
            } catch (InstantiationException e) {
                LOGGER.error("Assembler de classe " + desc.getKlass() + "non trouvé");
                throw new IllegalArgumentException("Assembler de classe " + desc.getKlass() + "non trouvé");
            } catch (IllegalAccessException e) {
                LOGGER.error("Assembler de classe " + desc.getKlass() + "non trouvé");
                throw new IllegalArgumentException("Problème d'accès à l'assembler de classe " + desc.getKlass());
            }
            setQueryAssembler(contributorName, assembler);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(
                    "Ajout du query assembler " + contributorName + " : de classe " + assembler.getClass().toString()
                );
                LOGGER.debug("Utilisation de l'objet assembler de classe = " + assembler.getClass());
            }
        }
        if (CORRESPONDENCES_EP.equals(extensionPoint) && getQueryAssembler(contributorName) != null) {
            CorrespondenceDescriptor correspondence = (CorrespondenceDescriptor) contribution;
            correspondence.setDependencies(new ArrayList<CorrespondenceDescriptor>());
            UFNXQLQueryAssembler queryAssembler = (UFNXQLQueryAssembler) getQueryAssembler(contributorName);
            queryAssembler.add((CorrespondenceDescriptor) contribution);
        }
        if (DEPENDENCIES_EP.equals(extensionPoint) && getQueryAssembler(contributorName) != null) {
            UFNXQLQueryAssembler queryAssembler = (UFNXQLQueryAssembler) getQueryAssembler(contributorName);
            queryAssembler.build((DependencyDescriptor) contribution);
        }
    }

    @Override
    public QueryAssembler getQueryAssembler(String name) {
        return queryAssemblerMap.get(name);
    }

    @Override
    public QueryAssembler getDefaultQueryAssembler() {
        for (QueryAssembler assembler : queryAssemblerMap.values()) {
            if (assembler.getIsDefault()) {
                return assembler;
            }
        }
        return null;
    }

    public void setQueryAssembler(String name, UFNXQLQueryAssembler assembler) {
        queryAssemblerMap.put(name, assembler);
    }
}

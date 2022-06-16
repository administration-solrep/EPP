package fr.dila.st.core.operation.version;

import static fr.dila.st.core.operation.STApplication.ANY;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.operation.STApplication;
import fr.dila.st.core.operation.STVersion;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.nuxeo.ecm.automation.AutomationService;
import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.OperationException;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.core.annotations.Param;
import org.nuxeo.ecm.core.api.CoreSession;
import org.reflections.Reflections;

@Operation(
    id = OperationsVersionExecuterOperation.ID,
    category = "Operation",
    label = "OperationsVersionExecuter",
    description = "Gère les opérations à lancer par rapport à une version spécifiée"
)
public class OperationsVersionExecuterOperation {
    public static final String ID = "ST.operations.execute";
    private static final STLogger LOGGER = STLogFactory.getLog(OperationsVersionExecuterOperation.class);

    @Param(name = "application", description = "L'application des opérations à éxécuter")
    private String application;

    @Param(name = "version", description = "La version des opérations à éxecuter")
    private String version;

    @Context
    private CoreSession session;

    @OperationMethod
    public void run() throws OperationException {
        Reflections reflections = new Reflections("fr.dila");

        Predicate<Class<?>> hasOperationAnnotation = operation -> operation.isAnnotationPresent(Operation.class);
        Predicate<Class<?>> isEligible = operation ->
            isEligible(operation, version, STApplication.fromString(application));

        List<String> operationsIds = Stream
            .of(
                reflections.getTypesAnnotatedWith(STVersion.class),
                reflections.getTypesAnnotatedWith(STVersion.List.class)
            )
            .flatMap(Set::stream)
            .filter(hasOperationAnnotation.and(isEligible))
            .map(operation -> operation.getAnnotation(Operation.class).id())
            .collect(Collectors.toList());

        AutomationService automationService = ServiceUtil.getRequiredService(AutomationService.class);
        LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, "Ces opérations " + operationsIds + " vont être éxécutées");

        for (String opeId : operationsIds) {
            try (OperationContext ctx = new OperationContext(session)) {
                LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, "Exécution de l'opération [" + opeId + "]");
                automationService.run(ctx, opeId, Collections.emptyMap());
                LOGGER.info(STLogEnumImpl.LOG_INFO_TEC, "Fin de l'éxécution de l'opération [" + opeId + "]");
            }
        }
    }

    protected boolean isEligible(Class<?> operation, String version, STApplication application) {
        List<STVersion> stoperations = Arrays.asList(
            Optional
                .ofNullable(operation.getAnnotation(STVersion.List.class))
                .map(STVersion.List::value)
                .orElseGet(() -> new STVersion[] { operation.getAnnotation(STVersion.class) })
        );
        return stoperations
            .stream()
            .anyMatch(
                o -> (o.application().equals(application) || o.application() == ANY) && o.version().equals(version)
            );
    }
}

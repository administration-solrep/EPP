package fr.dila.solonepp.core.service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.EvenementAssemblerService;
import fr.dila.solonepp.core.assembler.ws.evenement.BaseAssembler;
import fr.sword.xsd.solon.epp.EppEvtContainer;
import fr.sword.xsd.solon.epp.EvenementType;

/**
 * 
 * @author admin
 * 
 */
public class EvenementAssemblerServiceImpl extends DefaultComponent implements EvenementAssemblerService {

    private static final long serialVersionUID = -4591087662975851326L;

    private Map<String, Class<? extends Assembler>> typeEvenementMap;

    private final Pattern NUMBER_PATTERN = Pattern.compile("\\d+");
//    private final Pattern CHAR_PATTERN = Pattern.compile("\\w+");

    @Override
    public void activate(final ComponentContext context) throws ClientException, ClassNotFoundException {
        typeEvenementMap = new HashMap<String, Class<? extends Assembler>>();

        for (final EvenementType evenementType : EvenementType.values()) {

            if (EvenementType.EVT_BASE.equals(evenementType)) {
                continue;
            }

            String value = evenementType.value();
            // value = CHARNUMBERCHAR

            final List<String> numbers = new ArrayList<String>();
            // extract number
            final Matcher numberMatcher = NUMBER_PATTERN.matcher(value);
            while (numberMatcher.find()) {
                numbers.add(numberMatcher.group());
            }

            value = value.replaceAll("_", "#");
            value = value.replaceAll("-", "#");

            for (final String number : numbers) {
                value = value.replace(number, "#");
            }

            final List<String> chars = Arrays.asList(value.split("#"));

            final StringBuilder classeBuilder = new StringBuilder(BaseAssembler.class.getPackage().getName());
            classeBuilder.append(".");

            if (!chars.isEmpty()) {
                String first = chars.get(0);
                first = upperCaseFirstLetter(first.toLowerCase());
                classeBuilder.append(first);
                // add all number
                for (final String number : numbers) {
                    classeBuilder.append(number);
                }
                int cpt = 0;
                // add all other char
                for (final String car : chars) {
                    if (cpt++ > 0) {
                        classeBuilder.append(upperCaseFirstLetter(car.toLowerCase()));
                    }
                }

            } else {
                throw new ClientException(value + " is invalid");
            }

            classeBuilder.append(Assembler.class.getSimpleName());

            @SuppressWarnings("unchecked")
            final Class<? extends Assembler> clazz = (Class<? extends Assembler>) Class.forName(classeBuilder.toString());

            typeEvenementMap.put(evenementType.value(), clazz);

        }

    }

    private String upperCaseFirstLetter(final String value) {
        if (StringUtils.isNotBlank(value)) {
            final char[] stringArray = value.toCharArray();
            stringArray[0] = Character.toUpperCase(stringArray[0]);
            return new String(stringArray);
        }
        return value;
    }

    @Override
    public Assembler getAssemblerInstanceFor(final EvenementType evenementType, final EppEvtContainer eppEvtContainer, final CoreSession session,
            final EppPrincipal eppPrincipal) throws ClientException {
        try {
            final Class<? extends Assembler> assemblerClass = typeEvenementMap.get(evenementType.value());

            final Constructor<? extends Assembler> constructor = assemblerClass.getConstructor(EppEvtContainer.class, CoreSession.class,
                    EppPrincipal.class);

            return constructor.newInstance(eppEvtContainer, session, eppPrincipal);
        } catch (final Exception e) {
            throw new ClientException(e);
        }
    }

}

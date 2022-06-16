package fr.dila.st.core.messages;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

public abstract class SolonMessagesProperties {
    // le seul cas où on peut avoir des simples quotes non doublées, c'est pour échapper et afficher le caractère spécial accolade ouvrante : '{'
    private static final Pattern BAD_MESSAGE_WITH_PARAMETERS_PATTERN = Pattern.compile("(?<!'|\\{)'(?!'|\\{)");

    private static final String BAD_PROPERTIES_DESCRIPTION =
        "Liste des properties où les apostrophes ne sont pas doublées";
    private static final String BAD_PROPERTY_DESCRIPTION =
        "Il faut doubler toutes les apostrophes dans ce message : %s";

    private static final String DUPLICATED_KEYS_DESCRIPTION =
        "Il ne doit pas y avoir de clés dupliquées dans le fichier %s";

    private List<Pair<String, String>> properties;

    @Before
    public void setUp() throws IOException {
        File messagesFile = new File(getPropertiesFilePath());

        assertThat(messagesFile)
            .as(
                "Le fichier " +
                messagesFile.getName() +
                " dans le répertoire " +
                messagesFile.getPath() +
                " n'existe pas"
            )
            .exists()
            .isFile();

        properties = getProperties(messagesFile);
    }

    @Test
    public void validateMessagesWithParameters() {
        assertThat(getBadProperties())
            .as(BAD_PROPERTIES_DESCRIPTION)
            .allSatisfy(property -> assertThat(property).as(BAD_PROPERTY_DESCRIPTION, property.getValue()).isNull());
    }

    @Test
    public void checkNoDuplicatedKeys() {
        assertThat(getDuplicatedKeys()).as(DUPLICATED_KEYS_DESCRIPTION, getPropertiesFilePath()).isEmpty();
    }

    protected abstract String getPropertiesFilePath();

    protected List<Pair<String, String>> getBadProperties() {
        return properties
            .stream()
            .parallel()
            .filter(property -> BAD_MESSAGE_WITH_PARAMETERS_PATTERN.matcher(property.getValue()).find())
            .collect(Collectors.toList());
    }

    protected Set<Object> getDuplicatedKeys() {
        List<String> keys = getKeys();

        return keys.stream().parallel().filter(key -> Collections.frequency(keys, key) > 1).collect(Collectors.toSet());
    }

    private List<String> getKeys() {
        return properties.stream().parallel().map(Pair::getKey).collect(Collectors.toList());
    }

    private static List<Pair<String, String>> getProperties(File messagesFile) throws IOException {
        return FileUtils
            .readLines(messagesFile, "UTF-8")
            .stream()
            .parallel()
            .filter(StringUtils::isNotBlank) // exclut les lignes vides
            .filter(line -> !StringUtils.startsWith(line, "#")) // exclut les lignes de commentaires
            .map(SolonMessagesProperties::getProperty)
            .collect(Collectors.toList());
    }

    private static Pair<String, String> getProperty(String line) {
        String[] property = StringUtils.split(line, "=", 2);
        return ImmutablePair.of(property[0], property[1]);
    }
}

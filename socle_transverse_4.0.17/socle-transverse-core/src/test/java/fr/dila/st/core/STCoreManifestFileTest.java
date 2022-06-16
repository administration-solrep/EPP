package fr.dila.st.core;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class STCoreManifestFileTest {
    private static final String RESOURCES_PATH = "src/main/resources/";
    private static final String MANIFEST_FILE_PATH = RESOURCES_PATH + "META-INF/MANIFEST.MF";
    private static final String OSGI_INF_DIRECTORY_NAME = "OSGI-INF";

    private static final String COMMON_FILE_PATH_REGEX = OSGI_INF_DIRECTORY_NAME + "\\/[a-z0-9/-]+\\.xml";
    private static final String FILE_PATH_LINE_REGEX = "^ " + COMMON_FILE_PATH_REGEX + ",?$";

    private static final Pattern FILE_PATH_LINE_PATTERN = Pattern.compile(FILE_PATH_LINE_REGEX);
    private static final Pattern FILE_PATH_PATTERN = Pattern.compile("(" + COMMON_FILE_PATH_REGEX + ")");

    private static final String NUXEO_COMPONENT_LINE = "Nuxeo-Component: ";

    private static List<String> manifestLines;
    private static List<String> filePathManifestLines;
    private static List<Path> manifestPaths;

    @BeforeClass
    public static void setUp() throws IOException {
        manifestLines = getLinesFromManifest();

        int nuxeoComponentLineIndex = manifestLines.indexOf(NUXEO_COMPONENT_LINE);

        filePathManifestLines =
            manifestLines.stream().parallel().skip(nuxeoComponentLineIndex + 1).collect(Collectors.toList());

        manifestPaths =
            filePathManifestLines
                .stream()
                .parallel()
                .map(FILE_PATH_PATTERN::matcher)
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .map(path -> RESOURCES_PATH + path)
                .map(Paths::get)
                .collect(Collectors.toList());
    }

    @Test
    public void checkGlobalFormat() throws IOException {
        assertThat(manifestLines).isNotEmpty().doesNotHaveDuplicates();

        assertThat(manifestLines)
            .as(
                "Il faut vérifier qu'il y a bien une ligne contenant [%s] et qu'elle a bien un espace à la fin",
                NUXEO_COMPONENT_LINE
            )
            .contains(NUXEO_COMPONENT_LINE);
    }

    @Test
    public void checkFileFormatWithValidLinesForFilePaths() throws IOException {
        assertThat(filePathManifestLines).allSatisfy(line -> assertThat(line).matches(FILE_PATH_LINE_PATTERN));

        assertLastCharacterOfLines(filePathManifestLines);
    }

    @Test
    public void checkFilesExist() {
        assertThat(manifestPaths).allSatisfy(path -> assertThat(path).exists().isRegularFile());
    }

    @Test
    public void checkRealFilesAreIdenticalToManifest() throws IOException {
        List<Path> realPaths = getOrderedRealPaths();

        assertThat(realPaths).containsExactlyElementsOf(manifestPaths);
    }

    private static List<String> getLinesFromManifest() throws IOException {
        File manifestFile = new File(MANIFEST_FILE_PATH);

        assertThat(manifestFile)
            .as(
                "Le fichier " +
                manifestFile.getName() +
                " dans le répertoire " +
                manifestFile.getPath() +
                " n'existe pas"
            )
            .exists();

        return FileUtils.readLines(manifestFile, "UTF-8").stream().parallel().collect(Collectors.toList());
    }

    private static List<Path> getOrderedRealPaths() throws IOException {
        try (Stream<Path> paths = Files.walk(Paths.get(RESOURCES_PATH + OSGI_INF_DIRECTORY_NAME))) {
            return paths
                .filter(Files::isRegularFile)
                .filter(path -> !Objects.equals(path.getFileName(), Paths.get("deployment-fragment.xml")))
                .filter(path -> !path.getFileName().toString().endsWith("_old.xml"))
                .sorted(STCoreManifestFileTest::comparePath)
                .collect(Collectors.toList());
        }
    }

    private static void assertLastCharacterOfLines(List<String> lines) {
        // la dernière ligne ne doit pas finir par une virgule
        int lastLineIndex = lines.size() - 1;
        assertThat(lines.get(lastLineIndex)).doesNotEndWith(",");
        lines.remove(lastLineIndex);

        // toutes les autres lignes doivent finir par une virgule
        assertThat(lines).allSatisfy(line -> assertThat(line).endsWith(","));
    }

    /**
     * Permet de comparer 2 paths pour avoir un tri où les dossiers d'un répertoire courant sont affichés en premier
     * par ordre alpahabétique et ensuite ce sont les fichiers de ce répertoire courant qui sont affichés toujours par
     * ordre alphabétique.
     */
    private static int comparePath(Path p1, Path p2) {
        if (p1 == p2) {
            return 0;
        }

        if (p1 == null) {
            return -1;
        }

        if (p2 == null) {
            return 1;
        }

        if (p1.equals(p2)) {
            return 0;
        }

        if (p1.getParent().equals(p2.getParent())) {
            return p1.compareTo(p2);
        } else if (isChild(p1, p2)) {
            return -1;
        } else if (isChild(p2, p1)) {
            return 1;
        } else {
            return p1.getParent().compareTo(p2.getParent());
        }
    }

    private static boolean isChild(Path p1, Path p2) {
        return p1.startsWith(p2.getParent());
    }
}

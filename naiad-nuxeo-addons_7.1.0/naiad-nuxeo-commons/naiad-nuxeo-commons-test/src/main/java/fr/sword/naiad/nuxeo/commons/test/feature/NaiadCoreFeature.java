package fr.sword.naiad.nuxeo.commons.test.feature;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.runners.model.FrameworkMethod;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.core.event.EventService;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.RunnerFeature;
import org.nuxeo.runtime.test.runner.SimpleFeature;

import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;
import fr.sword.naiad.nuxeo.commons.test.annotation.PropertyFile;

/**
 * Feature de test étendant CoreFeature et ajoutant la gestion de l'annotation PropertyFile, permettant de charger des
 * fichiers .properties.
 * 
 * @author fmh
 */
@Features(CoreFeature.class)
public class NaiadCoreFeature extends SimpleFeature {
	/**
	 * Constructeur par défaut.
	 */
	public NaiadCoreFeature() {
		super();
	}

	/**
	 * Charge les propriétés présentes dans les fichiers .properties des annotations PropertyFile.
	 */
	@Override
	public void initialize(FeaturesRunner runner) throws IOException {
		List<String> files = new ArrayList<String>();

		addFeaturePropertyFile(runner, files);
		addTestPropertyFile(runner, files);

		if (!files.isEmpty()) {
			Properties properties = System.getProperties();
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

			for (String fileStr : files) {
				loadProperties(properties, new File(classLoader.getResource(fileStr).getFile()));
			}
		}
	}

	/**
	 * S'assure que les listeners asynchrones soient terminés.
	 */
	@Override
	public void afterMethodRun(FeaturesRunner runner, FrameworkMethod method, Object test) throws NuxeoException {
		ServiceUtil.getService(EventService.class).waitForAsyncCompletion();
	}

	private void addFeaturePropertyFile(FeaturesRunner runner, List<String> files) {
		for (RunnerFeature runnerFeature : runner.getFeatures()) {
			PropertyFile propertyFile = runnerFeature.getClass().getAnnotation(PropertyFile.class);
			if (propertyFile != null) {
				files.addAll(Arrays.asList(propertyFile.value()));
			}
		}
	}

	private void addTestPropertyFile(FeaturesRunner runner, List<String> files) {
		PropertyFile propertyFile = runner.getTestClass().getJavaClass().getAnnotation(PropertyFile.class);
		if (propertyFile != null) {
			files.addAll(Arrays.asList(propertyFile.value()));
		}
	}

	private void loadProperties(Properties properties, File file) throws IOException {
		FileInputStream fis = new FileInputStream(file);

		try {
			properties.load(fis);
		} finally {
			fis.close();
		}
	}
}

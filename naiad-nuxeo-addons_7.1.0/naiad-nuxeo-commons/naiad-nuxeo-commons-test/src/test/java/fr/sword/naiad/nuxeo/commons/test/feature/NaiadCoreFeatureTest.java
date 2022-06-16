package fr.sword.naiad.nuxeo.commons.test.feature;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

import fr.sword.naiad.nuxeo.commons.test.annotation.PropertyFile;

@RunWith(FeaturesRunner.class)
@Features(NaiadCoreDummyFeature.class)
@PropertyFile(NaiadCoreFeatureTest.PROP_FILE)
public class NaiadCoreFeatureTest {
	public static final String PROP_FILE = "test.properties";

	@Test
	public void testTestProperty() {
		String propKey = "naiad.commons.test.prop.test";
		String expectedValue = "17";

		Assert.assertEquals(expectedValue, Framework.getProperty(propKey));
	}

	@Test
	public void testFeatureProperty() {
		String propKey = "naiad.commons.test.prop.feature1";
		String expectedValue = "18";

		Assert.assertEquals(expectedValue, Framework.getProperty(propKey));

		propKey = "naiad.commons.test.prop.feature2";
		expectedValue = "19";

		Assert.assertEquals(expectedValue, Framework.getProperty(propKey));
	}
}

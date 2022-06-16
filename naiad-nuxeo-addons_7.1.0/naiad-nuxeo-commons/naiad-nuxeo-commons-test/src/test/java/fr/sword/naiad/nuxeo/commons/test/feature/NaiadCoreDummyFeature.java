package fr.sword.naiad.nuxeo.commons.test.feature;

import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.SimpleFeature;

import fr.sword.naiad.nuxeo.commons.test.annotation.PropertyFile;

@Features(NaiadCoreFeature.class)
@PropertyFile({ "feature1.properties", "feature2.properties" })
public class NaiadCoreDummyFeature extends SimpleFeature {

}

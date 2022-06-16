package fr.sword.naiad.nuxeo.commons.core.helper;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.platform.test.PlatformFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.LocalDeploy;

@RunWith(FeaturesRunner.class)
@Features(PlatformFeature.class)
@Deploy({ "fr.sword.naiad.nuxeo.commons.core" })
@LocalDeploy({"fr.sword.naiad.nuxeo.commons.core:OSGI-INF/test-voc.xml"})
public class VocabularyHelperTest {

	@Test
	public void testVocListing() throws NuxeoException {
		final String vocabularyName = "VocTest";
		List<VocabularyHelper.Entry> entries = VocabularyHelper.listValue(vocabularyName, VocabularyHelper.VocOrder.Label, false);
		Assert.assertEquals(3, entries.size());
		String[] labelsSortLabel = new String[]{
				"label_xxx", "label_yyy", "label_zzz"
		};
		int idx = 0;
		for(VocabularyHelper.Entry entry : entries){
			Assert.assertEquals(labelsSortLabel[idx], entry.getLabel());
			++idx;
		}
		
		entries = VocabularyHelper.listValue(vocabularyName, VocabularyHelper.VocOrder.OrderingField, false);
		Assert.assertEquals(3, entries.size());
		String[] labelsSortOrdering = new String[]{
				"label_zzz", "label_yyy", "label_xxx"
		};
		idx = 0;
		for(VocabularyHelper.Entry entry : entries){
			Assert.assertEquals(labelsSortOrdering[idx], entry.getLabel());
			++idx;
		}
		
		entries = VocabularyHelper.listValue(vocabularyName, VocabularyHelper.VocOrder.OrderingField, true);
		Assert.assertEquals(4, entries.size());
		String[] labelsSortOrdering2 = new String[]{
				"label_zzz", "label_yyy", "label_xxx", "label_obsolete"
		};
		idx = 0;
		for(VocabularyHelper.Entry entry : entries){
			Assert.assertEquals(labelsSortOrdering2[idx], entry.getLabel());
			Assert.assertEquals(idx == 3, entry.isObsolete());
			
			++idx;
		}
		
	
	}
	
	@Test 
	public void testParentVocListing() throws NuxeoException {
		List<VocabularyHelper.Entry> entries = VocabularyHelper.listValue("VocLevel0", VocabularyHelper.VocOrder.Label, false);
		Assert.assertEquals(3, entries.size());
		
		entries = VocabularyHelper.listValue("VocLevel1", VocabularyHelper.VocOrder.Label, false);
		Assert.assertEquals(6, entries.size());
		
		
		entries = VocabularyHelper.listValueForParent("VocLevel1", VocabularyHelper.VocOrder.Label, false, "level0_id2");
		Assert.assertEquals(2, entries.size());
		
		
	}
	
}

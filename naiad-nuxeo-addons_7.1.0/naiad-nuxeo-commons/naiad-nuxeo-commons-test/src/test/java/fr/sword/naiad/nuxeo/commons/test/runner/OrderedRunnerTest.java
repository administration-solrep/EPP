package fr.sword.naiad.nuxeo.commons.test.runner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(OrderedRunner.class)
public class OrderedRunnerTest {
	private static int i = 0;

	@Test
	public void aTest() {
		Assert.assertEquals(1, ++i);
	}

	@Test
	public void bTest() {
		Assert.assertEquals(2, ++i);
	}

	@Test
	public void eTest() {
		Assert.assertEquals(5, ++i);
	}

	@Test
	public void cTest() {
		Assert.assertEquals(3, ++i);
	}

	@Test
	public void dTest() {
		Assert.assertEquals(4, ++i);
	}
}

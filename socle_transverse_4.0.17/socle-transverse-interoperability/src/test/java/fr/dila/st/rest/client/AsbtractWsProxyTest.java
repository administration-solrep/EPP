package fr.dila.st.rest.client;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Assert;

import org.junit.Test;

public class AsbtractWsProxyTest {

	private static final String	EXPECTED	= "http://hostname:8080/some/path/myService/myMethod/";

	@Test
	public void testBuildServiceUrl01() {

		try {
			URL url = null;

			url = AbstractWsProxy.buildServiceUrl("http://hostname:8080", "some/path", "myService", "myMethod");
			Assert.assertNotNull(url);
			Assert.assertNotNull(url.toString());
			Assert.assertEquals(EXPECTED, url.toString());

		} catch (MalformedURLException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testBuildServiceUrl02() {

		try {
			URL url = null;

			url = AbstractWsProxy.buildServiceUrl("http://hostname:8080/", "some/path", "myService", "myMethod");
			Assert.assertNotNull(url);
			Assert.assertNotNull(url.toString());
			Assert.assertEquals(EXPECTED, url.toString());

		} catch (MalformedURLException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testBuildServiceUrl03() {

		try {
			URL url = null;

			url = AbstractWsProxy.buildServiceUrl("http://hostname:8080/", "/some/path", "myService", "myMethod");
			Assert.assertNotNull(url);
			Assert.assertNotNull(url.toString());
			Assert.assertEquals(EXPECTED, url.toString());

		} catch (MalformedURLException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testBuildServiceUrl04() {

		try {
			URL url = null;

			url = AbstractWsProxy.buildServiceUrl("http://hostname:8080/", "/some/path/", "myService", "myMethod");
			Assert.assertNotNull(url);
			Assert.assertNotNull(url.toString());
			Assert.assertEquals(EXPECTED, url.toString());

		} catch (MalformedURLException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testBuildServiceUrl05() {

		try {
			URL url = null;

			url = AbstractWsProxy.buildServiceUrl("http://hostname:8080/", "/some/path/", "/myService", "myMethod");
			Assert.assertNotNull(url);
			Assert.assertNotNull(url.toString());
			Assert.assertEquals(EXPECTED, url.toString());

		} catch (MalformedURLException e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void testBuildServiceUrl06() {

		try {
			URL url = null;

			url = AbstractWsProxy.buildServiceUrl("http://hostname:8080/", "/some/path/", "myService", "/myMethod");
			Assert.assertNotNull(url);
			Assert.assertNotNull(url.toString());
			Assert.assertEquals(EXPECTED, url.toString());

		} catch (MalformedURLException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void testBuildServiceUrl07() {

		try {
			URL url = null;

			url = AbstractWsProxy.buildServiceUrl("http://hostname:8080/", "/some/path/", "myService", "myMethod/");
			Assert.assertNotNull(url);
			Assert.assertNotNull(url.toString());
			Assert.assertEquals(EXPECTED, url.toString());

		} catch (MalformedURLException e) {
			Assert.fail(e.getMessage());
		}

	}
}

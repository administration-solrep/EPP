package org.nuxeo.ecm.platform.ui.web.auth;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.nuxeo.ecm.platform.ui.web.auth.DummyAuthPluginAnonymous.DUMMY_ANONYMOUS_LOGIN;
import static org.nuxeo.ecm.platform.ui.web.auth.DummyAuthPluginForm.DUMMY_AUTH_FORM_PASSWORD_KEY;
import static org.nuxeo.ecm.platform.ui.web.auth.DummyAuthPluginForm.DUMMY_AUTH_FORM_USERNAME_KEY;
import static org.nuxeo.ecm.platform.ui.web.auth.NXAuthConstants.REQUESTED_URL;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.security.Principal;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.platform.ui.web.auth.service.BruteforceSecurityService;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.mockito.MockitoFeature;
import org.nuxeo.runtime.mockito.RuntimeService;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.RuntimeFeature;

@RunWith(FeaturesRunner.class)
@Features({ RuntimeFeature.class, MockitoFeature.class })
@Deploy("org.nuxeo.ecm.platform.web.common:OSGI-INF/authentication-framework.xml")
@Deploy("org.nuxeo.runtime.jtajca")
//@Deploy("org.nuxeo.ecm.platform.web.common:OSGI-INF/bruteforce-service.xml")
//@Deploy("org.nuxeo.ecm.platform.web.common:OSGI-INF/bruteforce-persistence-framework.xml")
@Deploy("org.nuxeo.ecm.core.persistence")
@Deploy("org.nuxeo.runtime.datasource")
@Deploy("org.nuxeo.ecm.platform.web.common.test:OSGI-INF/hibernate.cfg.xml")
@Deploy("org.nuxeo.ecm.platform.web.common.test:OSGI-INF/bruteforce-ds.xml")
@Deploy("org.nuxeo.ecm.platform.web.common:OSGI-INF/bruteforce-service.xml")
public class TestBruteforceService {
	// from NuxeoAuthenticationFilter
	protected static final String BYPASS_AUTHENTICATION_LOG = "byPassAuthenticationLog";

	// from NuxeoAuthenticationFilter
	protected static final String SECURITY_DOMAIN = "securityDomain";

	// from NuxeoAuthenticationFilter
	protected static final String EVENT_LOGIN_SUCCESS = "loginSuccess";

	// from NuxeoAuthenticationFilter
	protected static final String EVENT_LOGOUT = "logout";

	protected static final String SCHEME = "http";

	protected static final String HOST = "localhost";

	protected static final int PORT = 8080;

	protected static final String CONTEXT = "/nuxeo";

	@Mock
	@RuntimeService
	protected UserManager userManager;

	@Mock
	@RuntimeService
	protected EventProducer eventProducer;

	protected NuxeoAuthenticationFilter filter;

	protected DummyFilterChain chain;

	protected ArgumentCaptor<Event> eventCaptor;

	private HttpServletRequest request;
	private HttpServletResponse response;

	public static class DummyFilterConfig implements FilterConfig {

		protected final Map<String, String> initParameters;

		public DummyFilterConfig(Map<String, String> initParameters) {
			this.initParameters = initParameters;
		}

		@Override
		public String getFilterName() {
			return "NuxeoAuthenticationFilter";
		}

		@Override
		public ServletContext getServletContext() {
			return null;
		}

		@Override
		public String getInitParameter(String name) {
			return initParameters.get(name);
		}

		@Override
		public Enumeration<String> getInitParameterNames() {
			return Collections.enumeration(initParameters.keySet());
		}
	}

	public static class DummyFilterChain implements FilterChain {

		protected boolean called;

		protected Principal principal;

		@Override
		public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			called = true;
			principal = ((HttpServletRequest) request).getUserPrincipal();
		}
	}

	@Before
	public void setUp() throws Exception {
		// filter config
		Map<String, String> initParameters = new HashMap<>();
		initParameters.put(BYPASS_AUTHENTICATION_LOG, "false");
		initParameters.put(SECURITY_DOMAIN, NuxeoAuthenticationFilter.LOGIN_DOMAIN);
		FilterConfig config = new DummyFilterConfig(initParameters);
		// filter
		filter = new NuxeoAuthenticationFilter();
		filter.init(config);
		// filter chain
		chain = new DummyFilterChain();

		// usemanager
		when(userManager.getAnonymousUserId()).thenReturn(DUMMY_ANONYMOUS_LOGIN);
		// events
		eventCaptor = ArgumentCaptor.forClass(Event.class);

		request = mock(HttpServletRequest.class);
		response = mock(HttpServletResponse.class);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		@SuppressWarnings("resource")
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, UTF_8), true);
		when(response.getWriter()).thenReturn(writer);

		HttpSession session = mock(HttpSession.class);

		when(request.getSession(anyBoolean())).thenReturn(session);
		mockRequestURI(request, "/doesnotmatter", "", "requestedUrl=mystart/foo");
		// login info
		when(request.getParameter(eq(DUMMY_AUTH_FORM_USERNAME_KEY))).thenReturn("bob");
		when(request.getParameter(eq(REQUESTED_URL))).thenReturn("mystart/foo");
	}

	@After
	public void tearDown() {
		filter.destroy();
	}

	protected void mockRequestURI(HttpServletRequest request, String servletPath, String pathInfo, String queryString) {
		mockRequestURI(request, servletPath, pathInfo, queryString, null);
	}

	@SuppressWarnings("boxing")
	protected void mockRequestURI(HttpServletRequest request, String servletPath, String pathInfo, String queryString,
			String requestURI) {
		if ("".equals(pathInfo)) {
			pathInfo = null;
		}
		if ("".equals(queryString)) {
			queryString = null;
		}
		if (requestURI == null) {
			// requestURI is not always exactly contextPath + servletPath + pathInfo,
			// despite the spec
			requestURI = CONTEXT + servletPath;
			if (pathInfo != null) {
				requestURI += pathInfo;
			}
		}
		// good enough for tests that don't use encoded/decoded URLs
		when(request.getScheme()).thenReturn(SCHEME);
		when(request.getServerName()).thenReturn(HOST);
		when(request.getServerPort()).thenReturn(PORT);
		when(request.getRequestURI()).thenReturn(requestURI);
		when(request.getContextPath()).thenReturn(CONTEXT);
		when(request.getServletPath()).thenReturn(servletPath);
		when(request.getPathInfo()).thenReturn(pathInfo);
		when(request.getQueryString()).thenReturn(queryString);
	}

	/**
	 * Check progression of waiting times.
	 */
	@Test
	public void testBruteForceWaitingTimes() throws Exception {
		
		for (int i = 0; i < 6; i++) {
			assertEquals(0,filter.calculateWaitingTime(i));
		}
		
		int seconds = 0;
		for (int i = 6; i < 20; i++) {
			int newSeconds = filter.calculateWaitingTime(i);
			assertTrue(newSeconds>=seconds && newSeconds>0);
			seconds = newSeconds;
		}
	}

	/**
	 * Login is successful, no bruteforceUserInfo available for bob
	 */
	@Test
	@Deploy("org.nuxeo.ecm.platform.web.common.test:OSGI-INF/test-authchain-bruteforce-dummy-loginmodule.xml")
	@Deploy("org.nuxeo.ecm.platform.web.common.test:OSGI-INF/test-authchain-bruteforce-dummy-form.xml")
	public void testBruteForceOnSuccess() throws Exception {
		when(request.getParameter(eq(DUMMY_AUTH_FORM_PASSWORD_KEY))).thenReturn("bob");

		filter.doFilter(request, response, chain);

		BruteforceSecurityService bruteforceSecurityService = Framework.getService(BruteforceSecurityService.class);
		Assert.assertNull(bruteforceSecurityService.getLoginInfos("bob"));
	}

	/**
	 * Login is failure, bruteforceUserInfo available for bob with 1 attempt
	 */
	@Test
	@Deploy("org.nuxeo.ecm.platform.web.common.test:OSGI-INF/test-authchain-bruteforce-dummy-loginmodule.xml")
	@Deploy("org.nuxeo.ecm.platform.web.common.test:OSGI-INF/test-authchain-bruteforce-dummy-form.xml")
	public void testBruteForceOnFailure() throws Exception {
		when(request.getParameter(eq(DUMMY_AUTH_FORM_PASSWORD_KEY))).thenReturn("");

		filter.doFilter(request, response, chain);

		BruteforceSecurityService bruteforceSecurityService = Framework.getService(BruteforceSecurityService.class);
		BruteforceUserInfo bruteforceUserInfo = bruteforceSecurityService.getLoginInfos("bob");
		Assert.assertNotNull(bruteforceUserInfo);
		Assert.assertEquals(1, bruteforceUserInfo.getAttemptCount());
		Assert.assertEquals("bob", bruteforceUserInfo.getUsername());
		Assert.assertNotNull(bruteforceUserInfo.getLastAttemptDate());
	}

	/**
	 * Login is failure, bruteforceUserInfo available for bob with several attempts,
	 * user is not blocked.
	 */
	@Test
	@Deploy("org.nuxeo.ecm.platform.web.common.test:OSGI-INF/test-authchain-bruteforce-dummy-loginmodule.xml")
	@Deploy("org.nuxeo.ecm.platform.web.common.test:OSGI-INF/test-authchain-bruteforce-dummy-form.xml")
	public void testBruteForceFailAndIncr() throws Exception {
		when(request.getParameter(eq(DUMMY_AUTH_FORM_PASSWORD_KEY))).thenReturn("");

		BruteforceSecurityService bruteforceSecurityService = Framework.getService(BruteforceSecurityService.class);
		
		for (int i = 1; i < 6; i++) {
			filter.doFilter(request, response, chain);

			BruteforceUserInfo bruteforceUserInfo = bruteforceSecurityService.getLoginInfos("bob");
			Assert.assertNotNull(bruteforceUserInfo);
			Assert.assertEquals(i, bruteforceUserInfo.getAttemptCount());
		}
	}

	/**
	 * Login is failure, bruteforceUserInfo available for bob with several attempts,
	 * user is blocked once.
	 */
	@Test
	@Deploy("org.nuxeo.ecm.platform.web.common.test:OSGI-INF/test-authchain-bruteforce-dummy-loginmodule.xml")
	@Deploy("org.nuxeo.ecm.platform.web.common.test:OSGI-INF/test-authchain-bruteforce-dummy-form.xml")
	public void testBruteForceFailThenBlocked() throws Exception {
		when(request.getParameter(eq(DUMMY_AUTH_FORM_PASSWORD_KEY))).thenReturn("");

		BruteforceSecurityService bruteforceSecurityService = Framework.getService(BruteforceSecurityService.class);
		for (int i = 1; i < 7; i++) {
			filter.doFilter(request, response, chain);
			
			BruteforceUserInfo bruteforceUserInfo = bruteforceSecurityService.getLoginInfos("bob");
			Assert.assertNotNull(bruteforceUserInfo);
			Assert.assertEquals(i, bruteforceUserInfo.getAttemptCount());
		}

		// 7-th time
		filter.doFilter(request, response, chain);
		assertNotNull(response);
		
		BruteforceUserInfo bruteforceUserInfo = bruteforceSecurityService.getLoginInfos("bob");
		Assert.assertNotNull(bruteforceUserInfo);
		Assert.assertEquals(6, bruteforceUserInfo.getAttemptCount()); // last is not counted because user is blocked
		
		// 8-th time
		filter.doFilter(request, response, chain);
		assertNotNull(response);
		
		bruteforceUserInfo = bruteforceSecurityService.getLoginInfos("bob");
		Assert.assertNotNull(bruteforceUserInfo);
		Assert.assertEquals(6, bruteforceUserInfo.getAttemptCount()); // last is not counted (again) because user is blocked
	}
	
	/**
	 * Login is success, bruteforce user info is deleted.
	 */
	@Test
	@Deploy("org.nuxeo.ecm.platform.web.common.test:OSGI-INF/test-authchain-bruteforce-dummy-loginmodule.xml")
	@Deploy("org.nuxeo.ecm.platform.web.common.test:OSGI-INF/test-authchain-bruteforce-dummy-form.xml")
	public void testBruteForceFailThenSuccess() throws Exception {
		when(request.getParameter(eq(DUMMY_AUTH_FORM_PASSWORD_KEY))).thenReturn("");

		BruteforceSecurityService bruteforceSecurityService = Framework.getService(BruteforceSecurityService.class);
		
		for (int i = 1; i < 3; i++) {
			filter.doFilter(request, response, chain);
			
			BruteforceUserInfo bruteforceUserInfo = bruteforceSecurityService.getLoginInfos("bob");
			Assert.assertNotNull(bruteforceUserInfo);
		}

		// 4-th time -> user infos is reset
		when(request.getParameter(eq(DUMMY_AUTH_FORM_PASSWORD_KEY))).thenReturn("bob");
		filter.doFilter(request, response, chain);
		BruteforceUserInfo bruteforceUserInfo = bruteforceSecurityService.getLoginInfos("bob");
		Assert.assertNull(bruteforceUserInfo);
		
	}
}

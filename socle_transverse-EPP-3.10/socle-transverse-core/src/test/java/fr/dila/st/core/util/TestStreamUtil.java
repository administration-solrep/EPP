package fr.dila.st.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import junit.framework.TestCase;

/**
 * Test de la classe StringUtil.
 * 
 * @author jtremeaux
 */
public class TestStreamUtil extends TestCase {

	private static final String	hibernateContent	= "<document>\n"
															+ "   <hibernateConfiguration name=\"nxaudit-logs\"> \n"
															+ "      <properties>\n"
															+ "        <property name=\"hibernate.connection.url\">jdbc:hsqldb:mem:.;sql.enforce_strict_size=true</property>;\n"
															+ "        <property name=\"hibernate.connection.driver_class\">org.hsqldb.jdbcDriver</property>\n"
															+ "        <property name=\"hibernate.connection.auto_commit\">true</property> \n"
															+ "        <property name=\"hibernate.connection.pool_size\">1</property>;\n"
															+ "        <property name=\"hibernate.dialect\">org.hibernate.dialect.HSQLDialect</property>;\n"
															+ "        <property name=\"hibernate.hbm2ddl.auto\">update</property>;\n"
															+ "        <property name=\"hibernate.show_sql\">false</property>; // true to debug\n"
															+ "        <property name=\"hibernate.format_sql\">true</property>;\n"
															+ "      </properties>\n"
															+ "    </hibernateConfiguration>\n" + "</document>\n";

	public void testInputStreamAsString() throws IOException {
		URL url = Thread.currentThread().getContextClassLoader().getResource("file/streamUtilFileTest.xml");
		System.out.println(hibernateContent);
		String res = StreamUtil.inputStreamAsString((InputStream) url.getContent());
		System.out.println(res);
		assertEquals(hibernateContent, res);
	}
}

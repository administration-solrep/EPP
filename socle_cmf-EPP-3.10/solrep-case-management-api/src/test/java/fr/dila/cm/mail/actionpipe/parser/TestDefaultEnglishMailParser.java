/*
 * (C) Copyright 2010 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Sun Seng David TAN <stan@nuxeo.com>
 */
package fr.dila.cm.mail.actionpipe.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import fr.dila.cm.contact.Contacts;
import fr.dila.cm.mail.actionpipe.parser.DefaultEnglishMailParser;

/**
 * Various unit test of {@link DefaultEnglishMailParser}
 * 
 * @author Sun Seng David TAN <stan@nuxeo.com>
 * 
 */
public class TestDefaultEnglishMailParser {
	Pattern						pattern;

	DefaultEnglishMailParser	bodyParser;

	public static final Log		log	= LogFactory.getLog(TestDefaultEnglishMailParser.class);

	@Before
	public void setUp() {
		bodyParser = new DefaultEnglishMailParser();
		pattern = bodyParser.getContactPattern();
	}

	/**
	 * Testing the parsing of contacts with the default contact pattern that should be the Thunderbird one.
	 */
	@Test
	public void testParseContacts() {

		String fullContact = "\t Sun Seng David Tan <stan@nuxeo.com> test";
		Contacts contacts = bodyParser.parseContacts(fullContact);
		Assert.assertEquals(1, contacts.size());
		Assert.assertEquals("Sun Seng David Tan", contacts.get(0).getName());
		Assert.assertEquals("stan@nuxeo.com", contacts.get(0).getEmail());

		String fullContact2 = "\t \"Sun Seng David Tan (sunix)\" <stan@nuxeo.com> test";
		contacts = bodyParser.parseContacts(fullContact2);
		Assert.assertEquals(1, contacts.size());
		Assert.assertEquals("Sun Seng David Tan (sunix)", contacts.get(0).getName());
		Assert.assertEquals("stan@nuxeo.com", contacts.get(0).getEmail());

		String simpleContact = "\t stan@nuxeo.com >test >test";
		contacts = bodyParser.parseContacts(simpleContact);
		Assert.assertEquals(1, contacts.size());
		Assert.assertTrue("".equals(contacts.get(0).getName()) || contacts.get(0).getName() == null);
		Assert.assertEquals("stan@nuxeo.com", contacts.get(0).getEmail());

		String multipleSimpleContacts = "\tsun@sue-fr.org, sxzhang@ead.naixtis.com, ehimarka@yaheo.fr decoooo>qsdfqsdf>";
		contacts = bodyParser.parseContacts(multipleSimpleContacts);
		Assert.assertEquals(3, contacts.size());
		Assert.assertTrue("".equals(contacts.get(0).getName()) || contacts.get(0).getName() == null);
		Assert.assertEquals("sun@sue-fr.org", contacts.get(0).getEmail());
		Assert.assertTrue("".equals(contacts.get(1).getName()) || contacts.get(1).getName() == null);
		Assert.assertEquals("sxzhang@ead.naixtis.com", contacts.get(1).getEmail());
		Assert.assertTrue("".equals(contacts.get(2).getName()) || contacts.get(2).getName() == null);
		Assert.assertEquals("ehimarka@yaheo.fr", contacts.get(2).getEmail());

		String multipleFullContacts = "\tJean-David TANG <jdtang@gmail.com>, \"Sun Seng David Tan (sunix)\" <sun.seng.david.tan@nuxeo.com>,  Louisa Cha <cha_louisa@yahoo.fr> qsdfqsdf < mlkjlmj > mlkj";
		contacts = bodyParser.parseContacts(multipleFullContacts);
		Assert.assertEquals(3, contacts.size());
		Assert.assertEquals("Jean-David TANG", contacts.get(0).getName());
		Assert.assertEquals("jdtang@gmail.com", contacts.get(0).getEmail());
		Assert.assertEquals("Sun Seng David Tan (sunix)", contacts.get(1).getName());
		Assert.assertEquals("sun.seng.david.tan@nuxeo.com", contacts.get(1).getEmail());
		Assert.assertEquals("Louisa Cha", contacts.get(2).getName());
		Assert.assertEquals("cha_louisa@yahoo.fr", contacts.get(2).getEmail());

		String mixedContacts = "prase.jesuth@sgs.socgene.com,  Anne so Paier <annesopaier@yahoo.fr>, jubaien@hotmail.com, \"Guillaume(chacha) Chateau\" <guillaume.chateaur@eeee.fr> >mlkjmlj sdf> qsdfq>";
		contacts = bodyParser.parseContacts(mixedContacts);
		Assert.assertEquals(4, contacts.size());
		Assert.assertTrue("".equals(contacts.get(0).getName()) || contacts.get(0).getName() == null);
		Assert.assertEquals("prase.jesuth@sgs.socgene.com", contacts.get(0).getEmail());
		Assert.assertEquals("Anne so Paier", contacts.get(1).getName());
		Assert.assertEquals("annesopaier@yahoo.fr", contacts.get(1).getEmail());
		Assert.assertTrue("".equals(contacts.get(2).getName()) || contacts.get(2).getName() == null);
		Assert.assertEquals("jubaien@hotmail.com", contacts.get(2).getEmail());
		Assert.assertEquals("Guillaume(chacha) Chateau", contacts.get(3).getName());
		Assert.assertEquals("guillaume.chateaur@eeee.fr", contacts.get(3).getEmail());
	}

	@Test
	public void testEmailEnglishParser() {
		String toParse = "\n-------- Original Message --------" + "\nSubject:    RENOUVELLEMENT DE SUPPORT ANNUEL"
				+ "\nDate:   Wed, 14 Jan 2009 15:15:25 +0100" + "\nFrom:   Anahide TCHERTCHIAN <at@nuxeo.com>"
				+ "\nTo:     <arussel@nuxeo.com>\n\n" + "\nObjet : [correspondence] courriel test pour fonctionnalité "
				+ "\nBonjour,\n" + "\nVeuillez trouver ci-joint un devis pour le renouvellement de votre support"
				+ "\nannuel pour la p�riode du 26/02/09 AU 26/02/10."
				+ "\n\nMerci de bien vouloir nous faire parvenir un bon de commande."
				+ "\n\nNous restons � votre disposition pour tout compl�ment d'information,"
				+ "\n\n\nBien cordialement," + "\n\nAnahide TCHERTCHIAN" + "\nAssistante Commerciale"
				+ "\ne-mail : at@nuxeo.com\n		";
		Matcher matcher = bodyParser.getHeaderPattern().matcher(toParse);
		Assert.assertTrue("The text to parse didn't match the header regexp", matcher.matches());
		if (log.isDebugEnabled()) {
			for (int i = 1; i < matcher.groupCount() + 1; i++) {
				log.debug(i + ": " + matcher.group(i));
			}
		}
		Assert.assertTrue("The original TO recipient should contain arussel@nuxeo.com",
				matcher.group(10).contains("arussel@nuxeo.com"));

	}

	@Test
	public void testEmailEnglishWithCc() {

		String toParse = "\n\n-------- Original Message --------\nSubject: \tRENOUVELLEMENT DE SUPPORT ANNUEL\nDate: \tWed, 14 Jan 2009 15:15:25 +0100\nFrom: \tAnahide TCHERTCHIAN <at@nuxeo.com>\nTo: \t<arussel@nuxeo.com> ; Jean Dupuis <jean.dupuis@mail.fr>\nCc: \tSebastien Blanc <s.b@mail.fr>; benoit perrier <b.l@mail.fr>\n\n\n\nBonjour,\n\nVeuillez trouver ci-joint un devis pour le renouvellement de votre support\nannuel pour la p�riode du 26/02/09 AU 26/02/10.\n\n\nMerci de bien vouloir nous faire parvenir un bon de commande.\n\nNous restons � votre disposition pour tout compl�ment d'information,\n\n\nBien cordialement,\n\nAnahide TCHERTCHIAN\n\ne-mail : at@nuxeo.com\n\n\n";

		Matcher matcher = bodyParser.getHeaderPattern().matcher(toParse);
		Assert.assertTrue("The text to parse didn't match the header regexp", matcher.matches());
		if (log.isDebugEnabled()) {
			for (int i = 1; i < matcher.groupCount() + 1; i++) {
				log.debug(i + ": " + matcher.group(i));
			}
		}
		Assert.assertTrue("The original TO recipient should contain arussel@nuxeo.com",
				matcher.group(10).contains("arussel@nuxeo.com"));
	}

}

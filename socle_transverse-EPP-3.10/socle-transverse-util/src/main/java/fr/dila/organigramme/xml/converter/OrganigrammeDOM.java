package fr.dila.organigramme.xml.converter;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.dila.organigramme.xml.converter.constant.OrganigrammeConstant;
import fr.dila.organigramme.xml.converter.element.Agent;
import fr.dila.organigramme.xml.converter.element.BaseElement;
import fr.dila.organigramme.xml.converter.element.Gouvernement;
import fr.dila.organigramme.xml.converter.element.Organigramme;
import fr.dila.organigramme.xml.converter.element.Poste;
import fr.dila.organigramme.xml.converter.element.UniteStructurelle;

/**
 * Classe qui charge le XML et renvoit la racine de l'organigramme
 * 
 * @author Fabio Esposito
 * 
 */
public class OrganigrammeDOM {

	private static final Log	LOGGER	= LogFactory.getLog(OrganigrammeDOM.class);

	public BaseElement getOrganigrammeFromXML(String filePath) {
		Organigramme organigramme = null;
		Document doc = loadDoc(filePath);
		if (doc != null) {
			organigramme = (Organigramme) loadData(doc);
		}

		return organigramme;
	}

	private Document loadDoc(String filePath) {
		Document document = null;

		try {
			// création d'une fabrique de documents
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

			// création d'un constructeur de documents
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// lecture du contenu d'un fichier XML avec DOM
			File xml = new File(filePath);
			document = docBuilder.parse(xml);

		} catch (ParserConfigurationException pce) {
			LOGGER.error(
					"Erreur de configuration du parseur DOM lors de l'appel à DocumentBuilderFactory.newDocumentBuilder();",
					pce);
		} catch (SAXException se) {
			LOGGER.error("Erreur lors du parsing du document lors de l'appel à DocumentBuilder.parse(xml)", se);
		} catch (IOException ioe) {
			LOGGER.error("Erreur d'entrée/sortie lors de l'appel à DocumentBuilder.parse(xml)", ioe);
		}

		return document;
	}

	private BaseElement loadData(Document document) {
		Organigramme organigramme = new Organigramme();
		Element root = document.getDocumentElement();

		if (root != null && OrganigrammeConstant.TAG_ORGANIGRAMME.equals(root.getNodeName())) {

			Gouvernement gouvernement = new Gouvernement();
			organigramme.add(gouvernement);

			NodeList nodeList = root.getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {
				Object node = nodeList.item(i);
				if (!(node instanceof Element)) {
					continue;
				}
				Element element = (Element) nodeList.item(i);
				if (OrganigrammeConstant.TAG_UNITE_STRUCTURELLE.equals(element.getNodeName())) {
					UniteStructurelle uniteStruct = loadUniteStructurelle(element, gouvernement);
					if (uniteStruct.getNorReference().length() == 3) {
						gouvernement.add(uniteStruct);
					}
				} else if (OrganigrammeConstant.TAG_POSTE.equals(element.getNodeName())) {
					Poste poste = loadPoste(element, gouvernement);
					gouvernement.add(poste);
				}
			}
		}

		return organigramme;
	}

	private UniteStructurelle loadUniteStructurelle(Element element, BaseElement parent) {

		UniteStructurelle uniteStructurelle = new UniteStructurelle(element, parent);

		NodeList childNodeList = element.getChildNodes();

		for (int i = 0; i < childNodeList.getLength(); i++) {
			Object node = childNodeList.item(i);
			if (!(node instanceof Element)) {
				continue;
			}
			Element childElement = (Element) childNodeList.item(i);
			if (OrganigrammeConstant.TAG_POSTE.equals(childElement.getNodeName())) {
				Poste poste = loadPoste(childElement, uniteStructurelle);
				uniteStructurelle.add(poste);
			} else if (OrganigrammeConstant.TAG_UNITE_STRUCTURELLE.equals(childElement.getNodeName())) {
				UniteStructurelle us = loadUniteStructurelle(childElement, uniteStructurelle);
				uniteStructurelle.add(us);
			}
		}

		return uniteStructurelle;
	}

	private Poste loadPoste(Element element, BaseElement parent) {

		Poste poste = new Poste(element, parent);

		NodeList childNodeList = element.getChildNodes();

		for (int i = 0; i < childNodeList.getLength(); i++) {
			Object childNode = childNodeList.item(i);
			if (!(childNode instanceof Element)) {
				continue;
			}
			Element childElement = (Element) childNodeList.item(i);
			if (OrganigrammeConstant.TAG_AGENT.equals(childElement.getNodeName())) {
				Agent agent = loadAgent(childElement, poste);
				poste.add(agent);
			}
		}

		return poste;
	}

	private Agent loadAgent(Element element, BaseElement parent) {
		Agent agent = new Agent(element, parent);
		return agent;
	}

}

package fr.dila.solonepp.webtest.documentation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;

import fr.dila.solonepp.webtest.webdriver010.dossier1240.TestSuite;
import fr.dila.solonepp.webtest.webdriver020.TestCreationCommAN;
import fr.dila.solonepp.webtest.webdriver020.TestBoutonAlerte;
import fr.dila.solonepp.webtest.webdriver020.TestAdministration;
import fr.dila.solonepp.webtest.webdriver020.TestCreationCommGouv;
import fr.dila.solonepp.webtest.webdriver020.TestCreationCommSenat;
import fr.dila.solonepp.webtest.webdriver020.TestCreationUtilisateur;
import fr.dila.solonepp.webtest.webdriver020.TestGestionOrga;
import fr.dila.solonepp.webtest.webdriver020.TestRectifier;
import fr.dila.solonepp.webtest.webdriver020.TestTransmettreParMail;
import fr.dila.solonepp.webtest.webdriver030.TestProcedureDeclarationPG;
import fr.dila.solonepp.webtest.webdriver030.TestProcedureTransmissionDocuments;
import fr.dila.solonepp.webtest.webdriver030.TestProcedureTransmissionDocuments2;
import fr.dila.solonepp.webtest.webdriver030.TestRecherche;
import fr.dila.st.annotations.AbstractGenerateDocTests;

public class SolonEppGenerateDocTests extends AbstractGenerateDocTests {

	public static void main(String[] args) {
		categoriesAndTestsMap = new TreeMap<String, List<String>>();
		
		for (Class clazz : getAllTestsClasses()) {
			updateMapWithTestClass(clazz);
		}

		StringBuilder html = generateHtml("SOLON EPP");
		
		File htmlFile = new File ("DocumentationTestsSolonEpp.html");
		
		try {
			FileUtils.writeStringToFile(htmlFile, html.toString());
		} catch (IOException e) {
			System.out.println(e);
		}
		System.out.println("Génération terminée");
	}
	
	private static List<Class> getAllTestsClasses() {
		List<Class> classes = new ArrayList<Class>();
		classes.add(TestSuite.class);
		classes.add(TestAdministration.class);
		classes.add(TestBoutonAlerte.class);
		classes.add(TestCreationCommAN.class);
		classes.add(TestCreationCommGouv.class);
		classes.add(TestCreationCommSenat.class);
		classes.add(TestCreationUtilisateur.class);
		classes.add(TestGestionOrga.class);
		classes.add(TestRectifier.class);
		classes.add(TestTransmettreParMail.class);
		classes.add(TestProcedureDeclarationPG.class);
		classes.add(TestProcedureTransmissionDocuments.class);
		classes.add(TestProcedureTransmissionDocuments2.class);
		classes.add(TestRecherche.class);
		return classes;
	}

}

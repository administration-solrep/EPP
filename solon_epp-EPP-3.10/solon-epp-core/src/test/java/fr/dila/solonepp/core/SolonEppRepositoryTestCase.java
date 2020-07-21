package fr.dila.solonepp.core;

import fr.dila.st.core.STRepositoryTestCase;

/**
 * Classe de test de base de l'application SOLON EPP.
 * 
 * - Inclut un annuaire utilisateur HSQLDB chargé à partir de fichier csv.
 */
public class SolonEppRepositoryTestCase extends STRepositoryTestCase {

    @Override
    protected void deployRepositoryContrib() throws Exception {
        super.deployRepositoryContrib();

        deployContrib("fr.dila.solonepp.core.tests", "OSGI-INF/test-sql-directories-contrib.xml");
        
        deployBundle("fr.dila.solonepp.core");
        deployContrib("fr.dila.solonepp.core", "OSGI-INF/solonepp-schema-contrib.xml");
        deployContrib("fr.dila.solonepp.core", "OSGI-INF/solonepp-core-type-contrib.xml");
        deployContrib("fr.dila.solonepp.core", "OSGI-INF/solonepp-content-template-contrib.xml");
        deployContrib("fr.dila.solonepp.core", "OSGI-INF/solonepp-adapter-contrib.xml");
        deployContrib("fr.dila.solonepp.core", "OSGI-INF/solonepp-usermanager-contrib.xml");
        deployContrib("fr.dila.solonepp.core", "OSGI-INF/solonepp-organigramme-contrib.xml");
        deployContrib("fr.dila.solonepp.core", "OSGI-INF/service/dossier-framework.xml");
        deployContrib("fr.dila.solonepp.core", "OSGI-INF/service/evenement-framework.xml");
        deployContrib("fr.dila.solonepp.core", "OSGI-INF/service/version-framework.xml");
        deployContrib("fr.dila.solonepp.core", "OSGI-INF/service/piece-jointe-framework.xml");
        deployContrib("fr.dila.solonepp.core.tests", "ldap/DirectoryTypes.xml");
        deployContrib("fr.dila.solonepp.core.tests", "OSGI-INF/test-st-computedgroups-contrib.xml");
    }

}
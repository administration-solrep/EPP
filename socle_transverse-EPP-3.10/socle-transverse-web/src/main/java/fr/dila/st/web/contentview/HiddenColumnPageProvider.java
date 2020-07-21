package fr.dila.st.web.contentview;

import org.nuxeo.ecm.core.api.ClientException;

/**
 * Interface pour les PageProvider capable de masquer des colonnes
 * 
 * @author asatre
 * 
 */
public interface HiddenColumnPageProvider {

	/**
	 * bean de la content gerant les colonnes
	 */
	String	COLUMN_BEAN_MANAGER	= "columnBeanManager";

	/**
	 * appel le bean contenu dans columnBeanManager (doit implementer ColumnBeanManager)
	 * 
	 * @param isHidden
	 * @return
	 * @throws ClientException
	 */
	Boolean isHiddenColumn(String isHidden) throws ClientException;

}

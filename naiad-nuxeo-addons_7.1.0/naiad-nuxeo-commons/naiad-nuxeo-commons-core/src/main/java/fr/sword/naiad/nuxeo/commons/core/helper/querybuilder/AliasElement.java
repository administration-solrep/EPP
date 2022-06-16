package fr.sword.naiad.nuxeo.commons.core.helper.querybuilder;

/**
 * 
 * Représente un element et son éventuel alias.
 * 
 * @author SPL
 *
 */
public interface AliasElement {

	String getAlias();
	
	Object[] getParams();
}



package fr.sword.naiad.nuxeo.ufnxql.core.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.sword.naiad.nuxeo.ufnxql.core.query.ufnxql.functions.UfnxqlFunction;

/**
 * Interface du service de confiuguration du requetage FNXQL
 *
 */
public interface FNXQLConfigService {

	/**
	 * Retourne pour certains type un schema utilisable à la place de hierarchy pour lister
	 * les documents de ce types.
	 * Ce schema doit posséder une ligne pour chaque document de ce type et seulement pour ce type de 
	 * document
	 * @return
	 */
	Map<String, String> getMappingTypeSchema();
	
	/**
	 * Retourne un schema utilisable à la place de hierarchy si il en existe un. retourne null sinon
	 * @param type
	 * @return
	 */
	String getSchemaForType(String type);
	
	/**
	 * Retourne l'ensemble des mixintypes qui sont definie sur les types et pas sur les instances des documents.
	 * Cet ensemble permet de limiter les requetes aux primary type qui possedent le(s) mixin types recherché(s) 
	 * @return
	 */
	Set<String> getMixinTypeGlobalToTypes();
	
	/**
	 * Retourne si un mixin type est defini comme etant que global (pas de definition par instance)
	 * @param mixinType
	 * @return
	 */
	boolean isMixinTypeGlobalToTypes(String mixinType);
	
	/**
	 * retourne les mixin types present dans l'ensemble en parametre pouvant etre redefini par instance
	 * @param mixinTypes
	 * @return
	 */
	Set<String> extractMixinTypePerInstance(Set<String> mixinTypes);
	
	/**
	 * retourne les fonctions déclarées
	 */
	List<UfnxqlFunction> getUFNXQLFunctions();
	
}

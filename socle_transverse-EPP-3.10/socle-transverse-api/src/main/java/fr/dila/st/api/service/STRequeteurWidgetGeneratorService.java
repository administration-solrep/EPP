package fr.dila.st.api.service;

import fr.dila.st.api.requeteur.RequeteurContribution;

/**
 * La classe de service pour le service de jointure. Renvoie un objet capable de prendre une clause WHERE d'un projet
 * spécifique, et de renvoyer une requête avec les jointures sur les objets du projet.
 * 
 * @author jgomez
 */

public interface STRequeteurWidgetGeneratorService {

	RequeteurContribution getRequeteurContribution(String contributorName);

}

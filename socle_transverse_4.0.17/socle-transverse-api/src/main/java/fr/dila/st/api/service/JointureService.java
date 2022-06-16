package fr.dila.st.api.service;

import fr.dila.st.api.recherche.QueryAssembler;

/**
 *
 *
 * @author JGZ La classe de service pour le service de jointure. Renvoie un objet capable de prendre une clause WHERE
 *         d'un projet spécifique, et de renvoyer une requête avec les jointures sur les objets du projet.
 *
 */

public interface JointureService {
    /**
     * Renvoie l'objet chargé de construire les jointures entre les documents du projet, à partir de préfixes connus.
     *
     * @return un queryAssembler.
     */
    QueryAssembler getQueryAssembler(String name);

    /**
     * Renvoie le query assembleur défini comme étant le query assembleur par défaut du projet
     *
     * @return un queryAssembler.
     */
    QueryAssembler getDefaultQueryAssembler();
}

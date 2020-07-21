package fr.dila.solonepp.api.service;

import java.io.Serializable;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.domain.evenement.NumeroVersion;

/**
 * Service permettant de gérer les numéros de versions.
 * 
 * @author jtremeaux
 */
public interface VersionNumeroService extends Serializable {

    /**
     * Retourne le premier numéro de version.
     * 
     * @param publie Document à l'état publié (sinon brouillon)
     * @return Numéro de version
     * @throws ClientException
     */
    NumeroVersion getFirstNumeroVersion(boolean publie) throws ClientException;

    /**
     * Retourne le prochain numéro de version.
     * 
     * @param lastVersionDoc Document de la dernière version
     * @param publie Publication de l'événement
     * @return Numéro de version
     * @throws ClientException
     */
    NumeroVersion getNextNumeroVersion(DocumentModel lastVersionDoc, boolean publie) throws ClientException;

    /**
     * Retourne le prochain numéro de version.
     * 
     * @param numeroVersion Numéro de version actuel
     * @param publie Publication de l'événement
     * @return Prochain numéro de version
     * @throws ClientException
     */
    NumeroVersion getNextNumeroVersion(NumeroVersion numeroVersion, boolean publie) throws ClientException;
}

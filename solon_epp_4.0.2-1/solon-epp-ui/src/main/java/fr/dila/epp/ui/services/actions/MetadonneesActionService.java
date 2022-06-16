package fr.dila.epp.ui.services.actions;

import fr.dila.solonepp.api.descriptor.metadonnees.MetaDonneesDescriptor;
import fr.dila.solonepp.api.dto.PieceJointeDTO;
import fr.dila.st.api.descriptor.parlement.PropertyDescriptor;
import fr.dila.st.ui.bean.SuggestionDTO;
import fr.dila.st.ui.th.model.SpecificContext;
import java.util.List;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface MetadonneesActionService {
    /**
     * isColumnVisible
     *
     * @param COLUMN_NAME
     *            nom de la métadonnée
     * @return true s'il existe un {@link PropertyDescriptor} correspondant à la
     *         colonne
     */
    boolean isColumnVisible(SpecificContext context);

    /**
     * Retourne le label pour la métadonnée s'il existe, sinon chaine vide
     *
     * @param COLUMN_NAME
     *            nom de la métadonnée
     * @return
     */
    String getCutomWidgetLabel(SpecificContext context);

    /**
     * isColumnVisibleFicheDossier
     *
     * @param COLUMN_NAME
     *            nom de la métadonnée
     * @return true s'il existe un {@link PropertyDescriptor} correspondant à la
     *         colonne
     */
    boolean isColumnVisibleFicheDossier(SpecificContext context);

    /**
     * isColumnRequired
     *
     * @param COLUMN_NAME
     *            nom de la métadonnée
     * @return true si le {@link PropertyDescriptor} de la métadonnée est
     *         obligatoire
     */
    boolean isColumnRequired(SpecificContext context);

    /**
     * Retourne le libellé du type d'événement
     *
     * @param TYPE_EVENEMENT
     * @return
     */
    String getLibelle(SpecificContext context);

    /**
     * Recherche de suggestion dans une table de référence
     *
     * @param INPUT
     *            string pour la recherche
     * @param TABLE_REFERENCE
     *            nom de la table de référence (docType)
     * @param FULL_TABLE_REF
     *            si true, ignore les dates de début et de fin de la table de
     *            référence
     * @param TYPE_ORGANISME
     * @return
     */
    List<SuggestionDTO> getSuggestions(SpecificContext context);

    /**
     * Recherche de suggestion dans une table de reference sans prendre en
     * compte l'institution de l'utilisateur
     *
     * @param INPUT
     *            string pour la recherche
     * @param TABLE_REFERENCE
     *            nom de la table de référence (docType)
     * @param FULL_TABLE_REF
     *            si true, ignore les dates de début et de fin de la table de
     *            référence
     * @param TYPE_ORGANISME
     * @return
     */
    Object getSuggestionsAll(SpecificContext context);

    /**
     * retourne la description de l'element de la table de reference
     *
     * @param ID
     *            identifiant
     * @param TABLE_REFERENCE
     *            nom de la table de référence (docType)
     * @return
     */
    String getTitleFromTableReference(SpecificContext context);

    /**
     * Retourne le libellé de la nature de la version
     *
     * @param CURRENT_VERSION_DOC
     * @return
     */
    String getLabelNatureVersion(SpecificContext context);

    /**
     * Retourne la liste des pieces jointe d'un certain type de la version
     * courant
     *
     * @param ID
     *            id de la version courante
     * @param TYPE_PIECE_JOINTE
     * @return
     */
    List<PieceJointeDTO> getListPieceJointeFichier(SpecificContext context);

    /**
     * Retourne la liste des pieces jointe d'un certain type de la version
     * courant
     *
     * @param CURRENT_VERSION_DOC
     * @param TYPE_PIECE_JOINTE
     * @return
     */
    List<PieceJointeDTO> getDeletedListPieceJointe(SpecificContext context);

    /**
     * Retourne vrai si une pièce jointe portant le titre choisi existe déjà
     *
     * @param CURRENT_VERSION_DOC
     * @param TYPE_PIECE_JOINTE
     * @param TITRE_PIECE_JOINTE
     * @return
     */
    boolean isNewPieceJointe(SpecificContext context);

    /**
     * Retourne un label custom de evenement-type-contrib, sinon du vocabulaire
     *
     * @param TYPE_PIECE_JOINTE
     * @return
     */
    String getPieceJointeType(SpecificContext context);

    /**
     * getDeletedUrl
     *
     * @param CURRENT_VERSION_DOC
     * @param TYPE_PIECE_JOINTE
     * @return
     */
    String getDeletedUrl(SpecificContext context);

    /**
     * Retourne la liste complète des métadonnées
     */
    List<MetaDonneesDescriptor> getListMetadonnees();

    /**
     * Retourne le dossier pour afficher la fiche dossier
     *
     * @param context
     * @return
     */
    DocumentModel getCurrentDossier(SpecificContext context);

    /**
     * Renvoie true si la méta a été modifiée
     *
     * @param XPATH le xpath de la méta
     * @param IS_EDIT_MODE vrai pour le mode edition
     * @param CURRENT_VERSION_DOC
     * @return
     */
    boolean notEqualsLastVersionPublieValue(SpecificContext context);

    /**
     * Renvoie du vocabulaire le label du niveau de lecture, sinon renvoie le
     * code
     *
     * @param niveauLectureCode
     * @return
     */
    String getNiveauLectureLabel(String niveauLectureCode);

    String getSelectedVersion(SpecificContext context);
}

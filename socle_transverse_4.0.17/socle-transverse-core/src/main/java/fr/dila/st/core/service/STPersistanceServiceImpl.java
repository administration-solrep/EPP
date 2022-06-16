package fr.dila.st.core.service;

import fr.dila.st.api.logging.STLogger;
import fr.dila.st.api.logging.enumerationCodes.STLogEnumImpl;
import fr.dila.st.api.service.STPersistanceService;
import fr.dila.st.core.factory.STLogFactory;
import fr.dila.st.core.user.STHistoriqueMDP;
import fr.dila.st.core.user.STPasswordHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import org.apache.commons.codec.Charsets;

/**
 * Implémentation du service de persistence de données. Utilisation de persistence provider et entitymanager
 *
 */
public class STPersistanceServiceImpl extends AbstractPersistenceDefaultComponent implements STPersistanceService {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -4989014118812082817L;

    private static final int TAILLE_HISTORIQUE_MDP = 2;

    private static final STLogger LOGGER = STLogFactory.getLog(STPersistanceServiceImpl.class);

    @Override
    public void saveCurrentPassword(final String currentPassword, final String currentUser, final byte[] salt) {
        accept(
            true,
            entityManager -> {
                savePw(entityManager, currentPassword, currentUser, salt);
            }
        );
    }

    @Override
    public boolean checkPasswordHistory(final String currentValue, final String currentUser) {
        final List<STHistoriqueMDP> historiqueMDP = new ArrayList<>();
        accept(
            true,
            entityManager -> {
                historiqueMDP.addAll(getHistoriqueForUser(entityManager, currentUser));
            }
        );

        if (historiqueMDP != null && !historiqueMDP.isEmpty()) {
            for (STHistoriqueMDP stHistoriqueMDP : historiqueMDP) {
                String valueToCompare = currentValue;
                if (stHistoriqueMDP.getSalt() != null) {
                    valueToCompare =
                        STPasswordHelper.hashPassword(currentValue, stHistoriqueMDP.getSalt().getBytes(Charsets.UTF_8));
                }

                if (valueToCompare.equals(stHistoriqueMDP.getDernierMDP())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Sauvegarde le mot de passe dans la base. Si la taille d'historique max n'a pas été atteinte, on ajoute le mot de
     * passe, sinon, on remplace le plus ancien
     *
     * @param entityManager
     * @param currentPassword
     * @param currentUser
     * @param salt
     */
    private void savePw(EntityManager entityManager, String currentPassword, String currentUser, byte[] salt) {
        List<STHistoriqueMDP> historiqueMDP = getHistoriqueForUser(entityManager, currentUser);

        if (historiqueMDP == null || historiqueMDP.isEmpty() || historiqueMDP.size() < TAILLE_HISTORIQUE_MDP) {
            entityManager.persist(
                new STHistoriqueMDP(currentUser, currentPassword, salt, Calendar.getInstance().getTime())
            );
        } else {
            entityManager.persist(updateHistorique(historiqueMDP, currentPassword, salt));
        }
        entityManager.flush();
    }

    /**
     * Tri la liste d'historique par ordre chronologique, met à jour et retourne le premier élément
     *
     * @param historiqueMDP
     * @param newPassword
     * @param salt
     * @return
     */
    private STHistoriqueMDP updateHistorique(List<STHistoriqueMDP> historiqueMDP, String newPassword, byte[] salt) {
        Collections.sort(historiqueMDP, new DateHistoriqueComparator());
        STHistoriqueMDP historiqueToUpdate = historiqueMDP.get(0);
        historiqueToUpdate.setDernierMDP(newPassword);
        historiqueToUpdate.setSalt(new String(salt));
        historiqueToUpdate.setDateEnregistrement(Calendar.getInstance().getTime());
        return historiqueToUpdate;
    }

    /**
     * Récupère l'historique des mots de passe pour un utilisateur
     *
     * @param entityManager
     * @param user
     * @return List historique des mots de passe, liste vide si aucun résultat
     */
    @SuppressWarnings("unchecked")
    private List<STHistoriqueMDP> getHistoriqueForUser(final EntityManager entityManager, final String user) {
        final Query nativeQuery = entityManager.createQuery(
            "SELECT h FROM  STHistoriqueMDP as h  WHERE  h.login = :currentUser"
        );
        nativeQuery.setParameter("currentUser", user);
        List<STHistoriqueMDP> historiqueMDP = null;
        try {
            historiqueMDP = nativeQuery.getResultList();
        } catch (NoResultException exc) {
            historiqueMDP = null;
            LOGGER.debug(null, STLogEnumImpl.FAIL_EXEC_SQL, exc);
            LOGGER.warn(null, STLogEnumImpl.FAIL_EXEC_SQL, user);
        }
        if (historiqueMDP == null) {
            historiqueMDP = new ArrayList<>();
        }
        return historiqueMDP;
    }

    /**
     * Comparateur pour les dates d'enregistrement des mots de passe
     *
     */
    private static class DateHistoriqueComparator implements Comparator<STHistoriqueMDP> {

        @Override
        public int compare(STHistoriqueMDP histo1, STHistoriqueMDP histo2) {
            return histo1.getDateEnregistrement().compareTo(histo2.getDateEnregistrement());
        }
    }
}

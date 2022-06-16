package fr.dila.solonepp.api.descriptor.evenementtype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XNodeMap;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 * Descripteur des types d'événements.
 *
 * @author jtremeaux
 */
@XObject("evenementType")
public class EvenementTypeDescriptor {
    /**
     * Nom du type d'événement (ex. "EVT01").
     */
    @XNode("@name")
    private String name;

    /**
     * Label du type d'événement (ex. "1 : ...").
     */
    @XNode("@label")
    private String label;

    /**
     * Événement de type créateur : crée un dossier.
     */
    @XNode("@createur")
    private boolean createur;

    /**
     * Événement de type successif : succède un autre événement dans un dossier.
     */
    @XNode("@successif")
    private boolean successif;

    /**
     * Événement de type successif : succède un autre événement dans un dossier.
     */
    @XNode("@limiteSuccesseur")
    private boolean limiteSuccesseur;

    /**
     * Si vrai, chaque version de ce type d'événement nécessite un accusé de réception.
     */
    @XNode("@demandeAr")
    private boolean demandeAr;

    /**
     * Autorise la création d'une version à l'état brouillon (pour initialisation) de ce type d'événement.
     * Valeur par défaut : vrai.
     */
    @XNode("@creerBrouillon")
    private boolean creerBrouillon;

    /**
     * Autorise la création d'une version complétée de ce type d'événement.
     * Valeur par défaut : vrai.
     */
    @XNode("@completer")
    private boolean completer;

    /**
     * Autorise la création d'une version rectifiée de ce type d'événement.
     * Valeur par défaut : vrai.
     */
    @XNode("@rectifier")
    private boolean rectifier;

    /**
     * Autorise l'annulation de ce type d'événement.
     * Valeur par défaut : vrai.
     */
    @XNode("@annuler")
    private boolean annuler;

    /**
     * Identifiant de la procédure (vocabulaire categorie_evenement).
     */
    @XNode("@procedure")
    private String procedure;

    /**
     * Paramètres de distribution du type d'événement.
     */
    @XNode(value = "distribution")
    private DistributionDescriptor distribution;

    private List<String> evenementSuccessifList;

    /**
     * Types de pièces jointes qui peuvent / doivent être fournies pour ce type d'événement.
     */
    @XNodeMap(value = "pieceJointe", key = "@type", type = HashMap.class, componentType = PieceJointeDescriptor.class)
    private Map<String, PieceJointeDescriptor> pieceJointe;

    /**
     * Types de pièces jointes triés par ordre qui peuvent / doivent être fournies pour ce type d'événement
     */
    private Map<String, PieceJointeDescriptor> orderedPieceJointe;

    @XNodeList(value = "evtSuccessif", componentType = String.class, type = String[].class)
    public void setEvenementSuccessifList(String[] evtSuivant) {
        evenementSuccessifList = new ArrayList<String>();
        evenementSuccessifList.addAll(Arrays.asList(evtSuivant));
        Collections.sort(evenementSuccessifList);
    }

    /**
     * Constructeur par défaut de EvenementTypeDescriptor.
     */
    public EvenementTypeDescriptor() {
        creerBrouillon = true;
        completer = true;
        rectifier = true;
        annuler = true;
    }

    /**
     * Getter de name.
     *
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter de name.
     *
     * @param name name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Getter de createur.
     *
     * @return createur
     */
    public boolean isCreateur() {
        return createur;
    }

    /**
     * Setter de createur.
     *
     * @param createur createur
     */
    public void setCreateur(boolean createur) {
        this.createur = createur;
    }

    /**
     * Getter de successif.
     *
     * @return successif
     */
    public boolean isSuccessif() {
        return successif;
    }

    /**
     * Setter de successif.
     *
     * @param successif successif
     */
    public void setSuccessif(boolean successif) {
        this.successif = successif;
    }

    /**
     * Getter de demandeAr.
     *
     * @return demandeAr
     */
    public boolean isDemandeAr() {
        return demandeAr;
    }

    /**
     * Setter de demandeAr.
     *
     * @param demandeAr demandeAr
     */
    public void setDemandeAr(boolean demandeAr) {
        this.demandeAr = demandeAr;
    }

    /**
     * Getter de creerBrouillon.
     *
     * @return creerBrouillon
     */
    public boolean isCreerBrouillon() {
        return creerBrouillon;
    }

    /**
     * Setter de creerBrouillon.
     *
     * @param creerBrouillon creerBrouillon
     */
    public void setCreerBrouillon(boolean creerBrouillon) {
        this.creerBrouillon = creerBrouillon;
    }

    /**
     * Getter de completer.
     *
     * @return completer
     */
    public boolean isCompleter() {
        return completer;
    }

    /**
     * Setter de completer.
     *
     * @param completer completer
     */
    public void setCompleter(boolean completer) {
        this.completer = completer;
    }

    /**
     * Getter de rectifier.
     *
     * @return rectifier
     */
    public boolean isRectifier() {
        return rectifier;
    }

    /**
     * Setter de rectifier.
     *
     * @param rectifier rectifier
     */
    public void setRectifier(boolean rectifier) {
        this.rectifier = rectifier;
    }

    /**
     * Getter de annuler.
     *
     * @return annuler
     */
    public boolean isAnnuler() {
        return annuler;
    }

    /**
     * Setter de annuler.
     *
     * @param annuler annuler
     */
    public void setAnnuler(boolean annuler) {
        this.annuler = annuler;
    }

    /**
     * Getter de distribution.
     *
     * @return distribution
     */
    public DistributionDescriptor getDistribution() {
        return distribution;
    }

    /**
     * Setter de distribution.
     *
     * @param distribution distribution
     */
    public void setDistribution(DistributionDescriptor distribution) {
        this.distribution = distribution;
    }

    /**
     * Getter de pieceJointe.
     *
     * @return pieceJointe
     */
    public Map<String, PieceJointeDescriptor> getPieceJointe() {
        return pieceJointe;
    }

    /**
     * Setter de pieceJointe.
     *
     * @param pieceJointe pieceJointe
     */
    public void setPieceJointe(Map<String, PieceJointeDescriptor> pieceJointe) {
        this.pieceJointe = pieceJointe;
        orderPieceJointe();
    }

    /**
     * Getter de procedure.
     *
     * @return procedure
     */
    public String getProcedure() {
        return procedure;
    }

    /**
     * Setter de procedure.
     *
     * @param procedure procedure
     */
    public void setProcedure(String procedure) {
        this.procedure = procedure;
    }

    /**
     * trie les pieces jointes par ordres
     */
    protected void orderPieceJointe() {
        orderedPieceJointe = new LinkedHashMap<String, PieceJointeDescriptor>();
        if (pieceJointe != null) {
            List<PieceJointeDescriptor> list = new LinkedList<PieceJointeDescriptor>();
            for (String key : pieceJointe.keySet()) {
                PieceJointeDescriptor descriptor = pieceJointe.get(key);
                list.add(descriptor);
            }
            Collections.sort(
                list,
                new Comparator<PieceJointeDescriptor>() {

                    @Override
                    public int compare(PieceJointeDescriptor arg0, PieceJointeDescriptor arg1) {
                        if (arg0.getOrder() != null && arg1.getOrder() != null) {
                            return arg0.getOrder().compareTo(arg1.getOrder());
                        }
                        return 0;
                    }
                }
            );

            for (PieceJointeDescriptor descriptor : list) {
                orderedPieceJointe.put(descriptor.getType(), descriptor);
            }
        }
    }

    /**
     * @return the orderedPieceJointe
     */
    public Map<String, PieceJointeDescriptor> getOrderedPieceJointe() {
        if (orderedPieceJointe == null) {
            orderPieceJointe();
        }
        return orderedPieceJointe;
    }

    /**
     * @param orderedPieceJointe the orderedPieceJointe to set
     */
    public void setOrderedPieceJointe(Map<String, PieceJointeDescriptor> orderedPieceJointe) {
        this.orderedPieceJointe = orderedPieceJointe;
    }

    /**
     * @return the evenementSuccessifList
     */
    public List<String> getEvenementSuccessifList() {
        if (evenementSuccessifList == null) {
            evenementSuccessifList = new ArrayList<String>();
        }
        return evenementSuccessifList;
    }

    /**
     * @param evenementSuccessifList the evenementSuccessifList to set
     */
    public void setEvenementSuccessifList(List<String> evenementSuccessifList) {
        this.evenementSuccessifList = evenementSuccessifList;
    }

    /**
     * @return limiteSuccesseur
     */
    public boolean isLimiteSuccesseur() {
        return limiteSuccesseur;
    }

    /**
     * @param limiteSuccesseur
     */
    public void setLimiteSuccesseur(boolean limiteSuccesseur) {
        this.limiteSuccesseur = limiteSuccesseur;
    }
}

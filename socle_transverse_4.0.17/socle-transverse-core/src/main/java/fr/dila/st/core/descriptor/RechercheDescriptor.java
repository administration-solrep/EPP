package fr.dila.st.core.descriptor;

import fr.dila.st.api.recherche.Recherche;
import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;

/**
 *
 * Une recherche est determinée par un ensemble de paramètres entrées par l'utilisateur (recherche simple, avec
 * mots-clef, sur des actes par exemple). Elle permet de choisir un layout pour le formulaire qui lui sera présenté pour
 * entrer les paramêtres de sa requête.
 *
 * @author jgomez
 *
 */
@XObject("recherche")
public class RechercheDescriptor implements Recherche {
    private static final long serialVersionUID = 1L;

    @XNode("rechercheName")
    private String rechercheName;

    /**
     * Le type de recherche, (avec mots-clefs, avec les meta-données, en plein texte)
     */
    @XNode("type")
    protected String type;

    /** Le mode de recherche (ex : simple ou complexe) */
    @XNode("mode")
    protected String mode;

    /** Le nom du layout associé à cette recherche */
    @XNode("layoutName")
    private String layoutName;

    /** Le nom du contentView associé à cette recherche */
    @XNode("contentViewName")
    private String contentViewName;

    /**
     * Le nom de la ressource sur la laquelle on effectue la recherche.
     */
    @XNode("targetResourceName")
    private String targetResourceName;

    @XNode("requeteName")
    private String requeteName;

    @XNode("isFolded")
    private boolean isFolded;

    /**
     * Default constructor
     */
    public RechercheDescriptor() {
        // do nothing
    }

    public String getRechercheName() {
        return this.rechercheName;
    }

    public String getType() {
        return this.type;
    }

    public String getMode() {
        return this.mode;
    }

    public String getLayoutName() {
        return this.layoutName;
    }

    public String getContentViewName() {
        return this.contentViewName;
    }

    public String getTargetResourceName() {
        return this.targetResourceName;
    }

    public String getRequeteName() {
        return this.requeteName;
    }

    @Override
    public boolean getIsFolded() {
        return isFolded;
    }

    @Override
    public void setIsFolded(boolean folded) {
        this.isFolded = folded;
    }
}

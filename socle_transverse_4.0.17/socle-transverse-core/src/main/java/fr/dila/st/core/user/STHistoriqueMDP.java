package fr.dila.st.core.user;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "HISTORIQUE_MDP")
public class STHistoriqueMDP implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "LOGIN")
    private String login;

    @Column(name = "DERNIER_MDP")
    private String dernierMDP;

    @Column(name = "SALT")
    private String salt;

    @Column(name = "DATE_ENREGISTREMENT")
    private Date dateEnregistrement;

    public STHistoriqueMDP() {}

    public STHistoriqueMDP(String login, String dernierMDP, byte[] salt, Date dateEnregistrement) {
        super();
        this.login = login;
        this.dernierMDP = dernierMDP;
        this.salt = new String(salt);
        this.dateEnregistrement = dateEnregistrement;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getDernierMDP() {
        return dernierMDP;
    }

    public void setDernierMDP(String dernierMDP) {
        this.dernierMDP = dernierMDP;
    }

    public Date getDateEnregistrement() {
        return dateEnregistrement;
    }

    public void setDateEnregistrement(Date dateEnregistrement) {
        this.dateEnregistrement = dateEnregistrement;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}

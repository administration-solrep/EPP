package fr.dila.solonepp.core.assembler.ws;

import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.api.organigramme.OrganigrammeNode;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.Acteur;
import fr.sword.xsd.solon.epp.Circonscription;
import fr.sword.xsd.solon.epp.Civilite;
import fr.sword.xsd.solon.epp.Gouvernement;
import fr.sword.xsd.solon.epp.Identite;
import fr.sword.xsd.solon.epp.IdentiteDenormalise;
import fr.sword.xsd.solon.epp.Institution;
import fr.sword.xsd.solon.epp.InstitutionReference;
import fr.sword.xsd.solon.epp.Mandat;
import fr.sword.xsd.solon.epp.MembreGroupe;
import fr.sword.xsd.solon.epp.Ministere;
import fr.sword.xsd.solon.epp.Organisme;
import fr.sword.xsd.solon.epp.Periode;
import fr.sword.xsd.solon.epp.TypeMandat;
import fr.sword.xsd.solon.epp.TypeOrganisme;
import fr.sword.xsd.solon.epp.TypePeriode;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Assembleur des données des objets tables de référence métier <-> Web service.
 *
 * @author sly
 */
public class TableReferenceAssembler {

    /**
     * Convertit un objet Acteur/Nuxeo en un objet Acteur/Webservices.
     *
     * @param acteur l'objet Acteur  au format Nuxeo
     * @return l'objet Acteur manipulable par les webservices
     */
    public static Acteur toActeurXsd(fr.dila.solonepp.api.domain.tablereference.Acteur acteur) {
        Acteur result = new Acteur();
        result.setId(acteur.getIdentifiant());
        return result;
    }

    /**
     * Renseigne un objet Acteur/Nuxeo avec des données d'un objet Acteur/Webservices
     * la session actuelle.
     *
     * @param acteur l'objet Acteur/Webservices
     * @param doc l'objet Acteur à renseigner
     * @return l'objet renseigné
     */
    public static DocumentModel toActeurDoc(CoreSession session, Acteur acteur, DocumentModel doc) {
        fr.dila.solonepp.api.domain.tablereference.Acteur adapted = doc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Acteur.class
        );
        adapted.setIdentifiant(acteur.getId());
        return doc;
    }

    // *************************************************************
    // Circonscription
    // *************************************************************
    /**
     * Convertit un objet Circonscription/Nuxeo en un objet Circonscription/Webservices.
     *
     * @param circonscription l'objet Circonscription  au format Nuxeo
     * @return l'objet Circonscription manipulable par les webservices
     */
    public static Circonscription toCirconscriptionXsd(
        fr.dila.solonepp.api.domain.tablereference.Circonscription circonscription
    ) {
        Circonscription result = new Circonscription();
        result.setId(circonscription.getIdentifiant());
        result.setNom(circonscription.getNom());
        result.setDateDebut(DateUtil.calendarToXMLGregorianCalendar(circonscription.getDateDebut()));
        result.setDateFin(DateUtil.calendarToXMLGregorianCalendar(circonscription.getDateFin()));
        Institution institution = null;
        if (circonscription.getProprietaire() != null) {
            institution = Institution.valueOf(circonscription.getProprietaire());
        }
        result.setProprietaire(institution);
        return result;
    }

    /**
     * Renseigne un objet Acteur/Nuxeo avec des données d'un objet Acteur/Webservices
     * la session actuelle.
     *
     * @param acteur l'objet Acteur/Webservicesl
     * @param doc l'objet Acteur à renseigner
     * @return l'objet renseigné
     */
    public static DocumentModel toCirconscriptionDoc(
        CoreSession session,
        Circonscription circonscription,
        DocumentModel doc
    ) {
        fr.dila.solonepp.api.domain.tablereference.Circonscription adapted = doc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Circonscription.class
        );

        adapted.setIdentifiant(circonscription.getId());
        if (circonscription.getDateDebut() != null) {
            adapted.setDateDebut(circonscription.getDateDebut().toGregorianCalendar());
        }
        if (circonscription.getDateFin() != null) {
            adapted.setDateFin(circonscription.getDateFin().toGregorianCalendar());
        }
        adapted.setNom(circonscription.getNom());
        adapted.setProprietaire(circonscription.getProprietaire().value());

        return doc;
    }

    // *************************************************************
    // Gouvernement
    // *************************************************************
    /**
     * Convertit un objet Gouvernement/Nuxeo en un objet Gouvernement/Webservices.
     *
     * @param gouvernement l'objet Gouvernement  au format Nuxeo
     * @return l'objet Gouvernement manipulable par les webservices
     */
    public static Gouvernement toGouvernementXsd(fr.dila.solonepp.api.domain.tablereference.Gouvernement gouvernement) {
        Gouvernement result = new Gouvernement();
        result.setId(gouvernement.getIdentifiant());
        result.setAppellation(gouvernement.getAppellation());
        result.setDateDebut(DateUtil.calendarToXMLGregorianCalendar(gouvernement.getDateDebut()));
        result.setDateFin(DateUtil.calendarToXMLGregorianCalendar(gouvernement.getDateFin()));
        result.setMinistereAttache(gouvernement.isMinistereAttache());
        return result;
    }

    /**
     * Renseigne un objet Acteur/Nuxeo avec des données d'un objet Acteur/Webservices
     * la session actuelle.
     *
     * @param acteur l'objet Acteur/Webservices
     * @param doc l'objet Acteur à renseigner
     * @return l'objet renseigné
     */
    public static DocumentModel toGouvernementDoc(CoreSession session, Gouvernement gouvernement, DocumentModel doc) {
        fr.dila.solonepp.api.domain.tablereference.Gouvernement adapted = doc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Gouvernement.class
        );

        adapted.setIdentifiant(gouvernement.getId());
        adapted.setAppellation(gouvernement.getAppellation());
        if (gouvernement.getDateDebut() != null) {
            adapted.setDateDebut(gouvernement.getDateDebut().toGregorianCalendar());
        }
        if (gouvernement.getDateFin() != null) {
            adapted.setDateFin(gouvernement.getDateFin().toGregorianCalendar());
        }

        return doc;
    }

    // *************************************************************
    // Identite
    // *************************************************************
    /**
     * Convertit un objet Identite/Nuxeo en un objet Ide            <xs:element name="date_fin" type="xs:date" minOccurs="0" maxOccurs="1"/>
ntite/Webservices.
     * 
     * @param identite l'objet Identite  au format Nuxeo
     * @return l'objet Identite manipulable par les webservices
     */
    public static Identite toIdentiteXsd(fr.dila.solonepp.api.domain.tablereference.Identite identite) {
        Identite result = new Identite();
        result.setId(identite.getIdentifiant());
        result.setCivilite(Civilite.fromValue(identite.getCivilite()));
        result.setDateDebut(DateUtil.calendarToXMLGregorianCalendar(identite.getDateDebut()));
        if (identite.getDateDebut() != null) {
            result.setDateFin(DateUtil.calendarToXMLGregorianCalendar(identite.getDateFin()));
        }
        result.setDateNaissance(DateUtil.calendarToXMLGregorianCalendar(identite.getDateNaissance()));
        result.setDeptNaissance(identite.getDepartementNaissance());
        result.setLieuNaissance(identite.getLieuNaissance());
        result.setPaysNaissance(identite.getPaysNaissance());
        result.setPrenom(identite.getPrenom());
        result.setIdActeur(identite.getActeur());
        result.setNom(identite.getNom());

        Institution institution = null;
        if (identite.getProprietaire() != null) {
            institution = Institution.valueOf(identite.getProprietaire());
        }
        result.setProprietaire(institution);

        return result;
    }

    /**
     * Renseigne un objet Acteur/Nuxeo avec des données d'un objet Acteur/Webservices
     * la session actuelle.
     *
     * @param acteur l'objet Acteur/Webservices
     * @param doc l'objet Acteur à renseigner
     * @return l'objet renseigné
     */
    public static DocumentModel toIdentiteDoc(CoreSession session, Identite identite, DocumentModel doc) {
        fr.dila.solonepp.api.domain.tablereference.Identite adapted = doc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Identite.class
        );

        adapted.setIdentifiant(identite.getId());

        if (identite.getCivilite() != null) {
            adapted.setCivilite(identite.getCivilite().value());
        }
        if (identite.getDateDebut() != null) {
            adapted.setDateDebut(identite.getDateDebut().toGregorianCalendar());
        }
        if (identite.getDateFin() != null) {
            adapted.setDateFin(identite.getDateFin().toGregorianCalendar());
        }
        if (identite.getDateNaissance() != null) {
            adapted.setDateNaissance(identite.getDateNaissance().toGregorianCalendar());
        }
        if (identite.getProprietaire() != null) {
            adapted.setProprietaire(identite.getProprietaire().value());
        }
        adapted.setDepartementNaissance(identite.getDeptNaissance());
        adapted.setLieuNaissance(identite.getLieuNaissance());
        adapted.setNom(identite.getNom());
        adapted.setPaysNaissance(identite.getPaysNaissance());
        adapted.setPrenom(identite.getPrenom());
        adapted.setActeur(identite.getIdActeur());

        return doc;
    }

    // *************************************************************
    // Mandat
    // *************************************************************
    /**
     * Convertit un objet Mandat/Nuxeo en un objet Mandat/Webservices.
     *
     * @param mandat l'objet Mandat  au format Nuxeo
     * @return l'objet Mandat manipulable par les webservices
     */
    public static Mandat toMandatXsd(CoreSession session, DocumentModel mandatDoc) {
        fr.dila.solonepp.api.domain.tablereference.Mandat mandat = mandatDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Mandat.class
        );
        Mandat result = new Mandat();
        result.setId(mandat.getIdentifiant());
        result.setDateDebut(DateUtil.calendarToXMLGregorianCalendar(mandat.getDateDebut()));
        result.setDateFin(DateUtil.calendarToXMLGregorianCalendar(mandat.getDateFin()));
        if (mandat.getOrdreProtocolaire() != null) {
            result.setOrdreProtocolaire(mandat.getOrdreProtocolaire().intValue());
        }

        Institution institution = null;
        if (mandat.getProprietaire() != null) {
            institution = Institution.valueOf(mandat.getProprietaire());
        }

        result.setProprietaire(institution);
        result.setTitre(mandat.getTitre());

        TypeMandat typeMandat = null;
        if (mandat.getTypeMandat() != null) {
            typeMandat = TypeMandat.valueOf(mandat.getTypeMandat());
        }
        result.setType(typeMandat);
        result.setIdCirconscription(mandat.getCirconscription());
        result.setIdIdentite(mandat.getIdentite());
        result.setIdMinistere(mandat.getMinistere());
        result.setAppellation(mandat.getAppellation());

        final TableReferenceService tableService = SolonEppServiceLocator.getTableReferenceService();

        DocumentModel idDoc = tableService.getIdentiteById(session, mandat.getIdentite());
        IdentiteDenormalise idDenorm = toIdentiteDenormaliseXsd(idDoc);
        result.setIdentiteDenormalise(idDenorm);

        if (mandat.getNor() != null) {
            result.setNor(mandat.getNor());
        }

        return result;
    }

    /**
     * Convertit un objet Mandat/Nuxeo en un objet Mandat/Webservices.
     *
     * @param identiteDoc
     * @return
     */
    public static IdentiteDenormalise toIdentiteDenormaliseXsd(DocumentModel identiteDoc) {
        fr.dila.solonepp.api.domain.tablereference.Identite identite = identiteDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Identite.class
        );
        IdentiteDenormalise idDenorm = new IdentiteDenormalise();

        idDenorm.setCivilite(Civilite.fromValue(identite.getCivilite()));
        idDenorm.setNom(identite.getNom());
        idDenorm.setPrenom(identite.getPrenom());

        return idDenorm;
    }

    /**
     * Renseigne un objet Mandat/Nuxeo avec des données d'un objet Mandat/Webservices
     * la session actuelle.
     *
     * @param mandat l'objet Mandat/Webservices
     * @param doc l'objet Acteur à renseigner
     * @return l'objet renseigné
     */
    public static DocumentModel toMandatDoc(CoreSession session, Mandat mandat, DocumentModel doc) {
        fr.dila.solonepp.api.domain.tablereference.Mandat adapted = doc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Mandat.class
        );

        adapted.setIdentifiant(mandat.getId());
        adapted.setCirconscription(mandat.getIdCirconscription());
        adapted.setAppellation(mandat.getAppellation());

        if (mandat.getDateDebut() != null) {
            adapted.setDateDebut(mandat.getDateDebut().toGregorianCalendar());
        }
        if (mandat.getDateFin() != null) {
            adapted.setDateFin(mandat.getDateFin().toGregorianCalendar());
        }
        adapted.setIdentite(mandat.getIdIdentite());
        adapted.setMinistere(mandat.getIdMinistere());
        if (mandat.getOrdreProtocolaire() != null) {
            adapted.setOrdreProtocolaire(Long.valueOf(mandat.getOrdreProtocolaire()));
        }
        if (mandat.getProprietaire() != null) {
            adapted.setProprietaire(mandat.getProprietaire().value());
        }
        adapted.setTitre(mandat.getTitre());
        if (mandat.getType() != null) {
            adapted.setTypeMandat(mandat.getType().value());
        }
        if (mandat.getAppellation() != null) {
            adapted.setAppellation(mandat.getAppellation());
        }

        if (mandat.getNor() != null) {
            adapted.setNor(mandat.getNor());
        }

        return doc;
    }

    // *************************************************************
    // MembreGroupe
    // *************************************************************
    /**
     * Convertit un objet MembreGroupe/Nuxeo en un objet MembreGroupe/Webservices.
     *
     * @param membreGroupe l'objet MembreGroupe  au format Nuxeo
     * @return l'objet MembreGroupe manipulable par les webservices
     */
    public static MembreGroupe toMembreGroupeXsd(fr.dila.solonepp.api.domain.tablereference.MembreGroupe membreGroupe) {
        MembreGroupe result = new MembreGroupe();
        result.setId(membreGroupe.getIdentifiant());
        result.setIdMandat(membreGroupe.getMandat());
        result.setIdOrganisme(membreGroupe.getOrganisme());
        result.setDateDebut(DateUtil.calendarToXMLGregorianCalendar(membreGroupe.getDateDebut()));
        result.setDateFin(DateUtil.calendarToXMLGregorianCalendar(membreGroupe.getDateFin()));

        return result;
    }

    /**
     * Renseigne un objet MembreGroupe/Nuxeo avec des données d'un objet MembreGroupe/Webservices
     * la session actuelle.
     *
     * @param membreGroupe l'objet MembreGroupe/Webservices
     * @param doc l'objet MembreGroupe à renseigner
     * @return l'objet renseigné
     */
    public static DocumentModel toMembreGroupeDoc(CoreSession session, MembreGroupe membreGroupe, DocumentModel doc) {
        fr.dila.solonepp.api.domain.tablereference.MembreGroupe adapted = doc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.MembreGroupe.class
        );

        adapted.setIdentifiant(membreGroupe.getId());

        if (membreGroupe.getDateDebut() != null) {
            adapted.setDateDebut(membreGroupe.getDateDebut().toGregorianCalendar());
        }
        if (membreGroupe.getDateFin() != null) {
            adapted.setDateFin(membreGroupe.getDateFin().toGregorianCalendar());
        }
        adapted.setMandat(membreGroupe.getIdMandat());
        adapted.setOrganisme(membreGroupe.getIdOrganisme());

        return doc;
    }

    // *************************************************************
    // Ministere
    // *************************************************************

    /**
     * Convertit un objet Ministere/Nuxeo en un objet Ministere/Webservices.
     *
     * @param ministere l'objet Ministere  au format Nuxeo
     * @return l'objet Ministere manipulable par les webservices
     */
    public static Ministere toMinistereXsd(fr.dila.solonepp.api.domain.tablereference.Ministere ministere) {
        Ministere result = new Ministere();
        result.setId(ministere.getIdentifiant());
        result.setAppellation(ministere.getAppellation());
        result.setLibelle(ministere.getLibelleMinistre());
        result.setNom(ministere.getNom());
        result.setDateDebut(DateUtil.calendarToXMLGregorianCalendar(ministere.getDateDebut()));
        result.setDateFin(DateUtil.calendarToXMLGregorianCalendar(ministere.getDateFin()));
        result.setEdition(ministere.getEdition());
        result.setIdGouvernement(ministere.getGouvernement());
        result.setMandatAttache(ministere.isMandatAttache());

        return result;
    }

    /**
     * Renseigne un objet Ministere/Nuxeo avec des données d'un objet Ministere/Webservices
     * la session actuelle.
     *
     * @param ministere l'objet Ministere/Webservices
     * @param doc l'objet Ministere à renseigner
     * @return l'objet renseigné
     */
    public static DocumentModel toMinistereDoc(CoreSession session, Ministere ministere, DocumentModel doc) {
        fr.dila.solonepp.api.domain.tablereference.Ministere adapted = doc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Ministere.class
        );

        adapted.setIdentifiant(ministere.getId());

        if (ministere.getDateDebut() != null) {
            adapted.setDateDebut(ministere.getDateDebut().toGregorianCalendar());
        }
        if (ministere.getDateFin() != null) {
            adapted.setDateFin(ministere.getDateFin().toGregorianCalendar());
        }
        adapted.setEdition(ministere.getEdition());
        adapted.setGouvernement(ministere.getIdGouvernement());
        adapted.setLibelleMinistre(ministere.getLibelle());
        adapted.setNom(ministere.getNom());
        adapted.setAppellation(ministere.getAppellation());

        return doc;
    }

    // *************************************************************
    // Organisme
    // *************************************************************
    /**
     * Convertit un objet Organisme/Nuxeo en un objet Organisme/Webservices.
     *
     * @param organismeDoc l'objet Organisme  au format Nuxeo
     * @return l'objet Organisme manipulable par les webservices
     */
    public static Organisme toOrganismeXsd(DocumentModel organismeDoc) {
        fr.dila.solonepp.api.domain.tablereference.Organisme organisme = organismeDoc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Organisme.class
        );
        Organisme result = new Organisme();
        result.setId(organisme.getIdentifiant());
        result.setNom(organisme.getNom());
        if (organisme.getProprietaire() != null) {
            result.setProprietaire(Institution.valueOf(organisme.getProprietaire()));
        }
        if (organisme.getTypeOrganisme() != null) {
            result.setType(TypeOrganisme.valueOf(organisme.getTypeOrganisme()));
        }

        result.setDateDebut(DateUtil.calendarToXMLGregorianCalendar(organisme.getDateDebut()));
        result.setDateFin(DateUtil.calendarToXMLGregorianCalendar(organisme.getDateFin()));

        if (organisme.getIdCommun() != null) {
            result.setIdCommun(organisme.getIdCommun());
        }
        if (organisme.getBaseLegale() != null) {
            result.setBaseLegale(organisme.getBaseLegale());
        }

        return result;
    }

    /**
     * Renseigne un objet Organisme/Nuxeo avec des données d'un objet Organisme/Webservices
     * la session actuelle.
     *
     * @param organisme l'objet Organisme/Webservices
     * @param doc l'objet Organisme à renseigner
     * @return l'objet renseigné
     */
    public static DocumentModel toOrganismeDoc(CoreSession session, Organisme organisme, DocumentModel doc) {
        fr.dila.solonepp.api.domain.tablereference.Organisme adapted = doc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Organisme.class
        );

        adapted.setIdentifiant(organisme.getId());
        if (organisme.getDateDebut() != null) {
            adapted.setDateDebut(organisme.getDateDebut().toGregorianCalendar());
        }
        if (organisme.getDateFin() != null) {
            adapted.setDateFin(organisme.getDateFin().toGregorianCalendar());
        }
        adapted.setNom(organisme.getNom());
        if (organisme.getProprietaire() != null) {
            adapted.setProprietaire(organisme.getProprietaire().value());
        }

        if (organisme.getType() != null) {
            adapted.setTypeOrganisme(organisme.getType().value());
        }

        if (organisme.getIdCommun() != null) {
            adapted.setIdCommun(organisme.getIdCommun());
        }

        if (organisme.getBaseLegale() != null) {
            adapted.setBaseLegale(organisme.getBaseLegale());
        }

        return doc;
    }

    // *************************************************************
    // Periode
    // *************************************************************
    /**
     * Convertit un objet Periode/Nuxeo en un objet Periode/Webservices.
     *
     * @param periode l'objet Periode  au format Nuxeo
     * @return l'objet Periode manipulable par les webservices
     */
    public static Periode toPeriodeXsd(fr.dila.solonepp.api.domain.tablereference.Periode periode) {
        Periode result = new Periode();
        result.setId(periode.getIdentifiant());
        result.setNumero(periode.getNumero());
        if (periode.getProprietaire() != null) {
            result.setProprietaire(Institution.valueOf(periode.getProprietaire()));
        }
        if (periode.getTypePeriode() != null) {
            result.setType(TypePeriode.valueOf(periode.getTypePeriode()));
        }
        result.setDateDebut(DateUtil.calendarToXMLGregorianCalendar(periode.getDateDebut()));
        result.setDateFin(DateUtil.calendarToXMLGregorianCalendar(periode.getDateFin()));
        return result;
    }

    /**
     * Renseigne un objet Periode/Nuxeo avec des données d'un objet Acteur/Webservices
     * la session actuelle.
     *
     * @param periode l'objet Periode/Webservices
     * @param doc l'objet Periode à renseigner
     * @return l'objet renseigné
     */
    public static DocumentModel toPeriodeDoc(CoreSession session, Periode periode, DocumentModel doc) {
        fr.dila.solonepp.api.domain.tablereference.Periode adapted = doc.getAdapter(
            fr.dila.solonepp.api.domain.tablereference.Periode.class
        );

        if (periode.getDateDebut() != null) {
            adapted.setDateDebut(periode.getDateDebut().toGregorianCalendar());
        }
        if (periode.getDateFin() != null) {
            adapted.setDateFin(periode.getDateFin().toGregorianCalendar());
        }
        adapted.setIdentifiant(periode.getId());
        adapted.setNumero(periode.getNumero());
        if (periode.getProprietaire() != null) {
            adapted.setProprietaire(periode.getProprietaire().value());
        }
        if (periode.getType() != null) {
            adapted.setTypePeriode(periode.getType().value());
        }

        return doc;
    }

    /**
     * Convertit un objet Insitution/Nuxeo en un objet Insitution/Webservices.
     *
     * @param node
     * @return
     */
    public static InstitutionReference toInstitutionReferenceXsd(OrganigrammeNode node) {
        InstitutionReference institution = new InstitutionReference();
        institution.setId(node.getId());
        institution.setLabel(node.getLabel());
        return institution;
    }
}

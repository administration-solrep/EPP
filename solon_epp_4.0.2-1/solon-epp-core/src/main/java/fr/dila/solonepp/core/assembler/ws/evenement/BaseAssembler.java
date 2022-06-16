package fr.dila.solonepp.core.assembler.ws.evenement;

import fr.dila.solonepp.api.assembler.Assembler;
import fr.dila.solonepp.api.domain.evenement.Version;
import fr.dila.solonepp.api.security.principal.EppPrincipal;
import fr.dila.solonepp.api.service.TableReferenceService;
import fr.dila.solonepp.core.assembler.ws.NiveauLectureCodeAssembler;
import fr.dila.solonepp.core.assembler.ws.TableReferenceAssembler;
import fr.dila.solonepp.core.exception.EppNuxeoException;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.st.core.util.DateUtil;
import fr.sword.xsd.solon.epp.Commission;
import fr.sword.xsd.solon.epp.Depot;
import fr.sword.xsd.solon.epp.EvenementType;
import fr.sword.xsd.solon.epp.Mandat;
import fr.sword.xsd.solon.epp.NiveauLecture;
import fr.sword.xsd.solon.epp.NiveauLectureCode;
import fr.sword.xsd.solon.epp.Organisme;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.NuxeoException;

/**
 * Classe abstraite des assembler
 *
 * @author asatre
 *
 */
public abstract class BaseAssembler implements Assembler {
    protected final CoreSession session;
    protected final EppPrincipal principal;

    private static Set<String> NIVEAUX_NUMEROTES = new HashSet<String>();

    static {
        NIVEAUX_NUMEROTES.add(NiveauLectureCode.AN.name());
        NIVEAUX_NUMEROTES.add(NiveauLectureCode.SENAT.name());
    }

    protected BaseAssembler(final CoreSession session, final EppPrincipal principal) {
        this.session = session;
        this.principal = principal;
    }

    @Override
    public abstract EvenementType getEvenementType();

    /**
     * Vérifie la possibilité pour un niveau de lecture d'avoir un numéro de version
     *
     * @param niveau
     * @return vrai si le niveau de lecture peut avoir un numero de version
     */
    public static boolean hasNumeroLecture(final String niveau) {
        return NIVEAUX_NUMEROTES.contains(niveau);
    }

    /**
     * Assemble les données du niveau de lecture XSD -> objet métier.
     *
     * @param niveauLecture Niveau de lecture à assembler
     * @param versionDoc Document version
     */
    protected void assembleNiveauLectureXsdToVersion(final NiveauLecture niveauLecture, final Version version) {
        if (niveauLecture != null) {
            if (niveauLecture.getNiveau() != null) {
                version.setNiveauLectureNumero((long) niveauLecture.getNiveau());
            }
            if (niveauLecture.getCode() != null) {
                version.setNiveauLecture(
                    NiveauLectureCodeAssembler.assembleXsdToNiveauLectureCode(niveauLecture.getCode())
                );
            }
        }
    }

    /**
     * Assemble les données du niveau de lecture objet métier -> XSD.
     *
     * @param version version à assembler
     * @return Niveau de lecture assemblé
     */
    public static NiveauLecture assembleNiveauLectureVersionToXsd(final Version version) {
        final NiveauLecture niveauLecture = new NiveauLecture();
        if (version.getNiveauLectureNumero() != null) {
            niveauLecture.setNiveau(version.getNiveauLectureNumero().intValue());
        }
        if (version.getNiveauLecture() != null) {
            final NiveauLectureCode niveauLectureCode = NiveauLectureCodeAssembler.assembleNiveauLectureCodeToXsd(
                version.getNiveauLecture()
            );
            niveauLecture.setCode(niveauLectureCode);

            if (!hasNumeroLecture(version.getNiveauLecture())) {
                niveauLecture.setNiveau(null);
            }
        }
        return niveauLecture;
    }

    /**
     * Assemble les données d'un mandat WS -> Identifiant. Vérifie la présence du mandat.
     *
     * @param mandat Mandat WS
     * @return Identifiant technique du mandat
     */
    protected String getMandatIdentifiant(final Mandat mandat) {
        if (mandat == null || mandat.getId() == null) {
            return null;
        }

        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        final boolean exist = tableReferenceService.hasMandat(session, mandat.getId());
        if (exist) {
            return mandat.getId();
        } else {
            throw new NuxeoException("Mandat inexistant: " + mandat.getId());
        }
    }

    /**
     * Assemble les données d'une liste de mandats WS -> Identifiant. Vérifie la présence des mandats.
     *
     * @param mandat Mandat WS
     * @return Identifiant technique du mandat
     */
    protected List<String> getMandatsIdentifiant(final List<Mandat> mandatList) {
        final List<String> mandatIdList = new ArrayList<String>();
        if (mandatList != null) {
            for (final Mandat mandat : mandatList) {
                final String mandatId = getMandatIdentifiant(mandat);
                mandatIdList.add(mandatId);
            }
        }
        return mandatIdList;
    }

    /**
     * Assemble les données du dépot de texte XSD -> objet métier.
     *
     * @param depot Dépot à assembler
     * @param versionDoc Document version
     */
    protected void assembleDepotTexteXsdToVersion(final Depot depot, final Version version) {
        if (depot != null) {
            if (depot.getDate() != null) {
                version.setDateDepotTexte(depot.getDate().toGregorianCalendar());
            }
            version.setNumeroDepotTexte(depot.getNumero());
        }
    }

    /**
     * Assemble les données du dépot de texte objet métier -> XSD.
     *
     * @param version Version à assembler
     * @return Objet dépot WS
     */
    protected Depot assembleDepotTexteVersionToXsd(final Version version) {
        Depot depot = null;
        if (version.getDateDepotTexte() != null || !StringUtils.isBlank(version.getNumeroDepotTexte())) {
            depot = new Depot();
            if (version.getDateDepotTexte() != null) {
                depot.setDate(DateUtil.calendarToXMLGregorianCalendar(version.getDateDepotTexte()));
            }
            depot.setNumero(version.getNumeroDepotTexte());
        }
        return depot;
    }

    /**
     * Assemble les données des commissions objet métier -> XSD.
     *
     * @param version Version à assembler
     * @return Objet dépot WS
     */
    protected Commission assembleCommissionVersionToXsd(final Version version) {
        Commission commission = null;
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        if (
            StringUtils.isNotBlank(version.getCommissionSaisieAuFond()) || version.getCommissionSaisiePourAvis() != null
        ) {
            commission = new Commission();
            DocumentModel organismeDoc = tableReferenceService.getOrganismeById(
                session,
                version.getCommissionSaisieAuFond()
            );
            if (organismeDoc != null) {
                commission.setSaisieAuFond(TableReferenceAssembler.toOrganismeXsd(organismeDoc));
            }
            if (version.getCommissionSaisiePourAvis() != null) {
                for (final String organismeId : version.getCommissionSaisiePourAvis()) {
                    organismeDoc = tableReferenceService.getOrganismeById(session, organismeId);
                    if (organismeDoc != null) {
                        commission.getSaisiePourAvis().add(TableReferenceAssembler.toOrganismeXsd(organismeDoc));
                    }
                }
            }
        }
        return commission;
    }

    /**
     * Assemble les données des commissions XSD -> objet métier.
     *
     * @param commission Commission à assembler
     * @param version version
     */
    protected void assembleCommissionXsdToVersion(final Commission commission, final Version version) {
        if (commission != null) {
            final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
            final Organisme commissionSaisieAuFond = commission.getSaisieAuFond();
            if (commissionSaisieAuFond != null && commissionSaisieAuFond.getId() != null) {
                final boolean exist = tableReferenceService.hasOrganisme(session, commissionSaisieAuFond.getId());
                if (exist) {
                    version.setCommissionSaisieAuFond(commissionSaisieAuFond.getId());
                } else {
                    throw new NuxeoException(
                        "Commission saisie au fond, organisme inexistant: " + commissionSaisieAuFond.getId()
                    );
                }
            }
            final List<Organisme> saisiePourAvis = commission.getSaisiePourAvis();
            if (saisiePourAvis != null) {
                final List<String> commissionPourAvis = new ArrayList<String>();
                for (final Organisme commissionSaisiePourAvis : saisiePourAvis) {
                    if (commissionSaisiePourAvis.getId() != null) {
                        final boolean exist = tableReferenceService.hasOrganisme(
                            session,
                            commissionSaisiePourAvis.getId()
                        );
                        if (exist) {
                            commissionPourAvis.add(commissionSaisiePourAvis.getId());
                        } else {
                            throw new NuxeoException(
                                "Commission saisie au fond, organisme inexistant: " + commissionSaisiePourAvis.getId()
                            );
                        }
                    }
                }
                version.setCommissionSaisiePourAvis(commissionPourAvis);
            }
        }
    }

    /**
     * Assemble les données du dépot de rapport XSD -> objet métier.
     *
     * @param depot Dépot à assembler
     * @param version version
     */
    protected void assembleDepotRapportXsdToVersion(final Depot depot, final Version version) {
        if (depot != null) {
            if (depot.getDate() != null) {
                version.setDateDepotRapport(depot.getDate().toGregorianCalendar());
            }
            version.setNumeroDepotRapport(depot.getNumero());
        }
    }

    /**
     * Assemble les données du dépot de texte objet métier -> XSD.
     *
     * @param versionDoc Version à assembler
     * @return Objet dépot WS
     */
    protected Depot assembleDepotRapportVersionToXsd(final Version version) {
        Depot depot = null;
        if (version.getDateDepotRapport() != null || !StringUtils.isBlank(version.getNumeroDepotRapport())) {
            depot = new Depot();
            if (version.getDateDepotRapport() != null) {
                depot.setDate(DateUtil.calendarToXMLGregorianCalendar(version.getDateDepotRapport()));
            }
            depot.setNumero(version.getNumeroDepotRapport());
        }
        return depot;
    }

    /**
     * Récupère et assemble une commission (organisme)
     *
     * @param organismeId
     * @return
     */
    protected Organisme assembleCommission(final String organismeId) {
        final TableReferenceService tableReferenceService = SolonEppServiceLocator.getTableReferenceService();
        final DocumentModel organismeDoc = tableReferenceService.getOrganismeById(session, organismeId);
        if (organismeDoc == null) {
            throw new NuxeoException("Commission, organisme inexistant: " + organismeId);
        }
        return TableReferenceAssembler.toOrganismeXsd(organismeDoc);
    }

    protected void doVerifyCopy() {
        if (this.getEppBaseEvenement() != null && this.getEppBaseEvenement().getCopie().size() > 0) {
            throw new EppNuxeoException("Cette communication ne peut pas contenir des copies");
        }
    }
}

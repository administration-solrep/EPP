package fr.dila.solonepp.core.assembler.ws;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.st.api.constant.STVocabularyConstants;
import fr.sword.xsd.solon.epp.AttributionCommissionReference;
import fr.sword.xsd.solon.epp.MotifIrrecevabiliteReference;
import fr.sword.xsd.solon.epp.NatureLoiReference;
import fr.sword.xsd.solon.epp.NatureRapportReference;
import fr.sword.xsd.solon.epp.NiveauLectureCodeReference;
import fr.sword.xsd.solon.epp.RapportParlementReference;
import fr.sword.xsd.solon.epp.ResultatCmpReference;
import fr.sword.xsd.solon.epp.SensAvisReference;
import fr.sword.xsd.solon.epp.SortAdoptionReference;
import fr.sword.xsd.solon.epp.TypeActeReference;
import fr.sword.xsd.solon.epp.TypeFusionReference;
import fr.sword.xsd.solon.epp.TypeLoiReference;

/**
 * Assembler pour les vocaulaires
 * 
 * @author Fabio Esposito 
 *
 */
public class VocabulaireReferenceAssembler {


    private static String getLabel(DocumentModel doc) throws ClientException {
        return (String) doc.getProperty(STVocabularyConstants.VOCABULARY, STVocabularyConstants.COLUMN_LABEL);
    }
    
    private static String getId(DocumentModel doc) throws ClientException {
        return (String) doc.getProperty(STVocabularyConstants.VOCABULARY, STVocabularyConstants.COLUMN_ID);
    }

    /**
     * Convertit un objet document vocabulaire AttributionCommission en un objet AttributionCommissionReference/Webservices.
     * 
     * @param document nuxeo vocabulaire
     * @return l'objet manipulable par les webservices
     * @throws ClientException 
     */
    public static AttributionCommissionReference toAttributionCommissionReferenceXsd(DocumentModel doc) throws ClientException {
        AttributionCommissionReference result = new AttributionCommissionReference();
        result.setId(getId(doc));
        result.setLabel(getLabel(doc));       
        return result;
    }
        
    /**
     * Convertit un objet document vocabulaire NatureLoi en un objet NatureLoiReference/Webservices.
     * 
     * @param document nuxeo vocabulaire
     * @return l'objet manipulable par les webservices
     * @throws ClientException 
     */
    public static NatureLoiReference toNatureLoiReferenceXsd(DocumentModel doc) throws ClientException {
        NatureLoiReference result = new NatureLoiReference();
        result.setId(getId(doc));
        result.setLabel(getLabel(doc));       
        return result;
    }
    
    /**
     * Convertit un objet document vocabulaire NatureRapport en un objet NatureRapportReference/Webservices.
     * 
     * @param document nuxeo vocabulaire
     * @return l'objet manipulable par les webservices
     * @throws ClientException 
     */
    public static NatureRapportReference toNatureRapportReferenceXsd(DocumentModel doc) throws ClientException {
        NatureRapportReference result = new NatureRapportReference();
        result.setId(getId(doc));
        result.setLabel(getLabel(doc));       
        return result;
    }
    
    /**
     * Convertit un objet document vocabulaire TypeLoi en un objet TypeLoiReference/Webservices.
     * 
     * @param document nuxeo vocabulaire
     * @return l'objet manipulable par les webservices
     * @throws ClientException 
     */
    public static TypeLoiReference toTypeLoiReferenceXsd(DocumentModel doc) throws ClientException {
        TypeLoiReference result = new TypeLoiReference();
        result.setId(getId(doc));
        result.setLabel(getLabel(doc));       
        return result;
    }
    
    /**
     * Convertit un objet document vocabulaire SortAdoption en un objet SortAdoptionReference/Webservices.
     * 
     * @param document nuxeo vocabulaire
     * @return l'objet manipulable par les webservices
     * @throws ClientException 
     */
    public static SortAdoptionReference toSortAdoptionReferenceXsd(DocumentModel doc) throws ClientException {
        SortAdoptionReference result = new SortAdoptionReference();
        result.setId(getId(doc));
        result.setLabel(getLabel(doc));       
        return result;
    }
    
    /**
     * Convertit un objet document vocabulaire MotifIrrecevabilite en un objet MotifIrrecevabiliteReference/Webservices.
     * 
     * @param document nuxeo vocabulaire
     * @return l'objet manipulable par les webservices
     * @throws ClientException 
     */
    public static MotifIrrecevabiliteReference toMotifIrrecevabiliteReferenceXsd(DocumentModel doc) throws ClientException {
        MotifIrrecevabiliteReference result = new MotifIrrecevabiliteReference();
        result.setId(getId(doc));
        result.setLabel(getLabel(doc));       
        return result;
    }
    
    /**
     * Convertit un objet document vocabulaire SensAvis en un objet SensAvisReference/Webservices.
     * 
     * @param document nuxeo vocabulaire
     * @return l'objet manipulable par les webservices
     * @throws ClientException 
     */
    public static SensAvisReference toSensAvisReferenceXsd(DocumentModel doc) throws ClientException {
        SensAvisReference result = new SensAvisReference();
        result.setId(getId(doc));
        result.setLabel(getLabel(doc));       
        return result;
    }

    /**
     * Convertit un objet document vocabulaire NiveauLecture en un objet NiveauLectureCodeReference/Webservices.
     * @param doc
     * @return
     * @throws ClientException
     */
    public static NiveauLectureCodeReference toNiveauLectureCodeReferenceXsd(DocumentModel doc) throws ClientException {
        NiveauLectureCodeReference result = new NiveauLectureCodeReference();
        result.setId(getId(doc));
        result.setLabel(getLabel(doc));       
        return result;
    }
    
    /**
     * Convertit un objet document vocabulaire RapportParlement en un objet RapportParlementReference/Webservices.
     * @param doc
     * @return
     * @throws ClientException
     */
    public static RapportParlementReference toRapportParlementReferenceXsd(DocumentModel doc) throws ClientException {
        RapportParlementReference result = new RapportParlementReference();
        result.setId(getId(doc));
        result.setLabel(getLabel(doc));       
        return result;
    }
    
    /**
     * Convertit un objet document vocabulaire RapportParlement en un objet RapportParlementReference/Webservices.
     * @param doc
     * @return
     * @throws ClientException
     */
    public static ResultatCmpReference toResultatCmpReferenceXsd(DocumentModel doc) throws ClientException {
        ResultatCmpReference result = new ResultatCmpReference();
        result.setId(getId(doc));
        result.setLabel(getLabel(doc));       
        return result;
    }
    
    /**
     * Convertit un objet document vocabulaire TypeFusion en un objet TypeFusionReference/Webservices.
     * @param doc
     * @return
     * @throws ClientException
     */
    public static TypeFusionReference toTypeFusionReferenceCodeXsd(DocumentModel doc) throws ClientException {
        TypeFusionReference result = new TypeFusionReference();
        result.setId(getId(doc));
        result.setLabel(getLabel(doc));       
        return result;
    }

    /**
     * Convertit un objet document vocabulaire TypeActe en un objet TypeActeReference/Webservices.
     * @param docModel
     * @return
     * @throws ClientException 
     */
    public static TypeActeReference toTypeActeReferenceXsd(DocumentModel doc) throws ClientException {
        TypeActeReference result = new TypeActeReference();
        result.setId(getId(doc));
        result.setLabel(getLabel(doc));    
        return result;
    }
}

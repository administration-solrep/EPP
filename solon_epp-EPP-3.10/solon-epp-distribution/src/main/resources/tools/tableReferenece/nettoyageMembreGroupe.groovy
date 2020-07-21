import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

import fr.dila.solonepp.api.domain.tablereference.MembreGroupe;
import fr.dila.solonepp.api.domain.tablereference.Organisme;
import fr.sword.xsd.solon.epp.TypeOrganisme;
import fr.dila.solonepp.core.service.SolonEppServiceLocator;
import fr.dila.solonepp.api.constant.SolonEppConstant;

    print "................... DEBUT DU SCRIPT ..................."; 	


    Map<String, List<DocumentModel>> groups = new HashMap<String, List<DocumentModel>>();

    List<String> groupAnOrganismeList = new ArrayList<String>();

    // STEP1: Get All Organisme of type GROUP_AN
    try {
      List<DocumentModel> allOrganisme = SolonEppServiceLocator.getTableReferenceService().findAllOrganisme(Session, false);
      for (DocumentModel documentModel : allOrganisme) {
        Organisme organisme = documentModel.getAdapter(Organisme.class);
        String orgType = organisme.getTypeOrganisme();
        if (orgType != null && TypeOrganisme.valueOf(orgType).equals(TypeOrganisme.GROUPE_AN) && !groupAnOrganismeList.contains(organisme.getIdentifiant())) {
          groupAnOrganismeList.add(organisme.getIdentifiant());
        }
      }

    } catch (ClientException e1) {
      print e1.getMessage() ;
    }

    // STEP2: Get All Documents of type MEMBRE_GROUPE
    List<DocumentModel> allMembreGroup = null;
    try {
      allMembreGroup = SolonEppServiceLocator.getTableReferenceService().findAllMembreGroupe(Session);
      for (DocumentModel documentModel : allMembreGroup) {
        MembreGroupe membreGroupe = documentModel.getAdapter(MembreGroupe.class);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyyHH:mm:ss");
        String dateDebut = membreGroupe.getDateDebut() != null ? sdf.format(membreGroupe.getDateDebut().getTime()) : "";
        Calendar dateFin = membreGroupe.getDateFin();
        boolean isGroupMandat = groupAnOrganismeList.contains(membreGroupe.getOrganisme());

        if (dateFin == null && isGroupMandat) {
          
          String mandat = membreGroupe.getMandat();
          String organisme = membreGroupe.getOrganisme() ;
          String key = "@"+dateDebut + "@" + mandat+"@"+organisme+"@";

          List<DocumentModel> documentsGroupByDateAndMandat = null;
          if (groups.containsKey(key)) {
            documentsGroupByDateAndMandat = groups.get(key);
          } else {
            documentsGroupByDateAndMandat = new ArrayList<DocumentModel>();
            groups.put(key, documentsGroupByDateAndMandat);
          }
          documentsGroupByDateAndMandat.add(documentModel);
        }
      }

    } catch (ClientException e) {
	print e.getMessage() ;
    }

    List<DocumentModel> toBeUpdatedDocs = new ArrayList<DocumentModel>();

   // STEP3: for each groupe in the map find the minimum and add all the rest to the removed list
    for (Map.Entry<String, List<DocumentModel>> entry : groups.entrySet()) {
      List<DocumentModel> value = entry.getValue();
      DocumentModel toBeremoved = null;
      
      int min = -1;

      if (value.size() > 1) {
        for (DocumentModel docModel : value) {
          MembreGroupe mbGroupe = docModel.getAdapter(MembreGroupe.class);
          
          String mgConstant = SolonEppConstant.MEMBRE_GROUPE_DOC_TYPE ;  
          String identifiant  = mbGroupe.getIdentifiant() ;   
          int pos  = identifiant.indexOf(mgConstant) ;
          
          
          try{

          	String intIdent = identifiant.substring(pos+mgConstant.length(), identifiant.length()) ;
          	
          	int ident = new Integer(intIdent).intValue()  ;
          	if (min == -1 || ident < min) {
            		min = ident;
            		toBeremoved = docModel;
          	}
          
          }catch (NumberFormatException e) {
      		print "Erreur : identifiant du membre.group non entier" +identifiant ; 
    	  }
          
        }
      }

      if (toBeremoved != null) {
        // Remove The Smallest from List
        value.remove(toBeremoved);
        toBeUpdatedDocs.addAll(value);
      }
     }
      //Finally Update DATEFIN for all documents
      for (DocumentModel toBeUpdatedDoc : toBeUpdatedDocs) {
        MembreGroupe toBeUpdateMG = toBeUpdatedDoc.getAdapter(MembreGroupe.class);
        Calendar dateFin = Calendar.getInstance();
        dateFin.set(2012, 6, 26, 22, 0, 0) ;
        toBeUpdateMG.setDateFin(dateFin);
        try {
          SolonEppServiceLocator.getTableReferenceService().updateMembreGroupe(Session, toBeUpdatedDoc);
        } catch (ClientException e) {
           print e.getMessage() ;
        }
      }

      
      
     return "................... FIN DU SCRIPT " +toBeUpdatedDocs.size()+ " document updated ..................."; 
      

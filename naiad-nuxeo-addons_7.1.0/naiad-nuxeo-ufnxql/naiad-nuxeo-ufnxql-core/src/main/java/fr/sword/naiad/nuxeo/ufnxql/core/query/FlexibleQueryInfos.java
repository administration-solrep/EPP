package fr.sword.naiad.nuxeo.ufnxql.core.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.sword.naiad.nuxeo.ufnxql.core.query.FlexibleQueryMaker.KeyCode;

/**
 * Construit sur une requete FNXQL 
 * Extrait les mot clé du debut de chaine 
 * 
 * @author SPL
 *
 */
class FlexibleQueryInfos {
    
    private final String queryWithoutCode;
    private final List<KeyCode> keycodes;
    private final Map<KeyCode, List<String>> keyargs;
    
    
    public FlexibleQueryInfos(String query){
        String tmpQuery = query;
        this.keycodes = new ArrayList<KeyCode>();
        this.keyargs = new HashMap<KeyCode, List<String>>();
        
        KeyCode kcode = extractKeyCode(tmpQuery);
        while(kcode != null){
            keycodes.add(kcode);
            tmpQuery = tmpQuery.substring(kcode.getKey().length());
            List<String> args = new ArrayList<String>();            
            String arg = extractArg(tmpQuery);
            while(arg != null){
                args.add(arg);
                tmpQuery = tmpQuery.substring(arg.length()+2);
                arg = extractArg(tmpQuery);
            }
            if(!args.isEmpty()){
                keyargs.put(kcode, args);
            }
            kcode = extractKeyCode(tmpQuery);
        }
        
        this.queryWithoutCode = tmpQuery;
    }
    
    /**
     * extract the first matchin keycode at the beginning of the query
     * @param query
     * @return
     */
    private KeyCode extractKeyCode(String query){
        for(KeyCode kc : KeyCode.values()){
            if(kc.match(query)){
                return kc;
            }
        }
        return null;
    }
    
    private String extractArg(String query){
        if(!query.isEmpty() && query.charAt(0) == '['){
            int idx = query.indexOf(']');
            if(idx == 1){
                return "";
            } else { 
                return query.substring(1, idx);
            }
        }
        return null;
    }
    
    /** read accessors */
    
    public String getQueryWithoutCode(){
        return queryWithoutCode;
    }
    
    public List<KeyCode> getKeyCodes(){
        return keycodes;
    }
    
    public boolean hasKeyCode(KeyCode kc){
        return keycodes.contains(kc);
    }
    
    public List<String> getArgs(KeyCode kc){
        return keyargs.get(kc);
    }
}

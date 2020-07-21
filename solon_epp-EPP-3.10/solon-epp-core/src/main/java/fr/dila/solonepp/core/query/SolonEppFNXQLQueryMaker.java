package fr.dila.solonepp.core.query;

import java.util.HashMap;
import java.util.Map;

import fr.dila.solonepp.api.constant.SolonEppConstant;
import fr.dila.solonepp.api.constant.SolonEppSchemaConstant;
import fr.dila.st.core.query.FlexibleQueryMaker;

public class SolonEppFNXQLQueryMaker extends FlexibleQueryMaker {

    protected static final Map<String, String> mapTypeSchema = new HashMap<String, String>();

    static {
        // Dossier
        mapTypeSchema.put(SolonEppConstant.DOSSIER_DOC_TYPE, SolonEppSchemaConstant.DOSSIER_SCHEMA);

        mapTypeSchema.put(SolonEppConstant.EVENEMENT_DOC_TYPE, SolonEppSchemaConstant.EVENEMENT_SCHEMA);

        mapTypeSchema.put(SolonEppConstant.VERSION_DOC_TYPE, SolonEppSchemaConstant.VERSION_SCHEMA);

        mapTypeSchema.put(SolonEppConstant.MESSAGE_DOC_TYPE, SolonEppSchemaConstant.CASE_LINK_SCHEMA);

        //mapTypeSchema.put(SolonEppConstant.PIECE_JOINTE_DOC_TYPE, SolonEppSchemaConstant.PIECE_JOINTE_SCHEMA);

        //mapTypeSchema.put(SolonEppConstant.PIECE_JOINTE_FICHIER_DOC_TYPE, STSchemaConstant.FILE_SCHEMA);

        // schema Mailbox
        mapTypeSchema.put(SolonEppConstant.MAILBOX_DOC_TYPE, SolonEppConstant.MAILBOX_SCHEMA);

        // Identite
        mapTypeSchema.put(SolonEppConstant.IDENTITE_DOC_TYPE, SolonEppSchemaConstant.IDENTITE_SCHEMA);

        // Mandat
        mapTypeSchema.put(SolonEppConstant.MANDAT_DOC_TYPE, SolonEppSchemaConstant.MANDAT_SCHEMA);

        // Organisme
        mapTypeSchema.put(SolonEppConstant.ORGANISME_DOC_TYPE, SolonEppSchemaConstant.ORGANISME_SCHEMA);

    }

    public SolonEppFNXQLQueryMaker() {
        super(mapTypeSchema);
    }
}

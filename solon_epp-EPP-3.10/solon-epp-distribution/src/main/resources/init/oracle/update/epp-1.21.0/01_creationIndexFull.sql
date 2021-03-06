-- Création d'un index fulltext --
CREATE INDEX "FULLTEXT_FULLTEXT_IDX" ON "FULLTEXT" ("FULLTEXT") INDEXTYPE IS "CTXSYS"."CONTEXT"  PARAMETERS (' SYNC (ON COMMIT) TRANSACTIONAL');
COMMIT;

-- Configuration de du lexer ORACLE utilisé pour l'analyseur fulltext
CREATE PROCEDURE "INIT_INDEX"
IS
BEGIN
        CTXSYS.CTX_DDL.CREATE_PREFERENCE ('EPP_LEXER', 'BASIC_LEXER');
        CTXSYS.CTX_DDL.SET_ATTRIBUTE ('EPP_LEXER', 'base_letter', 'YES');
        CTXSYS.CTX_DDL.SET_ATTRIBUTE ('EPP_LEXER', 'index_stems', 'FRENCH');
        CTXSYS.CTX_DDL.CREATE_PREFERENCE('EPP_WORDLIST', 'BASIC_WORDLIST');
        CTXSYS.CTX_DDL.SET_ATTRIBUTE('EPP_WORDLIST', 'stemmer', 'FRENCH');
        CTXSYS.CTX_DDL.SET_ATTRIBUTE('EPP_WORDLIST', 'substring_index', 'NO');
        CTXSYS.CTX_DDL.SET_ATTRIBUTE('EPP_WORDLIST', 'prefix_index', 'NO');
END;
/

CALL INIT_INDEX();

COMMIT;
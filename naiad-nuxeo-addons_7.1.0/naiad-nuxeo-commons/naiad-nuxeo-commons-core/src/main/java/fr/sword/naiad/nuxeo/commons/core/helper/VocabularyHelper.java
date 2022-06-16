package fr.sword.naiad.nuxeo.commons.core.helper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.nuxeo.common.utils.i18n.I18NUtils;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.ecm.directory.Session;
import org.nuxeo.ecm.directory.api.DirectoryService;

import com.ibm.icu.text.Collator;

import fr.sword.naiad.nuxeo.commons.core.util.PropertyUtil;
import fr.sword.naiad.nuxeo.commons.core.util.ServiceUtil;

/**
 * s'inspire de org.nuxeo.ecm.webapp.directoryVocabularyTreeNode
 * 
 * A vocabulary tree node based on l10nvocabulary or l10nxvocabulary directory. These schemas store translations in
 * columns of the form label_xx_XX or label_xx. The label of a node is retrieved from column label_xx_XX (where xx_XX is
 * the current locale name) if it exists, from column label_xx (where xx is the current locale language) else. If this
 * one doesn't exist either, the english label (from label_en) is used.
 * 
 * @author SPL
 * 
 */
public final class VocabularyHelper {
    public static final String PARENT_FIELD_ID = "parent";
    public static final String OBSOLETE_FIELD = "obsolete";
    public static final String L10NXVOCABULARY_SCHEMA = "l10nxvocabulary";
    public static final String XVOCABULARY_SCHEMA = "xvocabulary";

    public static final String LABEL_FIELD = "label";
    public static final String LABEL_FIELD_PREFIX = "label_";
    public static final String DEFAULT_LANGUAGE = "en";

    public static final String ORDERING_FIELD = "ordering";

    /**
     * Liste des types de comparateurs.
     * 
     * @author SPL
     */
    public enum VocOrder {
        OrderingField,
        Label
    };

    /**
     * Constructeur privé.
     */
    private VocabularyHelper() {
        // Classe utilitaire
    }

    /**
     * Conteneur d'une entrée.
     * 
     * @author SPL
     */
    public static class Entry {
        private final String entryId;
        private final String label;
        private final Long ordering;
        private final Long obsolete;
        private final String parentId;

        public Entry(final String entryId, final String label, final Long ordering, final Long obsolete,
                final String parentId) {
            this.entryId = entryId;
            this.label = label;
            if (ordering == null) {
                this.ordering = 0L;
            } else {
                this.ordering = ordering;
            }
            this.obsolete = obsolete;
            this.parentId = parentId;
        }

        public Entry(final String entryId, final String label, final Long ordering, final Long obsolete) {
            this(entryId, label, ordering, obsolete, null);
        }

        public String getId() {
            return entryId;
        }

        public String getLabel() {
            return label;
        }

        public Long getOrdering() {
            return ordering;
        }

        public boolean isObsolete() {
            if (obsolete != null) {
                return obsolete != 0;
            }
            return false;
        }

        public Long getObsoleteAsLong() {
            return obsolete;
        }

        public String getParentId() {
            return parentId;
        }

    }

    /**
     * Compare deux entrées grâce à leur label.
     * 
     * @author SPL
     */
    public static class LabelComparator implements Serializable, Comparator<Entry> {

        /**
         * Serial UID généré.
         */
        private static final long serialVersionUID = -4951486746160673761L;

        private Collator collator;
        private Locale locale;

        /**
         * Constructeur par défaut.
         */
        public LabelComparator() {
            super();
        }

        public LabelComparator(final Locale locale) {
            super();
            try {
                this.locale = locale;
                collator = Collator.getInstance(this.locale);
                collator.setStrength(Collator.TERTIARY);
            } catch (final NullPointerException e) {
                //locale est null
            }
        }

        @Override
        public int compare(final Entry entry1, final Entry entry2) {
            if (locale != null) {
                return collator.compare(entry1.getLabel(), entry2.getLabel());
            } else {
                return entry1.getLabel().compareTo(entry2.getLabel());
            }

        }

    }

    /**
     * Compare deux entrées grâce leur champ de tri.
     * 
     * @author fmh
     */
    public static class OrderingComparator implements Serializable, Comparator<Entry> {

        /**
         * Serial UID généré.
         */
        private static final long serialVersionUID = -945054069642381344L;

        /**
         * Constructeur par défaut.
         */
        public OrderingComparator() {
            super();
        }

        @Override
        public int compare(final Entry entry1, final Entry entry2) {
            final long order1 = entry1.getOrdering();
            final long order2 = entry2.getOrdering();
            return (int) (order1 - order2);
        }

    }

    /**
     * Liste les valeurs d'un vocabulaire
     * @param vocabularyName
     * @param order
     * @param displayObsoleteEntries inclure les entrées obsolètes
     * @return la liste des valeurs d'un vocabulaire
     * @throws NuxeoException
     */
    public static List<Entry> listValue(final String vocabularyName, final VocOrder order,
            final boolean displayObsoleteEntries) throws NuxeoException {
        return listValueForParent(vocabularyName, order, displayObsoleteEntries, null);
    }

    public static List<Entry> listValueForParent(final String vocabularyName, final VocOrder order,
            final boolean displayObsoleteEntries, final String parent) throws NuxeoException {
        final DirectoryService directoryService = getService();
        final String schemaName = directoryService.getDirectorySchema(vocabularyName);
        final DocumentModelList resultDocs = getChildrenEntries(directoryService, vocabularyName,
                displayObsoleteEntries, parent);

        final List<Entry> results = toEntries(schemaName, resultDocs, null);
        // sort children		
        return orderEntryList(order, results);
    }

    /**
     * Liste les valeurs d'un vocabulaire triées selon un comparateur
     * @param cmp le comparateur
     * @param vocabularyName
     * @param displayObsoleteEntries inclure les entrées obsolètes
     * @return la liste des valeurs d'un vocabulaire
     * @throws NuxeoException
     */
    public static List<Entry> listValue(final Comparator<? super Entry> cmp, final String vocabularyName,
            final boolean displayObsoleteEntries) throws NuxeoException {
        return listValueForParent(cmp, vocabularyName, displayObsoleteEntries, null);
    }

    public static List<Entry> listValueForParent(final Comparator<? super Entry> cmp, final String vocabularyName,
            final boolean displayObsoleteEntries, final String parent) throws NuxeoException {
        final DirectoryService directoryService = getService();
        final String schemaName = directoryService.getDirectorySchema(vocabularyName);
        final DocumentModelList resultDocs = getChildrenEntries(directoryService, vocabularyName,
                displayObsoleteEntries, parent);

        final List<Entry> results = toEntries(schemaName, resultDocs, null);
        // sort children		
        return orderEntryList(cmp, results);
    }

    /**
     * Recupere une entrée depuis un id
     * @param vocabularyName
     * @param entryid
     * @return l'entree associé à l'id
     * @throws NuxeoException
     */
    public static Entry getValueById(final String vocabularyName, final String entryid) throws NuxeoException {
        return getValueById(vocabularyName, entryid, null);
    }

    public static Entry getValueById(final String vocabularyName, final String entryid, final String lang)
            throws NuxeoException {
        final DirectoryService directoryService = getService();

        final DocumentModel doc = getEntry(directoryService, vocabularyName, entryid);
        if (doc != null) {
            final String schemaName = directoryService.getDirectorySchema(vocabularyName);
            Locale locale = null;
            if (StringUtils.isNotBlank(lang)) {
                locale = new Locale(lang);
            }
            return toEntry(schemaName, doc, locale);
        }
        return null;

    }

    protected static String translate(final String key, final Locale locale) {
        if (key == null) {
            return "";
        }
        return I18NUtils.getMessageString("messages", key, new Object[0], locale);
    }

    public static List<Entry> suggestValue(final String vocabularyName, final boolean displayObsoleteEntries,
            final String keyword) throws NuxeoException {
        return suggestValue(vocabularyName, displayObsoleteEntries, keyword, null, true, null);
    }

    public static List<Entry> suggestValue(final String vocabularyName, final boolean displayObsoleteEntries,
            final String keyword, final Map<String, Serializable> filters, final boolean postFilter, final String lang)
                    throws NuxeoException {

        final List<Entry> entries = suggestEntries(vocabularyName, displayObsoleteEntries, keyword, filters, postFilter,
                lang);

        return orderEntryList(VocabularyHelper.VocOrder.Label, entries);
    }

    public static List<Entry> suggestValue(final Comparator<? super Entry> cmp, final String vocabularyName,
            final boolean displayObsoleteEntries, final String keyword) throws NuxeoException {
        return suggestValue(cmp, vocabularyName, displayObsoleteEntries, keyword, null, false, null);
    }

    public static List<Entry> suggestValue(final Comparator<? super Entry> cmp, final String vocabularyName,
            final boolean displayObsoleteEntries, final String keyword, final Map<String, Serializable> filters,
            final boolean postFilter, final String lang) throws NuxeoException {
        final List<Entry> entries = suggestEntries(vocabularyName, displayObsoleteEntries, keyword, filters, postFilter,
                lang);

        return orderEntryList(cmp, entries);
    }

    private static List<Entry> suggestEntries(final String vocabularyName, final boolean displayObsoleteEntries,
            final String keyword, final Map<String, Serializable> filters, final boolean postFilter,
            final String lang) {
        final DirectoryService directoryService = getService();
        final Map<String, Serializable> filter = new HashMap<String, Serializable>();
        final Set<String> keywordSet = new HashSet<String>();
        if (!postFilter) {
            keywordSet.add(VocabularyHelper.LABEL_FIELD);
            filter.put(VocabularyHelper.LABEL_FIELD, "%" + keyword + "%");
        }

        if (!displayObsoleteEntries) {
            filter.put(OBSOLETE_FIELD, Long.valueOf(0));
        }

        if (filters != null) {
            filter.putAll(filters);
        }

        final DocumentModelList dml = getEntries(directoryService, vocabularyName, filter, keywordSet);
        final String schemaName = directoryService.getDirectorySchema(vocabularyName);
        final List<Entry> entries = toEntries(schemaName, dml, lang);

        if (postFilter && entries != null && StringUtils.isNotBlank(keyword)) {
            String search = keyword.replaceAll("\\*", "");
            search = search.replaceAll("%", "");

            final List<Entry> filteredEntries = new ArrayList<>();
            for (final Entry entry : entries) {
                if (!entry.getLabel().toLowerCase().contains(keyword.toLowerCase())) {
                    filteredEntries.add(entry);
                }
            }
            return filteredEntries;
        } else {
            return entries;
        }
    }

    /**
     * Supprime une entrée dans un vocabulaire
     * @param vocabularyName
     * @param entryId
     * @throws NuxeoException
     */
    public static void removeEntry(final String vocabularyName, final String entryId) throws NuxeoException {
        Session session = null;
        try {
            final DirectoryService directoryService = VocabularyHelper.getService();
            session = directoryService.open(vocabularyName);
            session.deleteEntry(entryId);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Met a jour le label lié à une entrée d'un vocabulaire 
     * @param vocabularyName le voc
     * @param entryId l'id de l'entrée
     * @param newLabel la nouvelle valeur pour le label
     * @throws NuxeoException
     */
    public static void updateLabel(final String vocabularyName, final String entryId, final String newLabel)
            throws NuxeoException {
        Session session = null;
        try {
            final DirectoryService directoryService = getService();
            final String schemaName = directoryService.getDirectorySchema(vocabularyName);
            session = directoryService.open(vocabularyName);
            final DocumentModel entryDoc = session.getEntry(entryId);
            PropertyUtil.setProperty(entryDoc, schemaName, VocabularyHelper.LABEL_FIELD, newLabel);
            session.updateEntry(entryDoc);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public static void addVocEntries(final String vocabularyName, final List<VocabularyHelper.Entry> entries)
            throws NuxeoException {
        final List<Map<String, Object>> dataEntries = new ArrayList<Map<String, Object>>();
        for (final VocabularyHelper.Entry entry : entries) {
            dataEntries.add(convert(entry));
        }
        addEntries(vocabularyName, dataEntries);
    }

    public static void addEntries(final String vocabularyName, final List<Map<String, Object>> entries)
            throws NuxeoException {
        Session session = null;
        try {
            session = VocabularyHelper.getService().open(vocabularyName);
            for (final Map<String, Object> data : entries) {
                session.createEntry(data);
            }
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    private static Map<String, Object> convert(final VocabularyHelper.Entry entry) {
        final Map<String, Object> data = new HashMap<String, Object>();
        data.put("id", entry.getId());
        data.put(VocabularyHelper.LABEL_FIELD, entry.getLabel());
        data.put(VocabularyHelper.ORDERING_FIELD, entry.getOrdering());
        data.put(VocabularyHelper.OBSOLETE_FIELD, entry.isObsolete() ? "1" : "0");
        if (entry.getParentId() != null) {
            data.put(VocabularyHelper.PARENT_FIELD_ID, entry.getParentId());
        }
        return data;
    }

    /**
     * Convertit une liste de document vers une liste d'objet Entry
     * @param schemaName
     * @param docList
     * @param lang 
     * @return liste d'entrée
     * @throws NuxeoException
     */
    protected static List<Entry> toEntries(final String schemaName, final DocumentModelList docList, final String lang)
            throws NuxeoException {
        Locale locale = null;
        if (StringUtils.isNotBlank(lang)) {
            locale = new Locale(lang);
        }
        final List<Entry> results = new ArrayList<Entry>();
        for (final DocumentModel result : docList) {
            results.add(toEntry(schemaName, result, locale));
        }
        return results;
    }

    /**
     * cree un objet Entry depuis une entree de vocabulaire represente par un documentModel
     * @param schemaName
     * @param doc
     * @return une entree
     * @throws NuxeoException
     */
    protected static Entry toEntry(final String schemaName, final DocumentModel doc) throws NuxeoException {
        final String childIdendifier = doc.getId();

        final String childLabel = PropertyUtil.getStringProperty(doc, schemaName, LABEL_FIELD);
        final Long ordering = PropertyUtil.getLongProperty(doc, schemaName, ORDERING_FIELD);
        final Long obsolete = PropertyUtil.getLongProperty(doc, schemaName, OBSOLETE_FIELD);
        String parentid = null;
        if (schemaName.endsWith(XVOCABULARY_SCHEMA)) {
            parentid = PropertyUtil.getStringProperty(doc, schemaName, PARENT_FIELD_ID);
        }

        return new Entry(childIdendifier, childLabel, ordering, obsolete, parentid);
    }

    protected static Entry toEntry(final String schemaName, final DocumentModel doc, final Locale locale)
            throws NuxeoException {
        final String childIdendifier = doc.getId();

        String childLabel = PropertyUtil.getStringProperty(doc, schemaName, LABEL_FIELD);
        if (locale != null) {
            childLabel = translate(childLabel, locale);
        }
        final Long ordering = PropertyUtil.getLongProperty(doc, schemaName, ORDERING_FIELD);
        final Long obsolete = PropertyUtil.getLongProperty(doc, schemaName, OBSOLETE_FIELD);
        String parentid = null;
        if (schemaName.endsWith(XVOCABULARY_SCHEMA)) {
            parentid = PropertyUtil.getStringProperty(doc, schemaName, PARENT_FIELD_ID);
        }

        return new Entry(childIdendifier, childLabel, ordering, obsolete, parentid);
    }

    /**
     * Ordonne une liste d'entree de vocabulaire
     * @param order le mode de tri  
     * @param entries  les entrées à trier
     * @return liste d'entrée triée
     */
    protected static List<Entry> orderEntryList(final VocOrder order, final List<Entry> entries) {
        Comparator<? super Entry> cmp = null;
        switch (order) {
            case OrderingField:
                cmp = new OrderingComparator();
                break;
            case Label:
            default:
                cmp = new LabelComparator();
        }
        Collections.sort(entries, cmp);
        return entries;
    }

    /**
     * Ordonne une liste d'entree de vocabulaire
     * @param cmp le comparateur déjà instancié
     * @param entries  les entrées à trier
     * @return liste d'entrée triée
     */
    protected static List<Entry> orderEntryList(final Comparator<? super Entry> cmp, final List<Entry> entries) {
        Collections.sort(entries, cmp);
        return entries;
    }

    protected static DocumentModelList getChildrenEntries(final DirectoryService service, final String vocabularyName,
            final boolean displayObsoleteEntries, final String parent) throws NuxeoException {

        final Map<String, Serializable> filter = new HashMap<String, Serializable>();

        if (parent != null && service.getDirectorySchema(vocabularyName).endsWith(XVOCABULARY_SCHEMA)) {
            filter.put(PARENT_FIELD_ID, parent);
        }

        if (!displayObsoleteEntries) {
            filter.put(OBSOLETE_FIELD, Long.valueOf(0));
        }

        return getEntries(service, vocabularyName, filter, null);
    }

    protected static DocumentModelList getEntries(final DirectoryService service, final String vocabularyName,
            final Map<String, Serializable> filter, final Set<String> keywordSet) throws NuxeoException {

        Session session = null;
        try {
            DocumentModelList entries;
            session = service.open(vocabularyName);

            if (filter == null) {
                entries = session.query(Collections.emptyMap());
            } else {
                if (keywordSet == null || keywordSet.isEmpty()) {
                    entries = session.query(filter);
                } else {
                    entries = session.query(filter, keywordSet);
                }
            }
            return entries;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Recupere une entree depuis son id
     * @param service
     * @param vocabularyName
     * @param entryId
     * @return l'entree du vocabulaire matchant l'id
     * @throws NuxeoException
     */
    protected static DocumentModel getEntry(final DirectoryService service, final String vocabularyName,
            final String entryId) throws NuxeoException {
        Session session = null;
        try {
            session = service.open(vocabularyName);
            return session.getEntry(entryId);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public static DirectoryService getService() throws NuxeoException {
        return ServiceUtil.getService(DirectoryService.class);
    }
}

package fr.dila.ss.api.birt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Un adapter JAXB pour transformer une liste d'éléments XML en HashMap.
 *
 * @author tlombard
 */
public class MapAdapter extends XmlAdapter<MapAdapter.AdaptedMap, Map<String, BirtReport>> {

    public static class AdaptedMap {
        private List<Entry> entry = new ArrayList<>();

        public List<Entry> getEntry() {
            return entry;
        }

        public void setEntry(List<Entry> entry) {
            this.entry = entry;
        }
    }

    public static class Entry {
        private String key;
        private BirtReport value;

        private Entry() {
            // Default constructor
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public BirtReport getValue() {
            return value;
        }

        public void setValue(BirtReport value) {
            this.value = value;
        }
    }

    @Override
    public AdaptedMap marshal(Map<String, BirtReport> map) throws Exception {
        throw new UnsupportedOperationException("Map marshalling not supported");
    }

    @Override
    public Map<String, BirtReport> unmarshal(AdaptedMap adaptedMap) throws Exception {
        HashMap<String, BirtReport> map = new HashMap<>();
        for (Entry entry : adaptedMap.entry) {
            map.put(entry.key, entry.value);
        }
        return map;
    }
}

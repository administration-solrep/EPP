package fr.dila.st.ui.jaxrs.provider.converters;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.commons.beanutils.converters.AbstractConverter;

public class SwordBooleanConverter extends AbstractConverter {
    /**
     * The set of strings that are known to map to Boolean.TRUE.
     */
    private final List<String> trueStrings = Lists.newArrayList("true", "yes", "y", "on", "1");

    /**
     * The set of strings that are known to map to Boolean.FALSE.
     */
    private final List<String> falseStrings = Lists.newArrayList("false", "no", "n", "off", "0");

    private Boolean defaultValue;

    public SwordBooleanConverter(final Object defaultValue) {
        if (defaultValue == null) {
            this.defaultValue = null;
        } else if (defaultValue instanceof Boolean) {
            this.defaultValue = Boolean.class.cast(defaultValue);
        } else {
            this.defaultValue = false;
        }
    }

    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
        if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
            // All the values in the trueStrings and falseStrings arrays are
            // guaranteed to be lower-case. By converting the input value
            // to lowercase too, we can use the efficient String.equals method
            // instead of the less-efficient String.equalsIgnoreCase method.
            final String stringValue = value.toString().toLowerCase();

            for (String trueString : trueStrings) {
                if (trueString.equals(stringValue)) {
                    return type.cast(Boolean.TRUE);
                }
            }

            for (String falseString : falseStrings) {
                if (falseString.equals(stringValue)) {
                    return type.cast(Boolean.FALSE);
                }
            }
        }

        throw conversionException(type, value);
    }

    @Override
    protected <T> T handleMissing(Class<T> type) {
        return type.cast(defaultValue);
    }

    @Override
    protected Class<?> getDefaultType() {
        return Boolean.class;
    }
}

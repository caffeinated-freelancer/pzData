package ncu.mac.commons.models;

import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.SequencedMap;
import java.util.SequencedSet;

public interface HaveKeyValues {
    SequencedSet<String> getKeySet();

    Optional<String> getValue(String key);

    void putValue(String key, String value);

    SequencedMap<String, String> getExtraKeyValues();

    class HaveKeyValuesImpl implements HaveKeyValues {
        private final SequencedMap<String, String> extraKeyValues = new LinkedHashMap<>();

        @Override
        public SequencedSet<String> getKeySet() {
            return extraKeyValues.sequencedKeySet();
        }

        @Override
        public Optional<String> getValue(String key) {
            if (extraKeyValues.containsKey(key)) {
                return Optional.of(extraKeyValues.get(key));
            }
            return Optional.empty();
        }

        @Override
        public void putValue(String key, String value) {
            if (!extraKeyValues.containsKey(key)) {
                extraKeyValues.put(key, value);
            }
        }

        @Override
        public SequencedMap<String, String> getExtraKeyValues() {
            return extraKeyValues;
        }
    }
}

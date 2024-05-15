package ncu.mac.commons.utils;

import ncu.mac.commons.annotations.ColumnIndex;
import ncu.mac.commons.annotations.KeyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class MapToObjectConverter<T, U> {
    private static final Logger logger = LoggerFactory.getLogger(MapToObjectConverter.class);

    public record MapKey(String columnName, int columnIndex) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MapKey mapKey = (MapKey) o;
            return columnIndex == mapKey.columnIndex && Objects.equals(columnName, mapKey.columnName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(columnName, columnIndex);
        }
    }

    public static class DataHolder<U> {
        private final Field field;
        private U padLoad;

        private DataHolder(Field field) {
            this.field = field;
        }

        public Field getField() {
            return field;
        }

        public U getPadLoad() {
            return padLoad;
        }

        public void setPadLoad(U padLoad) {
            this.padLoad = padLoad;
        }
    }

    private final Class<T> tClass;
    private final Class<U> uClass;
    private final Map<MapKey, DataHolder<U>> columnFieldMap;

    public MapToObjectConverter(Class<T> tClass, Class<U> uClass) {
        this.tClass = tClass;
        this.uClass = uClass;

//        Arrays.stream(ImportFromCtWorld.CtWorldMemberData.class.getDeclaredFields())
//                .forEach(f -> {
//                    StackTraceUtil.print1(f.getName());
//                });
//        Arrays.stream(tClass.getDeclaredFields())
//                .forEach(f -> {
//                    StackTraceUtil.print1(f.getName());
//                });

        columnFieldMap = Arrays.stream(tClass.getDeclaredFields())
                .map(field -> Optional.ofNullable(field.getAnnotation(KeyName.class))
                        .map(KeyName::value)
                        .filter(StringUtils::hasLength)
                        .map(s -> {
                            logger.debug("{}: annotation: {}, fieldName: {}", tClass.getSimpleName(), s, field.getName());
                            return Pair.of(new MapKey(s, -1), field);
                        })
                        .orElseGet(() -> Optional.ofNullable(field.getAnnotation(ColumnIndex.class))
                                .map(ColumnIndex::value)
                                .filter(StringUtils::hasLength)
                                .map(MapToObjectConverter::spreadsheetLetter2Index)
                                .map(integer -> {
                                    logger.debug("index: {}: fieldName: {} ({})", integer, field.getName(), field.getAnnotation(ColumnIndex.class).value());
                                    return Pair.of(new MapKey("", integer), field);
                                })
                                .orElseGet(() -> {
                                    logger.debug("{}: fieldName: {}", tClass.getSimpleName(), field.getName());
                                    return Pair.of(new MapKey(field.getName(), -1), field);
                                })
                        ))
                .collect(Collectors.toUnmodifiableMap(Pair::getFirst,
                        p -> new DataHolder<>(p.getSecond())));


//        StackTraceUtil.print1(columnFieldMap);
//        StackTraceUtil.print1(columnFieldMap.keySet().size());
//        StackTraceUtil.print1(columnFieldMap.keySet());
    }

    private static int spreadsheetLetter2Index(String letter) {
        char[] charArray = letter.toCharArray();
        int v = 0;

        for (var i = 0; i < charArray.length; i++) {
            var c = charArray[i];

            if (c >= 'A' && c <= 'Z') {
                v += (int) Math.pow(26, charArray.length - i - 1) * (c - 'A' + 1);
            }
        }
        return v - 1;
    }

    public Optional<T> getInstance() {
        try {
            final Class<?>[] parameterTypes = {};

            return Optional.of(tClass.getDeclaredConstructor(parameterTypes).newInstance());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            logger.warn(e.getMessage());
            return Optional.empty();
        }
    }

    public boolean store(T tObject, String columnName, Object value) {
        final var dataHolder = columnFieldMap.get(columnName);

        if (dataHolder != null) {
            try {
                dataHolder.field.setAccessible(true);
                dataHolder.field.set(tObject, value);
                return true;
            } catch (IllegalAccessException e) {
                logger.warn("{}: {}", e.getClass().getName(), e.getMessage());
            }
        }
        return false;
    }

    public boolean store(T tObject, Field field, Object value) {
        if (field != null) {
            try {
                field.setAccessible(true);
                field.set(tObject, value);
                return true;
            } catch (IllegalAccessException e) {
                logger.warn("{}: {}", e.getClass().getName(), e.getMessage());
            }
        }
        return false;
    }

    public Set<MapKey> keySet() {
//        columnFieldMap.keySet().forEach(s -> logger.info("key: {}", s));
        return columnFieldMap.keySet();
    }

    public Optional<DataHolder<U>> addPadLoad(MapKey mapKey, U padLoad) {
        return Optional.ofNullable(columnFieldMap.get(mapKey))
                .map(uDataHolder -> {
                    uDataHolder.setPadLoad(padLoad);
                    return uDataHolder;
                });
    }
}

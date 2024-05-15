package ncu.mac.commons.utils;

import ncu.mac.commons.annotations.ResultIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class ResultSetConverter<T> {
    private static final Logger logger = LoggerFactory.getLogger(ResultSetConverter.class);
    private final Class<T> tClass;
    private final Map<Integer, Field> columnFieldMap;

    public ResultSetConverter(Class<T> tClass) {
        this.tClass = tClass;
        this.columnFieldMap = Arrays.stream(tClass.getDeclaredFields())
                .map(field -> Optional.ofNullable(field.getAnnotation(ResultIndex.class))
                        .map(ResultIndex::value)
                        .map(i -> Pair.of(i, field))
                        .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toUnmodifiableMap(
                        Pair::getFirst, Pair::getSecond));
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

    public Optional<T> convert(Object[] objects) {
        return getInstance()
                .map(t -> {
                    for (int i = 0; i < objects.length; i++) {
                        final var o = objects[i];
                        Optional.of(columnFieldMap.get(i))
                                .ifPresent(field -> {
                                    field.setAccessible(true);
                                    try {
                                        field.set(t, o);
                                    } catch (IllegalAccessException ignore) {
                                    }
                                });
                    }
                    return t;
                });
    }
}

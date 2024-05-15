package ncu.mac.commons.utils;

import ncu.mac.commons.constants.JavaConstants;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtil {
    public static <T> Stream<T> intersection(Collection<T> first, Collection<T> second) {
        return first.stream()
                .filter(second::contains)
                .distinct();
    }

    public static <T> Stream<T> intersection(Collection<T> first, Stream<T> second) {
        return second.filter(first::contains)
                .distinct();
    }

    public static <T> Stream<T> union(Collection<T> first, Collection<T> second) {
        return Stream.concat(first.stream(), second.stream())
                .distinct();
    }

    @SuppressWarnings(JavaConstants.UNCHECKED)
    public static <T> Set<T> toSet(T... objects) {
        return objects.length == 0 ? Collections.EMPTY_SET : Arrays.stream(objects).collect(Collectors.toSet());
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}

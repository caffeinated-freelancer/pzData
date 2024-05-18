package ncu.mac.commons.helpers;

import ncu.mac.commons.annotations.KeyName;
import ncu.mac.commons.constants.JavaConstants;
import ncu.mac.commons.models.HaveKeyValues;
import ncu.mac.commons.utils.StackTraceUtil;
import ncu.mac.pzdata.models.MemberModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ExcelKeyModelHelper {
    private static final Logger logger = LoggerFactory.getLogger(ExcelKeyModelHelper.class);

    public interface ExcelImportHelper<T> {
        Optional<String> retrieveField(T model, String keyName);

        void putValueAt(T model, String keyName, String value);

        List<T> importFromExcel(InputStream inputStream, int sheetIndex) throws IOException;
    }

    public static class ExcelImportHelpImp<T> implements ExcelImportHelper<T> {
        private final Class<T> clazz;
        private final List<String> headerKeyNames;
        private final Map<String, Field> fieldMap;

        @SuppressWarnings(JavaConstants.UNCHECKED)
        public ExcelImportHelpImp(T tObject) {
            this.clazz = (Class<T>) tObject.getClass();

            final var names = new ArrayList<String>();
            fieldMap = Arrays.stream(MemberModel.class.getDeclaredFields())
                    .map(field -> {
                        final var keyName = field.getAnnotation(KeyName.class);
                        if (keyName != null) {
                            names.add(keyName.value());
                            return Pair.of(keyName.value(), field);
                        } else {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableMap(Pair::getFirst, Pair::getSecond));

            headerKeyNames = names.stream().toList();
        }

        @Override
        public Optional<String> retrieveField(T model, String keyName) {
            return Optional.ofNullable(fieldMap.get(keyName))
                    .map(field -> {
                        try {
                            field.setAccessible(true);
                            return Optional.ofNullable(field.get(model))
                                    .map(Object::toString)
                                    .orElse("");
                        } catch (IllegalAccessException e) {
                            logger.warn("{}: {}", e.getClass().getName(), e.getMessage());
                            return null;
                        }
                    });
        }

        @Override
        public void putValueAt(T model, String keyName, String value) {
            Optional.ofNullable(fieldMap.get(keyName))
                    .ifPresent(field -> {
                        try {
                            field.setAccessible(true);
                            field.set(model, value);
                        } catch (IllegalAccessException e) {
                            logger.warn("{}: {}", e.getClass().getName(), e.getMessage());
                        }
                    });
        }

        @Override
        public List<T> importFromExcel(InputStream inputStream, int sheetIndex) throws IOException {
            final var excelReader = ExcelReaderHelper.getInstance(inputStream);
            final var keyNames = new HashSet<>(headerKeyNames);
            final var columnMap = new AtomicReference<Map<Integer, String>>(null);
            final SequencedMap<String, Integer> extraHeaderNames = new LinkedHashMap<>();
            final var dataList = new ArrayList<T>();

            excelReader.each(sheetIndex, (row, cells) -> {
                if (cells.stream().anyMatch(StringUtils::hasLength)) {
                    if (columnMap.get() == null) {
                        columnMap.set(IntStream.range(0, cells.size())
                                .boxed()
                                .filter(index -> {
                                    final var headerName = cells.get(index);

                                    if (keyNames.contains(headerName)) {
                                        keyNames.remove(headerName);
                                        return true;
                                    } else {
                                        if (!extraHeaderNames.containsKey(headerName)) {
                                            extraHeaderNames.put(headerName, index);
                                        } else {
                                            logger.info("ignore {}", headerName);
                                        }
                                    }
                                    return false;
                                })
                                .collect(Collectors.toMap(Function.identity(), cells::get)));

                        if (logger.isDebugEnabled()) {
                            columnMap.get().forEach((k, v) -> {
                                logger.info("{} -> {}", k, v);
                            });
                        }
                    } else {
                        try {
                            final T model = clazz.getDeclaredConstructor().newInstance();

                            IntStream.range(0, cells.size())
                                    .boxed()
                                    .filter(index -> columnMap.get().containsKey(index))
                                    .map(index -> Pair.of(index, columnMap.get().get(index)))
                                    .forEach(pair -> putValueAt(model, pair.getSecond(), cells.get(pair.getFirst())));

                            if (model instanceof HaveKeyValues haveKeyValues) {
                                extraHeaderNames.forEach((extraName, index) -> {
                                    if (index < cells.size()) {
                                        haveKeyValues.putValue(extraName, cells.get(index));
                                    }
                                });
                            }
                            if (logger.isDebugEnabled()) {
                                System.out.printf("Row: %d, Cells: %d - ", row, cells.size());
                                for (String cell : cells) {
                                    System.out.printf("[%s]", cell);
                                }
                                System.out.println();
                            }
                            if (logger.isDebugEnabled()) {
                                StackTraceUtil.print1(model);
                            }

                            dataList.add(model);
                        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                                 NoSuchMethodException e) {
                            logger.warn("{}: {}", e.getClass().getName(), e.getMessage());
                        }
                    }
                }
            });

            columnMap.get().forEach((k, v) -> {
                logger.info("{} -> {}", k, v);
            });

            return dataList;
        }
    }

    private static final Map<Class<?>, ExcelImportHelper<?>> keysAndFieldsMap = new ConcurrentHashMap<>();

    public static <U> ExcelImportHelper<U> getHelper(U uObject) {
        return (ExcelImportHelper<U>) keysAndFieldsMap.computeIfAbsent(uObject.getClass(), k -> new ExcelImportHelpImp<>(uObject));
    }
}

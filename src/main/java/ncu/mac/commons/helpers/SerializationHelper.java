package ncu.mac.commons.helpers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import ncu.mac.commons.utils.Miscellaneous;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public class SerializationHelper {
    private static final Logger logger = LoggerFactory.getLogger(SerializationHelper.class);

    private static final ObjectMapper STRICT_OBJECT_MAPPER = new ObjectMapper()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    private static final ObjectMapper RELAX_OBJECT_MAPPER = new ObjectMapper()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
            .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
            .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    private static final ObjectWriter PRETTY_PRINTER = new ObjectMapper().writerWithDefaultPrettyPrinter();

    public static <T> T twoLevelDeserialization(String data, Class<T> clazz) throws JsonProcessingException {
        try {
            return STRICT_OBJECT_MAPPER.readValue(data, clazz);
        } catch (JsonProcessingException e) {
            Stream.of(new Throwable().getStackTrace())
                    .findFirst()
                    .ifPresentOrElse(ste -> logger.warn("{}.{}:{} {}", ste.getClassName(), ste.getMethodName(), ste.getLineNumber(), e.getMessage()),
                            () -> logger.warn(e.getMessage()));
            return RELAX_OBJECT_MAPPER.readValue(data, clazz);
        }
    }

    public static <T> List<T> twoLevelDeserializationList(String data, TypeReference<List<T>> typeReference) throws JsonProcessingException {
        try {
            return STRICT_OBJECT_MAPPER.readValue(data, typeReference);
        } catch (JsonProcessingException e) {
            Stream.of(new Throwable().getStackTrace())
                    .findFirst()
                    .ifPresentOrElse(ste -> logger.warn("{}.{}:{} {}", ste.getClassName(), ste.getMethodName(), ste.getLineNumber(), e.getMessage()),
                            () -> logger.warn(e.getMessage()));
            return RELAX_OBJECT_MAPPER.readValue(data, typeReference);
        }
    }

//    public static <T> Mono<Optional<T>> deserializeToMonoOptionalOf(String data, Class<T> clazz) {
//        try {
////            final var t = twoLevelDeserialization(data, clazz);
//            return Mono.just(Optional.ofNullable(twoLevelDeserialization(data, clazz)));
//        } catch (JsonProcessingException e) {
//            logger.error(e.getMessage());
//            return Mono.error(e);
//        }
//    }
//
//    public static <T> Mono<List<T>> deserializeToMonoListOf(String data, TypeReference<List<T>> typeReference) {
//        try {
////            final var t = twoLevelDeserialization(data, clazz);
//            return Mono.just(twoLevelDeserializationList(data, typeReference));
//        } catch (JsonProcessingException e) {
//            logger.error(e.getMessage());
//            return Mono.error(e);
//        }
//    }

    public static String toPrettyJson(Object object) {
        try {
            return PRETTY_PRINTER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    public static String serialize(Object object) {
        try {
            return STRICT_OBJECT_MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            return "";

        }
    }

    public static <T> T deserialize(String payload, Class<T> clazz) throws JsonProcessingException {
        return twoLevelDeserialization(payload, clazz);
    }

    @Nullable
    public static <T> T mapToObject(Map<String, String> data, Class<T> clazz) {
        try {
            return twoLevelDeserialization(STRICT_OBJECT_MAPPER.writeValueAsString(data), clazz);
        } catch (JsonProcessingException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    public static List<String> deserializeAsListOfString(String jsonString) {
        return (List<String>) new Gson().fromJson(jsonString, Miscellaneous.LIST_TYPE);
    }
}

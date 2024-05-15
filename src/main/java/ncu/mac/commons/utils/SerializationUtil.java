package ncu.mac.commons.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import ncu.mac.commons.constants.JavaConstants;
import org.springframework.lang.Nullable;

import java.io.*;
import java.util.List;

public class SerializationUtil {
    public static String EMPTY_JSON_LIST = "[]";

    public static String toJson(Object obj) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(obj);
    }
    public static String toJsonOrDefault(Object obj, @Nullable String defaultValue)  {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return defaultValue;
        }
    }

    public static <T> T fromJson(String data, Class<T> clazz) throws IOException {
        return new ObjectMapper().readValue(data.getBytes(), clazz);
    }

    @SuppressWarnings(JavaConstants.UNCHECKED)
    public static List<String> jsonToStringList(String jsonString) {
        return ((List<String>) new Gson().fromJson(jsonString, Miscellaneous.LIST_TYPE));
    }

    public static byte[] toByteArray(Object obj) throws IOException {
        var bos = new ByteArrayOutputStream();
        ObjectOutput out = null;

        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(obj);
            out.flush();
            return bos.toByteArray();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
            }
        }
    }

    @SuppressWarnings(JavaConstants.UNCHECKED)
    public static <T> T toObject(byte[] byteArray, Class<T> clazz) throws IOException, ClassNotFoundException {
        var bis = new ByteArrayInputStream(byteArray);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (T) in.readObject();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }
    }
}

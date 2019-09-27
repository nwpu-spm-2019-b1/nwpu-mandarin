package mandarin.utils;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectUtils {
    public static void copyFields(Object src, Object dest, String... fieldNames) {
        Class<?> srcClass = src.getClass();
        Class<?> destClass = dest.getClass();
        for (String fieldName : fieldNames) {
            try {
                Field srcField = FieldUtils.getField(srcClass, fieldName, true);
                Field destField = FieldUtils.getField(destClass, fieldName, true);
                destField.set(dest, srcField.get(src));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Map<String, Object> copyFieldsIntoMap(Object src, Map<String, Object> dest, String... fieldNames) {
        Class<?> srcClass = src.getClass();
        if (dest == null) {
            dest = new HashMap<>();
        }
        for (String fieldName : fieldNames) {
            try {
                Field srcField = FieldUtils.getField(srcClass, fieldName, true);
                dest.put(fieldName, srcField.get(src));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return dest;
    }
}

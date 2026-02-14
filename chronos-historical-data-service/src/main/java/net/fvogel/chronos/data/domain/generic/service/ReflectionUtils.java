package net.fvogel.chronos.data.domain.generic.service;

import org.apache.commons.lang3.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ReflectionUtils {

    private static final Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);

    /**
     * Gets the type of a generically typed field
     * E.g.
     * private List<MyEntity> entities; -> MyEntity.class
     *
     * @param field The Field to derive the generic type from
     * @return The type's class
     */
    public static Class<?> getListElementType(Field field) {
        // First check that the field is assignable from List
        if (!List.class.isAssignableFrom(field.getType())) {
            return null;
        }

        // Check if the generic type is parameterized
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType pt) {
            Type[] typeArgs = pt.getActualTypeArguments();
            if (typeArgs.length == 1) {
                Type arg = typeArgs[0];
                if (arg instanceof Class<?>) {
                    return (Class<?>) arg;
                } else if (arg instanceof ParameterizedType) {
                    // e.g. List<Map<String, Foo>>
                    Type rawType = ((ParameterizedType) arg).getRawType();
                    if (rawType instanceof Class<?>) {
                        return (Class<?>) rawType;
                    }
                }
            }
        }

        return null;
    }


    /**
     * Returns all fields of the given class (including inherited ones)
     * that are annotated with the given annotation.
     *
     * @param clazz           the class to inspect
     * @param annotationClass the annotation type to look for
     * @return list of Fields that are annotated with annotationClass
     */
    public static List<Field> getAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Field> annotatedFields = new ArrayList<>();

        // Walk up the class hierarchy to include inherited fields
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(annotationClass)) {
                    annotatedFields.add(field);
                }
            }
            current = current.getSuperclass();
        }

        return annotatedFields;
    }

    /**
     * Returns all fields of the given class (including inherited ones)
     * that are NOT annotated with the given annotation.
     *
     * @param clazz           the class to inspect
     * @param annotationClass the annotation type to exclude fields with
     * @return list of Fields that are annotated with annotationClass
     */
    public static List<Field> getNonAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        List<Field> annotatedFields = new ArrayList<>();

        // Walk up the class hierarchy to include inherited fields
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            for (Field field : current.getDeclaredFields()) {
                if (!field.isAnnotationPresent(annotationClass)) {
                    annotatedFields.add(field);
                }
            }
            current = current.getSuperclass();
        }

        return annotatedFields;
    }

    /**
     * Retrieves the value of a String field from an object, specified by the given key.
     *
     * @param obj The object to retrieve the field value from.
     * @param key The key of the field.
     * @return The String value from the object
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    public static String getFieldStringValue(Object obj, String key) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(key);
        Object value = field.get(obj);
        if (value != null && !(value instanceof String)) {
            throw new NotImplementedException("No implementation ");
        }
        if (value == null) {
            return null;
        }
        return (String) value;
    }

}

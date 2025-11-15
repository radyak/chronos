package net.fvogel.chronosbackend.domain.generic.service;

import net.fvogel.chronosbackend.domain.generic.model.EntityMetadata;
import net.fvogel.chronosbackend.domain.generic.model.RelationMetadata;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Transactional
public class MetadataService {

    private static final Logger logger = LoggerFactory.getLogger(MetadataService.class);
    private static final String packageName = "net.fvogel.chronosbackend.domain";
    private static final Class<? extends Annotation> nodeAnnotationClass = Node.class;
    private static final Class<? extends Annotation> relationshipPropertiesAnnotation = Relationship.class;

    /**
     * Scans for all classes annotated with org.springframework.data.neo4j.core.schema.Node
     */
    public List<EntityMetadata> listNodeClasses() {
        // Initialize Reflections with a specific scanner
        Reflections reflections = new Reflections(packageName, Scanners.TypesAnnotated);

        // Get all classes annotated with the given annotation
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(nodeAnnotationClass);

        return annotatedClasses.stream()
                .map(clazz -> {

                    Annotation annotation = clazz.getAnnotation(nodeAnnotationClass);
                    String[] values = null;


                    try {
                        Method valueMethod = annotation.annotationType().getMethod("value");
                        values = (String[]) valueMethod.invoke(annotation);
                    } catch (Exception e) {
                        logger.error("Error reading annotation from " + clazz.getName() + ": " + e.getMessage());
                    }

                    if (values != null && values.length > 0) {
                        EntityMetadata ec = new EntityMetadata();
                        ec.setName(values[0]);
                        ec.setRelations(extractRelationConfigs(clazz));

                        // Map fields
//                        Field[] fields = clazz.getDeclaredFields();
//                        for (Field field : fields) {
//                            EntityFieldMetadata efm = new EntityFieldMetadata();
//                            efm.setName(field.getName());
//                            efm.setType(field.getType().getSimpleName());
//                            ec.getFields().add(efm);
//                        }

                        return ec;
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Scans for fields in a class annotated with @Relationship and maps the info to RelationConfigs
     *
     * @param clazz The class to scan
     * @return The meta info as RelationConfig
     */
    private List<RelationMetadata> extractRelationConfigs(Class<?> clazz) {
        List<Field> annotatedFields = getAnnotatedFields(clazz, relationshipPropertiesAnnotation);
        return annotatedFields.stream().map(field -> {
            RelationMetadata rc = new RelationMetadata();

            // Set annotated field's name
            rc.setEntityField(field.getName());


            Annotation annotation = field.getAnnotation(relationshipPropertiesAnnotation);

            // Set type of field's Relationship annotation
            try {
                Method typeMethod = annotation.annotationType().getMethod("type");
                String type = (String) typeMethod.invoke(annotation);
                if (type != null) {
                    rc.setRelationName(type);
                }
            } catch (Exception e) {
                logger.error("Error reading annotation from " + clazz.getName() + ": " + e.getMessage());
            }

            // Set direction of field's Relationship annotation
            try {
                Method directionMethod = annotation.annotationType().getMethod("direction");
                Relationship.Direction direction = (Relationship.Direction) directionMethod.invoke(annotation);
                if (direction != null) {
                    if (direction == Relationship.Direction.OUTGOING) {
                        rc.setDirection(RelationMetadata.Direction.OUT);
                    } else {
                        rc.setDirection(RelationMetadata.Direction.IN);
                    }
                }
            } catch (Exception e) {
                logger.error("Error reading annotation from " + clazz.getName() + ": " + e.getMessage());
            }

            // Set target type of field's
            Class targetClass = getListElementType(field);
            rc.setRelationClass(targetClass.getSimpleName());

            // Set node name of target
            List<Field> targetNodeFields = getAnnotatedFields(targetClass, TargetNode.class);
            if (targetNodeFields.size() == 1) { // Spring Data should not allow something else
                Field targetNodeField = targetNodeFields.get(0);
                Class targetNodeFieldClass = targetNodeField.getType();
                logger.info("Found {} fields in {} with TargetNode: {}", targetNodeFields.size(), targetClass.getSimpleName(), targetNodeFieldClass);

                Annotation targetNodeFieldClassAnnotation = targetNodeFieldClass.getAnnotation(nodeAnnotationClass);

                try {
                    logger.info("Trying to read 'value' from annotation {} in class {}", targetNodeFieldClassAnnotation.annotationType(), targetNodeFieldClass.getSimpleName());
                    Method valueMethod = targetNodeFieldClassAnnotation.annotationType().getMethod("value");
                    String[] values = (String[]) valueMethod.invoke(targetNodeFieldClassAnnotation);

                    if (values != null && values.length > 0) {
                        rc.setTagetEntity(values[0]);
                    }
                } catch (Exception e) {
                    logger.error("Error reading annotation {} from {}: {}", nodeAnnotationClass, targetNodeFieldClass.getName(), e.getMessage());
                    logger.error("Exception", e);
                }
            }

            return rc;
        }).collect(Collectors.toList());
    }

    /**
     * Gets the type of a generically typed field
     * E.g.
     * private List<MyEntity> entities; -> MyEntity.class
     *
     * @param field The Field to derive the generic type from
     * @return The type's class
     */
    private Class<?> getListElementType(Field field) {
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
    public List<Field> getAnnotatedFields(Class<?> clazz, Class<? extends Annotation> annotationClass) {
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

}

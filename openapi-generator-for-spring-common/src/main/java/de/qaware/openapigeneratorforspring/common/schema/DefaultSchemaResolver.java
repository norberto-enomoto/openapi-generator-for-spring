package de.qaware.openapigeneratorforspring.common.schema;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import de.qaware.openapigeneratorforspring.common.util.OpenApiObjectMapperSupplier;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.RepeatableContainers;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class DefaultSchemaResolver implements SchemaResolver {

    private final OpenApiObjectMapperSupplier openApiObjectMapperSupplier;
    private final SchemaAnnotationMapperFactory schemaAnnotationMapperFactory;

    @Override
    public Schema resolveFromClass(Class<?> clazz, ReferencedSchemaConsumer referencedSchemaConsumer) {
        ObjectMapper mapper = openApiObjectMapperSupplier.get();

        Context context = new Context(mapper, schemaAnnotationMapperFactory.create(this), referencedSchemaConsumer);
        AnnotationsSupplierForClass annotationsSupplier = new AnnotationsSupplierForClass(clazz);
        JavaType javaType = mapper.constructType(clazz);
        Schema schema = context.buildSchemaFromTypeWithoutProperties(javaType, annotationsSupplier);
        context.addPropertiesToSchema(javaType, schema);
        context.resolveReferencedSchemas();

        return schema;
    }

    @RequiredArgsConstructor
    private static class Context {

        private final ObjectMapper mapper;
        private final SchemaAnnotationMapper schemaAnnotationMapper;
        private final ReferencedSchemaConsumer referencedSchemaConsumer;
        private final ReferencedSchemas referencedSchemas = new ReferencedSchemas();

        private void buildSchemaFromType(Consumer<Schema> schemaConsumer, JavaType javaType, AnnotationsSupplier annotationsSupplier) {
            if (javaType.isCollectionLikeType()) {
                Schema containerSchema = new Schema();
                containerSchema.setType("array");
                // TODO adapt annotations supplier to content type, consider @ArraySchema
                buildSchemaFromType(containerSchema::setItems, javaType.getContentType(), annotationsSupplier);
                schemaConsumer.accept(containerSchema);
                return;
            }

            Schema newSchema = buildSchemaFromTypeWithoutProperties(javaType, annotationsSupplier);
            List<Consumer<Schema>> schemaReferenceSetters = referencedSchemas.findSchemaReferenceIgnoringProperties(newSchema);
            if (schemaReferenceSetters != null) {
                // we've seen this newSchema before, then simply reference it
                schemaReferenceSetters.add(schemaConsumer);
            } else {
                // important to add the newSchema first before traversing the nested properties
                referencedSchemas.addNewSchemaReference(newSchema, schemaConsumer);
                addPropertiesToSchema(javaType, newSchema);
            }
        }

        private Schema buildSchemaFromTypeWithoutProperties(JavaType javaType, AnnotationsSupplier annotationsSupplier) {

            // TODO do some more primitive type handling here
            if (javaType.getRawClass().equals(String.class)) {
                // TODO properly handle "referenced" and "inline" schemas
                Schema schema = new Schema();
                schema.setType("string");
                return schema;
            }


            Schema schema = new Schema();
            schema.setType("object");
            schema.setName(javaType.getRawClass().getSimpleName());

            // TODO support other nullable annotations?
            if (annotationsSupplier.findAnnotations(Nullable.class).findFirst().isPresent()) {
                schema.setNullable(true);
            }

            // TODO respect schema implementation type if any is given
            annotationsSupplier.findAnnotations(io.swagger.v3.oas.annotations.media.Schema.class)
                    .collect(Collectors.toCollection(LinkedList::new))
                    .descendingIterator()
                    .forEachRemaining(schemaAnnotation -> schemaAnnotationMapper.applyFromAnnotation(schema, schemaAnnotation, referencedSchemaConsumer));

            return schema;
        }

        private void addPropertiesToSchema(JavaType javaType, Schema schema) {

            BeanDescription beanDesc = getBeanDescription(javaType);
            Set<String> ignoredPropertyNames = beanDesc.getIgnoredPropertyNames();

            for (BeanPropertyDefinition propDef : beanDesc.findProperties()) {
                if (ignoredPropertyNames.contains(propDef.getName())) {
                    continue;
                }

                AnnotatedMember member = propDef.getAccessor();
                AnnotationSupplierForMember annotationsSupplier = new AnnotationSupplierForMember(member);
                buildSchemaFromType(schemaReference -> schema.addProperties(propDef.getName(), schemaReference),
                        member.getType(), annotationsSupplier);
            }
        }

        private BeanDescription getBeanDescription(JavaType type) {

            BeanDescription recurBeanDesc = mapper.getSerializationConfig().introspect(type);
            HashSet<String> visited = new HashSet<>();
            JsonSerialize jsonSerialize = recurBeanDesc.getClassAnnotations().get(JsonSerialize.class);

            while (jsonSerialize != null && !Void.class.equals(jsonSerialize.as())) {
                String asName = jsonSerialize.as().getName();
                if (visited.contains(asName)) {
                    break;
                }
                visited.add(asName);

                recurBeanDesc = mapper.getSerializationConfig().introspect(
                        mapper.constructType(jsonSerialize.as())
                );
                jsonSerialize = recurBeanDesc.getClassAnnotations().get(JsonSerialize.class);
            }
            return recurBeanDesc;
        }

        public void resolveReferencedSchemas() {
            referencedSchemas.referencedSchemas.forEach(
                    referencedSchema -> {
                        if (referencedSchema.referenceConsumers.size() == 1 || referencedSchema.schema.getName() == null) {
                            // TODO make handling of "name == null" more flexible?
                            referencedSchema.referenceConsumers.forEach(schemaConsumer -> schemaConsumer.accept(referencedSchema.schema));
                        } else {
                            Schema schemaForReferenceName = new Schema();
                            referencedSchema.referenceConsumers.forEach(schemaConsumer -> schemaConsumer.accept(schemaForReferenceName));
                            referencedSchemaConsumer.consume(
                                    referencedSchema.schema,
                                    referenceName -> schemaForReferenceName.set$ref(referenceName.asUniqueString())
                            );
                        }
                    }
            );
        }
    }

    private static class ReferencedSchemas {

        @RequiredArgsConstructor
        public static class ReferencedSchema {
            private final Schema schema;
            private final List<Consumer<Schema>> referenceConsumers;
        }

        private final List<ReferencedSchema> referencedSchemas = new ArrayList<>();

        @Nullable
        public List<Consumer<Schema>> findSchemaReferenceIgnoringProperties(Schema schema) {
            return referencedSchemas.stream()
                    .filter(referencedSchema -> {
                        // Schema.equals ignores the name, that's why we check it here manually
                        if (!Objects.equals(referencedSchema.schema.getName(), schema.getName())) {
                            return false;
                        }
                        Map<String, Schema> originalProperties = schema.getProperties();
                        schema.setProperties(referencedSchema.schema.getProperties());
                        boolean equalsIgnoringProperties = referencedSchema.schema.equals(schema);
                        schema.setProperties(originalProperties);
                        return equalsIgnoringProperties;
                    })
                    .findFirst()
                    .map(referencedSchema -> referencedSchema.referenceConsumers)
                    .orElse(null);
        }

        public void addNewSchemaReference(Schema schema, Consumer<Schema> firstSchemaConsumer) {
            ReferencedSchema referencedSchema = new ReferencedSchema(
                    schema,
                    new ArrayList<>(Collections.singleton(firstSchemaConsumer))
            );
            referencedSchemas.add(referencedSchema);
        }
    }


    @FunctionalInterface
    private interface AnnotationsSupplier {
        // "nearest" or "most relevant" annotations should come first in stream
        <A extends Annotation> Stream<A> findAnnotations(Class<A> annotationType);
    }

    private static class AnnotationSupplierForMember implements AnnotationsSupplier {
        private final AnnotatedMember annotatedMember;
        private final AnnotationsSupplierForClass supplierForType;

        public AnnotationSupplierForMember(AnnotatedMember annotatedMember) {
            this.annotatedMember = annotatedMember;
            this.supplierForType = new AnnotationsSupplierForClass(annotatedMember.getType().getRawClass());
        }

        @Override
        public <A extends Annotation> Stream<A> findAnnotations(Class<A> annotationType) {
            Stream<A> annotationFromMember = Optional.ofNullable(annotatedMember.getAnnotation(annotationType))
                    .map(Stream::of)
                    .orElse(Stream.empty());
            return Stream.concat(annotationFromMember, supplierForType.findAnnotations(annotationType));
        }
    }

    private static class AnnotationsSupplierForClass implements AnnotationsSupplier {
        private final MergedAnnotations mergedAnnotations;

        public AnnotationsSupplierForClass(Class<?> clazz) {
            this.mergedAnnotations = MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY, RepeatableContainers.none());
        }

        @Override
        public <A extends Annotation> Stream<A> findAnnotations(Class<A> annotationType) {
            return mergedAnnotations.stream(annotationType)
                    .map(MergedAnnotation::synthesize);
        }
    }

}

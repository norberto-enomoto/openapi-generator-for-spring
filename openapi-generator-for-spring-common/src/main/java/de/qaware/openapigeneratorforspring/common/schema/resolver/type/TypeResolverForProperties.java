package de.qaware.openapigeneratorforspring.common.schema.resolver.type;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import de.qaware.openapigeneratorforspring.common.annotation.AnnotationsSupplier;
import de.qaware.openapigeneratorforspring.common.annotation.AnnotationsSupplierFactory;
import de.qaware.openapigeneratorforspring.common.schema.customizer.SchemaPropertiesCustomizer;
import de.qaware.openapigeneratorforspring.common.schema.resolver.SchemaBuilderFromType;
import de.qaware.openapigeneratorforspring.common.schema.resolver.type.initial.InitialSchema;
import de.qaware.openapigeneratorforspring.model.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static de.qaware.openapigeneratorforspring.common.util.OpenApiMapUtils.buildStringMapFromStream;

@RequiredArgsConstructor
@Slf4j
public class TypeResolverForProperties implements TypeResolver {

    // this resolver does not have any condition, so run this always later then the other resolvers as a fallback
    public static final int ORDER = DEFAULT_ORDER + 100;

    private final List<SchemaPropertiesCustomizer> schemaPropertiesCustomizers;
    private final AnnotationsSupplierFactory annotationsSupplierFactory;

    @Nullable
    public RecursionKey resolve(
            InitialSchema initialSchema,
            JavaType javaType,
            AnnotationsSupplier annotationsSupplier,
            SchemaBuilderFromType schemaBuilderFromType
    ) {
        Map<String, AnnotatedMember> properties = initialSchema.getProperties();
        if (properties.isEmpty()) {
            return null;
        }

        Map<String, PropertyCustomizer> propertyCustomizers = customizeSchemaProperties(initialSchema.getSchema(), javaType, annotationsSupplier, properties);

        properties.forEach((propertyName, member) -> {
            PropertyCustomizer propertyCustomizer = propertyCustomizers.get(propertyName);
            JavaType propertyType = member.getType();
            AnnotationsSupplier propertyAnnotationsSupplier = annotationsSupplierFactory.createFromMember(member)
                    .andThen(annotationsSupplierFactory.createFromAnnotatedElement(propertyType.getRawClass()));
            schemaBuilderFromType.buildSchemaFromType(propertyType, propertyAnnotationsSupplier,
                    propertySchema -> initialSchema.getSchema().setProperty(propertyName, propertyCustomizer.customize(propertySchema, propertyType, propertyAnnotationsSupplier))
            );
        });

        return new UniqueSchemaKey(javaType, initialSchema.getSchema().hashCode());
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    private static class UniqueSchemaKey implements RecursionKey {
        private final JavaType javaType;
        private final int schemaHash;
    }

    private Map<String, PropertyCustomizer> customizeSchemaProperties(Schema schema, JavaType javaType, AnnotationsSupplier annotationsSupplier, Map<String, AnnotatedMember> properties) {
        Map<String, PropertyCustomizer> customizerProperties = buildStringMapFromStream(
                properties.entrySet().stream(),
                Map.Entry::getKey,
                entry -> new PropertyCustomizer()
        );
        schemaPropertiesCustomizers.forEach(customizer -> customizer.customize(schema, javaType, annotationsSupplier, customizerProperties));
        return customizerProperties;
    }

    @RequiredArgsConstructor
    private static class PropertyCustomizer implements SchemaPropertiesCustomizer.SchemaProperty {

        @Nullable
        private SchemaPropertiesCustomizer.SchemaPropertyCustomizer schemaPropertyCustomizer;

        @Override
        public void customize(SchemaPropertiesCustomizer.SchemaPropertyCustomizer propertySchemaCustomizer) {
            this.schemaPropertyCustomizer = propertySchemaCustomizer;
        }

        public Schema customize(Schema propertySchema, JavaType javaType, AnnotationsSupplier annotationsSupplier) {
            if (schemaPropertyCustomizer != null) {
                schemaPropertyCustomizer.customize(propertySchema, javaType, annotationsSupplier);
                // TODO check if this is still necessary
                // avoid running customizers multiple again when referenced schemas are consumed
                schemaPropertyCustomizer = null;
            }
            return propertySchema;
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}

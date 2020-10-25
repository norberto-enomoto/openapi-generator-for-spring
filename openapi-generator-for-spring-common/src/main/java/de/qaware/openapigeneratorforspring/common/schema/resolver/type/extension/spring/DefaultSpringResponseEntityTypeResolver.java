package de.qaware.openapigeneratorforspring.common.schema.resolver.type.extension.spring;

import com.fasterxml.jackson.databind.JavaType;
import de.qaware.openapigeneratorforspring.common.annotation.AnnotationsSupplier;
import de.qaware.openapigeneratorforspring.common.schema.resolver.type.initial.InitialSchema;
import de.qaware.openapigeneratorforspring.common.schema.resolver.type.initial.InitialSchemaFactory;
import de.qaware.openapigeneratorforspring.common.schema.resolver.type.initial.InitialTypeResolver;
import org.springframework.http.ResponseEntity;

import javax.annotation.Nullable;

public class DefaultSpringResponseEntityTypeResolver implements InitialSchemaFactory {

    public static final int ORDER = DEFAULT_ORDER;

    @Nullable
    @Override
    public InitialSchema resolveFromType(JavaType javaType, AnnotationsSupplier annotationsSupplier, InitialTypeResolver fallbackResolver) {
        if (javaType.getRawClass().equals(ResponseEntity.class)) {
            return fallbackResolver.resolveFromType(javaType.containedType(0), annotationsSupplier);
        }
        return null;
    }

    @Override
    public int getOrder() {
        return ORDER;
    }


}

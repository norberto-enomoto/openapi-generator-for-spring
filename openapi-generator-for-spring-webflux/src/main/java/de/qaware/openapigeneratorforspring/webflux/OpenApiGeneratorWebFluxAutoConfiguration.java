package de.qaware.openapigeneratorforspring.webflux;

import de.qaware.openapigeneratorforspring.common.OpenApiGenerator;
import de.qaware.openapigeneratorforspring.common.OpenApiResourceParameterProvider;
import de.qaware.openapigeneratorforspring.common.paths.HandlerMethodsProvider;
import de.qaware.openapigeneratorforspring.common.schema.resolver.type.InitialSchemaFactoryForFlux;
import de.qaware.openapigeneratorforspring.common.schema.resolver.type.InitialSchemaFactoryForMono;
import de.qaware.openapigeneratorforspring.common.schema.resolver.type.TypeResolverForFlux;
import de.qaware.openapigeneratorforspring.common.util.OpenApiObjectMapperSupplier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;

public class OpenApiGeneratorWebFluxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OpenApiResourceForWebFlux openApiResource(OpenApiGenerator openApiGenerator, OpenApiObjectMapperSupplier openApiObjectMapperSupplier) {
        return new OpenApiResourceForWebFlux(openApiGenerator, openApiObjectMapperSupplier);
    }

    @Bean
    @ConditionalOnMissingBean
    public HandlerMethodsProvider handlerMethodsProviderFromWebMvc(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return new HandlerMethodsProviderForWebFlux(requestMappingHandlerMapping);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenApiResourceParameterProvider openApiResourceParameterProviderForWebFlux() {
        return new OpenApiResourceParameterProviderForWebFlux();
    }

    @Bean
    @ConditionalOnMissingBean
    public TypeResolverForFlux defaultTypeResolverForFlux() {
        return new TypeResolverForFlux();
    }

    @Bean
    @ConditionalOnMissingBean
    public InitialSchemaFactoryForFlux defaultInitialSchemaFactoryForFlux() {
        return new InitialSchemaFactoryForFlux();
    }

    @Bean
    @ConditionalOnMissingBean
    public InitialSchemaFactoryForMono defaultInitialSchemaFactoryForMono() {
        return new InitialSchemaFactoryForMono();
    }
}

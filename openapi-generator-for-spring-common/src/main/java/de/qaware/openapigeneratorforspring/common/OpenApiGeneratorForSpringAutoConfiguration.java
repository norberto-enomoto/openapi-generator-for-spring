package de.qaware.openapigeneratorforspring.common;

import de.qaware.openapigeneratorforspring.common.filter.operation.ExcludeHiddenOperationFilter;
import de.qaware.openapigeneratorforspring.common.filter.operation.OperationFilter;
import de.qaware.openapigeneratorforspring.common.filter.pathitem.NoOperationsPathItemFilter;
import de.qaware.openapigeneratorforspring.common.filter.pathitem.PathItemFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

@Configuration
public class OpenApiGeneratorForSpringAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public OpenApiResource openApiResource(
            RequestMappingHandlerMapping requestMappingHandlerMapping,
            List<PathItemFilter> pathItemFilters,
            List<OperationFilter> operationFilters
    ) {
        return new OpenApiResource(requestMappingHandlerMapping, pathItemFilters, operationFilters);
    }

    @Bean
    @ConditionalOnMissingBean
    public NoOperationsPathItemFilter noOperationsPathItemFilter() {
        return new NoOperationsPathItemFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    public ExcludeHiddenOperationFilter excludeHiddenOperationFilter() {
        return new ExcludeHiddenOperationFilter();
    }

}

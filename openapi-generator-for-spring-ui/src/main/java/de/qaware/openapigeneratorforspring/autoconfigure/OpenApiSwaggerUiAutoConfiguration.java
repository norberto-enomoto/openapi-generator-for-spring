/*-
 * #%L
 * OpenAPI Generator for Spring Boot :: UI
 * %%
 * Copyright (C) 2020 QAware GmbH
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

package de.qaware.openapigeneratorforspring.autoconfigure;

import de.qaware.openapigeneratorforspring.common.OpenApiConfigurationProperties;
import de.qaware.openapigeneratorforspring.ui.DefaultOpenApiSwaggerUiApiDocsUrisSupplier;
import de.qaware.openapigeneratorforspring.ui.OpenApiSwaggerUiApiDocsUrisSupplier;
import de.qaware.openapigeneratorforspring.ui.OpenApiSwaggerUiConfigurationProperties;
import de.qaware.openapigeneratorforspring.ui.swagger.SwaggerUiIndexHtmlWebJarResourceTransformerFactory;
import de.qaware.openapigeneratorforspring.ui.swagger.SwaggerUiSupport;
import de.qaware.openapigeneratorforspring.ui.webjar.DefaultWebJarTransformedResourceBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import java.time.Instant;

@EnableConfigurationProperties(OpenApiSwaggerUiConfigurationProperties.class)
public class OpenApiSwaggerUiAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SwaggerUiIndexHtmlWebJarResourceTransformerFactory swaggerUiIndexHtmlWebJarResourceTransformerFactory(
            OpenApiConfigurationProperties openApiConfigurationProperties,
            OpenApiSwaggerUiApiDocsUrisSupplier swaggerUiApiDocsUrisSupplier
    ) {
        return new SwaggerUiIndexHtmlWebJarResourceTransformerFactory(openApiConfigurationProperties, swaggerUiApiDocsUrisSupplier);
    }

    @Bean
    @ConditionalOnMissingBean
    public SwaggerUiSupport swaggerUiSupport(OpenApiSwaggerUiConfigurationProperties openApiSwaggerUiConfigurationProperties) {
        return new SwaggerUiSupport(openApiSwaggerUiConfigurationProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenApiSwaggerUiApiDocsUrisSupplier openApiSwaggerUiApiDocsUrisSupplier() {
        return new DefaultOpenApiSwaggerUiApiDocsUrisSupplier();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultWebJarTransformedResourceBuilder defaultWebJarTransformedResourceBuilder(OpenApiSwaggerUiConfigurationProperties openApiSwaggerUiConfigurationProperties) {
        return new DefaultWebJarTransformedResourceBuilder(openApiSwaggerUiConfigurationProperties, Instant::now);
    }
}

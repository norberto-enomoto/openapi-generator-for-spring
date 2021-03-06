/*-
 * #%L
 * OpenAPI Generator for Spring Boot :: Autoconfigure
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

import de.qaware.openapigeneratorforspring.common.util.OpenApiLoggingUtils;
import de.qaware.openapigeneratorforspring.model.media.Schema;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

public class OpenApiGeneratorUtilAutoConfiguration {

    public interface OpenApiLoggingUtilsRegistrationBean extends InitializingBean {

    }

    @Bean
    @ConditionalOnMissingBean
    public OpenApiLoggingUtilsRegistrationBean defaultOpenApiLoggingUtilsRegistrationBean() {
        return () -> OpenApiLoggingUtils.registerPrettyPrinter(Schema.class, OpenApiLoggingUtils::prettyPrintSchema);
    }

}

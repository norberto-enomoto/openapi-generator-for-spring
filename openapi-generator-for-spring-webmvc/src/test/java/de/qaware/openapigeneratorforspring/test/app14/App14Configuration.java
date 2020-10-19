package de.qaware.openapigeneratorforspring.test.app14;

import de.qaware.openapigeneratorforspring.common.reference.component.schema.ReferenceDeciderForSchema;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class App14Configuration {
    @Bean
    public ReferenceDeciderForSchema referenceDeciderForSchema() {
        return (schema, numberOfUsages) -> false;
    }
}

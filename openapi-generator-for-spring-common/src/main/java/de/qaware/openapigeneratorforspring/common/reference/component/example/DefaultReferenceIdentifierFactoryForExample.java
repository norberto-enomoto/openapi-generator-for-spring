package de.qaware.openapigeneratorforspring.common.reference.component.example;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultReferenceIdentifierFactoryForExample implements ReferenceIdentifierFactoryForExample {

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public String buildSuggestedIdentifier(@Nullable String exampleName) {
        return StringUtils.isNotBlank(exampleName) ? exampleName
                : "Example" + counter.getAndIncrement();
    }
}
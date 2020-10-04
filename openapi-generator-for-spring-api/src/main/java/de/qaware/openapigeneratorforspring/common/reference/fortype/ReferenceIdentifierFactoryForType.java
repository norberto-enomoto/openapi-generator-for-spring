package de.qaware.openapigeneratorforspring.common.reference.fortype;

import javax.annotation.Nullable;
import java.util.List;

public interface ReferenceIdentifierFactoryForType<T> {
    List<Object> buildIdentifierComponents(T item, @Nullable String suggestedIdentifier);
}
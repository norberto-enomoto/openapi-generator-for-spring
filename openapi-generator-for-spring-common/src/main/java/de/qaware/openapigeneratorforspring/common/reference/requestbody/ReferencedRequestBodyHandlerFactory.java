package de.qaware.openapigeneratorforspring.common.reference.requestbody;

import de.qaware.openapigeneratorforspring.common.reference.handler.ReferencedItemHandler;
import de.qaware.openapigeneratorforspring.common.reference.handler.ReferencedItemHandlerFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReferencedRequestBodyHandlerFactory implements ReferencedItemHandlerFactory {
    private final ReferenceDeciderForRequestBody referenceDecider;
    private final ReferenceIdentifierFactoryForRequestBody referenceIdentifierFactory;
    private final ReferenceIdentifierConflictResolverForRequestBody referenceIdentifierConflictResolver;

    @Override
    public ReferencedItemHandler create() {
        ReferencedRequestBodyStorage storage = new ReferencedRequestBodyStorage(referenceDecider, referenceIdentifierFactory, referenceIdentifierConflictResolver);
        return new ReferencedRequestBodyHandlerImpl(storage);
    }
}

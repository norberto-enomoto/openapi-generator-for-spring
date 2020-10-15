package de.qaware.openapigeneratorforspring.common.operation.response.reference;

import de.qaware.openapigeneratorforspring.common.reference.handler.ReferencedItemHandler;
import de.qaware.openapigeneratorforspring.common.reference.handler.ReferencedItemHandlerFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ReferencedApiResponsesHandlerFactory implements ReferencedItemHandlerFactory {

    private final ReferenceDeciderForApiResponse referenceDecider;
    private final ReferenceIdentifierFactoryForApiResponse referenceIdentifierFactory;
    private final ReferenceIdentifierConflictResolverForApiResponse referenceIdentifierConflictResolver;

    @Override
    public ReferencedItemHandler create() {
        ReferencedApiResponseStorage storage = new ReferencedApiResponseStorage(referenceDecider, referenceIdentifierFactory, referenceIdentifierConflictResolver);
        return new ReferencedApiResponsesHandlerImpl(storage);
    }

}

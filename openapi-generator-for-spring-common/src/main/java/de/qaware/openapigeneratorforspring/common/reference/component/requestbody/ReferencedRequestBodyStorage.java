package de.qaware.openapigeneratorforspring.common.reference.component.requestbody;

import de.qaware.openapigeneratorforspring.common.reference.AbstractReferencedItemStorage;
import de.qaware.openapigeneratorforspring.common.reference.ReferenceType;
import de.qaware.openapigeneratorforspring.common.reference.fortype.ReferenceDeciderForType;
import de.qaware.openapigeneratorforspring.common.reference.fortype.ReferenceIdentifierConflictResolverForType;
import de.qaware.openapigeneratorforspring.common.reference.fortype.ReferenceIdentifierFactoryForType;
import de.qaware.openapigeneratorforspring.model.requestbody.RequestBody;

import java.util.function.Consumer;


public class ReferencedRequestBodyStorage extends AbstractReferencedItemStorage<RequestBody, ReferencedRequestBodyStorage.Entry> {

    ReferencedRequestBodyStorage(ReferenceDeciderForType<RequestBody> referenceDecider, ReferenceIdentifierFactoryForType<RequestBody> referenceIdentifierFactory, ReferenceIdentifierConflictResolverForType<RequestBody> referenceIdentifierConflictResolver) {
        super(ReferenceType.REQUEST_BODY, referenceDecider, referenceIdentifierFactory, referenceIdentifierConflictResolver, RequestBody::new, Entry::new);
    }

    void storeMaybeReference(RequestBody requestBody, Consumer<RequestBody> setter) {
        getEntryOrAddNew(requestBody).addSetter(setter::accept);
    }

    static class Entry extends AbstractReferencedItemStorage.AbstractReferencableEntry<RequestBody> {
        protected Entry(RequestBody item) {
            super(item);
        }
    }
}

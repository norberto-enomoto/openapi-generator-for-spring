/*-
 * #%L
 * OpenAPI Generator for Spring Boot :: Common
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

package de.qaware.openapigeneratorforspring.common.reference.component.parameter;

import de.qaware.openapigeneratorforspring.common.reference.AbstractReferencedItemStorage;
import de.qaware.openapigeneratorforspring.common.reference.ReferenceType;
import de.qaware.openapigeneratorforspring.common.reference.fortype.ReferenceDeciderForType;
import de.qaware.openapigeneratorforspring.common.reference.fortype.ReferenceIdentifierBuilderForType;
import de.qaware.openapigeneratorforspring.common.reference.fortype.ReferenceIdentifierConflictResolverForType;
import de.qaware.openapigeneratorforspring.model.operation.Operation;
import de.qaware.openapigeneratorforspring.model.parameter.Parameter;
import de.qaware.openapigeneratorforspring.model.path.PathItem;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static de.qaware.openapigeneratorforspring.common.reference.ReferenceType.EXAMPLE;
import static de.qaware.openapigeneratorforspring.common.reference.ReferenceType.HEADER;
import static de.qaware.openapigeneratorforspring.common.reference.ReferenceType.SCHEMA;

public class ReferencedParameterStorage extends AbstractReferencedItemStorage<Parameter> {

    ReferencedParameterStorage(ReferenceDeciderForType<Parameter> referenceDecider, ReferenceIdentifierBuilderForType<Parameter> referenceIdentifierFactory, ReferenceIdentifierConflictResolverForType<Parameter> referenceIdentifierConflictResolver) {
        super(ReferenceType.PARAMETER, referenceDecider, referenceIdentifierFactory, referenceIdentifierConflictResolver, Parameter::new,
                Arrays.asList(SCHEMA, EXAMPLE, HEADER));
    }

    void maybeReferenceParameters(List<Parameter> parameters, Object owner) {
        // we use the fact that we can modify each parameter via index if it's supposed to be changed to a reference
        // we still need to remove previously set parameters to the same owner then
        checkOwner(owner);
        removeEntriesOwnedBy(owner);
        // using an IntStream here makes i effectively final and thus can be captured in nested lambda
        IntStream.range(0, parameters.size()).forEach(i -> addEntry(
                parameters.get(i),
                parameter -> parameters.set(i, parameter),
                AddEntryParameters.builder()
                        .suggestedIdentifier(parameters.get(i).getName())
                        .owner(owner)
                        .build()
        ));
    }

    private void checkOwner(Object owner) {
        if (owner instanceof Operation) {
            return;
        }
        if (owner instanceof PathItem) {
            return;
        }
        throw new IllegalStateException("Owner of parameter list is invalid: " + owner);
    }
}

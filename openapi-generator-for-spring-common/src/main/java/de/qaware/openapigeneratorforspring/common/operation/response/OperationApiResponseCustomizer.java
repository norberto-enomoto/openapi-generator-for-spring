package de.qaware.openapigeneratorforspring.common.operation.response;

import de.qaware.openapigeneratorforspring.common.operation.OperationBuilderContext;
import io.swagger.v3.oas.models.responses.ApiResponses;

public interface OperationApiResponseCustomizer {
    void customize(ApiResponses apiResponses, OperationBuilderContext operationBuilderContext);
}

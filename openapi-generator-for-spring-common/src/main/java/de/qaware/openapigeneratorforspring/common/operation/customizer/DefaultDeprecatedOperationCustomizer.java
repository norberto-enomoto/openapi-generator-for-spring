package de.qaware.openapigeneratorforspring.common.operation.customizer;

import de.qaware.openapigeneratorforspring.common.operation.OperationBuilderContext;
import de.qaware.openapigeneratorforspring.common.util.OpenApiAnnotationUtils;
import io.swagger.v3.oas.models.Operation;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

public class DefaultDeprecatedOperationCustomizer implements OperationCustomizer, Ordered {

    public static final int ORDER = Ordered.HIGHEST_PRECEDENCE + 1000;

    @Override
    public void customize(Operation operation, OperationBuilderContext operationBuilderContext) {
        Method method = operationBuilderContext.getHandlerMethod().getMethod();
        Deprecated deprecatedOnMethodOrClass = OpenApiAnnotationUtils.findAnnotationOnMethodOrClass(method, Deprecated.class);
        if (deprecatedOnMethodOrClass != null) {
            operation.deprecated(true);
        }
    }

    @Override
    public void customize(Operation operation, OperationBuilderContext operationBuilderContext,
                          io.swagger.v3.oas.annotations.Operation operationAnnotation) {
        if (operationAnnotation.deprecated()) {
            operation.setDeprecated(true);
        } else {
            customize(operation, operationBuilderContext);
        }
    }

    @Override
    public int getOrder() {
        return ORDER;
    }
}
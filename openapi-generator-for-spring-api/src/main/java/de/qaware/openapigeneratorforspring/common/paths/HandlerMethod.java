package de.qaware.openapigeneratorforspring.common.paths;

import de.qaware.openapigeneratorforspring.common.annotation.AnnotationsSupplier;
import de.qaware.openapigeneratorforspring.model.response.ApiResponse;
import org.springframework.core.annotation.Order;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface HandlerMethod {
    String getIdentifier();

    AnnotationsSupplier getAnnotationsSupplier();

    List<Parameter> getParameters();

    interface Type {
        java.lang.reflect.Type getType();

        AnnotationsSupplier getAnnotationsSupplier();
    }

    interface Parameter {
        Optional<String> getName();

        Optional<Type> getType();

        AnnotationsSupplier getAnnotationsSupplier();

        default void customize(de.qaware.openapigeneratorforspring.model.parameter.Parameter parameter) {
            // do nothing, customization callback is optional
        }
    }

    interface RequestBody {
        Optional<Type> getType();

        Set<String> getConsumesContentTypes();

        AnnotationsSupplier getAnnotationsSupplier();

        default void customize(de.qaware.openapigeneratorforspring.model.requestbody.RequestBody requestBody) {
            // do nothing, customization callback is optional
        }
    }

    interface Response {
        String getResponseCode();

        Optional<Type> getType();

        Set<String> getProducesContentTypes();

        default void customize(ApiResponse apiResponse) {
            // do nothing, customization callback is optional
        }
    }

    // TODO use this at least for Spring Web!
    @FunctionalInterface
    @Order(0)
    interface MediaTypesParameterMapper {
        @Nullable
        List<String> map(Parameter parameter);
    }

    @FunctionalInterface
    @Order(0)
    interface RequestBodyMapper {
        @Nullable
        List<RequestBody> map(HandlerMethod handlerMethod);
    }

    @FunctionalInterface
    @Order(0)
    interface ResponseMapper {
        @Nullable
        List<Response> map(HandlerMethod handlerMethod);
    }

    @FunctionalInterface
    @Order(0)
    interface Merger {
        @Nullable
        HandlerMethod merge(List<HandlerMethod> handlerMethods);
    }
}

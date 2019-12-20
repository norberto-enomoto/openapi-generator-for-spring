package de.qaware.openapigeneratorforspring.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.qaware.openapigeneratorforspring.common.filter.operation.OperationFilter;
import de.qaware.openapigeneratorforspring.common.filter.pathitem.PathItemFilter;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@Hidden
public class OpenApiResource {

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;

    private final List<PathItemFilter> pathItemFilters;
    private final List<OperationFilter> operationFilters;

    @Autowired
    public OpenApiResource(RequestMappingHandlerMapping requestMappingHandlerMapping, List<PathItemFilter> pathItemFilters, List<OperationFilter> operationFilters) {
        this.requestMappingHandlerMapping = requestMappingHandlerMapping;
        this.pathItemFilters = pathItemFilters;
        this.operationFilters = operationFilters;
    }

    @GetMapping(value = "/v3/api-docs", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getOpenApiAsJson() throws JsonProcessingException {

        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();

        Paths paths = new Paths();
        map.forEach((info, handlerMethod) -> info.getPatternsCondition().getPatterns().forEach(pathPattern -> {
            PathItem pathItem = new PathItem();
            Set<RequestMethod> requestMethods = info.getMethodsCondition().getMethods();
            Map<RequestMethod, Operation> operationPerMethod = new EnumMap<>(RequestMethod.class);
            requestMethods.forEach(requestMethod -> {
                Operation operation = new Operation().description(handlerMethod.getShortLogMessage());
                if (isAcceptedByAllOperationFilters(operation, handlerMethod)) {
                    operationPerMethod.put(requestMethod, operation);
                    setOperationOnPathItem(requestMethod, pathItem, operation);
                }
            });

            if (isAcceptedByAllPathFilters(pathItem, pathPattern, operationPerMethod)) {
                paths.addPathItem(pathPattern, pathItem);
            }
        }));

        OpenAPI openApi = new OpenAPI();
        if (!paths.isEmpty()) {
            openApi.setPaths(paths);
        }
        return Json.mapper().writeValueAsString(openApi);
    }

    private boolean isAcceptedByAllOperationFilters(Operation operation, HandlerMethod handlerMethod) {
        for (OperationFilter operationFilter : operationFilters) {
            if (!operationFilter.accept(operation, handlerMethod)) {
                return false;
            }
        }
        return true;
    }

    private boolean isAcceptedByAllPathFilters(PathItem pathItem, String pathPattern, Map<RequestMethod, Operation> operationPerMethod) {
        for (PathItemFilter pathItemFilter : pathItemFilters) {
            if (!pathItemFilter.accept(pathItem, pathPattern, operationPerMethod)) {
                return false;
            }
        }
        return true;
    }

    private static void setOperationOnPathItem(RequestMethod requestMethod, PathItem pathItem, Operation operation) {
        switch (requestMethod) {
            case GET:
                pathItem.setGet(operation);
                break;
            case HEAD:
                pathItem.setHead(operation);
                break;
            case POST:
                pathItem.setPost(operation);
                break;
            case PUT:
                pathItem.setPut(operation);
                break;
            case PATCH:
                pathItem.setPatch(operation);
                break;
            case DELETE:
                pathItem.setDelete(operation);
                break;
            case OPTIONS:
                pathItem.setOptions(operation);
                break;
            case TRACE:
                pathItem.setTrace(operation);
                break;
        }
    }

}

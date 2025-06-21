package org.finance.financemanager.common.config;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static java.lang.String.format;
import static org.finance.financemanager.common.config.Constants.OPERATION_ID_NAME;
import static org.finance.financemanager.common.config.Constants.OPERATION_ID_PATH_VARIABLE;

@Slf4j
@Configuration
public class OpenApiConfig {

    static {
        var schema = new Schema<LocalTime>();
        schema.example(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))).type("string");
        SpringDocUtils.getConfig().replaceWithSchema(LocalTime.class, schema);
    }

    @Bean
    public OpenAPI customApi() {
        var securitySchemeName = "bearerAuth";
        var schema = new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT");
        var component = new Components().addSecuritySchemes(securitySchemeName, schema);
        return new OpenAPI()
                .components(component)
                .security(List.of(new SecurityRequirement().addList(securitySchemeName)))
                .info(new Info().title("FinanceManager server").version("v1"));
    }

    @Bean
    OpenApiCustomizer operationIdCustomNames() {
        return openApi -> {
            var values = openApi.getPaths().values();
            values.stream().flatMap(pathItem -> pathItem.readOperations().stream()).forEach(this::customOperation);
        };
    }

    private void customOperation(Operation operation) {
        updateOperationName(operation);
        updateOperationParameters(operation);
        operation.setTags(operation.getTags().stream().filter(item -> !item.startsWith(OPERATION_ID_NAME) && !item.startsWith(OPERATION_ID_PATH_VARIABLE)).toList());
        if (operation.getSummary() == null || operation.getSummary().isEmpty()) {
            operation.setSummary(getSummary(operation.getTags()));
        }
    }

    private void updateOperationName(Operation operation) {
        var tag = operation.getTags().stream()
                .filter(item -> item.startsWith(OPERATION_ID_NAME))
                .map(item -> item.replace(OPERATION_ID_NAME, ""))
                .findFirst();
        var operationId = operation.getOperationId();
        if (tag.isPresent()) {
            operationId = operationId.replace("#", tag.get()).replaceAll("_\\d+", "");
        } else {
            operationId = operationId.replace("#", "");
        }
        operation.setOperationId(operationId);
    }

    private void updateOperationParameters(Operation operation) {
        if (operation.getParameters() != null) {
            var tag = operation.getTags().stream()
                    .filter(item -> item.startsWith(OPERATION_ID_PATH_VARIABLE))
                    .map(item -> item.replace(OPERATION_ID_PATH_VARIABLE, ""))
                    .findFirst();
            if (tag.isPresent()) {
                if (!hasParameter(operation.getParameters(), tag.get(), ParameterIn.PATH.toString())) {
                    var parameter = new Parameter();
                    parameter.setName(tag.get());
                    parameter.setIn(ParameterIn.PATH.toString());
                    parameter.setExample(tag.get());
                    operation.getParameters().add(parameter);
                }
            } else {
                operation.setParameters(operation.getParameters().stream().filter(item -> !item.getName().startsWith("#")).toList());
            }
        }
    }

    private boolean hasParameter(List<Parameter> parameters, String tag, String location) {
        if (parameters == null || parameters.isEmpty()) {
            return false;
        } else {
            return parameters.stream().anyMatch(item -> Objects.equals(item.getName(), tag) && Objects.equals(item.getIn(), location));
        }
    }

    private String getSummary(List<String> tags) {
        var first = tags.stream().map(String::toLowerCase).filter(item -> item.startsWith("common") ||
                item.startsWith("admin") || item.startsWith("client")).findFirst();
        if (first.isPresent()) {
            var name = first.get();
            if (name.toLowerCase().startsWith("common")) {
                return "Required bearer token with any role or token without roles.";
            }
            for (String role : List.of("admin", "client")) {
                if (name.toLowerCase().startsWith(role)) {
                    return format("Required bearer token with %s role.", role);
                }
            }
        }
        return "Bearer token is not required for this endpoint.";
    }
}

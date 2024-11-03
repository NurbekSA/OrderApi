package org.example.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenApiCustomizer customOpenApiCustomizer() {
        return openApi -> openApi.getPaths().values()
                .forEach(pathItem -> pathItem.readOperations()
                        .forEach(operation -> operation.getResponses().values()
                                .forEach(apiResponse -> {
                                    // Убираем пример из каждого ответа
                                    if (apiResponse.getContent() != null) {
                                        apiResponse.getContent().values()
                                                .forEach(mediaType -> mediaType.setExample(null));
                                    }
                                })
                        )
                );
    }
}

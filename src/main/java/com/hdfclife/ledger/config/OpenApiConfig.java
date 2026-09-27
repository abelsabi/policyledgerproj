package com.hdfclife.ledger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ledgerOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("HDFC Life Policy Ledger")
                        .version("1.0.0")
                        .description("REST API for customers, policies, claims, and riders"));
    }
}

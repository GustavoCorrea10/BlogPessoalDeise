package com.gustavo.blogpessoaldeise.configuration;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;

// IMPORTS NECESSÁRIOS PARA A SEGURANÇA NO SWAGGER
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.Components;


@Configuration
public class SwaggerConfig {

	@Bean
	OpenAPI springBlogPessoalOpenAPI() {

		return new OpenAPI()

				.info(new Info().title("Projeto Blog Pessoal/Deise").description("Blog Pessoal da Deise")
						.version("v0.1")
						.license(new License().name("Generation Brasil").url("https://brazil.generation.org/"))

						.contact(new Contact().name("Gustavo Corrêa")
								.url("https://github.com/GustavoCorrea10/Projeto_guia_blogPessoal")
								.email("gustavocorreaa11@gmail.com")))

				.externalDocs(new ExternalDocumentation().description("Github")
						.url("https://github.com/GustavoCorrea10/Projeto_guia_blogPessoal/"))
                
                
                
                .components(new Components()
                        .addSecuritySchemes("JWT", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP) 
                                .scheme("bearer") 
                                .bearerFormat("JWT") 
                                .name("JWT"))) 

                .addSecurityItem(new SecurityRequirement().addList("JWT"));
                // **** FIM DA NOVA CONFIGURAÇÃO DE SEGURANÇA ****
	}

	@Bean
	OpenApiCustomizer customerGlobalHeaderOpenApiCustomiser() {

		return openApi -> {

			openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {

				ApiResponses apiResponses = operation.getResponses();

				apiResponses.addApiResponse("200", createApiResponse("Sucesso!"));
				apiResponses.addApiResponse("201", createApiResponse("Objeto Persistido!"));
				apiResponses.addApiResponse("204", createApiResponse("Objeto Excluído!"));
				apiResponses.addApiResponse("400", createApiResponse("Erro na Requisição!"));
				apiResponses.addApiResponse("404", createApiResponse("Objeto Não Encontrado!"));
				apiResponses.addApiResponse("500", createApiResponse("Erro na Aplicação!"));

			}));
		};
	}

	private ApiResponse createApiResponse(String message) {

		return new ApiResponse().description(message);

	}

}
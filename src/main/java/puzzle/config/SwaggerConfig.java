package puzzle.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket puntoGeograficoLotApi() {
		return new Docket(DocumentationType.SWAGGER_2).groupName("Puzzle-api").apiInfo(apiInfo()).select()
				.apis(RequestHandlerSelectors.basePackage("puzzle.controller")).build();

	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Puzzle API").description("Puzzle").termsOfServiceUrl("http://ejemplo.com")
				.contact(new Contact("Puzzle Application", "http://ejemplo.com", "ejemplo@gmail.com"))
				.license("Ejemplo Lot License").licenseUrl("manuelt84@gmail.com").version("1.0").build();
	}

}
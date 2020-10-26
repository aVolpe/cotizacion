package py.com.volpe.cotizacion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .enableUrlTemplating(false)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(buildInfo());
    }

    private ApiInfo buildInfo() {
        return new ApiInfoBuilder()
                .license("MIT")
                .title("Cotizaciones")
                .version("2.0.0")
                .contact(new Contact("Arturo Volpe",
                        "https://www.volpe.com.py",
                        "arturovolpe@gmail.com"))
                .build();
    }
}

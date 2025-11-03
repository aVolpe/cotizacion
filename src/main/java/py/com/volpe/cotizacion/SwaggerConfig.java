package py.com.volpe.cotizacion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cotizaciones")
                        .version("2.0.0")
                        .license(new License().name("MIT"))
                        .contact(new Contact()
                                .name("Arturo Volpe")
                                .url("https://www.volpe.com.py")
                                .email("arturovolpe@gmail.com")));
    }
}

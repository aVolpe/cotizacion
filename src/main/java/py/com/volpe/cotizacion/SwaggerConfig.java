package py.com.volpe.cotizacion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .servers(List.of(new Server()
                        .url("https://cotizaciones.volpe.com.py/")))
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

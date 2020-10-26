package py.com.volpe.cotizacion;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@EnableWebMvc
public class Cotizacion {

    public static void main(String[] args) {
        SpringApplication.run(Cotizacion.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**");
            }

            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/swagger-ui/")
                        .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/index.html")
                        .resourceChain(false);
                if (!registry.hasMappingForPattern("/**")) {
                    registry.addResourceHandler("/**").addResourceLocations("classpath:/public/");
                }
            }
        };

    }

    @Bean
    @Profile("production")
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }
}

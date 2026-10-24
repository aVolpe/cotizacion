package py.com.volpe.cotizacion;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Real Postgres for the tests, same major version as production (see embedded-postgres-binaries.version
 * in the pom), without docker.
 * <p>
 * A single server is started per JVM and shared by every Spring context; the schema comes from the same
 * Flyway migrations used in production. Tests that write must roll back (e.g. {@code @DataJpaTest}).
 */
@TestConfiguration(proxyBeanMethods = false)
public class EmbeddedPostgresConfiguration {

    private static EmbeddedPostgres server;

    static synchronized EmbeddedPostgres server() {
        if (server == null) {
            try {
                server = EmbeddedPostgres.builder().start();
            } catch (IOException e) {
                throw new UncheckedIOException("Can't start the embedded Postgres", e);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    server.close();
                } catch (IOException ignored) {
                    // the JVM is going down anyway
                }
            }));
        }
        return server;
    }

    @Bean
    DataSource dataSource() {
        return server().getPostgresDatabase();
    }
}

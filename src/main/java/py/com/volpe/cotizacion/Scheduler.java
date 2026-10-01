package py.com.volpe.cotizacion;

import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Arturo Volpe
 * @since 5/2/18
 */
@Log4j2
@Component
@Profile("production")
public class Scheduler {

    private static final long MAX_EXECUTION_MINUTES = 5;

    private final GathererManager manager;

    /**
     * The query runs here instead of in the (single) scheduler thread, so a hung gatherer is
     * abandoned after {@link #MAX_EXECUTION_MINUTES} and doesn't block the next executions.
     */
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public Scheduler(GathererManager manager) {
        this.manager = manager;
    }

    @Scheduled(cron = "0 */10 7-20 * * MON-SAT", zone = "America/Asuncion")
    public void triggerQuery() throws InterruptedException {
        log.info("Querying the services");
        Future<?> work = executor.submit(() -> manager.doQuery(null));
        try {
            work.get(MAX_EXECUTION_MINUTES, TimeUnit.MINUTES);
        } catch (TimeoutException e) {
            log.warn("The query took more than {} minutes, cancelling it", MAX_EXECUTION_MINUTES);
            work.cancel(true);
        } catch (ExecutionException e) {
            log.warn("The query failed", e.getCause());
        }
    }

    @PreDestroy
    void shutdown() {
        executor.shutdownNow();
    }
}

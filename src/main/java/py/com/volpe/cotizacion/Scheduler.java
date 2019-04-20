package py.com.volpe.cotizacion;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Arturo Volpe
 * @since 5/2/18
 */
@Log4j2
@Component
@Profile("production")
@RequiredArgsConstructor
public class Scheduler {

    private final GathererManager manager;

    @PostConstruct
    public void init() {
        try {
            this.triggerQuery();
        } catch (ExecutionException e) {
            // we don't care
        }
    }

    @Scheduled(cron = "0 */10 7-20 * * MON-SAT", zone = "America/Asuncion")
    public void triggerQuery() throws ExecutionException {
        doWork().get(5, TimeUnit.MINUTES);
    }

    @Async
    AsyncResult<Boolean> doWork() {
        log.info("Querying the services");
        manager.doQuery(null);
        return new AsyncResult<>(true);
    }
}

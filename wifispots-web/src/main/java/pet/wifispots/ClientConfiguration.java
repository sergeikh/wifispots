package pet.wifispots;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.web.client.AsyncRestTemplate;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Rest template configuration.
 */
@Configuration
public class ClientConfiguration {
    @Bean
    public AsyncRestTemplate asyncRestTemplate() {
        Executor executor = Executors.newFixedThreadPool(10);
        ConcurrentTaskExecutor taskExecutor = new ConcurrentTaskExecutor(executor);
        return new AsyncRestTemplate(taskExecutor);
    }
}

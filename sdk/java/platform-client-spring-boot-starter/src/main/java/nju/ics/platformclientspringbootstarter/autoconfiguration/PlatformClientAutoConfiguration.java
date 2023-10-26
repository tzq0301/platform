package nju.ics.platformclientspringbootstarter.autoconfiguration;

import jakarta.annotation.Resource;
import lombok.Setter;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
@EnableConfigurationProperties({PlatformClientProperties.class})
@EnableAsync
public class PlatformClientAutoConfiguration implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
    @Resource
    @Setter
    private PlatformClientProperties properties;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public TaskScheduler taskScheduler() {
        return new ConcurrentTaskScheduler();
    }

    @Override
    public void customize(ConfigurableWebServerFactory factory) {
        if (properties.getClientPort() == null) {
            return;
        }

        factory.setPort(properties.getClientPort());
    }
}

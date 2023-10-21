package pub;

import jakarta.annotation.Resource;
import nju.ics.platformclientspringbootstarter.handler.Handler;
import nju.ics.platformclientspringbootstarter.handler.Listener;
import nju.ics.platformclientspringbootstarter.model.Topic;
import nju.ics.platformclientspringbootstarter.publish.Publisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableScheduling
public class PubApplication {
    public static void main(String[] args) {
        SpringApplication.run(PubApplication.class, args);
    }

    @Bean
    public Topic topic() {
        return new Topic("topic");
    }

    @Component
    public static class L {
        @Resource
        Publisher publisher;

        @Resource
        Topic topic;

        @Scheduled(cron = "*/1 * * * * ?")
        public void publish() {
            int data = 10;
            System.out.println(data);
            publisher.publish(topic, data);
        }
    }
}

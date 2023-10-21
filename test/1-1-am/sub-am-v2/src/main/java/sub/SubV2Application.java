package sub;

import nju.ics.platformclientspringbootstarter.handler.Handler;
import nju.ics.platformclientspringbootstarter.handler.Listener;
import nju.ics.platformclientspringbootstarter.model.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SubV2Application {
    public static void main(String[] args) {
        SpringApplication.run(SubV2Application.class, args);
    }

    @Listener
    public static class L {
        @Handler(topic = "topic")
        public void onMessage(Message<Integer> message) {
            System.out.println(message.data() + 5);
        }
    }
}

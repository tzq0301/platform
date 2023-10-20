package nju.ics.platformserver.pubsub;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PubSubTest {
    @Test
    void test() {
        // -----------------------------------------------------------------
        // init
        // -----------------------------------------------------------------

        PubSub pubsub = new PubSub();

        Client pubString = new Client(pubsub, "pubString", "");

        Client pubInteger = new Client(pubsub, "pubInteger", "");

        Topic topic = new Topic("topic");

        Client subV1 = new Client(pubsub, "sub", "");
        Client subV2 = new Client(pubsub, "sub", "");

        // -----------------------------------------------------------------
        // test: basic pubsub
        // -----------------------------------------------------------------

        pubsub.registerClient(pubString, Set.of(topic), Set.of());
        pubsub.registerClient(pubInteger, Set.of(topic), Set.of());
        pubsub.registerClient(subV1, Set.of(), Set.of(topic));

        assertEquals(0, subV1.listUnreadMessages().size());

        {
            pubString.publish(topic, "1");
            pubString.publish(topic, "1");
            pubInteger.publish(topic, 0);

            List<Message<?>> messages = subV1.listUnreadMessages();
            assertEquals("1", messages.get(0).data());
            assertEquals("1", messages.get(1).data());
            assertEquals(0, messages.get(2).data());
        }

        {
            pubsub.unregisterClient(subV1);

            pubString.publish(topic, "1");
            assertEquals(0, subV1.listUnreadMessages().size());

            pubsub.registerClient(subV1, Set.of(), Set.of(topic));
        }

        // -----------------------------------------------------------------
        // test: same name then choose the latest
        // -----------------------------------------------------------------

        {
            pubsub.registerClient(subV2, Set.of(), Set.of(topic));

            {
                pubString.publish(topic, 2);

                List<Message<?>> messages = subV2.listUnreadMessages();
                assertEquals(1, messages.size());
                assertEquals(2, messages.get(0).data());

                assertEquals(0, subV1.listUnreadMessages().size());
            }

            pubsub.unregisterClient(subV2);

            {
                pubString.publish(topic, 1);

                List<Message<?>> messages = subV1.listUnreadMessages();
                assertEquals(1, messages.size());
                assertEquals(1, messages.get(0).data());
            }
        }
    }

    @Test
    void testAllTopic() {
        PubSub pubsub = new PubSub();

        pubsub.registerPlugin(new AllTopicSolverPlugin());

        Client pub = new Client(pubsub, "pub", "");

        Topic t1 = new Topic("t1");
        Topic t2 = new Topic("t2");
        Topic t3 = new Topic("t3");

        Client subAll = new Client(pubsub, "subAll", "");
        Client subT1 = new Client(pubsub, "subT1", "");

        pubsub.registerClient(pub, Set.of(t1, t2, t3), Set.of());
        pubsub.registerClient(subAll, Set.of(), Set.of(new Topic("*")));
        pubsub.registerClient(subT1, Set.of(), Set.of(t1));

        pub.publish(t1, null);
        pub.publish(t2, null);

        assertEquals(2, subAll.listUnreadMessages().size());

        pub.publish(t1, null);
        pub.publish(t2, null);
        pub.publish(t3, null);

        assertEquals(3, subAll.listUnreadMessages().size());

        assertEquals(0, subAll.listUnreadMessages().size());
        assertEquals(2, subT1.listUnreadMessages().size());

        Client subT1V2 = new Client(pubsub, "subT1", "");
        pubsub.registerClient(subT1V2, Set.of(), Set.of(t1));
        pub.publish(t1, null);
        assertEquals(0, subT1.listUnreadMessages().size());
        assertEquals(1, subT1V2.listUnreadMessages().size());
    }

    @Test
    void testKeepAliveness() throws InterruptedException {
        PubSub pubsub = new PubSub();

        Topic t = new Topic("t");

        Client c1 = new Client(pubsub, "c1", "");
        pubsub.registerClient(c1, Set.of(t), Set.of());

        Client c2 = new Client(pubsub, "c2", "");
        pubsub.registerClient(c2, Set.of(t), Set.of());

        AlivenessCheckerPlugin alivenessChecker = new AlivenessCheckerPlugin(Duration.ofMillis(50)); // duration = 0.05s
        pubsub.registerPlugin(alivenessChecker);

        alivenessChecker.keepAlive(c1);
        alivenessChecker.keepAlive(c2);

        assertEquals(0, alivenessChecker.removeDeadClients().size());

        Thread.sleep(20); // now -> 0.02s

        alivenessChecker.keepAlive(c1);

        Thread.sleep(40); // now += 0.04s -> 0.06s (1st check time)
                                // 0.06s > 0.05s

        {
            List<Client> removed = alivenessChecker.removeDeadClients();
            assertFalse(removed.contains(c1));
            assertTrue(removed.contains(c2));
        }

        Thread.sleep(50); // now += 0.05s -> 0.11s
                                // 0.11s > 0.06s (1st check time) + 0.05s (duration)
        {
            List<Client> removed = alivenessChecker.removeDeadClients();
            assertTrue(removed.contains(c1));
        }
    }
}
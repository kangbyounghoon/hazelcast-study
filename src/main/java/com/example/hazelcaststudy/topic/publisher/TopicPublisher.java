package com.example.hazelcaststudy.topic.publisher;

import com.example.hazelcaststudy.configuration.ClusterConfiguration;
import com.example.hazelcaststudy.configuration.CoreConfiguration;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.topic.ITopic;
import com.hazelcast.topic.Message;
import com.hazelcast.topic.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Random;

@Component
public class TopicPublisher {
    private final static Logger log = LoggerFactory.getLogger(TopicPublisher.class);

    private final CoreConfiguration coreConfiguration;
    private final ClusterConfiguration clusterConfiguration;

    @Autowired
    public TopicPublisher(CoreConfiguration coreConfiguration, ClusterConfiguration clusterConfiguration) {
        this.coreConfiguration = coreConfiguration;
        this.clusterConfiguration = clusterConfiguration;
    }

    @PostConstruct
    public synchronized void init() {
        HazelcastInstance hz = Hazelcast.newHazelcastInstance(config());
        Random random = new Random();
        log.info("ibro-cluster :: {}", hz.getName());

        long messageId = 0;

        ITopic<Long> cacheSyncTopic = hz.getReliableTopic("cacheSync");
        cacheSyncTopic.addMessageListener(new MessageListenerImpl());

        while (true) {
            cacheSyncTopic.publish(messageId);
            messageId++;
            log.debug("ibro-Written: {}", messageId);
            sleepMillis(random.nextInt(1000));
        }
    }

    public Config config() {
        Config config = new Config();
        config.setInstanceName("ibro-cluster-hazelcast");

        return config;
    }

    public static boolean sleepMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        return true;
    }

    private static class MessageListenerImpl implements MessageListener<Long> {
        @Override
        public void onMessage(Message<Long> message) {
            log.error("ibro-Received: {}, uuid:: {}", message.getMessageObject(), message.getPublishingMember().getUuid());

        }
    }
}

package com.example.hazelcaststudy.server;

import com.example.hazelcaststudy.configuration.ClusterConfiguration;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.collection.IQueue;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class CustomerServer {
    private final static Logger log = LoggerFactory.getLogger(CustomerServer.class);

    private final ClusterConfiguration clusterConfiguration;

    @Autowired
    public CustomerServer(ClusterConfiguration clusterConfiguration) {
        this.clusterConfiguration = clusterConfiguration;
    }

    private final Runnable clientThread = new Runnable() {
        @Override
        public void run() {
            log.info("clientThread run Start :: {}", Runnable.class.getName());

            ClientConfig clientConfig = new ClientConfig();
            HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
            IMap<Object, Object> map = client.getMap("customers");
            System.out.println("Map size: " + map.size());

            IQueue<Object> queue = client.getQueue("customers");
            System.out.println("Queue size: " + queue.size());
        }
    };

    @PostConstruct
    public void init() {
        log.info("init :: {}", CustomerServer.class.getName());

        for (int i = 0; i < 2; i++) {
            initServer(i);
        }
        asyncClient();
    }

    public void initServer(int init) {
        Config cfg = new Config();

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);

        //각각의 instance name은 다르나 데이터는 공유 한다.
        log.info("HazelcastInstance Name :: {}", instance.getName());

        if (init == 0) {
            IMap<Object, Object> mapCustomers = instance.getMap("customers");
            mapCustomers.put(1, "Joe");
            mapCustomers.put(2, "Ali");
            mapCustomers.put(3, "Avi");

            IQueue<Object> queueCustomers = instance.getQueue("customers");
            queueCustomers.offer("Tom");
            queueCustomers.offer("Mary");
            queueCustomers.offer("Jane");
        }

        IMap<Object, Object> mapCustomers = instance.getMap("customers");
        System.out.println("Customer with key 1: " + mapCustomers.get(1));
        System.out.println("Map Size: " + mapCustomers.size());

        IQueue<Object> queueCustomers = instance.getQueue("customers");
//        System.out.println("First customer: " + queueCustomers.poll());
        System.out.println("Second customer: " + queueCustomers.peek());
        System.out.println("Queue size: " + queueCustomers.size());

        log.info("ClusterConfiguration api-url-prefix :: {}", clusterConfiguration.getApiUrlPrefix());
        log.info("ClusterConfiguration img-url-prefix :: {}", clusterConfiguration.getImgUrlPrefix());
        log.info("ClusterConfiguration front-url-prefix :: {}", clusterConfiguration.getFrontUrlPrefix());
    }

    @Async
    public void asyncClient() {
        try {
            Thread.sleep(2000);
            clientThread.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void destroy() {
        log.info("destory :: {}", CustomerServer.class.getName());
    }
}

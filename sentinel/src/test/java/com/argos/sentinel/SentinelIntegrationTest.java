package com.argos.sentinel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.argos.sentinel.service.RedisService;
import com.argos.sentinel.service.TrafficAnalyzer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class SentinelIntegrationTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
    .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private TrafficAnalyzer trafficAnalyzer;
    @Autowired
    private RedisService redisService;

    // Clean Redis data base for each test
    @BeforeEach
    void cleanRedis() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void slidingWindowNotBlockedIpTest() throws InterruptedException {
        String ip = "127.0.0.1";

        int numberOfThreads = 49;
        ExecutorService eService = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            eService.execute(() -> {
                try {
                    startLatch.await();
                    trafficAnalyzer.processAndCheckLimit(ip);
                } catch (Exception e) {
                    System.out.println("An error Ocurred: " + e.getMessage());
                } finally {
                    endLatch.countDown();
                }
            });
        }
        startLatch.countDown();
        endLatch.await();
        assertEquals(false , redisService.isBanned(ip)); // Must be false
    }

    @Test
    void slidingWindowBlockedIpTest() throws InterruptedException {
        
        String ip = "127.0.0.1";
        int numberOfThreads = 50;
        ExecutorService eService = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
        for (int i = 0; i < numberOfThreads; i++) {
            eService.execute(() -> {
                try {
                    startLatch.await();
                    trafficAnalyzer.processAndCheckLimit(ip);
                } catch (Exception e) {
                    // Complete here
                } finally {
                    endLatch.countDown();
                }
            });
        }
        startLatch.countDown();
        endLatch.await();

        assertEquals(true , redisService.isBanned(ip)); // Must be true
    }

    @Test
    void slidingWindowTestWindowsBlocked() throws InterruptedException {
        
        String ip = "127.0.0.1";
        
        int numberOfThreads = 49;
        ExecutorService eService = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
        CyclicBarrier barrier = new CyclicBarrier(49); // 49 threads for lot

        for (int i = 0; i < numberOfThreads; i++) {
            eService.execute(() -> {
                try {
                    barrier.await();
                    trafficAnalyzer.processAndCheckLimit(ip);
                } catch (Exception e) {
                    e.getMessage();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        endLatch.await();
        Thread.sleep(9000);
        CountDownLatch endLatch2 = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            eService.execute(() -> {
                try {
                    barrier.await();
                    trafficAnalyzer.processAndCheckLimit(ip);
                } catch (Exception e) {
                    e.getMessage();
                } finally {
                    endLatch2.countDown();
                }
            });
        }
        endLatch2.await();
        assertEquals(true , redisService.isBanned(ip)); // Must be TRUE
    }

    @Test
    void slidingWindowTestWindowsNotBlocked() throws InterruptedException {
        
        String ip = "127.0.0.1";
        
        int numberOfThreads = 49;
        ExecutorService eService = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
        CyclicBarrier barrier = new CyclicBarrier(49); // 49 threads for lot

        for (int i = 0; i < numberOfThreads; i++) {
            eService.execute(() -> {
                try {
                    barrier.await();
                    trafficAnalyzer.processAndCheckLimit(ip);
                } catch (Exception e) {
                    e.getMessage();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        endLatch.await();
        Thread.sleep(11000);
        CountDownLatch endLatch2 = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            eService.execute(() -> {
                try {
                    barrier.await();
                    trafficAnalyzer.processAndCheckLimit(ip);
                } catch (Exception e) {
                    e.getMessage();
                } finally {
                    endLatch2.countDown();
                }
            });
        }
        endLatch2.await();
        assertEquals(false , redisService.isBanned(ip)); // Must be false
    }
}
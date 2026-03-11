package com.argos.orders;

import com.argos.orders.model.Product;
import com.argos.orders.repository.ProductRepository;
import com.argos.orders.service.IProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class OrderIntegrationTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("shop_db")
            .withUsername("user_shop")
            .withPassword("secretPassword");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.2-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
    }
    @Autowired
    private IProductService service;
    @Autowired
    private ProductRepository repository;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @BeforeEach
    void cleanRedis() {
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void testSellAnElement() {
        // ARRANGE
        Product prod = new Product();
        prod.setProductName("Test");
        prod.setProductPrice(BigDecimal.valueOf(100));
        prod.setProductStock(1);
        Product savedProd = repository.save(prod);
        Long id = savedProd.getProductID();
        String ip = "127.0.0.1";

        //ACT
        service.sellProduct(id, ip, 1);

        String redisKey = "blacklist:ip:" + ip;
        Boolean hasKey = redisTemplate.hasKey(redisKey);
        //ASSERT
        Product updatedProduct = repository.findById(id).get();
        assertEquals((int) updatedProduct.getProductStock(), (savedProd.getProductStock() - 1));
        assertFalse(hasKey);
    }

    @Test
    void testBlockFileWhenIUpdateAnElement() throws InterruptedException{
        Product prod = new Product();
        prod.setProductName("Test");
        prod.setProductPrice(BigDecimal.valueOf(100));
        prod.setProductStock(1);
        Product savedProd = repository.save(prod);
        Long id = savedProd.getProductID();
        String ip = "127.0.0.1";

        Integer numberOfThreads = 100;
        ExecutorService eService = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(numberOfThreads);
        AtomicInteger successfulPurchases = new AtomicInteger(0);
        AtomicInteger failedPurchases = new AtomicInteger(0);
        for (int i = 0; i < numberOfThreads; i++) {
            eService.execute(() -> {
                try {
                    startLatch.await();
                    service.sellProduct(id, ip, 1);
                    successfulPurchases.incrementAndGet();
                } catch (Exception e) {
                    failedPurchases.incrementAndGet();
                } finally {
                    endLatch.countDown();
                }
            });
        }
        startLatch.countDown();
        endLatch.await();

        Product finalProduct = repository.findById(id).get();
        assertEquals(0, finalProduct.getProductStock(), "The stock must be 0");
        assertEquals(1, successfulPurchases.get());
        assertEquals(99, failedPurchases.get());
    }

}

package ru.shop.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProductTest {
    @Test
    void takesAvailableProduct() {
        Product product = new Product(1L, "Товар", BigDecimal.TEN, 1);

        assertTrue(product.takeOne());
        assertEquals(0, product.getQuantity());
        assertFalse(product.takeOne());
    }

    @Test
    void preventsOversellingDuringConcurrentRequests() throws InterruptedException {
        Product product = new Product(1L, "Товар", BigDecimal.TEN, 5);
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(20);
        AtomicInteger successfulPurchases = new AtomicInteger();

        for (int i = 0; i < 20; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    if (product.takeOne()) {
                        successfulPurchases.incrementAndGet();
                    }
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                } finally {
                    done.countDown();
                }
            });
        }

        start.countDown();
        assertTrue(done.await(5, TimeUnit.SECONDS));
        executor.shutdownNow();

        assertEquals(5, successfulPurchases.get());
        assertEquals(0, product.getQuantity());
    }
}

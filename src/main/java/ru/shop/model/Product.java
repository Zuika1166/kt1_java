package ru.shop.model;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

public final class Product {
    private final long id;
    private final String name;
    private final BigDecimal price;
    private final AtomicInteger quantity;

    public Product(long id, String name, BigDecimal price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = new AtomicInteger(quantity);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public boolean takeOne() {
        int current;
        do {
            current = quantity.get();
            if (current == 0) {
                return false;
            }
        } while (!quantity.compareAndSet(current, current - 1));
        return true;
    }
}

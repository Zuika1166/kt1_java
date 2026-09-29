package ru.shop.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import ru.shop.model.Product;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@WebListener
public class CatalogContextListener implements ServletContextListener {
    public static final String CATALOG_ATTRIBUTE = "catalog";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ConcurrentMap<Long, Product> catalog = new ConcurrentHashMap<>();
        catalog.put(1L, new Product(1L, "Ноутбук", new BigDecimal("89990.00"), 4));
        catalog.put(2L, new Product(2L, "Смартфон", new BigDecimal("54990.00"), 7));
        catalog.put(3L, new Product(3L, "Наушники", new BigDecimal("12990.00"), 10));
        catalog.put(4L, new Product(4L, "Клавиатура", new BigDecimal("7990.00"), 6));
        catalog.put(5L, new Product(5L, "Монитор", new BigDecimal("32990.00"), 3));
        event.getServletContext().setAttribute(CATALOG_ATTRIBUTE, catalog);
    }
}

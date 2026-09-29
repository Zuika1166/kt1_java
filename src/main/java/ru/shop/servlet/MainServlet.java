package ru.shop.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import ru.shop.listener.CatalogContextListener;
import ru.shop.model.Product;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@WebServlet("/")
public class MainServlet extends HttpServlet {
    public static final String CART_ATTRIBUTE = "cart";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ConcurrentMap<Long, Product> catalog = getCatalog();
        List<Product> products = catalog.values().stream()
                .sorted(Comparator.comparingLong(Product::getId))
                .toList();
        ConcurrentMap<Long, Integer> cart = getCart(request.getSession());
        List<CartItem> cartItems = cart.entrySet().stream()
                .map(entry -> new CartItem(catalog.get(entry.getKey()), entry.getValue()))
                .filter(item -> item.getProduct() != null)
                .sorted(Comparator.comparingLong(item -> item.getProduct().getId()))
                .toList();
        request.setAttribute("products", products);
        request.setAttribute("cartItems", cartItems);
        request.setAttribute("cartTotal", cartItems.stream()
                .map(CartItem::getTotal)
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            long productId = Long.parseLong(request.getParameter("productId"));
            Product product = getCatalog().get(productId);
            if (product == null) {
                request.getSession().setAttribute("error", "Товар не найден");
            } else if (!product.takeOne()) {
                request.getSession().setAttribute("error", "Товар закончился на складе");
            } else {
                getCart(request.getSession()).merge(productId, 1, Integer::sum);
                request.getSession().setAttribute("message", "Товар добавлен в корзину");
            }
        } catch (NumberFormatException exception) {
            request.getSession().setAttribute("error", "Некорректный идентификатор товара");
        }
        response.sendRedirect(request.getContextPath() + "/");
    }

    @SuppressWarnings("unchecked")
    private ConcurrentMap<Long, Product> getCatalog() {
        return (ConcurrentMap<Long, Product>) getServletContext()
                .getAttribute(CatalogContextListener.CATALOG_ATTRIBUTE);
    }

    @SuppressWarnings("unchecked")
    private ConcurrentMap<Long, Integer> getCart(HttpSession session) {
        Object current = session.getAttribute(CART_ATTRIBUTE);
        if (current instanceof ConcurrentMap<?, ?>) {
            return (ConcurrentMap<Long, Integer>) current;
        }
        synchronized (session) {
            current = session.getAttribute(CART_ATTRIBUTE);
            if (current instanceof ConcurrentMap<?, ?>) {
                return (ConcurrentMap<Long, Integer>) current;
            }
            ConcurrentMap<Long, Integer> cart = new ConcurrentHashMap<>();
            session.setAttribute(CART_ATTRIBUTE, cart);
            return cart;
        }
    }

    public static final class CartItem {
        private final Product product;
        private final int quantity;

        public CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }

        public Product getProduct() {
            return product;
        }

        public int getQuantity() {
            return quantity;
        }

        public java.math.BigDecimal getTotal() {
            return product.getPrice().multiply(java.math.BigDecimal.valueOf(quantity));
        }
    }
}

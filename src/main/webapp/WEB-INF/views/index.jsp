<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Каталог товаров</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<main>
    <h1>Интернет-магазин</h1>

    <c:if test="${not empty sessionScope.message}">
        <div class="notice success">${sessionScope.message}</div>
        <c:remove var="message" scope="session"/>
    </c:if>
    <c:if test="${not empty sessionScope.error}">
        <div class="notice error">${sessionScope.error}</div>
        <c:remove var="error" scope="session"/>
    </c:if>

    <section>
        <h2>Каталог</h2>
        <div class="catalog">
            <c:forEach var="product" items="${products}">
                <article class="card">
                    <h3><c:out value="${product.name}"/></h3>
                    <p>ID: ${product.id}</p>
                    <p class="price">${product.price} ₽</p>
                    <p>На складе: ${product.quantity}</p>
                    <c:choose>
                        <c:when test="${product.quantity > 0}">
                            <form method="post" action="${pageContext.request.contextPath}/">
                                <input type="hidden" name="productId" value="${product.id}">
                                <button type="submit">Добавить в корзину</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <button type="button" disabled>Нет в наличии</button>
                        </c:otherwise>
                    </c:choose>
                </article>
            </c:forEach>
        </div>
    </section>

    <section>
        <h2>Корзина</h2>
        <c:choose>
            <c:when test="${empty cartItems}">
                <p>Корзина пуста</p>
            </c:when>
            <c:otherwise>
                <table>
                    <thead>
                    <tr>
                        <th>Товар</th>
                        <th>Цена</th>
                        <th>Количество</th>
                        <th>Сумма</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="item" items="${cartItems}">
                        <tr>
                            <td><c:out value="${item.product.name}"/></td>
                            <td>${item.product.price} ₽</td>
                            <td>${item.quantity}</td>
                            <td>${item.total} ₽</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                    <tfoot>
                    <tr>
                        <td colspan="3">Итого</td>
                        <td>${cartTotal} ₽</td>
                    </tr>
                    </tfoot>
                </table>
            </c:otherwise>
        </c:choose>
    </section>
</main>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carrello - Readify</title>

    <link rel="stylesheet" href="<c:url value='/css/main.css' />">
    <link rel="stylesheet" href="<c:url value='/css/style.css' />">
    <link rel="stylesheet" href="<c:url value='/css/cart.css' />">

    <script src="<c:url value='/js/main.js' />" defer></script>
</head>
<body>
<jsp:include page="header.jsp"/>

<div class="cart-container ${empty carrello.articoli ? 'is-empty' : ''}">
    <div class="cart-header">
        <h1>Il tuo carrello</h1>
        <p>Rivedi gli articoli selezionati e procedi al checkout.</p>
    </div>

    <c:if test="${not empty successMessage}">
        <div class="alert alert-success show">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger show">${errorMessage}</div>
    </c:if>

    <c:choose>
        <c:when test="${empty carrello.articoli}">
            <div class="cart-empty">
                <h2>Il tuo carrello &egrave; vuoto</h2>
                <p>Non hai ancora aggiunto nessun libro al carrello.</p>
                <a href="${pageContext.request.contextPath}/libri" class="btn btn-primary">Esplora il catalogo</a>
            </div>
        </c:when>
        <c:otherwise>

            <c:if test="${empty sessionScope.utente}">
                <div class="alert alert-warning show">
                    Per salvare il carrello e procedere all'acquisto,
                    <a href="${pageContext.request.contextPath}/login">accedi</a> o
                    <a href="${pageContext.request.contextPath}/registrazione">registrati</a>.
                </div>
            </c:if>

            <div class="cart-grid">
                <div class="cart-items">
                    <c:forEach items="${carrello.articoli}" var="articolo">
                        <div class="cart-item" data-product-id="${articolo.libro.idLibro}">
                            <img
                                    src="${pageContext.request.contextPath}/img/libri/copertine/${not empty articolo.libro.copertina ? articolo.libro.copertina : 'default.jpg'}"
                                    alt="${articolo.libro.titolo}"
                            >

                            <div class="cart-item-details">
                                <div class="cart-item-top">
                                    <div>
                                        <div class="cart-item-title">${articolo.libro.titolo}</div>
                                        <div class="cart-item-author">${articolo.libro.autore}</div>
                                        <div class="cart-item-meta">Disponibilita: ${articolo.libro.disponibilita}
                                            copie
                                        </div>
                                    </div>
                                </div>

                                <div class="cart-item-controls">
                                    <div class="quantity-controls">
                                        <button
                                                type="button"
                                                class="quantity-btn minus"
                                                data-product-id="${articolo.libro.idLibro}"
                                            ${articolo.quantita <= 1 ? 'disabled' : ''}>
                                            -
                                        </button>

                                        <input
                                                type="number"
                                                class="quantity-input"
                                                data-product-id="${articolo.libro.idLibro}"
                                                value="${articolo.quantita}"
                                                min="1"
                                                max="${articolo.libro.disponibilita}"
                                        >

                                        <button
                                                type="button"
                                                class="quantity-btn plus"
                                                data-product-id="${articolo.libro.idLibro}"
                                            ${articolo.quantita >= articolo.libro.disponibilita ? 'disabled' : ''}>
                                            +
                                        </button>
                                    </div>

                                    <div class="cart-item-pricebox">
                                        <span>Prezzo</span>
                                        <strong class="price-value" id="totale-${articolo.libro.idLibro}">
                                            <fmt:formatNumber value="${articolo.totale}" type="currency"
                                                              currencySymbol="&#8364;" minFractionDigits="2"
                                                              maxFractionDigits="2"/>
                                        </strong>
                                    </div>
                                </div>
                            </div>

                            <button
                                    type="button"
                                    class="remove-btn remove-item"
                                    data-product-id="${articolo.libro.idLibro}"
                                    aria-label="Rimuovi">
                                &times;
                            </button>
                        </div>
                    </c:forEach>
                </div>

                <div class="cart-summary">
                    <div class="summary-card">
                        <h2>Riepilogo</h2>

                        <div class="summary-row">
                            <span>Totale articoli</span>
                            <strong id="totale-articoli">${carrello.totaleArticoli}</strong>
                        </div>

                        <div class="summary-row">
                            <span>Totale</span>
                            <strong id="totale-prezzo">
                                <fmt:formatNumber value="${carrello.totale}" type="currency" currencySymbol="&#8364;"
                                                  minFractionDigits="2" maxFractionDigits="2"/>
                            </strong>
                        </div>

                        <div class="summary-actions">
                            <a href="${pageContext.request.contextPath}/libri" class="btn btn-outline-secondary">Continua
                                lo shopping</a>

                            <c:choose>
                                <c:when test="${not empty sessionScope.utente}">
                                    <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary">Procedi
                                        all'acquisto</a>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Accedi
                                        per acquistare</a>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="footer.jsp"/>

<script>
    const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="<c:url value='/js/carrello.js' />" defer></script>

</body>
</html>




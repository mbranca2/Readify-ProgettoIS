<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carrello - Librorama</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css' />">
    <link rel="stylesheet" href="<c:url value='/css/cart.css' />">
</head>
<body>
<jsp:include page="header.jsp" />

<c:if test="${not empty successMessage}">
    <div class="alert alert-success">${successMessage}</div>
</c:if>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div class="container mt-4">
    <h2>Il tuo carrello</h2>

    <c:choose>
        <c:when test="${empty carrello.articoli}">
            <div class="alert alert-info">
                Il tuo carrello è vuoto. <a href="${pageContext.request.contextPath}/">Torna allo shopping</a>
            </div>
        </c:when>
        <c:otherwise>
            <c:if test="${empty sessionScope.utente}">
                <div class="alert alert-warning">
                    <i class="fas fa-info-circle"></i>
                    Per salvare il carrello e procedere all'acquisto,
                    <a href="${pageContext.request.contextPath}/login">accedi</a> o
                    <a href="${pageContext.request.contextPath}/registrazione">registrati</a>.
                </div>
            </c:if>

            <div class="table-responsive">
                <table class="table">
                    <thead>
                    <tr>
                        <th>Prodotto</th>
                        <th class="text-center">Prezzo unitario</th>
                        <th class="text-center">Quantità</th>
                        <th class="text-center">Totale</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${carrello.articoli}" var="articolo">
                        <tr data-product-id="${articolo.libro.idLibro}">
                            <td>
                                <div class="d-flex align-items-center">
                                    <img src="${pageContext.request.contextPath}/img/libri/copertine/${not empty articolo.libro.copertina ? articolo.libro.copertina : 'default.jpg'}"
                                         alt="${articolo.libro.titolo}" class="img-thumbnail me-3" style="max-width: 80px;">
                                    <div>
                                        <h5 class="mb-1">${articolo.libro.titolo}</h5>
                                        <p class="text-muted mb-0">${articolo.libro.autore}</p>
                                    </div>
                                </div>
                            </td>
                            <td class="align-middle text-center">
                                <fmt:formatNumber value="${articolo.libro.prezzo}" type="currency" currencySymbol="€" minFractionDigits="2" maxFractionDigits="2"/>
                            </td>
                            <td class="align-middle text-center">
                                <div class="d-flex justify-content-center">
                                    <form action="${pageContext.request.contextPath}/carrello" method="post" class="d-inline">
                                        <input type="hidden" name="idLibro" value="${articolo.libro.idLibro}">
                                        <input type="hidden" name="quantita" value="${articolo.quantita - 1}">
                                        <input type="hidden" name="azione" value="aggiorna">
                                        <button type="submit" class="btn btn-sm btn-outline-secondary" ${articolo.quantita <= 1 ? 'disabled' : ''}>-</button>
                                    </form>
                                    <span class="mx-2">${articolo.quantita}</span>
                                    <form action="${pageContext.request.contextPath}/carrello" method="post" class="d-inline">
                                        <input type="hidden" name="idLibro" value="${articolo.libro.idLibro}">
                                        <input type="hidden" name="quantita" value="${articolo.quantita + 1}">
                                        <input type="hidden" name="azione" value="aggiorna">
                                        <button type="submit" class="btn btn-sm btn-outline-secondary" ${articolo.quantita >= articolo.libro.disponibilita ? 'disabled' : ''}>+</button>
                                    </form>
                                </div>
                            </td>
                            <td class="align-middle text-center">
                                <fmt:formatNumber value="${articolo.totale}" type="currency" currencySymbol="€" minFractionDigits="2" maxFractionDigits="2"/>
                            </td>
                            <td class="align-middle text-center">
                                <form action="${pageContext.request.contextPath}/carrello" method="post" class="d-inline">
                                    <input type="hidden" name="idLibro" value="${articolo.libro.idLibro}">
                                    <input type="hidden" name="azione" value="rimuovi">
                                    <button type="submit" class="btn btn-link text-danger p-0">
                                        <i class="fas fa-trash-alt"></i>Rimuovi
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                    <tfoot>
                    <tr>
                        <td colspan="3" class="text-end fw-bold">Totale articoli:</td>
                        <td class="text-center fw-bold">${carrello.totaleArticoli}</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td colspan="3" class="text-end fw-bold">Totale:</td>
                        <td class="text-center fw-bold">
                            <fmt:formatNumber value="${carrello.totale}" type="currency" currencySymbol="€" minFractionDigits="2" maxFractionDigits="2"/>
                        </td>
                        <td></td>
                    </tr>
                    </tfoot>
                </table>
            </div>

            <div class="d-flex justify-content-between mt-4">
                <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary">
                    <i class="fas fa-arrow-left me-2"></i>Continua lo shopping
                </a>
                <c:choose>
                    <c:when test="${not empty sessionScope.utente}">
                        <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary">
                            Procedi all'acquisto <i class="fas fa-arrow-right ms-2"></i>
                        </a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">
                            Accedi per acquistare <i class="fas fa-sign-in-alt ms-2"></i>
                        </a>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="footer.jsp" />

<script>
    const contextPath = '${pageContext.request.contextPath}';
</script>
<script src="<c:url value='/js/carrello.js' />"></script>

</body>
</html>
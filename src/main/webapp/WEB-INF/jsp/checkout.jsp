<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Completa il tuo acquisto in modo sicuro su Readify">
    <title>Checkout - Readify</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/checkout.css'/>">
</head>
<body>
<jsp:include page="header.jsp" />

<div class="checkout-container">
    <div class="checkout-header">
        <h2 class="checkout-title">Conferma Ordine</h2>
    </div>

        <c:if test="${not empty errore}">
            <div class="alert alert-warning alert-dynamic" role="alert">
                    ${errore}
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty sessionScope.carrello or sessionScope.carrello.vuoto}">
                <div class="alert alert-warning">
                    Il tuo carrello è vuoto.
                </div>
                <a href="${pageContext.request.contextPath}/libri" class="btn btn-primary">
                    Torna al catalogo
                </a>
            </c:when>

            <c:otherwise>
                <h3>Riepilogo del tuo ordine:</h3>

                <div class="checkout-items">
                    <c:forEach items="${sessionScope.carrello.articoli}" var="articolo">
                        <div class="checkout-item">
                            <div class="checkout-item-details">
                                <span class="checkout-item-title">${articolo.libro.titolo}</span>
                                <div class="checkout-item-meta">
                                    <span>Quantità: ${articolo.quantita}</span>
                                    <span class="checkout-item-sep">|</span>
                                    <span>
                                        Prezzo unitario:
                                        <fmt:formatNumber value="${articolo.libro.prezzo}"
                                                          type="currency"
                                                          currencyCode="EUR"
                                                          maxFractionDigits="2"
                                                          minFractionDigits="2"
                                                          groupingUsed="true"/>
                                    </span>
                                </div>
                            </div>
                            <div class="checkout-item-price">
                                <span class="checkout-item-price-label">Totale riga</span>
                                <c:set var="prezzo" value="${articolo.libro.prezzo}" />
                                <c:set var="quantita" value="${articolo.quantita}" />
                                <fmt:formatNumber value="${prezzo * quantita}"
                                                  type="currency"
                                                  currencyCode="EUR"
                                                  maxFractionDigits="2"
                                                  minFractionDigits="2"
                                                  groupingUsed="true"/>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div class="order-summary">
                    <h4 class="summary-title">Riepilogo</h4>
                    <div class="summary-row">
                        <span class="summary-label">Totale articoli</span>
                        <span class="summary-value">${sessionScope.carrello.totaleArticoli}</span>
                    </div>
                    <div class="summary-row summary-total">
                        <span class="summary-label">Totale ordine</span>
                        <span class="summary-value">
                            <fmt:formatNumber value="${sessionScope.carrello.totale}"
                                              type="currency" currencyCode="EUR" maxFractionDigits="2"/>
                        </span>
                    </div>

                    <c:choose>
                        <c:when test="${empty indirizzi}">
                            <div class="alert alert-warning" style="margin-top: 12px;">
                                Non hai ancora inserito un indirizzo di spedizione.
                            </div>
                            <a class="btn btn-primary" href="${pageContext.request.contextPath}/gestione-indirizzo" style="margin-top: 10px;">
                                Aggiungi un indirizzo
                            </a>
                        </c:when>

                        <c:otherwise>
                            <form method="post" action="${pageContext.request.contextPath}/checkout">
                                <div class="form-group">
                                    <label for="indirizzoSpedizione" class="form-label">Indirizzo di spedizione</label>
                                    <select class="form-select" id="indirizzoSpedizione" name="indirizzoSpedizione" required>
                                        <option value="" disabled selected>Seleziona un indirizzo</option>
                                        <c:forEach items="${indirizzi}" var="ind">
                                            <option value="${ind.idIndirizzo}">
                                                    ${ind.via}, ${ind.cap} ${ind.citta} (${ind.provincia}) - ${ind.paese}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <button type="submit" class="btn btn-success" id="submitBtn">
                                    <span class="btn-text">Conferma e paga</span>
                                </button>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:otherwise>
        </c:choose>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const form = document.querySelector('form');
        if (form) {
            form.addEventListener('submit', function(e) {
                const indirizzo = document.getElementById('indirizzoSpedizione');
                const submitBtn = form.querySelector('button[type="submit"]');

                if (!indirizzo || !indirizzo.value) {
                    e.preventDefault();
                    showAlert('Per favore, seleziona un indirizzo di spedizione', 'error');
                    return false;
                }

                if (submitBtn) {
                    submitBtn.disabled = true;
                    submitBtn.innerHTML = '<span class="loading"></span> Elaborazione...';
                }

                return true;
            });
        }

        function showAlert(message, type = 'info') {
            const existingAlert = document.querySelector('.alert-dynamic');
            if (existingAlert) existingAlert.remove();

            const alertDiv = document.createElement('div');
            alertDiv.className = `alert alert-${type} alert-dynamic`;
            alertDiv.role = 'alert';
            alertDiv.textContent = message;

            const header = document.querySelector('.checkout-header');
            if (header && header.parentNode) {
                header.parentNode.insertBefore(alertDiv, header.nextSibling);

                setTimeout(() => {
                    alertDiv.style.opacity = '0';
                    setTimeout(() => alertDiv.remove(), 300);
                }, 5000);
            }
        }
    });
</script>

</body>
</html>




<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <title>Conferma Ordine - Readify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="header.jsp" />

<div class="container">
    <div class="order-confirmation">
        <h1>Ordine Confermato!</h1>
        <div class="confirmation-message">
            <i class="fas fa-check-circle"></i>
            <p>Grazie per il tuo acquisto! Il tuo ordine e' stato ricevuto con successo.</p>
            <p>Numero ordine: <strong>#${not empty idOrdine ? idOrdine : param.id}</strong></p>
        </div>

        <c:if test="${not empty ordine}">
            <div class="order-summary">
                <div class="summary-block">
                    <div class="summary-title">Riepilogo articoli</div>
                    <ul class="summary-list">
                        <c:forEach items="${ordine.dettagli}" var="det">
                            <li>
                                <span class="summary-item-title">${det.titoloLibro}</span>
                                <span class="summary-item-qty">x${det.quantita}</span>
                                <span class="summary-item-total">
                                    EUR <fmt:formatNumber value="${det.subTotale}" minFractionDigits="2" maxFractionDigits="2"/>
                                </span>
                            </li>
                        </c:forEach>
                    </ul>
                    <div class="summary-total">
                        Totale: EUR <fmt:formatNumber value="${ordine.totale}" minFractionDigits="2" maxFractionDigits="2"/>
                    </div>
                </div>

                <c:if test="${not empty indirizzoOrdine}">
                    <div class="summary-block">
                        <div class="summary-title">Indirizzo di spedizione</div>
                        <div class="summary-address">
                            ${indirizzoOrdine.via}, ${indirizzoOrdine.cap} ${indirizzoOrdine.citta}
                            (${indirizzoOrdine.provincia}) - ${indirizzoOrdine.paese}
                        </div>
                    </div>
                </c:if>
            </div>
        </c:if>

        <div class="order-actions">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Torna alla Home</a>
            <a href="${pageContext.request.contextPath}/profilo?tab=orders" class="btn btn-outline">I Miei Ordini</a>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />
</body>
</html>

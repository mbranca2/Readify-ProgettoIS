<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dettaglio Ordine - Readify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/order-detail.css">
</head>
<body>
<jsp:include page="header.jsp" />

<main class="order-detail-page">
    <div class="order-detail-container">
        <div class="order-detail-header">
            <div>
                <h1 class="order-detail-title">Ordine #${ordine.idOrdine}</h1>
                <p class="order-detail-subtitle">
                    <c:choose>
                        <c:when test="${not empty ordine.dataOrdine}">
                            <fmt:formatDate value="${ordine.dataOrdine}" pattern="dd/MM/yyyy" />
                        </c:when>
                        <c:otherwise>Data non disponibile</c:otherwise>
                    </c:choose>
                </p>
            </div>
            <a href="${pageContext.request.contextPath}/profilo?tab=orders" class="btn btn-outline">
                Torna ai miei ordini
            </a>
        </div>

        <div class="order-detail-grid">
            <section class="order-detail-card">
                <h2 class="panel-title">Riepilogo</h2>
                <div class="order-detail-row">
                    <span>Stato</span>
                    <span class="order-status">${ordine.stato}</span>
                </div>
                <div class="order-detail-row">
                    <span>Totale</span>
                    <span>EUR <fmt:formatNumber value="${ordine.totale}" minFractionDigits="2" maxFractionDigits="2"/></span>
                </div>
                <div class="order-detail-row">
                    <span>Righe ordine</span>
                    <span>${fn:length(ordine.dettagli)}</span>
                </div>
            </section>

            <section class="order-detail-card">
                <h2 class="panel-title">Spedizione</h2>
                <c:choose>
                    <c:when test="${not empty indirizzoOrdine}">
                        <div class="address-block">
                            ${indirizzoOrdine.via}<br>
                            ${indirizzoOrdine.cap} ${indirizzoOrdine.citta} (${indirizzoOrdine.provincia})<br>
                            ${indirizzoOrdine.paese}
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="address-block muted">Indirizzo non disponibile.</div>
                    </c:otherwise>
                </c:choose>
            </section>
        </div>

        <section class="order-detail-card">
            <h2 class="panel-title">Articoli</h2>
            <c:choose>
                <c:when test="${empty ordine.dettagli}">
                    <p class="muted">Nessun articolo presente.</p>
                </c:when>
                <c:otherwise>
                    <table class="order-items-table">
                        <thead>
                        <tr>
                            <th>Articolo</th>
                            <th class="text-right">Qta</th>
                            <th class="text-right">Prezzo</th>
                            <th class="text-right">Subtotale</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${ordine.dettagli}" var="dettaglio">
                            <c:set var="coverPath" value="${dettaglio.immagineCopertina}" />
                            <c:if test="${empty coverPath}">
                                <c:set var="coverPath" value="img/libri/copertine/default.jpg" />
                            </c:if>
                            <tr>
                                <td>
                                    <div class="item-row">
                                        <img class="item-cover" src="${pageContext.request.contextPath}/${coverPath}"
                                             alt="${dettaglio.titoloLibro}">
                                        <div>
                                            <div class="item-title">${dettaglio.titoloLibro}</div>
                                            <div class="item-meta">${dettaglio.autoreLibro}</div>
                                        </div>
                                    </div>
                                </td>
                                <td class="text-right">${dettaglio.quantita}</td>
                                <td class="text-right">
                                    EUR <fmt:formatNumber value="${dettaglio.prezzoUnitario}" minFractionDigits="2" maxFractionDigits="2"/>
                                </td>
                                <td class="text-right">
                                    EUR <fmt:formatNumber value="${dettaglio.subTotale}" minFractionDigits="2" maxFractionDigits="2"/>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>

<jsp:include page="footer.jsp" />
</body>
</html>

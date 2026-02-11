<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dettaglio Ordine - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        .icon {
            display: inline-block;
            width: 1em;
            height: 1em;
            margin-right: .5em;
            vertical-align: middle;
        }

        .icon-orders::before {
            content: '🧾';
        }

        .icon-back::before {
            content: '←';
        }

        .grid {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 16px;
        }

        .card {
            border: 1px solid #eee;
            border-radius: 14px;
            padding: 16px;
            background: #fff;
        }

        .muted {
            color: #666;
        }

        .badge {
            padding: 4px 8px;
            border-radius: 10px;
            background: #eee;
            font-size: 0.9em;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        th, td {
            padding: 10px;
            border-bottom: 1px solid #ddd;
            text-align: left;
            vertical-align: top;
        }

        th {
            background: #f7f7f7;
        }

        .book {
            display: flex;
            gap: 12px;
            align-items: flex-start;
        }

        .cover {
            width: 52px;
            height: 78px;
            object-fit: cover;
            border-radius: 8px;
            border: 1px solid #ddd;
            background: #fafafa;
        }

        .btn-link {
            text-decoration: none;
            padding: 6px 10px;
            border: 1px solid #ddd;
            border-radius: 8px;
            display: inline-block;
        }

        .form-row {
            display: flex;
            gap: 10px;
            align-items: center;
            margin-top: 10px;
        }

        select {
            padding: 8px;
            border-radius: 10px;
            border: 1px solid #ddd;
        }

        button {
            padding: 8px 12px;
            border-radius: 10px;
            border: 1px solid #ddd;
            background: #f7f7f7;
            cursor: pointer;
        }

        @media (max-width: 900px) {
            .grid {
                grid-template-columns: 1fr;
            }
        }
    </style>
</head>
<body>
<jsp:include page="../header.jsp"/>

<div class="container" style="margin-top:20px;">
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
        <h1><span class="icon icon-orders"></span>Dettaglio Ordine #${ordine.idOrdine}</h1>
        <a class="btn-link" href="${pageContext.request.contextPath}/admin/ordini">
            <span class="icon icon-back"></span> Torna alla lista
        </a>
    </div>

    <c:if test="${param.updated == '1'}">
        <div class="card" style="border-color:#cdeccd; background:#f3fff3; margin-bottom:16px;">
            Stato aggiornato con successo.
        </div>
    </c:if>

    <c:if test="${not empty param.error}">
        <div class="card" style="border-color:#ffd4d4; background:#fff3f3; margin-bottom:16px;">
            Operazione non riuscita (${param.error}).
        </div>
    </c:if>

    <div class="grid">
        <div class="card">
            <h3>Informazioni Ordine</h3>
            <p><strong>ID Ordine:</strong> #${ordine.idOrdine}</p>
            <p><strong>Data:</strong>
                <c:choose>
                    <c:when test="${not empty ordine.dataOrdine}">
                        <fmt:formatDate value="${ordine.dataOrdine}" pattern="dd/MM/yyyy"/>
                    </c:when>
                    <c:otherwise>-</c:otherwise>
                </c:choose>
            </p>
            <p><strong>Stato attuale:</strong> <span class="badge">${ordine.stato}</span></p>
            <p><strong>Totale:</strong>
                € <fmt:formatNumber value="${ordine.totale}" minFractionDigits="2" maxFractionDigits="2"/>
            </p>

            <hr style="border:none;border-top:1px solid #eee; margin:12px 0;">

            <h4 style="margin:0 0 8px 0;">Aggiorna stato</h4>
            <form method="post" action="${pageContext.request.contextPath}/admin/ordine/stato">
                <input type="hidden" name="idOrdine" value="${ordine.idOrdine}">
                <div class="form-row">
                    <select name="nuovoStato" required>
                        <option value="IN_ATTESA" ${ordine.stato == 'IN_ATTESA' ? 'selected' : ''}>IN_ATTESA</option>
                        <option value="PAGATO" ${ordine.stato == 'PAGATO' ? 'selected' : ''}>PAGATO</option>
                        <option value="IN_ELABORAZIONE" ${ordine.stato == 'IN_ELABORAZIONE' ? 'selected' : ''}>
                            IN_ELABORAZIONE
                        </option>
                        <option value="SPEDITO" ${ordine.stato == 'SPEDITO' ? 'selected' : ''}>SPEDITO</option>
                        <option value="CONSEGNATO" ${ordine.stato == 'CONSEGNATO' ? 'selected' : ''}>CONSEGNATO</option>
                        <option value="ANNULLATO" ${ordine.stato == 'ANNULLATO' ? 'selected' : ''}>ANNULLATO</option>
                    </select>
                    <button type="submit">Salva</button>
                </div>
            </form>
        </div>

        <div class="card">
            <h3>Utente e Spedizione</h3>

            <c:if test="${not empty utenteOrdine}">
                <p><strong>ID Utente:</strong> ${utenteOrdine.idUtente}</p>
                <p><strong>Nome:</strong> ${utenteOrdine.nome} ${utenteOrdine.cognome}</p>
                <p><strong>Email:</strong> ${utenteOrdine.email}</p>
                <c:if test="${not empty utenteOrdine.telefono}">
                    <p><strong>Telefono:</strong> ${utenteOrdine.telefono}</p>
                </c:if>
            </c:if>

            <hr style="border:none;border-top:1px solid #eee; margin:12px 0;">

            <c:choose>
                <c:when test="${not empty indirizzoOrdine}">
                    <p class="muted" style="margin:0 0 6px 0;">Indirizzo di spedizione</p>
                    <p style="margin:0;">
                            ${indirizzoOrdine.via}<br>
                            ${indirizzoOrdine.cap} ${indirizzoOrdine.citta} (${indirizzoOrdine.provincia})<br>
                            ${indirizzoOrdine.paese}
                    </p>
                </c:when>
                <c:otherwise>
                    <p class="muted">Nessun indirizzo associato all’ordine.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <div class="card" style="margin-top:16px;">
        <h3>Righe Ordine</h3>

        <table>
            <thead>
            <tr>
                <th>Libro</th>
                <th>Q.tà</th>
                <th>Prezzo unitario</th>
                <th>Subtotale</th>
            </tr>
            </thead>
            <tbody>
            <c:choose>
                <c:when test="${empty ordine.dettagli}">
                    <tr>
                        <td colspan="4" class="muted">Nessun dettaglio presente.</td>
                    </tr>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${ordine.dettagli}" var="d">
                        <tr>
                            <td>
                                <div class="book">
                                    <c:choose>
                                        <c:when test="${not empty d.immagineCopertina}">
                                            <img class="cover"
                                                 src="${pageContext.request.contextPath}/${d.immagineCopertina}"
                                                 alt="Copertina">
                                        </c:when>
                                        <c:otherwise>
                                            <div class="cover"></div>
                                        </c:otherwise>
                                    </c:choose>

                                    <div>
                                        <div><strong>${d.titoloLibro}</strong></div>
                                        <div class="muted">${d.autoreLibro}</div>
                                        <c:if test="${not empty d.isbnLibro}">
                                            <div class="muted">ISBN: ${d.isbnLibro}</div>
                                        </c:if>
                                        <div class="muted">ID libro: ${d.idLibro}</div>
                                    </div>
                                </div>
                            </td>
                            <td>${d.quantita}</td>
                            <td>€ <fmt:formatNumber value="${d.prezzoUnitario}" minFractionDigits="2"
                                                    maxFractionDigits="2"/></td>
                            <td>€ <fmt:formatNumber value="${d.subTotale}" minFractionDigits="2"
                                                    maxFractionDigits="2"/></td>
                        </tr>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="../footer.jsp"/>
</body>
</html>

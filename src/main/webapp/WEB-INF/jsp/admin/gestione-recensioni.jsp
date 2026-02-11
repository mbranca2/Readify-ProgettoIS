<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestione Recensioni - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <style>
        .table-container {
            overflow-x: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
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

        .btn {
            padding: 6px 10px;
            border-radius: 10px;
            border: 1px solid #ddd;
            background: #f7f7f7;
            cursor: pointer;
        }

        .btn-danger {
            background: #fff3f3;
            border-color: #ffd4d4;
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

        .comment {
            max-width: 520px;
            white-space: pre-wrap;
        }
    </style>
</head>
<body>
<jsp:include page="../header.jsp"/>

<div class="container" style="margin-top:20px;">
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:16px;">
        <h1>Gestione Recensioni</h1>
        <a class="muted" href="${pageContext.request.contextPath}/admin/dashboard">Torna alla dashboard</a>
    </div>

    <c:if test="${param.deleted == '1'}">
        <div class="card" style="border-color:#cdeccd; background:#f3fff3; margin-bottom:16px; padding:12px;">
            Recensione eliminata.
        </div>
    </c:if>

    <c:if test="${not empty param.error}">
        <div class="card" style="border-color:#ffd4d4; background:#fff3f3; margin-bottom:16px; padding:12px;">
            Operazione non riuscita (${param.error}).
        </div>
    </c:if>

    <div class="card" style="padding:12px;">
        <div class="table-container">
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Libro</th>
                    <th>Utente</th>
                    <th>Voto</th>
                    <th>Data</th>
                    <th>Commento</th>
                    <th>Azioni</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${empty reviews}">
                        <tr>
                            <td colspan="7" class="muted">Nessuna recensione presente.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${reviews}" var="r">
                            <tr>
                                <td>#${r.idRecensione}</td>
                                <td>
                                    <div><strong>${r.titoloLibro}</strong></div>
                                    <div class="muted">ID libro: ${r.idLibro}</div>
                                </td>
                                <td>
                                    <div>${r.nomeUtente}</div>
                                    <div class="muted">${r.emailUtente}</div>
                                    <div class="muted">ID utente: ${r.idUtente}</div>
                                </td>
                                <td><span class="badge">${r.voto}/5</span></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${not empty r.dataValutazione}">
                                            <fmt:formatDate value="${r.dataValutazione}" pattern="dd/MM/yyyy"/>
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="comment">${r.commento}</td>
                                <td>
                                    <form method="post"
                                          action="${pageContext.request.contextPath}/admin/recensioni/elimina"
                                          onsubmit="return confirm('Eliminare definitivamente la recensione #${r.idRecensione}?');">
                                        <input type="hidden" name="idRecensione" value="${r.idRecensione}">
                                        <button class="btn btn-danger" type="submit">Elimina</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="../footer.jsp"/>
</body>
</html>

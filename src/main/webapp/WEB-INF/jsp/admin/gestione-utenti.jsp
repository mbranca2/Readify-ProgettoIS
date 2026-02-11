<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestione Utenti - Readify</title>
    <jsp:include page="../header.jsp"/>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/user-management.css">
</head>
<body>
<!--devug -->
<c:if test="${empty listaUtenti}">
    <div class="alert alert-warning">
        La lista degli utenti è vuota o non è stata caricata correttamente.
    </div>
</c:if>
<div class="user-management-container">
    <div class="page-header">
        <h1 class="page-title">Gestione Utenti</h1>
        <a href="${pageContext.request.contextPath}/admin/utenti?azione=nuovo" class="btn btn-primary">
            <span class="icon icon-user-plus"></span>Nuovo Utente
        </a>
    </div>

    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/admin/utenti" class="btn btn-secondary">
            <span class="icon icon-refresh"></span> Aggiorna lista
        </a>
    </div>

    <!-- Tabella utenti -->
    <div class="card">
        <div class="table-responsive">
            <table class="table">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Nome e Cognome</th>
                    <th>Email</th>
                    <th>Registrato il</th>
                    <th>Ruolo</th>
                    <th style="text-align: right;">Azioni</th>
                </tr>
                </thead>
                <tbody>
                <c:choose>
                    <c:when test="${not empty listaUtenti}">
                        <c:forEach items="${listaUtenti}" var="utente">
                            <tr>
                                <td>#${utente.idUtente}</td>
                                <td>${utente.nome} ${utente.cognome}</td>
                                <td>${utente.email}</td>
                                <td><fmt:formatDate value="${utente.dataRegistrazione}"
                                                    pattern="dd/MM/yyyy HH:mm"/></td>
                                <td>
                                        <span class="badge ${utente.ruolo == 'admin' ? 'badge-primary' : 'badge-secondary'}">
                                                ${utente.ruolo == 'admin' ? 'Amministratore' : 'Cliente'}
                                        </span>
                                </td>
                                <td class="actions-cell">
                                    <div class="btn-group">
                                        <a href="${pageContext.request.contextPath}/admin/dettaglio-utenti/${utente.idUtente}"
                                           class="btn btn-sm btn-outline-primary" title="Dettagli">
                                            <span class="icon icon-eye"></span>
                                            <span class="sr-only">Dettagli</span>
                                        </a>
                                        <a href="${pageContext.request.contextPath}/admin/utenti/modifica?id=${utente.idUtente}"
                                           class="btn btn-sm btn-outline-secondary" title="Modifica">
                                            <span class="icon icon-edit"></span>
                                            <span class="sr-only">Modifica</span>
                                        </a>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <tr>
                            <td colspan="6" class="no-results">
                                <div class="icon icon-users"></div>
                                <p>Nessun utente trovato</p>
                            </td>
                        </tr>
                    </c:otherwise>
                </c:choose>
                </tbody>
            </table>
        </div>

        <c:if test="${totalePagine > 1}">
            <div class="pagination-container">
                <ul class="pagination">
                    <li class="page-item ${paginaCorrente == 1 ? 'disabled' : ''}">
                        <a class="page-link" href="?pagina=${paginaCorrente - 1}" aria-label="Precedente">
                            <span class="icon icon-chevron-left" aria-hidden="true"></span>
                            <span class="sr-only">Precedente</span>
                        </a>
                    </li>

                    <c:forEach begin="1" end="${totalePagine}" var="i">
                        <li class="page-item ${i == paginaCorrente ? 'active' : ''}">
                            <a class="page-link" href="?pagina=${i}">${i}</a>
                        </li>
                    </c:forEach>

                    <li class="page-item ${paginaCorrente == totalePagine ? 'disabled' : ''}">
                        <a class="page-link" href="?pagina=${paginaCorrente + 1}" aria-label="Successivo">
                            <span class="icon icon-chevron-right" aria-hidden="true"></span>
                            <span class="sr-only">Successivo</span>
                        </a>
                    </li>
                </ul>
            </div>
        </c:if>
    </div>
</div>

</body>
</html>


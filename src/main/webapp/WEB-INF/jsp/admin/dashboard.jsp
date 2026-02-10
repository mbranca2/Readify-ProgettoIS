<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Amministratore - Readify</title>
    <jsp:include page="../header.jsp" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard-styles.css">
    <style>
        .icon {
            display: inline-block;
            width: 1em;
            height: 1em;
            margin-right: 0;
            vertical-align: middle;
        }
        .icon-users::before { content: "\1F465"; }
        .icon-book::before { content: "\1F4DA"; }
        .icon-orders::before { content: "\1F9FE"; }
        .icon-arrow-right::before { content: "\2192"; }
    </style>
</head>
<body>
<div class="container">
    <div class="dashboard-header">
        <div class="dashboard-title">
            <h1>Dashboard</h1>
            <p class="dashboard-subtitle">Panoramica amministrativa</p>
        </div>
    </div>

    <!-- Azioni / Statistiche -->
    <div class="dashboard-stats">

        <!-- Utenti Registrati -->
        <div class="stat-card">
            <div class="stat-icon users">
                <span class="icon icon-users"></span>
            </div>
            <div class="stat-content">
                <div class="stat-value">
                    <c:choose>
                        <c:when test="${not empty stats && not empty stats.numeroUtenti}">${stats.numeroUtenti}</c:when>
                        <c:otherwise>&mdash;</c:otherwise>
                    </c:choose>
                </div>
                <div class="stat-label">Utenti Registrati</div>
                <a href="${pageContext.request.contextPath}/admin/utenti" class="stat-link">
                    Visualizza utenti <span class="icon icon-arrow-right"></span>
                </a>
            </div>
        </div>

        <!-- Libri in Catalogo -->
        <div class="stat-card">
            <div class="stat-icon books">
                <span class="icon icon-book"></span>
            </div>
            <div class="stat-content">
                <div class="stat-value">
                    <c:choose>
                        <c:when test="${not empty stats && not empty stats.numeroLibri}">${stats.numeroLibri}</c:when>
                        <c:otherwise>&mdash;</c:otherwise>
                    </c:choose>
                </div>
                <div class="stat-label">Libri in Catalogo</div>
                <a href="${pageContext.request.contextPath}/admin/libri" class="stat-link">
                    Gestisci libri <span class="icon icon-arrow-right"></span>
                </a>
            </div>
        </div>

        <!-- Ordini -->
        <div class="stat-card">
            <div class="stat-icon orders">
                <span class="icon icon-orders"></span>
            </div>
            <div class="stat-content">
                <div class="stat-value">&mdash;</div>
                <div class="stat-label">Ordini Piattaforma</div>
                <a href="${pageContext.request.contextPath}/admin/ordini" class="stat-link">
                    Gestisci ordini <span class="icon icon-arrow-right"></span>
                </a>
            </div>
        </div>

    </div>
</div>
</body>

<jsp:include page="../footer.jsp" />

</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Amministratore - Librorama</title>
    <jsp:include page="../header.jsp" />
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard-styles.css">
    <style>
        .icon {
            display: inline-block;
            width: 1em;
            height: 1em;
            margin-right: 0.5em;
            vertical-align: middle;
        }
        .icon-users::before { content: '👥'; }
        .icon-book::before { content: '📚'; }
        .icon-user-shield::before { content: '🛡️'; }
        .icon-arrow-right::before { content: '→'; }
        .icon-home::before { content: '🏠'; }
    </style>
</head>
<body>
<div class="container">
    <div class="dashboard-header">
        <div class="dashboard-title">
            <h1>Dashboard</h1>
            <nav class="breadcrumb">
                <a href="${pageContext.request.contextPath}" class="breadcrumb-item">
                    <span class="icon icon-home"></span>Home
                </a>
            </nav>
        </div>
        <div class="admin-badge">
            <span class="icon icon-user-shield"></span> Admin Panel
        </div>
    </div>

    <!-- Statistiche -->
    <div class="dashboard-stats">
        <!-- Utenti Registrati -->
        <div class="stat-card">
            <div class="stat-icon users">
                <span class="icon icon-users"></span>
            </div>
            <div class="stat-content">
                <div class="stat-value">${stats.numeroUtenti}</div>
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
                <div class="stat-value">${stats.numeroLibri}</div>
                <div class="stat-label">Libri in Catalogo</div>
                <a href="${pageContext.request.contextPath}/admin/libri" class="stat-link">
                    Gestisci libri <span class="icon icon-arrow-right"></span>
                </a>
            </div>
        </div>
    </div>
</div>
</body>


<jsp:include page="../footer.jsp" />

</html>

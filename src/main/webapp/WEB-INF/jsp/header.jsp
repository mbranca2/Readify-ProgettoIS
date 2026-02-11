<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="<c:url value='/css/main.css' />">
<link rel="stylesheet" href="<c:url value='/css/forms.css' />">

<script src="<c:url value='/js/main.js' />" defer></script>

<header>
    <div class="container">
        <div class="header-content">
            <h1>
                <a href="<c:url value='/home' />" class="logo-link">
                    <img src="<c:url value='/img/logo.png' />" alt="Readify" class="logo-image">
                </a>
            </h1>

            <nav>
                <a href="<c:url value='/home' />" class="nav-link">
                    <i class="fas fa-home"></i> Home
                </a>

                <a href="${pageContext.request.contextPath}/libri" class="nav-link">
                    <i class="fas fa-book"></i> Catalogo
                </a>

                <a href="<c:url value='/carrello' />" class="nav-link cart-link">
                    <i class="fas fa-shopping-cart"></i>
                    <c:if test="${not empty sessionScope.carrello and sessionScope.carrello.totaleArticoli > 0}">
                        <span class="cart-count cart-badge">${sessionScope.carrello.totaleArticoli}</span>
                    </c:if>
                    Carrello
                </a>

                <c:choose>
                    <c:when test="${not empty sessionScope.utente}">
                        <div class="user-menu">
                            <a href="<c:url value='/profilo' />" class="nav-link user-link">
                                <i class="fas fa-user"></i>
                                    ${sessionScope.utente.nome}
                            </a>

                            <div class="user-dropdown">
                                <a href="<c:url value='/profilo' />">Profilo</a>
                                <a href="<c:url value='/profilo?tab=orders' />">I miei ordini</a>

                                <c:if test="${sessionScope.utente.ruolo eq 'admin'}">
                                    <div class="dropdown-divider"></div>
                                    <a href="<c:url value='/admin/dashboard' />" class="admin-link">
                                        <i class="fas fa-cog me-1"></i> Area Amministrativa
                                    </a>
                                </c:if>

                                <div class="dropdown-divider"></div>
                                <a href="<c:url value='/logout' />">Esci</a>
                            </div>
                        </div>
                    </c:when>

                    <c:otherwise>
                        <a href="<c:url value='/login' />" class="nav-link">
                            <i class="fas fa-sign-in-alt"></i> Accedi
                        </a>
                        <a href="<c:url value='/registrazione' />" class="btn btn-primary">Registrati</a>
                    </c:otherwise>
                </c:choose>
            </nav>
        </div>
    </div>
</header>

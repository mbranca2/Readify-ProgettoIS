<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/carrello.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/forms.css">

<script src="${pageContext.request.contextPath}/js/main.js" defer></script>
<script src="${pageContext.request.contextPath}/js/carrello.js" defer></script>
<header>
  <div class="container">
    <div class="header-content">
      <h1><a href="<c:url value='/' />" class="logo-link">Readify</a></h1>
      <nav>
        <a href="<c:url value='/' />" class="nav-link">
          <i class="fas fa-home"></i> Home
        </a>
        <a href="${pageContext.request.contextPath}/libri" class="nav-link">
          <i class="fas fa-book"></i> Catalogo
        </a>
        <a href="<c:url value='/carrello' />" class="nav-link cart-link">
          <i class="fas fa-shopping-cart"></i>
          <c:if test="${not empty sessionScope.carrello and sessionScope.carrello.totaleArticoli > 0}">
            <span class="cart-count">${sessionScope.carrello.totaleArticoli}</span>
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
                <a href="<c:url value='/profilo/ordini' />">I miei ordini</a>
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
  
  <style>
    /* Stili per l'header */
    header {
      background-color: #2c3e50;
      color: white;
      padding: 1rem 0;
      box-shadow: 0 2px 4px rgba(0,0,0,0.1);
    }
    
    .header-content {
      display: flex;
      justify-content: space-between;
      align-items: center;
      flex-wrap: wrap;
    }
    
    .logo-link {
      color: white;
      text-decoration: none;
      font-size: 1.5rem;
      font-weight: bold;
    }
    
    nav {
      display: flex;
      gap: 1.5rem;
      align-items: center;
      flex-wrap: wrap;
    }
    
    .nav-link {
      color: rgba(255, 255, 255, 0.9);
      text-decoration: none;
      display: flex;
      align-items: center;
      gap: 0.5rem;
      transition: color 0.2s;
    }
    
    .nav-link:hover {
      color: white;
    }
    
    .cart-link {
      position: relative;
    }
    
    .cart-count {
      background-color: #e74c3c;
      color: white;
      border-radius: 50%;
      width: 20px;
      height: 20px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 0.7rem;
      position: absolute;
      top: -8px;
      right: -10px;
    }
    
    .user-menu {
      position: relative;
    }
    
    .user-dropdown {
      display: none;
      position: absolute;
      right: 0;
      background: white;
      border-radius: 4px;
      box-shadow: 0 2px 10px rgba(0,0,0,0.1);
      min-width: 200px;
      z-index: 1000;
    }
    
    .user-menu:hover .user-dropdown {
      display: block;
    }
    
    .user-dropdown a {
      display: block;
      padding: 0.5rem 1rem;
      color: #333;
      text-decoration: none;
      transition: background 0.2s;
    }
    
    .user-dropdown a:hover {
      background: #f5f5f5;
    }
    
    .btn {
      padding: 0.5rem 1rem;
      border-radius: 4px;
      text-decoration: none;
      font-weight: 500;
      transition: all 0.2s;
    }
    
    .btn-primary {
      background-color: #3498db;
      color: white;
      border: 1px solid #2980b9;
    }
    
    .btn-primary:hover {
      background-color: #2980b9;
    }
    
    @media (max-width: 768px) {
      .header-content {
        flex-direction: column;
        gap: 1rem;
      }
      
      nav {
        width: 100%;
        justify-content: center;
      }
    }
  </style>
</header>

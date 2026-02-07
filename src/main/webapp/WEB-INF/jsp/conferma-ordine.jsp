<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
            <p>Grazie per il tuo acquisto! Il tuo ordine è stato ricevuto con successo.</p>
            <p>Numero ordine: <strong>#${param.id}</strong></p>
        </div>

        <div class="order-actions">
            <a href="${pageContext.request.contextPath}/" class="btn btn-primary">Torna alla Home</a>
            <a href="${pageContext.request.contextPath}/profilo?tab=orders" class="btn btn-outline">I Miei Ordini</a>
        </div>
    </div>
</div>

<jsp:include page="footer.jsp" />
</body>
</html>

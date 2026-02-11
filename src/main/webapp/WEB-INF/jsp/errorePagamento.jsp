<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Errore nel Pagamento - Readify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/error-pages.css">
</head>
<body class="error-page">
<jsp:include page="header.jsp"/>

<main class="error-main-wrapper">
    <div class="error-container">
        <div class="error-icon" aria-hidden="true">⚠️</div>
        <h1>Si è verificato un errore durante il pagamento</h1>
        <p>Ci scusiamo per l'inconveniente. Il tuo ordine non è stato elaborato correttamente.</p>

        <c:if test="${not empty requestScope.errore}">
            <div class="error-details" role="alert">
                <strong>Dettagli:</strong> ${requestScope.errore}
            </div>
        </c:if>

        <div class="button-group">
            <a href="${pageContext.request.contextPath}/carrello" class="btn btn-primary">
                Torna al carrello
            </a>
            <a href="${pageContext.request.contextPath}/contatti" class="btn btn-secondary">
                Contatta assistenza
            </a>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp"/>
</body>
</html>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Login - Librorama</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
</head>
<body>
<jsp:include page="header.jsp" />

<main class="form-container">
    <h2>Accedi al tuo account</h2>

    <c:if test="${not empty error}">
        <div class="alert alert-error">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post"
          class="form needs-validation" onsubmit="return validateLoginForm()" novalidate>
        <div class="form-group">
            <label for="email">Email:</label>
            <input type="email" id="email" name="email" class="form-control"
                   value="${empty email ? '' : fn:escapeXml(email)}"
                   required>
        </div>
        <div class="form-group">
            <label for="password">Password:</label>
            <input type="password" id="password" name="password" class="form-control"
                   required minlength="8">
        </div>
        <div class="form-group">
            <button type="submit" class="btn btn-primary">Accedi</button>
        </div>
    </form>
    <script src="${pageContext.request.contextPath}/js/login-validation.js"></script>

    <p>Non hai un account? <a href="${pageContext.request.contextPath}/registrazione">Registrati</a></p>
</main>

<jsp:include page="footer.jsp" />
</body>
</html>

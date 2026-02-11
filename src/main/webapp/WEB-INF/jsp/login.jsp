<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>Accedi - Readify</title>

    <link rel="stylesheet" href="<c:url value='/css/main.css' />">
    <link rel="stylesheet" href="<c:url value='/css/forms.css' />">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<main class="auth-page">
    <div class="container auth-container">
        <div class="auth-card">
            <div class="d-flex justify-content-between align-items-center auth-title-row">
                <h2 class="mt-0 mb-0">Accedi</h2>
            </div>

            <c:if test="${not empty requestScope.error}">
                <div class="alert alert-danger">${requestScope.error}</div>
            </c:if>

            <c:if test="${not empty requestScope.success}">
                <div class="alert alert-success">${requestScope.success}</div>
            </c:if>

            <c:if test="${not empty requestScope.errors}">
                <div class="alert alert-danger">
                    <ul class="mt-0 mb-0" style="padding-left: 18px;">
                        <c:forEach var="e" items="${requestScope.errors}">
                            <li>${e}</li>
                        </c:forEach>
                    </ul>
                </div>
            </c:if>

            <form class="auth-form" action="<c:url value='/login' />" method="post">
                <div class="form-group">
                    <label for="email">Email</label>
                    <input id="email" name="email" type="email" class="form-control"
                           value="${param.email}" autocomplete="email" required/>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input id="password" name="password" type="password" class="form-control"
                           autocomplete="current-password" required/>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">Accedi</button>
                    <a class="btn btn-secondary" href="<c:url value='/registrazione' />">Registrati</a>
                </div>

                <div class="mt-4 text-center text-muted auth-home-link">
                    <a href="<c:url value='/home' />">Torna alla home</a>
                </div>
            </form>
        </div>
    </div>
</main>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>

</body>
</html>

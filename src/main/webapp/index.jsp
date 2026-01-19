<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it" data-bs-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home - Librorama</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/homepage.css'/>">
</head>
<body>
    <!-- Header -->
    <%@ include file="/jsp/header.jsp" %>

    <!-- Home Section -->
    <section class="home">
        <div class="container">
            <h1 style="color: white">Benvenuto su Librorama</h1>
            <p>Scopri la nostra vasta selezione di libri e approfitta delle migliori offerte</p>
            <div class="home-buttons">
                <a href="<c:url value='/libri'/>" class="button">
                    Esplora il catalogo
                </a>
            </div>
        </div>
    </section>

    <!-- Sezione Caratteristiche -->
    <section class="features">
        <div class="container">
            <h2 class="section-title">Perché scegliere Librorama?</h2>
            <div class="features-grid">
                <div class="feature">
                    <div class="feature-icon">📚</div>
                    <h3>Vasta selezione</h3>
                    <p>Migliaia di titoli tra cui scegliere, dai classici alle ultime uscite.</p>
                </div>
                <div class="feature">
                    <div class="feature-icon">🚚</div>
                    <h3>Spedizione veloce</h3>
                    <p>Consegna rapida in tutta Italia entro 2-3 giorni lavorativi.</p>
                </div>
                <div class="feature">
                    <div class="feature-icon">🔒</div>
                    <h3>Pagamento sicuro</h3>
                    <p>Transazioni protette e pagamenti sicuri al 100%.</p>
                </div>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <jsp:include page="/jsp/footer.jsp" />
</body>
</html>

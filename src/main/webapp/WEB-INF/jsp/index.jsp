<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it" data-bs-theme="light">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home - Readify</title>

    <link rel="stylesheet" href="<c:url value='/css/main.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/homepage.css'/>">

    <script src="<c:url value='/js/main.js'/>" defer></script>
</head>
<body>
<%@ include file="/WEB-INF/jsp/header.jsp" %>

<section class="home">
    <div class="container">
        <div class="home-content">
            <span class="home-eyebrow">Pronto a scegliere il tuo prossimo libro?</span>
            <h1>Benvenuto su Readify</h1>
            <p>Scopri novità e classici, aggiungi al carrello in un attimo e tieni d'occhio i tuoi ordini quando
                vuoi.</p>
            <div class="home-buttons">
                <a href="<c:url value='/libri'/>" class="button">
                    Esplora il catalogo
                </a>
                <a href="<c:url value='/profilo'/>" class="btn btn-outline">
                    Vai al profilo
                </a>
            </div>
        </div>
    </div>
</section>

<section class="features">
    <div class="container">
        <h2 class="section-title">Perchè scegliere Readify</h2>
        <div class="features-grid">
            <div class="feature">
                <div class="feature-icon">1</div>
                <h3>Selezione che si fa trovare</h3>
                <p>Novità, classici e titoli scelti per farti scoprire qualcosa di buono al volo.</p>
            </div>
            <div class="feature">
                <div class="feature-icon">2</div>
                <h3>Carrello dinamico e veloce</h3>
                <p>Aumenti o riduci la quantità in un clic e vedi subito il totale.</p>
            </div>
            <div class="feature">
                <div class="feature-icon">3</div>
                <h3>Ordini sempre sotto controllo</h3>
                <p>Profilo aggiornato e storico acquisti pronto quando ti serve.</p>
            </div>
        </div>
    </div>
</section>

<jsp:include page="/WEB-INF/jsp/footer.jsp"/>
</body>
</html>

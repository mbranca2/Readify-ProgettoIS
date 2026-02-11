<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Catalogo - Readify</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css' />">
    <link rel="stylesheet" href="<c:url value='/css/catalogo.css' />">
</head>
<body class="page-catalogo-v2">

<jsp:include page="header.jsp"/>

<main class="catalogo-shell">
    <section class="catalogo-topbar">
        <div class="topbar-inner">
            <div class="topbar-left">
                <h1 class="catalogo-title">Catalogo</h1>
                <p class="catalogo-subtitle">Trova il tuo prossimo libro in pochi secondi.</p>
            </div>

            <div class="topbar-actions">
                <div class="searchbox">
                    <span class="search-ico">🔎</span>
                    <input id="searchInput" type="text" placeholder="Cerca per titolo o autore..." autocomplete="off">
                    <button type="button" id="clearSearch" class="icon-btn" aria-label="Pulisci">✕</button>
                </div>

                <button type="button" id="openFilters" class="btn-soft">
                    <span class="btn-ico">⚙️</span>
                    Filtri
                </button>

                <div class="sortbox">
                    <select id="sortSelect" aria-label="Ordinamento">
                        <option value="rel">Rilevanza</option>
                        <option value="priceAsc">Prezzo: crescente</option>
                        <option value="priceDesc">Prezzo: decrescente</option>
                        <option value="titleAsc">Titolo: A-Z</option>
                        <option value="titleDesc">Titolo: Z-A</option>
                    </select>
                </div>
            </div>
        </div>
    </section>

    <section class="catalogo-body">
        <aside class="filters-panel" id="filtersPanel" aria-label="Filtri catalogo">
            <div class="filters-header">
                <div class="filters-title">
                    <span class="filters-ico">⚙️</span>
                    <span>Filtri</span>
                </div>
                <button type="button" id="closeFilters" class="icon-btn" aria-label="Chiudi">✕</button>
            </div>

            <div class="filters-block">
                <div class="filters-block-title">Categorie</div>
                <div class="category-grid" id="categorieGrid">
                    <button type="button" class="cat-chip active" data-categoria-id="0">Tutte</button>
                    <c:forEach items="${categorie}" var="categoria">
                        <button type="button" class="cat-chip" data-categoria-id="${categoria.idCategoria}">
                                ${fn:escapeXml(categoria.nomeCategoria)}
                        </button>
                    </c:forEach>
                </div>
            </div>

            <div class="filters-block">
                <div class="filters-block-title">Prezzo</div>
                <div class="price-row">
                    <div class="field">
                        <label for="priceMin">Min</label>
                        <input id="priceMin" type="number" inputmode="decimal" min="0" step="0.01" placeholder="0">
                    </div>
                    <div class="field">
                        <label for="priceMax">Max</label>
                        <input id="priceMax" type="number" inputmode="decimal" min="0" step="0.01" placeholder="100">
                    </div>
                </div>
                <div class="hint">Lascia vuoto per ignorare.</div>
            </div>

            <div class="filters-block">
                <div class="filters-block-title">Disponibilità</div>
                <div class="availability-grid">
                    <button type="button" class="avail-chip active" data-availability="all">Tutti</button>
                    <button type="button" class="avail-chip" data-availability="in">Disponibili</button>
                    <button type="button" class="avail-chip" data-availability="out">Esauriti</button>
                </div>
            </div>

            <div class="filters-footer">
                <button type="button" id="resetFilters" class="btn-ghost">Reset</button>
                <button type="button" id="applyFilters" class="btn-primary">Applica</button>
            </div>
        </aside>

        <section class="results-panel">
            <div class="results-meta">
                <div class="results-count">
                    <span id="resultsCount">0</span> risultati
                </div>
                <div class="active-filters" id="activeFilters"></div>
            </div>

            <div class="books-grid" id="booksGrid">
                <c:forEach items="${libri}" var="libro">
                    <article class="book-card"
                             data-categorie="<c:forEach items='${libro.categorie}' var='cat' varStatus='loop'>${cat}${!loop.last ? ',' : ''}</c:forEach>"
                             data-prezzo="${libro.prezzo}"
                             data-disponibile="${libro.disponibilita > 0}"
                             data-titolo="${fn:escapeXml(libro.titolo)}"
                             data-autore="${fn:escapeXml(libro.autore)}">
                        <a class="book-media"
                           href="${pageContext.request.contextPath}/dettaglio-libro?id=${libro.idLibro}">
                            <img class="book-cover"
                                 src="${pageContext.request.contextPath}/img/libri/copertine/${not empty libro.copertina ? libro.copertina : 'default.jpg'}"
                                 alt="${fn:escapeXml(libro.titolo)}"
                                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/img/libri/copertine/default.jpg';">
                            <c:choose>
                                <c:when test="${libro.disponibilita > 0}">
                                    <span class="badge badge-ok">Disponibile</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-ko">Esaurito</span>
                                </c:otherwise>
                            </c:choose>
                        </a>

                        <div class="book-body">
                            <div class="book-main">
                                <a class="book-title-link"
                                   href="${pageContext.request.contextPath}/dettaglio-libro?id=${libro.idLibro}">
                                    <h3 class="book-title">${fn:escapeXml(libro.titolo)}</h3>
                                </a>
                                <div class="book-author">${fn:escapeXml(libro.autore)}</div>

                                <c:if test="${not empty libro.categorie}">
                                    <div class="book-categories">
                                        <c:forEach items="${libro.categorie}" var="catId">
                                            <c:forEach items="${categorie}" var="categoria">
                                                <c:if test="${categoria.idCategoria == catId}">
                                                    <span class="category-pill">${fn:escapeXml(categoria.nomeCategoria)}</span>
                                                </c:if>
                                            </c:forEach>
                                        </c:forEach>
                                    </div>
                                </c:if>

                                <c:if test="${not empty libro.descrizione}">
                                    <p class="book-desc">
                                            ${fn:length(libro.descrizione) > 120 ? fn:substring(libro.descrizione, 0, 120).concat('...') : libro.descrizione}
                                    </p>
                                </c:if>
                            </div>

                            <div class="book-footer">
                                <div class="price">
                                    <span class="price-label">Prezzo</span>
                                    <span class="price-value">${libro.prezzo} €</span>
                                </div>

                                <div class="cta">
                                    <a class="btn-primary btn-sm"
                                       href="${pageContext.request.contextPath}/dettaglio-libro?id=${libro.idLibro}">
                                        Dettagli
                                    </a>
                                    <c:choose>
                                        <c:when test="${libro.disponibilita > 0}">
                                            <form method="post" action="${pageContext.request.contextPath}/carrello"
                                                  class="inline-form">
                                                <input type="hidden" name="azione" value="aggiungi">
                                                <input type="hidden" name="idLibro" value="${libro.idLibro}">
                                                <input type="hidden" name="quantita" value="1">
                                                <button type="submit" class="btn-soft btn-sm">Aggiungi al carrello
                                                </button>
                                            </form>
                                        </c:when>
                                        <c:otherwise>
                                            <button type="button" class="btn-ghost btn-sm" disabled>Non disponibile
                                            </button>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </article>
                </c:forEach>

                <c:if test="${empty libri}">
                    <div class="empty-state">
                        <div class="empty-ico">📚</div>
                        <div class="empty-title">Nessun libro disponibile</div>
                        <div class="empty-sub">Prova a cambiare filtri o controlla più tardi.</div>
                    </div>
                </c:if>
            </div>

            <div class="no-results" id="noResults" style="display:none;">
                <div class="empty-state">
                    <div class="empty-ico">🔎</div>
                    <div class="empty-title">Nessun risultato</div>
                    <div class="empty-sub">Prova a modificare ricerca o filtri.</div>
                </div>
            </div>
        </section>
    </section>
</main>

<jsp:include page="footer.jsp"/>

<script src="${pageContext.request.contextPath}/js/catalogo-filtri.js"></script>

</body>
</html>

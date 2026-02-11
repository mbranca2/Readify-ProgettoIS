<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<c:if test="${empty libro}">
    <c:redirect url="/libri"/>
</c:if>

<!DOCTYPE html>
<html lang="it" class="book-detail-page">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <c:set var="descrizioneBreve" value="${libro.descrizione != null ? libro.descrizione : ''}"/>
    <c:set var="descrizioneLunghezza" value="${not empty libro.descrizione ? libro.descrizione.length() : 0}"/>
    <c:set var="descrizioneDaMostrare"
           value="${descrizioneLunghezza > 160 ? descrizioneBreve.substring(0, 160) : descrizioneBreve}"/>
    <meta name="description"
          content="${not empty libro.descrizione ? descrizioneDaMostrare : 'Dettagli del libro ' += libro.titolo}">
    <title>${libro.titolo} - Librorama</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/book-detail.css">
    <style>
        .review-actions {
            display: flex;
            gap: 8px;
            margin-top: 8px;
        }

        .btn-small {
            padding: 6px 10px;
            border-radius: 10px;
            border: 1px solid #ddd;
            background: #f7f7f7;
            cursor: pointer;
        }

        .btn-danger {
            background: #fff3f3;
            border-color: #ffd4d4;
        }

        .edit-form {
            margin-top: 10px;
            padding: 10px;
            border: 1px solid #eee;
            border-radius: 12px;
            background: #fafafa;
            display: none;
        }

        .edit-form .form-group {
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
<jsp:include page="header.jsp"/>
<main class="container">
    <div class="book-detail-container">
        <div class="book-cover-container">
            <c:choose>
                <c:when test="${not empty libro.copertina}">
                    <img src="${pageContext.request.contextPath}/img/libri/copertine/${libro.copertina}"
                         alt="${libro.titolo}" class="book-cover">
                </c:when>
                <c:otherwise>
                    <div class="no-cover">
                        <div class="no-cover-icon">📚</div>
                        <p>Nessuna copertina disponibile</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="book-info">
            <h1 class="book-title">${libro.titolo}</h1>
            <p class="book-author">di ${libro.autore}</p>

            <div class="book-meta">
                <span>📋 ISBN: ${libro.isbn}</span>
                <span class="availability">
                    <c:choose>
                        <c:when test="${libro.disponibilita > 0}">
                            ✅ Disponibile (${libro.disponibilita} copie)
                        </c:when>
                        <c:otherwise>
                            ❌ Non disponibile
                        </c:otherwise>
                    </c:choose>
                </span>
            </div>

            <div class="book-price">
                ${libro.prezzo} €
            </div>

            <c:choose>
                <c:when test="${libro.disponibilita > 0}">
                    <form action="${pageContext.request.contextPath}/carrello" method="post">
                        <input type="hidden" name="azione" value="aggiungi">
                        <input type="hidden" name="idLibro" value="${libro.idLibro}">
                        <input type="hidden" name="quantita" value="1">
                        <button type="submit" class="btn-add-to-cart">
                            🛒 Aggiungi al carrello
                        </button>
                    </form>
                </c:when>
                <c:otherwise>
                    <button class="btn-add-to-cart" disabled>
                        ❌ Non disponibile
                    </button>
                </c:otherwise>
            </c:choose>

            <c:if test="${not empty param.aggiunto}">
                <div class="alert alert-success" role="alert">
                    <span>✅</span>
                    <span>Prodotto aggiunto al carrello!</span>
                </div>
            </c:if>

            <!-- Feedback inserimento recensione -->
            <c:if test="${param.review == 'ok'}">
                <div class="alert alert-success" role="alert">
                    <span>✅</span>
                    <span>Recensione inserita con successo!</span>
                </div>
            </c:if>
            <c:if test="${param.review == 'not_allowed'}">
                <div class="alert alert-error" role="alert">
                    <span>❌</span>
                    <span>Puoi inserire una recensione solo se hai acquistato questo libro.</span>
                </div>
            </c:if>

            <!-- Feedback modifica/elimina recensione -->
            <c:if test="${param.review_edit == 'ok'}">
                <div class="alert alert-success" role="alert">
                    <span>✅</span>
                    <span>Recensione modificata con successo.</span>
                </div>
            </c:if>
            <c:if test="${param.review_edit == 'ko'}">
                <div class="alert alert-error" role="alert">
                    <span>❌</span>
                    <span>Impossibile modificare la recensione.</span>
                </div>
            </c:if>
            <c:if test="${param.review_del == 'ok'}">
                <div class="alert alert-success" role="alert">
                    <span>✅</span>
                    <span>Recensione eliminata con successo.</span>
                </div>
            </c:if>
            <c:if test="${param.review_del == 'ko'}">
                <div class="alert alert-error" role="alert">
                    <span>❌</span>
                    <span>Impossibile eliminare la recensione.</span>
                </div>
            </c:if>

            <div class="book-details">
                <h3>Descrizione</h3>
                <p>${not empty libro.descrizione ? libro.descrizione : 'Nessuna descrizione disponibile per questo libro.'}</p>

                <h3>Dettagli</h3>
                <ul class="details-list">
                    <li><strong>Autore:</strong> ${libro.autore}</li>
                    <li><strong>ISBN:</strong> ${libro.isbn}</li>
                    <li><strong>Prezzo:</strong> ${libro.prezzo} €</li>
                    <li><strong>Disponibilità:</strong>
                        <c:choose>
                            <c:when test="${libro.disponibilita > 0}">
                                <span class="in-stock">In magazzino (${libro.disponibilita} copie)</span>
                            </c:when>
                            <c:otherwise>
                                <span class="out-of-stock">Esaurito</span>
                            </c:otherwise>
                        </c:choose>
                    </li>
                </ul>

                <h3>Recensioni</h3>

                <!-- Form inserimento recensione -->
                <c:choose>
                    <c:when test="${empty sessionScope.utente}">
                        <p class="no-reviews">Per lasciare una recensione devi effettuare il login.</p>
                    </c:when>

                    <c:when test="${not empty sessionScope.utente && (empty canReview || canReview == false)}">
                        <p class="no-reviews">Per lasciare una recensione devi aver acquistato questo libro.</p>
                    </c:when>

                    <c:otherwise>
                        <div class="review-form-container">
                            <form action="${pageContext.request.contextPath}/recensioni/aggiungi" method="post">
                                <input type="hidden" name="idLibro" value="${libro.idLibro}"/>

                                <div class="form-group">
                                    <label for="voto">Valutazione</label>
                                    <select id="voto" name="voto" required>
                                        <option value="">Seleziona</option>
                                        <option value="5">★★★★★ (5)</option>
                                        <option value="4">★★★★☆ (4)</option>
                                        <option value="3">★★★☆☆ (3)</option>
                                        <option value="2">★★☆☆☆ (2)</option>
                                        <option value="1">★☆☆☆☆ (1)</option>
                                    </select>
                                </div>

                                <div class="form-group">
                                    <label for="commento">Commento</label>
                                    <textarea id="commento" name="commento" rows="4" maxlength="2000"
                                              placeholder="Scrivi qui la tua recensione..."></textarea>
                                </div>

                                <button type="submit" class="btn-add-to-cart">
                                    ✍️ Pubblica recensione
                                </button>
                            </form>
                        </div>
                    </c:otherwise>
                </c:choose>

                <!-- Lista recensioni -->
                <c:choose>
                    <c:when test="${not empty recensioni}">
                        <div class="reviews-container">
                            <c:forEach items="${recensioni}" var="r">
                                <div class="review-card" id="review-${r.idRecensione}">
                                    <div class="review-header">
                                        <span class="reviewer-name">${r.nomeUtente}</span>
                                        <div class="rating">
                                            <c:forEach begin="1" end="5" var="i">
                                                <span class="star ${i <= r.voto ? 'filled' : ''}">★</span>
                                            </c:forEach>
                                        </div>
                                        <span class="review-date">
                                            <fmt:formatDate value="${r.dataRecensione}" pattern="dd/MM/yyyy"/>
                                        </span>
                                    </div>

                                    <p class="review-comment">${r.commento}</p>

                                    <!-- Azioni: SOLO autore recensione -->
                                    <c:if test="${not empty sessionScope.utente && sessionScope.utente.idUtente == r.idUtente}">
                                        <div class="review-actions">
                                            <button type="button" class="btn-small"
                                                    onclick="toggleEdit(${r.idRecensione})">
                                                ✏️ Modifica
                                            </button>

                                            <form action="${pageContext.request.contextPath}/recensioni/elimina"
                                                  method="post"
                                                  onsubmit="return confirm('Vuoi eliminare questa recensione?');">
                                                <input type="hidden" name="idRecensione" value="${r.idRecensione}">
                                                <input type="hidden" name="idLibro" value="${libro.idLibro}">
                                                <button type="submit" class="btn-small btn-danger">🗑️ Elimina</button>
                                            </form>
                                        </div>

                                        <div class="edit-form" id="edit-form-${r.idRecensione}">
                                            <form action="${pageContext.request.contextPath}/recensioni/modifica"
                                                  method="post">
                                                <input type="hidden" name="idRecensione" value="${r.idRecensione}">
                                                <input type="hidden" name="idLibro" value="${libro.idLibro}">

                                                <div class="form-group">
                                                    <label>Valutazione</label>
                                                    <select name="voto" required>
                                                        <option value="5" ${r.voto == 5 ? 'selected' : ''}>★★★★★ (5)
                                                        </option>
                                                        <option value="4" ${r.voto == 4 ? 'selected' : ''}>★★★★☆ (4)
                                                        </option>
                                                        <option value="3" ${r.voto == 3 ? 'selected' : ''}>★★★☆☆ (3)
                                                        </option>
                                                        <option value="2" ${r.voto == 2 ? 'selected' : ''}>★★☆☆☆ (2)
                                                        </option>
                                                        <option value="1" ${r.voto == 1 ? 'selected' : ''}>★☆☆☆☆ (1)
                                                        </option>
                                                    </select>
                                                </div>

                                                <div class="form-group">
                                                    <label>Commento</label>
                                                    <textarea name="commento" rows="4"
                                                              maxlength="2000">${r.commento}</textarea>
                                                </div>

                                                <div class="review-actions">
                                                    <button type="submit" class="btn-small">Salva</button>
                                                    <button type="button" class="btn-small"
                                                            onclick="toggleEdit(${r.idRecensione})">
                                                        Annulla
                                                    </button>
                                                </div>
                                            </form>
                                        </div>
                                    </c:if>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="no-reviews">Non ci sono ancora recensioni per questo libro.</p>
                    </c:otherwise>
                </c:choose>

            </div>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp"/>

<script>
    function toggleEdit(idRec) {
        const el = document.getElementById('edit-form-' + idRec);
        if (!el) return;
        el.style.display = (el.style.display === 'none' || el.style.display === '') ? 'block' : 'none';
    }
</script>
</body>
</html>

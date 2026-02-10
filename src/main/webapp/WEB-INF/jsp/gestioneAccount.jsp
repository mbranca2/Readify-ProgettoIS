<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gestione Account - Readify</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/account.css">
</head>
<body>

<jsp:include page="header.jsp" />

<div class="account-container">
    <c:if test="${not empty requestScope.messaggio}">
        <div class="alert alert-${requestScope.tipoMessaggio}">
            ${requestScope.messaggio}
        </div>
    </c:if>
    
    <c:choose>
        <c:when test="${not empty sessionScope.utente}">
            <div class="profile-header">
                <h2>Benvenuto, ${sessionScope.utente.nome}!</h2>
                <p>Gestisci il tuo account e i tuoi ordini</p>

                <c:if test="${not empty requestScope.messaggio}">
                    <div class="alert-success">${requestScope.messaggio}</div>
                </c:if>

                <c:if test="${not empty requestScope.errore}">
                    <div class="alert-error">${requestScope.errore}</div>
                </c:if>
            </div>

            <div class="account-content">
                <!-- Menu laterale -->
                <div class="account-menu">
                    <button class="tab-button active" data-target="profile">Dati personali</button>
                    <button class="tab-button" data-target="address">Indirizzo</button>
                    <button class="tab-button" data-target="password">Password</button>
                    <button class="tab-button" data-target="orders">Ordini</button>
                </div>

                <!-- Contenuti -->
                <div class="tab-section">
                    <div id="profile" class="tab-panel active">
                        <h3 class="section-title">Informazioni personali</h3>
                        <form method="post" action="${pageContext.request.contextPath}/profilo" class="account-form">
                            <div class="form-group">
                                <label for="nome" class="form-label">Nome</label>
                                <input type="text" id="nome" name="nome" class="form-input"
                                       value="${sessionScope.utente.nome}" required>
                            </div>
                            <div class="form-group">
                                <label for="cognome" class="form-label">Cognome</label>
                                <input type="text" id="cognome" name="cognome" class="form-input"
                                       value="${sessionScope.utente.cognome}" required>
                            </div>
                            <div class="form-group">
                                <label for="email" class="form-label">Email</label>
                                <input type="email" id="email" name="email" class="form-input"
                                       value="${sessionScope.utente.email}" required>
                            </div>
                            <div class="form-group">
                                <label for="telefono" class="form-label">Telefono</label>
                                <input type="tel" id="telefono" name="telefono" class="form-input"
                                       value="${sessionScope.utente.telefono}">
                            </div>
                            <button type="submit" class="btn">Salva modifiche</button>
                        </form>
                    </div>

                    <div id="address" class="tab-panel">
                        <h3 class="section-title">Indirizzi salvati</h3>
                        <c:choose>
                            <c:when test="${not empty indirizzi}">
                                <div class="address-list">
                                    <c:forEach items="${indirizzi}" var="ind">
                                        <div class="address-card${sessionScope.indirizzo != null && sessionScope.indirizzo.idIndirizzo == ind.idIndirizzo ? ' selected' : ''}">
                                            <div class="address-info">
                                                <div class="address-line">${ind.via}</div>
                                                <div class="address-line">
                                                    ${ind.cap} ${ind.citta} (${ind.provincia}) - ${ind.paese}
                                                </div>
                                            </div>
                                            <div class="address-actions">
                                                <form method="post" action="${pageContext.request.contextPath}/gestione-indirizzo">
                                                    <input type="hidden" name="azione" value="seleziona">
                                                    <input type="hidden" name="idIndirizzo" value="${ind.idIndirizzo}">
                                                    <button type="submit" class="btn btn-outline btn-sm btn-action">Modifica</button>
                                                </form>
                                                <form method="post" action="${pageContext.request.contextPath}/gestione-indirizzo"
                                                      onsubmit="return confirm('Vuoi eliminare questo indirizzo?');">
                                                    <input type="hidden" name="azione" value="elimina">
                                                    <input type="hidden" name="idIndirizzo" value="${ind.idIndirizzo}">
                                                    <button type="submit" class="btn btn-outline btn-sm btn-danger">Elimina</button>
                                                </form>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-orders">
                                    <p>Nessun indirizzo salvato</p>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <div class="address-toolbar">
                            <form method="post" action="${pageContext.request.contextPath}/gestione-indirizzo">
                                <input type="hidden" name="azione" value="nuovo">
                                <button type="submit" class="btn btn-outline btn-add">Nuovo indirizzo</button>
                            </form>
                        </div>

                        <h3 class="section-title">Indirizzo selezionato</h3>
                        <form method="post" action="${pageContext.request.contextPath}/gestione-indirizzo" class="account-form">
                            <input type="hidden" name="idIndirizzo"
                                   value="${sessionScope.indirizzo != null ? sessionScope.indirizzo.idIndirizzo : ''}">
                            <div class="form-group">
                                <label for="via" class="form-label">Via e numero civico*</label>
                                <input type="text" id="via" name="via" class="form-input"
                                       value="${sessionScope.indirizzo != null ? sessionScope.indirizzo.via : ''}" required>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="cap" class="form-label">CAP*</label>
                                    <input type="text" id="cap" name="cap" class="form-input"
                                           pattern="[0-9]{5}" title="Inserisci un CAP valido (5 cifre)"
                                           value="${sessionScope.indirizzo != null ? sessionScope.indirizzo.cap : ''}" required>
                                </div>
                                <div class="form-group">
                                    <label for="citta" class="form-label">Città*</label>
                                    <input type="text" id="citta" name="citta" class="form-input"
                                           value="${sessionScope.indirizzo != null ? sessionScope.indirizzo.citta : ''}" required>
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="provincia" class="form-label">Provincia*</label>
                                    <input type="text" id="provincia" name="provincia" class="form-input"
                                           maxlength="2" style="text-transform: uppercase;"
                                           value="${sessionScope.indirizzo != null ? sessionScope.indirizzo.provincia : ''}" required>
                                </div>
                                <div class="form-group">
                                    <label for="paese" class="form-label">Paese*</label>
                                    <input type="text" id="paese" name="paese" class="form-input"
                                           value="${sessionScope.indirizzo != null && sessionScope.indirizzo.paese != null ? sessionScope.indirizzo.paese : 'Italia'}" required>
                                </div>
                            </div>
                            <button type="submit" class="btn">Salva indirizzo</button>
                        </form>
                    </div>

                    <div id="password" class="tab-panel">
                        <h3 class="section-title">Cambia password</h3>
                        <form method="post" action="${pageContext.request.contextPath}/cambia-password" class="account-form" id="passwordChangeForm" onsubmit="return validatePasswordChange()">
                            <div class="form-group">
                                <label for="vecchiaPassword" class="form-label">Password attuale</label>
                                <input type="password" id="vecchiaPassword" name="vecchiaPassword"
                                       class="form-input" required>
                                <div class="invalid-feedback"></div>
                            </div>
                            <div class="form-group">
                                <label for="nuovaPassword" class="form-label">Nuova password</label>
                                <input type="password" id="nuovaPassword" name="nuovaPassword"
                                       class="form-input" required minlength="8"
                                       pattern="^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$">
                                <div class="invalid-feedback"></div>
                            </div>
                            <div class="form-group">
                                <label for="confermaPassword" class="form-label">Conferma nuova password</label>
                                <input type="password" id="confermaPassword" name="confermaPassword"
                                       class="form-input" required>
                                <div class="invalid-feedback"></div>
                            </div>
                            <button type="submit" class="btn">Aggiorna password</button>
                        </form>
                    </div>

                    <div id="orders" class="tab-panel">
                        <h3 class="section-title">I miei ordini</h3>
                        <c:choose>
                            <c:when test="${not empty requestScope.ordini}">
                                <table class="order-table">
                                    <thead>
                                    <tr>
                                        <th>Ordine #</th>
                                        <th>Data</th>
                                        <th>Totale</th>
                                        <th>Stato</th>
                                        <th>Azioni</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${requestScope.ordini}" var="ordine">
                                        <tr>
                                            <td>#${ordine.idOrdine}</td>
                                            <td><fmt:formatDate value="${ordine.dataOrdine}" pattern="dd/MM/yyyy"/></td>
                                            <td><fmt:formatNumber value="${ordine.totale}" type="currency" currencyCode="EUR"/></td>
                                            <td><span class="order-badge">${ordine.stato}</span></td>
                                            <td><a href="${pageContext.request.contextPath}/dettaglio-ordine?id=${ordine.idOrdine}">Dettagli</a></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <div class="empty-orders">
                                    <p>Nessun ordine effettuato</p>
                                    <a href="${pageContext.request.contextPath}/libri" class="btn">Inizia a fare acquisti</a>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </c:when>
        <c:otherwise>
            <div class="empty-orders">
                <h3>Accesso richiesto</h3>
                <p>Per accedere a questa pagina, effettua il <a href="${pageContext.request.contextPath}/login" class="btn btn-outline">Login</a> o <a href="${pageContext.request.contextPath}/registrazione" class="btn btn-primary">Registrati</a></p>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="footer.jsp" />

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const tabButtons = document.querySelectorAll('.tab-button');

        // Funzione per attivare un tab
        function activateTab(tabId) {
            // Rimuovi la classe active da tutti i bottoni e dai pannelli
            document.querySelectorAll('.tab-button').forEach(btn => btn.classList.remove('active'));
            document.querySelectorAll('.tab-panel').forEach(panel => panel.classList.remove('active'));

            // Aggiungi la classe active al bottone cliccato
            const activeButton = document.querySelector(`[data-target="${tabId}"]`);
            if (activeButton) {
                activeButton.classList.add('active');
            }

            // Mostra il pannello corrispondente
            const activePanel = document.getElementById(tabId);
            if (activePanel) {
                activePanel.classList.add('active');
            }

            // Salva il tab attivo nell'URL
            const url = new URL(window.location.href);
            url.searchParams.set('tab', tabId);
            window.history.pushState({}, '', url);
        }

        // Aggiungi gestore di eventi a ciascun pulsante
        tabButtons.forEach(button => {
            button.addEventListener('click', () => {
                const target = button.getAttribute('data-target');
                activateTab(target);
            });
        });

        // Gestisci il popstate per il tasto indietro/avanti del browser
        window.addEventListener('popstate', function() {
            const urlParams = new URLSearchParams(window.location.search);
            const tab = urlParams.get('tab') || 'profile';
            activateTab(tab);
        });

        // Attiva il tab corretto al caricamento della pagina
        const urlParams = new URLSearchParams(window.location.search);
        const activeTab = urlParams.get('tab') || 'profile';
        activateTab(activeTab);
    });

    // Funzione per la validazione del cambio password
    function validatePasswordChange() {
        const form = document.getElementById('passwordChangeForm');
        const nuovaPassword = document.getElementById('nuovaPassword');
        const confermaPassword = document.getElementById('confermaPassword');
        const feedback = document.querySelectorAll('.invalid-feedback');

        // Resetta i messaggi di errore
        feedback.forEach(el => el.textContent = '');
        nuovaPassword.classList.remove('is-invalid');
        confermaPassword.classList.remove('is-invalid');

        // Verifica se le password coincidono
        if (nuovaPassword.value !== confermaPassword.value) {
            confermaPassword.nextElementSibling.textContent = 'Le password non coincidono';
            confermaPassword.classList.add('is-invalid');
            return false;
        }

        // Verifica la complessità della password
        const passwordRegex = /^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$/;
        if (!passwordRegex.test(nuovaPassword.value)) {
            nuovaPassword.nextElementSibling.textContent = 'La password deve contenere almeno 8 caratteri, una maiuscola e un numero';
            nuovaPassword.classList.add('is-invalid');
            return false;
        }

        return true;
    }
</script>

</body>
</html>

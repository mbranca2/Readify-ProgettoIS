<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="../header.jsp"/>

<main class="form-container">
    <h2>Nuovo Utente</h2>

    <c:if test="${not empty errore}">
        <div class="alert alert-danger">${fn:escapeXml(errore)}</div>
    </c:if>

    <form id="utenteForm" method="post" action="${pageContext.request.contextPath}/admin/utenti"
          class="form needs-validation" novalidate>
        <input type="hidden" name="azione" value="nuovo">

        <div class="form-group">
            <label for="nome">Nome *</label>
            <input type="text" class="form-control" id="nome" name="nome"
                   required minlength="2" maxlength="50"
                   pattern="[A-Za-z\s\-\.,;:!?()\[\]{}'\" \u00C0-\u017F]+">
            <div class="invalid-feedback">Inserisci un nome valido (almeno 2 caratteri)</div>
        </div>

        <div class="form-group">
            <label for="cognome">Cognome *</label>
            <input type="text" class="form-control" id="cognome" name="cognome"
                   required minlength="2" maxlength="50"
                   pattern="[A-Za-z\s\-\.,;:!?()\[\]{}'\" \u00C0-\u017F]+">
            <div class="invalid-feedback">Inserisci un cognome valido (almeno 2 caratteri)</div>
        </div>

        <div class="form-group">
            <label for="email">Email *</label>
            <input type="email" class="form-control" id="email" name="email"
                   required maxlength="100">
            <div class="invalid-feedback">Inserisci un indirizzo email valido</div>
        </div>

        <div class="form-group">
            <label for="password">Password *</label>
            <input type="password" class="form-control" id="password" name="password"
                   required minlength="8" maxlength="100"
                   pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+">
            <div class="form-text">Almeno 8 caratteri, una maiuscola, una minuscola e un numero</div>
            <div class="invalid-feedback">La password non soddisfa i requisiti minimi</div>
        </div>

        <div class="form-group">
            <label for="confermaPassword">Conferma Password *</label>
            <input type="password" class="form-control" id="confermaPassword" name="confermaPassword"
                   required>
            <div class="invalid-feedback">Le password non coincidono</div>
        </div>

        <div class="form-group">
            <label for="telefono">Telefono</label>
            <input type="tel" class="form-control" id="telefono" name="telefono"
                   pattern="[0-9+\-\s()]{8,15}"
                   title="Formato: +39 123 456 7890 o 0123 456789">
            <div class="form-text">Formato: +39 123 456 7890 o 0123 456789</div>
            <div class="invalid-feedback">Inserisci un numero di telefono valido</div>
        </div>

        <div class="form-group">
            <label for="ruolo">Ruolo *</label>
            <select class="form-select" id="ruolo" name="ruolo" required>
                <option value="CLIENTE">Cliente</option>
                <option value="AMMINISTRATORE">Amministratore</option>
            </select>
            <div class="invalid-feedback">Seleziona un ruolo</div>
        </div>

        <div class="form-group">
            <button type="submit" class="btn btn-primary">Crea Utente</button>
        </div>
    </form>

    <p>
        <a href="${pageContext.request.contextPath}/admin/utenti" class="btn btn-link">
            <i class="fas fa-arrow-left me-2"></i>Torna alla lista degli utenti
        </a>
    </p>
</main>

<jsp:include page="../footer.jsp"/>

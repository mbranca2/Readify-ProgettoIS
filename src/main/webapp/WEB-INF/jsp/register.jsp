<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="header.jsp" />

<main class="form-container">
    <h2>Registrazione</h2>

    <c:if test="${not empty erroreRegistrazione}">
        <div class="alert alert-danger">${fn:escapeXml(erroreRegistrazione)}</div>
    </c:if>
    
    <c:if test="${not empty errori and not empty errori.generico}">
        <div class="alert alert-danger">${fn:escapeXml(errori.generico)}</div>
    </c:if>

    <form id="registrationForm" action="${pageContext.request.contextPath}/registrazione" method="post" class="form" novalidate>
        <div class="form-group">
            <label for="nome">Nome *</label>
            <input type="text" id="nome" name="nome" value="${nome}" 
                   required minlength="2" maxlength="50" 
                   pattern="[A-Za-z\s']+" title="Inserisci un nome valido (solo lettere e spazi)" 
                   class="form-control">
            <div class="invalid-feedback">Inserisci un nome valido (almeno 2 caratteri)</div>
        </div>

        <div class="form-group">
            <label for="cognome">Cognome *</label>
            <input type="text" id="cognome" name="cognome" value="${cognome}" 
                   required minlength="2" maxlength="50" 
                   pattern="[A-Za-z\s']+" title="Inserisci un cognome valido (solo lettere e spazi)" 
                   class="form-control">
            <div class="invalid-feedback">Inserisci un cognome valido (almeno 2 caratteri)</div>
        </div>

        <div class="form-group">
            <label for="email">Email *</label>
            <input type="email" id="email" name="email" 
                   value="${email}" required maxlength="100" 
                   class="form-control">
            <div class="invalid-feedback">Inserisci un indirizzo email valido</div>
        </div>

        <div class="form-group">
            <label for="telefono">Telefono</label>
            <input type="tel" id="telefono" name="telefono" 
                   value="${telefono}" pattern="[0-9+\-\s()]{8,15}" 
                   title="Inserisci un numero di telefono valido" 
                   class="form-control">
            <div class="form-text">Formato: +39 123 456 7890 o 0123 456789</div>
            <div class="invalid-feedback">Inserisci un numero di telefono valido</div>
        </div>

        <div class="form-group">
            <label for="via">Indirizzo *</label>
            <input type="text" id="via" name="via"
                   value="${via}" required
                   class="form-control">
            <div class="invalid-feedback">Inserisci un indirizzo valido</div>
        </div>

        <div class="form-group">
            <label for="citta">Città *</label>
            <input type="text" id="citta" name="citta"
                   value="${citta}" required
                   class="form-control">
            <div class="invalid-feedback">Inserisci una città valida</div>
        </div>

        <div class="row">
            <div class="col-md-4">
                <div class="form-group">
                    <label for="cap">CAP *</label>
                    <input type="text" id="cap" name="cap"
                           value="${cap}" required pattern="\d{5}"
                           title="Inserisci un CAP valido (5 cifre)"
                           class="form-control">
                    <div class="invalid-feedback">Inserisci un CAP valido (5 cifre)</div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="form-group">
                    <label for="provincia">Provincia *</label>
                    <input type="text" id="provincia" name="provincia"
                           value="${provincia}" required maxlength="2"
                           pattern="[A-Za-z]{2}" title="Inserisci la sigla della provincia (es: RM, MI, TO)"
                           class="form-control text-uppercase">
                    <div class="invalid-feedback">Inserisci una provincia valida (es: SA, MI, NA)</div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="form-group">
                    <label for="paese">Paese *</label>
                    <input type="text" id="paese" name="paese"
                           value="${not empty paese ? paese : 'Italia'}"
                           required
                           class="form-control">
                </div>
            </div>
        </div>

        <div class="form-group">
            <label for="password">Password *</label>
            <input type="password" id="password" name="password" 
                   required minlength="8" maxlength="100"
                   pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+"
                   title="La password deve contenere almeno 8 caratteri, una maiuscola, una minuscola e un numero" 
                   class="form-control">
            <div class="form-text">Almeno 8 caratteri, una maiuscola, una minuscola e un numero</div>
            <div class="invalid-feedback">La password non soddisfa i requisiti minimi</div>
        </div>

        <div class="form-group">
            <label for="confermaPassword">Conferma Password *</label>
            <input type="password" id="confermaPassword" 
                   name="confermaPassword" required 
                   class="form-control">
            <div class="invalid-feedback">Le password non coincidono</div>
        </div>

        <div class="form-group">
            <div class="form-check">
                <input type="checkbox" class="form-check-input" id="privacy" name="privacy" required>
                <label class="form-check-label" for="privacy">
                    Accetto la <a href="#" data-bs-toggle="modal" data-bs-target="#privacyModal">privacy policy</a> *
                </label>
                <div class="invalid-feedback">Devi accettare la privacy policy per registrarti</div>
            </div>
        </div>

        <div class="form-group">
            <button type="submit" class="btn btn-primary">Registrati</button>
        </div>
    </form>

    <p>Hai già un account? <a href="${pageContext.request.contextPath}/login">Accedi</a></p>
</main>

<script src="${pageContext.request.contextPath}/js/form-validation.js"></script>

<jsp:include page="footer.jsp" />

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:include page="header.jsp" />

<title>Registrazione - Readify</title>

<main class="auth-page register-page">
    <div class="container auth-container">
        <div class="auth-card auth-card--wide">
            <div class="d-flex justify-content-between align-items-center auth-title-row">
                <h2 class="mt-0 mb-0">Registrazione</h2>
            </div>

            <c:if test="${not empty erroreRegistrazione}">
                <div class="alert alert-danger">${fn:escapeXml(erroreRegistrazione)}</div>
            </c:if>

            <c:if test="${not empty errori and not empty errori.generico}">
                <div class="alert alert-danger">${fn:escapeXml(errori.generico)}</div>
            </c:if>

            <form id="registrationForm" action="${pageContext.request.contextPath}/registrazione" method="post" class="auth-form" novalidate>
                <div class="auth-grid">
                    <div class="form-group">
                        <label for="nome">Nome *</label>
                        <input type="text" id="nome" name="nome" value="${nome}"
                               required minlength="2" maxlength="50"
                               pattern="[A-Za-z\\s']+" title="Inserisci un nome valido (solo lettere e spazi)"
                               class="form-control" placeholder="Es: Mario">
                        <div class="invalid-feedback">Inserisci un nome valido (almeno 2 caratteri)</div>
                    </div>

                    <div class="form-group">
                        <label for="cognome">Cognome *</label>
                        <input type="text" id="cognome" name="cognome" value="${cognome}"
                               required minlength="2" maxlength="50"
                               pattern="[A-Za-z\\s']+" title="Inserisci un cognome valido (solo lettere e spazi)"
                               class="form-control" placeholder="Es: Rossi">
                        <div class="invalid-feedback">Inserisci un cognome valido (almeno 2 caratteri)</div>
                    </div>

                    <div class="form-group">
                        <label for="email">Email *</label>
                        <input type="email" id="email" name="email"
                               value="${email}" required maxlength="100"
                               class="form-control" placeholder="nome.cognome@email.it">
                        <div class="invalid-feedback">Inserisci un indirizzo email valido</div>
                    </div>

                    <div class="form-group">
                        <label for="telefono">Telefono</label>
                        <input type="tel" id="telefono" name="telefono"
                               value="${telefono}" pattern="[0-9+\\-\\s()]{8,15}"
                               title="Inserisci un numero di telefono valido"
                               class="form-control" placeholder="+39 123 456 7890">
                        <div class="form-text format-hint">Formato: +39 123 456 7890 o 0123 456789</div>
                        <div class="invalid-feedback">Inserisci un numero di telefono valido</div>
                    </div>

                    <div class="form-group">
                        <label for="via">Indirizzo *</label>
                        <input type="text" id="via" name="via"
                               value="${via}" required
                               class="form-control" placeholder="Via Roma 12">
                        <div class="invalid-feedback">Inserisci un indirizzo valido</div>
                    </div>

                    <div class="form-group">
                        <label for="citta">Citta *</label>
                        <input type="text" id="citta" name="citta"
                               value="${citta}" required
                               class="form-control" placeholder="Es: Milano">
                        <div class="invalid-feedback">Inserisci una citta valida</div>
                    </div>
                </div>

                <div class="auth-grid auth-grid--triple">
                    <div class="form-group">
                        <label for="cap">CAP *</label>
                        <input type="text" id="cap" name="cap"
                               value="${cap}" required pattern="\\d{5}"
                               title="Inserisci un CAP valido (5 cifre)"
                               class="form-control" placeholder="Es: 20100">
                        <div class="invalid-feedback">Inserisci un CAP valido (5 cifre)</div>
                    </div>
                    <div class="form-group">
                        <label for="provincia">Provincia *</label>
                        <input type="text" id="provincia" name="provincia"
                               value="${provincia}" required maxlength="2"
                               pattern="[A-Za-z]{2}" title="Inserisci la sigla della provincia (es: RM, MI, TO)"
                               class="form-control text-uppercase" placeholder="Es: MI">
                        <div class="invalid-feedback">Inserisci una provincia valida (es: SA, MI, NA)</div>
                    </div>
                    <div class="form-group">
                        <label for="paese">Paese *</label>
                        <input type="text" id="paese" name="paese"
                               value="${not empty paese ? paese : 'Italia'}"
                               required
                               class="form-control" placeholder="Es: Italia">
                    </div>
                </div>

                <div class="auth-grid">
                    <div class="form-group">
                        <label for="password">Password *</label>
                        <input type="password" id="password" name="password"
                               required minlength="8" maxlength="100"
                               pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+"
                               title="La password deve contenere almeno 8 caratteri, una maiuscola, una minuscola e un numero"
                               class="form-control" placeholder="Min 8 caratteri, 1 maiuscola, 1 minuscola, 1 numero">
                        <div class="form-text format-hint">Almeno 8 caratteri, una maiuscola, una minuscola e un numero</div>
                        <div class="invalid-feedback">La password non soddisfa i requisiti minimi</div>
                    </div>

                    <div class="form-group">
                        <label for="confermaPassword">Conferma Password *</label>
                        <input type="password" id="confermaPassword"
                               name="confermaPassword" required
                               class="form-control" placeholder="Ripeti la password">
                        <div class="invalid-feedback">Le password non coincidono</div>
                    </div>
                </div>

                <div class="form-actions auth-actions">
                    <button type="submit" class="btn btn-primary">Registrati</button>
                    <a class="btn btn-secondary" href="${pageContext.request.contextPath}/login">Accedi</a>
                </div>
            </form>

            <div class="auth-footer text-center text-muted">
                <span>Hai gia un account?</span> <a href="${pageContext.request.contextPath}/login">Accedi</a>
            </div>
        </div>
    </div>
</main>

<script src="${pageContext.request.contextPath}/js/form-validation.js"></script>

<jsp:include page="footer.jsp" />

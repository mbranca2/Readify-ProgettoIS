<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pagamento - Readify</title>
    <link rel="stylesheet" href="<c:url value='/css/main.css' />">
    <link rel="stylesheet" href="<c:url value='/css/style.css' />">
    <link rel="stylesheet" href="<c:url value='/css/pagamento.css' />">
</head>
<body>
<jsp:include page="header.jsp" />

<main class="payment-page">
    <div class="payment-container">
        <div class="payment-header">
            <h1>Completa il tuo ordine</h1>
            <p>Controlla i dati di spedizione e inserisci il pagamento.</p>
        </div>

        <div class="payment-grid">
            <section class="payment-panel">
                <c:if test="${not empty errore}">
                    <div class="alert alert-danger">${errore}</div>
                </c:if>

                <c:choose>
                    <c:when test="${not empty sessionScope.carrello}">
                        <c:set var="totale" value="${sessionScope.carrello.totale}"/>
                    </c:when>
                    <c:otherwise>
                        <c:set var="totale" value="0"/>
                    </c:otherwise>
                </c:choose>

                <form id="payment-form" action="${pageContext.request.contextPath}/conferma-ordine" method="post">
                    <div class="panel-block">
                        <div class="panel-title">Indirizzo di spedizione</div>

                <c:set var="indirizzoSpedizioneParam" value="${param.indirizzoSpedizione}"/>
                <c:set var="indirizzoSpedizioneAttr" value="${indirizzoSpedizione}"/>
                <c:choose>
                    <c:when test="${not empty indirizzi}">
                        <div class="form-group">
                            <label for="indirizzoSpedizione" class="form-label">Seleziona un indirizzo</label>
                            <select id="indirizzoSpedizione" name="indirizzoSpedizione" class="form-select" required>
                                <c:choose>
                                    <c:when test="${empty indirizzoSpedizioneParam and empty indirizzoSpedizioneAttr}">
                                        <option value="" disabled selected>Seleziona un indirizzo</option>
                                    </c:when>
                                    <c:otherwise>
                                        <option value="" disabled>Seleziona un indirizzo</option>
                                    </c:otherwise>
                                </c:choose>
                                <c:forEach items="${indirizzi}" var="ind">
                                    <c:set var="isSelected" value="false"/>
                                    <c:choose>
                                        <c:when test="${not empty indirizzoSpedizioneParam}">
                                            <c:if test="${indirizzoSpedizioneParam ne 'new'}">
                                                <c:if test="${indirizzoSpedizioneParam == ind.idIndirizzo}">
                                                    <c:set var="isSelected" value="true"/>
                                                </c:if>
                                            </c:if>
                                        </c:when>
                                        <c:otherwise>
                                            <c:if test="${not empty indirizzoSpedizioneAttr}">
                                                <c:if test="${indirizzoSpedizioneAttr == ind.idIndirizzo}">
                                                    <c:set var="isSelected" value="true"/>
                                                </c:if>
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                    <c:choose>
                                        <c:when test="${isSelected}">
                                            <option value="${ind.idIndirizzo}" selected>
                                                ${ind.via}, ${ind.cap} ${ind.citta} (${ind.provincia}) - ${ind.paese}
                                            </option>
                                        </c:when>
                                        <c:otherwise>
                                            <option value="${ind.idIndirizzo}">
                                                ${ind.via}, ${ind.cap} ${ind.citta} (${ind.provincia}) - ${ind.paese}
                                            </option>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>
                                <c:choose>
                                    <c:when test="${not empty indirizzoSpedizioneParam and indirizzoSpedizioneParam eq 'new'}">
                                        <option value="new" selected>Aggiungi nuovo indirizzo</option>
                                    </c:when>
                                            <c:otherwise>
                                                <option value="new">Aggiungi nuovo indirizzo</option>
                                            </c:otherwise>
                                        </c:choose>
                                    </select>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="indirizzoSpedizione" value="new" />
                            </c:otherwise>
                        </c:choose>

                        <div id="new-address-form" class="address-form">
                            <div class="form-group">
                                <label for="via" class="form-label">Via e numero civico</label>
                                <input type="text" id="via" name="via" class="form-input" value="${param.via}">
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="cap" class="form-label">CAP</label>
                                    <input type="text" id="cap" name="cap" class="form-input" value="${param.cap}">
                                </div>
                                <div class="form-group">
                                    <label for="citta" class="form-label">Citta</label>
                                    <input type="text" id="citta" name="citta" class="form-input" value="${param.citta}">
                                </div>
                            </div>
                            <div class="form-row">
                                <div class="form-group">
                                    <label for="provincia" class="form-label">Provincia</label>
                                    <input type="text" id="provincia" name="provincia" class="form-input" value="${param.provincia}" maxlength="2">
                                </div>
                                <div class="form-group">
                                    <label for="paese" class="form-label">Paese</label>
                                    <c:set var="paeseVal" value="${param.paese}" />
                                    <c:if test="${empty paeseVal}">
                                        <c:set var="paeseVal" value="Italia" />
                                    </c:if>
                                    <input type="text" id="paese" name="paese" class="form-input" value="${paeseVal}">
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="panel-block">
                        <div class="panel-title">Dati di pagamento</div>

                        <div class="form-group">
                            <label for="card-name" class="form-label">Nome sulla carta</label>
                            <input type="text" id="card-name" name="cardName" class="form-input" required>
                        </div>

                        <div class="form-group">
                            <label for="card-number" class="form-label">Numero carta</label>
                            <input type="text" id="card-number" name="cardNumber" class="form-input" placeholder="1234 5678 9012 3456" required>
                        </div>

                        <div class="form-row">
                            <div class="form-group">
                                <label for="expiry-date" class="form-label">Scadenza (MM/AA)</label>
                                <input type="text" id="expiry-date" name="expiryDate" class="form-input" placeholder="MM/AA" required>
                            </div>

                            <div class="form-group">
                                <label for="cvv" class="form-label">CVV</label>
                                <input type="text" id="cvv" name="cvv" class="form-input" placeholder="123" required>
                            </div>
                        </div>
                    </div>

                    <div class="form-actions">
                        <a href="${pageContext.request.contextPath}/carrello" class="btn btn-outline">Torna indietro</a>
                        <button type="submit" class="btn btn-primary">
                            Paga ora EUR <fmt:formatNumber value="${totale}" minFractionDigits="2" maxFractionDigits="2"/>
                        </button>
                    </div>
                </form>
            </section>

            <aside class="order-panel">
                <div class="panel-title">Il tuo ordine</div>
                <c:choose>
                    <c:when test="${empty sessionScope.carrello or empty sessionScope.carrello.articoli}">
                        <p>Il carrello e vuoto</p>
                    </c:when>
                    <c:otherwise>
                        <div class="order-items">
                            <c:forEach items="${sessionScope.carrello.articoli}" var="articolo">
                                <div class="order-item">
                                    <c:set var="copertinaNome" value="${articolo.libro.copertina}" />
                                    <c:if test="${empty copertinaNome}">
                                        <c:set var="copertinaNome" value="default.jpg" />
                                    </c:if>
                                    <img src="${pageContext.request.contextPath}/img/libri/copertine/${copertinaNome}"
                                         alt="${articolo.libro.titolo}">
                                    <div class="order-item-info">
                                        <div class="order-item-title">${articolo.libro.titolo}</div>
                                        <div class="order-item-meta">Quantita: ${articolo.quantita}</div>
                                        <div class="order-item-price">
                                            EUR <fmt:formatNumber value="${articolo.totale}"
                                                       minFractionDigits="2" maxFractionDigits="2"/>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="order-total">
                    Totale: EUR <fmt:formatNumber value="${totale}" minFractionDigits="2"
                                               maxFractionDigits="2"/>
                </div>
            </aside>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp" />

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const form = document.getElementById('payment-form');
        const submitBtn = form.querySelector('button[type="submit"]');
        const addressSelect = document.getElementById('indirizzoSpedizione');
        const newAddressForm = document.getElementById('new-address-form');
        const newAddressInputs = newAddressForm ? newAddressForm.querySelectorAll('input') : [];

        function toggleNewAddress() {
            const isNew = !addressSelect || addressSelect.value === 'new';
            if (newAddressForm) {
                newAddressForm.style.display = isNew ? 'block' : 'none';
            }
            newAddressInputs.forEach(input => {
                if (isNew) {
                    input.disabled = false;
                    input.setAttribute('required', 'required');
                } else {
                    input.disabled = true;
                    input.removeAttribute('required');
                }
            });
        }

        if (addressSelect) {
            addressSelect.addEventListener('change', toggleNewAddress);
        }
        toggleNewAddress();

        const cardNumber = document.getElementById('card-number');
        if (cardNumber) {
            cardNumber.addEventListener('input', function (e) {
                let value = e.target.value.replace(/\D/g, '');
                value = value.replace(/(\d{4})(?=\d)/g, '$1 ');
                e.target.value = value.trim();
            });
        }

        const expiryDate = document.getElementById('expiry-date');
        if (expiryDate) {
            expiryDate.addEventListener('input', function (e) {
                let value = e.target.value.replace(/\D/g, '');
                if (value.length > 2) {
                    value = value.substring(0, 2) + '/' + value.substring(2, 4);
                }
                e.target.value = value;

                if (value.length >= 2) {
                    const month = parseInt(value.substring(0, 2), 10);
                    if (month < 1 || month > 12) {
                        e.target.setCustomValidity('Inserisci un mese valido (01-12)');
                    } else {
                        e.target.setCustomValidity('');
                    }
                } else {
                    e.target.setCustomValidity('');
                }
            });
        }

        form.addEventListener('submit', function (e) {
            if (!form.checkValidity()) {
                e.preventDefault();
                form.reportValidity();
                return;
            }

            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.innerHTML = 'Elaborazione in corso...';
            }
        });
    });
</script>
</body>
</html>


<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Pagamento - Readify</title>
    <style>
        * {
            box-sizing: border-box;
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
        }

        body {
            background-color: #f5f5f5;
            color: #333;
            line-height: 1.6;
            padding: 20px;
        }

        .container {
            max-width: 1000px;
            margin: 0 auto;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
            overflow: hidden;
        }

        .header {
            background: #2c3e50;
            color: white;
            padding: 20px;
            text-align: center;
        }

        .header h1 {
            margin: 0;
            font-size: 24px;
        }

        .content {
            display: flex;
            flex-wrap: wrap;
        }

        .payment-section {
            flex: 2;
            padding: 20px;
            border-right: 1px solid #eee;
        }

        .order-summary {
            flex: 1;
            padding: 20px;
            background: #f9f9f9;
        }

        .form-group {
            margin-bottom: 15px;
        }

        label {
            display: block;
            margin-bottom: 5px;
            font-weight: bold;
        }

        input[type="text"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 16px;
        }

        .form-row {
            display: flex;
            gap: 15px;
        }

        .form-row .form-group {
            flex: 1;
        }

        .btn {
            display: inline-block;
            padding: 12px 24px;
            background: #3498db;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 16px;
            text-decoration: none;
            text-align: center;
        }

        .btn:hover {
            background: #2980b9;
        }

        .btn-outline {
            background: white;
            border: 1px solid #3498db;
            color: #3498db;
        }

        .btn-outline:hover {
            background: #f5f5f5;
        }

        .form-actions {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
        }

        .order-item {
            display: flex;
            padding: 10px 0;
            border-bottom: 1px solid #eee;
        }

        .order-item img {
            width: 60px;
            height: 90px;
            object-fit: cover;
            margin-right: 15px;
        }

        .order-item-info {
            flex: 1;
        }

        .order-item-title {
            font-weight: bold;
            margin-bottom: 5px;
        }

        .order-item-price {
            color: #e74c3c;
            font-weight: bold;
        }

        .order-total {
            margin-top: 20px;
            padding-top: 15px;
            border-top: 2px solid #2c3e50;
            text-align: right;
            font-size: 18px;
            font-weight: bold;
        }

        @media (max-width: 768px) {
            .content {
                flex-direction: column;
            }

            .payment-section, .order-summary {
                border-right: none;
                border-bottom: 1px solid #eee;
            }

            .form-row {
                flex-direction: column;
                gap: 0;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>Completa il tuo ordine</h1>
    </div>

    <div class="content">
        <div class="payment-section">
            <h2>Dati di pagamento</h2>

            <form id="payment-form" action="${pageContext.request.contextPath}/ConfermaOrdine" method="post">
                <!-- fondamentale: inoltra l'id indirizzo scelto nel checkout -->
                <input type="hidden" name="indirizzoSpedizione" value="${indirizzoSpedizione}"/>

                <div class="form-group">
                    <label for="card-name">Nome sulla carta</label>
                    <input type="text" id="card-name" name="cardName" required>
                </div>

                <div class="form-group">
                    <label for="card-number">Numero carta</label>
                    <input type="text" id="card-number" name="cardNumber" placeholder="1234 5678 9012 3456" required>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="expiry-date">Scadenza (MM/AA)</label>
                        <input type="text" id="expiry-date" name="expiryDate" placeholder="MM/AA" required>
                    </div>

                    <div class="form-group">
                        <label for="cvv">CVV</label>
                        <input type="text" id="cvv" name="cvv" placeholder="123" required>
                    </div>
                </div>

                <div class="form-actions">
                    <a href="${pageContext.request.contextPath}/checkout" class="btn btn-outline">
                        ← Torna indietro
                    </a>
                    <button type="submit" class="btn">
                        <c:set var="totale" value="0"/>
                        <c:if test="${not empty sessionScope.carrello and not empty sessionScope.carrello.articoli}">
                            <c:forEach items="${sessionScope.carrello.articoli}" var="articolo">
                                <c:set var="totale" value="${totale + (articolo.libro.prezzo * articolo.quantita)}"/>
                            </c:forEach>
                        </c:if>
                        Paga ora €<fmt:formatNumber value="${totale}" minFractionDigits="2" maxFractionDigits="2"/>
                    </button>
                </div>
            </form>
        </div>

        <div class="order-summary">
            <h2>Il tuo ordine</h2>
            <c:choose>
                <c:when test="${empty sessionScope.carrello or empty sessionScope.carrello.articoli}">
                    <p>Il carrello è vuoto</p>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${sessionScope.carrello.articoli}" var="articolo">
                        <div class="order-item">
                            <img src="${pageContext.request.contextPath}/img/libri/copertine/${not empty articolo.libro.copertina ? articolo.libro.copertina : 'default.jpg'}"
                                 alt="${articolo.libro.titolo}">
                            <div class="order-item-info">
                                <div class="order-item-title">${articolo.libro.titolo}</div>
                                <div>Quantità: ${articolo.quantita}</div>
                                <div class="order-item-price">
                                    €<fmt:formatNumber value="${articolo.libro.prezzo * articolo.quantita}"
                                                       minFractionDigits="2" maxFractionDigits="2"/>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>

            <div class="order-total">
                Totale: €<fmt:formatNumber value="${not empty totale ? totale : 0}" minFractionDigits="2"
                                           maxFractionDigits="2"/>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const form = document.getElementById('payment-form');
        const submitBtn = form.querySelector('button[type="submit"]');

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

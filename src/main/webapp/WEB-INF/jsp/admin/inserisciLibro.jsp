<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inserisci Libro - Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/form-styles.css">
    <style>
        .icon {
            display: inline-block;
            width: 1em;
            height: 1em;
            margin-right: 0.5em;
            vertical-align: middle;
        }

        .icon-arrow-left::before {
            content: '←';
        }

        .icon-save::before {
            content: '💾';
        }

        .icon-error::before {
            content: '⚠️';
        }
    </style>
</head>
<body>
<jsp:include page="../header.jsp"/>

<div class="container">
    <div class="form-container">
        <h2>Inserisci Nuovo Libro</h2>

        <c:if test="${not empty errore}">
            <div class="alert alert-danger">
                <span class="icon icon-error"></span>${fn:escapeXml(errore)}
            </div>
        </c:if>

        <form id="libroForm" method="post" action="${pageContext.request.contextPath}/admin/libri"
              class="form needs-validation" novalidate>
            <input type="hidden" name="azione" value="aggiungi">

            <div class="form-group">
                <label for="titolo">Titolo *</label>
                <input type="text" class="form-control" id="titolo" name="titolo"
                       required minlength="2" maxlength="200"
                       pattern="[A-Za-z0-9\s\-\.,;:!?()\[\]{}'\" \u00C0-\u017F]+">
                <div class="invalid-feedback">Inserisci un titolo valido (almeno 2 caratteri)</div>
            </div>

            <div class="form-group">
                <label for="autore">Autore *</label>
                <input type="text" class="form-control" id="autore" name="autore"
                       required minlength="2" maxlength="100"
                       pattern="[A-Za-z\s\-\.,;:!?()\[\]{}'\" \u00C0-\u017F]+">
                <div class="invalid-feedback">Inserisci un autore valido (almeno 2 caratteri)</div>
            </div>

            <div class="form-group">
                <label for="prezzo">Prezzo *</label>
                <div class="input-group">
                    <span class="input-group-text">€</span>
                    <input type="number" class="form-control" id="prezzo" name="prezzo"
                           required min="0.01" step="0.01"
                           pattern="^\d+(\.\d{1,2})?$">
                </div>
                <div class="invalid-feedback">Inserisci un prezzo valido (minimo 0,01€)</div>
            </div>

            <div class="form-group">
                <label for="isbn">ISBN *</label>
                <input type="text" class="form-control" id="isbn" name="isbn"
                       required minlength="13" maxlength="13"
                       pattern="\d{13}"
                       title="L'ISBN deve essere un numero di 13 cifre">
                <div class="invalid-feedback">Inserisci un ISBN valido di 13 cifre</div>
            </div>

            <div class="form-group">
                <label for="descrizione">Descrizione *</label>
                <textarea class="form-control" id="descrizione" name="descrizione"
                          required rows="4"></textarea>
                <div class="invalid-feedback">Inserisci una descrizione per il libro</div>
            </div>

            <div class="form-group">
                <label for="disponibilita">Disponibilità *</label>
                <input type="number" class="form-control" id="disponibilita" name="disponibilita"
                       required min="0" max="999">
                <div class="invalid-feedback">Inserisci un numero valido di copie</div>
            </div>

            <div class="form-group">
                <label for="copertina">URL Copertina *</label>
                <input type="url" class="form-control" id="copertina" name="copertina"
                       required placeholder="https://">
                <div class="invalid-feedback">Inserisci un URL valido per l'immagine</div>
            </div>


            <div class="book-form-actions">
                <a href="${pageContext.request.contextPath}/admin/libri" class="btn btn-link">
                    <span class="icon icon-arrow-left"></span>Torna alla lista
                </a>
                <button type="submit" class="btn btn-primary">
                    <span class="icon icon-save"></span>Salva Libro
                </button>
            </div>
        </form>


    </div>
</div>

<script>

    //validazione form
    if (document.querySelectorAll) {
        var forms = document.querySelectorAll('.needs-validation');

        var formValidation = function (event) {
            if (!this.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            this.classList.add('was-validated');
        };

        for (var i = 0; i < forms.length; i++) {
            forms[i].addEventListener('submit', formValidation, false);
        }
    }
</script>

<jsp:include page="../footer.jsp"/>
</body>
</html>
